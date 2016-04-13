package edu.gatech.johndoe.carecoordinator.util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.util.ParsedLatLong.LatLongResponse;
import edu.gatech.johndoe.carecoordinator.util.ParsedLatLong.Location;

/**
 * Created by colton on 4/12/16.
 */
public class LatLongUpdate extends AsyncTask<List<Community>, Void, String> {

    HttpURLConnection urlConnection;

    @Override
    protected String doInBackground(List<Community>... args) {

        for(int i=0; i<args[0].size(); i++){

            if(args[0].get(i).getLatitude() != 0)
                continue;

            String urlString = ("http://maps.google.com/maps/api/geocode/json?address=" + args[0].get(i).getFullAddress() + "&sensor=false");
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urlString.replaceAll("\\s", "%20"));
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Gson latLongGson = new Gson();
                LatLongResponse response = latLongGson.fromJson(result.toString(), LatLongResponse.class);
                args[0].get(i).setLatitude(response.getResults().get(0).getGeometry().getLocation().getLat());
                args[0].get(i).setLongitude(response.getResults().get(0).getGeometry().getLocation().getLng());

            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }

            //this pause is needed to avoid Google API OVER_QUERY_LIMIT of 5 requests per second
            try{
                TimeUnit.MILLISECONDS.sleep(200);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        return ("done");
    }

    @Override
    protected void onPostExecute(String result) {

        if (result != null){
            Log.d("LatLongUpdate", "done getting resource lat/long");
        }

    }

}
