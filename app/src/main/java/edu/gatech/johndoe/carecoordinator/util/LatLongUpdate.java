package edu.gatech.johndoe.carecoordinator.util;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import edu.gatech.johndoe.carecoordinator.util.ParsedLatLong.LatLongResponse;


public class LatLongUpdate extends AsyncTask<String, Void, double[]> {

    private HttpURLConnection urlConnection;
    private OnLatLongUpdateListener listener;

    public LatLongUpdate(OnLatLongUpdateListener listener) {
        this.listener = listener;
    }

    public LatLongUpdate() {
        this(null);
    }

    @Override
    protected double[] doInBackground(String... args) {
        double[] latLongResult = new double[2];
        StringBuilder result = new StringBuilder();

        try {
            String urlString = "http://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(args[0], "UTF-8");
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Gson latLongGson = new Gson();
            LatLongResponse response = latLongGson.fromJson(result.toString(), LatLongResponse.class);
            latLongResult[0] = response.getResults().get(0).getGeometry().getLocation().getLat();
            latLongResult[1] = response.getResults().get(0).getGeometry().getLocation().getLng();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }

        return latLongResult;
    }

    @Override
    protected void onPostExecute(double[] result) {
        listener.onUpdate(result);
    }
}
