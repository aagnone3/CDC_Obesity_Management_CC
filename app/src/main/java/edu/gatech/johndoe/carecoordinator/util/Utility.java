package edu.gatech.johndoe.carecoordinator.util;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;
import edu.gatech.johndoe.carecoordinator.Community;
import edu.gatech.johndoe.carecoordinator.patient.EHR;


public class Utility {
    private static final String SERVER_BASE = "http://52.72.172.54:8080/fhir/baseDstu2";
    private static final FhirContext ctx = FhirContext.forDstu2();

    private static ArrayList<edu.gatech.johndoe.carecoordinator.patient.Patient> dummy_patients;

    public static ca.uhn.fhir.model.dstu2.resource.Patient get_patient_info_by_id(int id) {
        // Create the client for interacting with the FHIR server
        IGenericClient client = ctx.newRestfulGenericClient(SERVER_BASE);

        // Obtain the results from the patient query to the FHIR server
        Bundle results = client.search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly()
                        .systemAndIdentifier("uniqueId", "99999" + String.valueOf(id)))
                .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                .execute();
        ca.uhn.fhir.model.dstu2.resource.Patient patient = null;
        if (results.getEntry().size() == 0) {
            System.out.println("No results matching the search criteria!");
        } else {
            patient = (Patient) results.getEntry().get(0).getResource();
        }
        return patient;
    }

    public static ArrayList<edu.gatech.johndoe.carecoordinator.patient.Patient> getPatients() {
        if (dummy_patients == null) {
//        ArrayList<edu.gatech.johndoe.carecoordinator.patient.Patient> patients = new ArrayList<>();
            dummy_patients = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                ca.uhn.fhir.model.dstu2.resource.Patient p = get_patient_info_by_id(i);

                dummy_patients.add(new edu.gatech.johndoe.carecoordinator.patient.Patient(p));
                for (int j = 0; j < (int) (Math.random() * 10); j++) {
                    dummy_patients.get(i - 1).addEHR(new EHR());
                }
            }
        }
        return dummy_patients;
    }

    public static ArrayList<Community> getCommunities() {
        ArrayList<Community> communities = new ArrayList<>(Arrays.asList(
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54)
        ));  // FIXME: replace with real data

        return communities;
    }
}
