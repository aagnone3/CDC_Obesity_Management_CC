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
    private static final String SERVER_BASE = "http://52.72.172.54:8080/fhir/baseDstu2";
    private static final FhirContext ctx = FhirContext.forDstu2();

    public static Patient get_patient_info_by_id(int id) {
        // Create the client for interacting with the FHIR server
        IGenericClient client = ctx.newRestfulGenericClient(SERVER_BASE);

        // Obtain the results from the patient query to the FHIR server
        Bundle results = client.search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly()
                        .systemAndIdentifier("uniqueId", "99999" + String.valueOf(id)))
                .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                .execute();
        Patient patient = null;
        if (results.getEntry().size() == 0) {
            System.out.println("No results matching the search criteria!");
        } else {
            patient = (Patient) results.getEntry().get(0).getResource();
        }
        return patient;
    }
}