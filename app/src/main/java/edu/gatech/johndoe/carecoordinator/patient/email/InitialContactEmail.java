package edu.gatech.johndoe.carecoordinator.patient.email;

import edu.gatech.johndoe.carecoordinator.patient.Patient;

/**
 * Handles forming the content for an initial contact email with the patient.
 */
public class InitialContactEmail extends PatientEmail {

    public InitialContactEmail() {
        this(null);
    }

    public InitialContactEmail(Patient patient) {
        super(patient);
        // TODO insert care coordinator name into subject when login is finalized
        subject = "Greetings from your CDC Care Coordinator!";
        formContent();
        formIntent();
    }

    public void formContent() {
        StringBuilder s = new StringBuilder("");
        s.append(greeting)
                .append("My name is ")
                .append(coordinatorName)
                .append(", and I will be your care coordinator at the CDC! ");
        if (providerName.length() > 0) {
            s.append("I've received your information from Doctor ")
                    .append(providerName)
                    .append(", and ");
        } else {
            s.append("I ");
        }
        s.append("will be in touch soon with some suggestions for resources in the ")
                .append(patientCity)
                .append(" area which you can utilize to improve your health!\n\n")
                .append(signature);
        content = s.toString();
    }
}
