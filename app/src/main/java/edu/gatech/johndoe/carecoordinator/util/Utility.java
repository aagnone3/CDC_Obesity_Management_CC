package edu.gatech.johndoe.carecoordinator.util;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utility {
    private static final String GET_PATIENT_INFO =
            "http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base/Patient?_id=";

    public static JSONObject get_patient_info_by_id(int id) {
        HttpURLConnection urlConnection = null;
        JSONObject object = null;
        InputStream inStream = null;
        try {
            URL url = new URL(GET_PATIENT_INFO.concat(String.valueOf(id)));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }
            object = new JSONObject(response);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ignored) {
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return object;
    }
}
