package com.example.krishnateja.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    private final static String TAG="ForecastFragment";
    private ArrayAdapter<String> arrayAdapterForecast;
    private ListView listViewForecast;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> arraylistForecast=new ArrayList<String>();
        for (int i=0;i<10;i++){
            arraylistForecast.add("Today-sunny-88/63");
        }
        arrayAdapterForecast=new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,R.id.list_item_forecast_textview,arraylistForecast);
        listViewForecast=(ListView) rootView.findViewById(R.id.listview_forecast);
        listViewForecast.setAdapter(arrayAdapterForecast);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_refresh){
            String url=buildUrl();
            new FetchWeatherTask().execute(url);
        }
        return super.onOptionsItemSelected(item);
    }
    public String buildUrl(){
        Uri.Builder builder=new Uri.Builder();
        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("q","94043")
                .appendQueryParameter("mode","json")
                .appendQueryParameter("units","metric")
                .appendQueryParameter("cnt","7");
        String url=builder.build().toString();
        Log.v(TAG,"url->"+url);
        return url;
    }
    public class FetchWeatherTask extends AsyncTask<String,Void,String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(params[0]);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(TAG,"forecastJsonStr "+forecastJsonStr);
                return new WeatherDataParser().getWeatherDataFromJson(forecastJsonStr,7);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            Log.d(TAG,"onPostExecute->"+strings.toString());
            arrayAdapterForecast.clear();
            for(String s:strings){
                arrayAdapterForecast.add(s);
            }


        }
    }
}