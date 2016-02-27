package edu.gatech.johndoe.carecoordinator.util;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;

public class Utility {
    private static final String SERVER_BASE =
            "http://52.72.172.54:8080/fhir/baseDstu2";
    private static final String GET_PATIENT_INFO =
            SERVER_BASE + "/Patient?_id=";

    public static Patient get_patient_info_by_id(int id) {
        // Establish the Fhir context (DSTU2 vs others)
        // TODO Creating the FhirContext is computationally expensive, consider making this method
        // non-static and having the FhirContext created in the Utility() constructor
        FhirContext ctx = FhirContext.forDstu2();
        //FhirContext ctx = FhirContext.forDstu2Hl7Org();

        // Create the client for interacting with the FHIR server
        IGenericClient client = ctx.newRestfulGenericClient(SERVER_BASE);

        // Obtain the results from the patient query to the FHIR server
        Bundle results = client.search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly().code(String.valueOf(id)))
                .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                .execute();
        if (results.getEntry().size() == 0) {
            System.out.println("No results matching the search criteria!");
        }
        Patient patient = (Patient) results.getEntry().get(0).getResource();

        /*
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
        */

        return patient;
    }

    /*
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
    */
}
