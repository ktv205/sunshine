package com.example.krishnateja.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

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
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//        final ArrayList<String> arraylistForecast=new ArrayList<String>();
//        for (int i=0;i<10;i++){
//            arraylistForecast.add("Today-sunny-88/63");
//        }
        arrayAdapterForecast=new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,R.id.list_item_forecast_textview);
        listViewForecast=(ListView) rootView.findViewById(R.id.listview_forecast);
        listViewForecast.setAdapter(arrayAdapterForecast);
        listViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView forecastTextView=(TextView)view.findViewById(R.id.list_item_forecast_textview);
                String forecast=forecastTextView.getText().toString();

                //Toast.makeText(getActivity(),"list item clicked",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(),
                        DetailActivity.class).putExtra(Intent.EXTRA_TEXT,forecast);
                getActivity().startActivity(intent);
            }
        });
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
           updateWeather();
        }
        return super.onOptionsItemSelected(item);
    }
    public void updateWeather(){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String zip=pref.getString(getString(R.string.key_editpref), getString(R.string.default_value_editpref));
        Log.d(TAG,"zip->"+zip);
        Log.d(TAG,"default->"+getString(R.string.default_value_editpref));
        String url=buildUrl(zip);
        new FetchWeatherTask(getActivity(),arrayAdapterForecast).execute(url);
    }
    public String buildUrl(String zip){
        Uri.Builder builder=new Uri.Builder();
        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("q",zip)
                .appendQueryParameter("mode","json")
                .appendQueryParameter("units","metric")
                .appendQueryParameter("cnt","7");
        String url=builder.build().toString();
        Log.v(TAG,"url->"+url);
        return url;
    }
 }