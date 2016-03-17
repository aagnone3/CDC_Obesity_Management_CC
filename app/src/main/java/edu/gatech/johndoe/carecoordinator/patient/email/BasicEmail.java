package edu.gatech.johndoe.carecoordinator.patient.email;

import edu.gatech.johndoe.carecoordinator.patient.Patient;

/**
 * Handles forming the content for a basic contact email with the patient, in which the care
 * coordinator will manually fill in desired information.
 */
public class BasicEmail extends PatientEmail {

    public BasicEmail() {
        this(null);
    }

    public BasicEmail(Patient patient) {
        super(patient);
        subject = "Hello from your CDC Care Coordinator!";
        formContent();
        formIntent();
    }

    public void formContent() {
        content = "Hello " + patient.getFirst_name() + "!\n\n\n\n" + signature;
    }
}
