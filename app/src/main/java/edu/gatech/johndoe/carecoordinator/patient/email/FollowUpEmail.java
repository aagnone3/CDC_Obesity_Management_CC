package edu.gatech.johndoe.carecoordinator.patient.email;

import edu.gatech.johndoe.carecoordinator.patient.Patient;

/**
 * Handles forming the content for a generic follow-up email with the patient.
 */
public class FollowUpEmail extends PatientEmail {

    public FollowUpEmail() {
        this(null);
    }

    public FollowUpEmail(Patient patient) {
        super(patient);
        // TODO insert care coordinator name into subject when login is finalized
        subject = "Follow-up from your CDC Care Coordinator!";
        formContent();
        formIntent();
    }

    public void formContent() {
        content = greeting +
                "I hope you are doing well! I'd like to follow up about \n\n" +
                signature;
    }
}
