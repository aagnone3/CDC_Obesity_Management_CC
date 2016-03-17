package edu.gatech.johndoe.carecoordinator.patient.email;

import edu.gatech.johndoe.carecoordinator.patient.Patient;

/**
 * Handles forming the content for a final referral email with the patient.
 */
public class FinalReferralEmail extends PatientEmail {

    public FinalReferralEmail() {
        this(null);
    }

    public FinalReferralEmail(Patient patient) {
        super(patient);
        // TODO insert care coordinator name into subject when login is finalized
        subject = "Final Referral from your CDC Care Coordinator!";
        formContent();
        formIntent();
    }

    public void formContent() {
        //
        StringBuilder s = new StringBuilder("");
        s.append(greeting)
                .append("We are excited to let you know that we have completed your referral to ")
                // TODO community resource name
                .append("<community resource>!\n\n");
        // TODO give a bunch of details about the community resource and the referral. Maybe attach a generated document
        s.append("At your convenience, take some time to look over these details, and give ")
                // TODO community resource name
                .append(" a call when you are are ready to get started!");
        s.append(signature);
        content = s.toString();
    }
}
