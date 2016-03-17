package edu.gatech.johndoe.carecoordinator.patient.email;

import edu.gatech.johndoe.carecoordinator.patient.Patient;

/**
 * Handles forming the content for an initial community resource suggestions email with the patient.
 */
public class InitialSuggestionsEmail extends PatientEmail {

    public InitialSuggestionsEmail() {
        this(null);
    }

    public InitialSuggestionsEmail(Patient patient) {
        super(patient);
        // TODO insert care coordinator name into subject when login is finalized
        subject = "Community Resource Suggestions from your CDC Care Coordinator!";
        formContent();
        formIntent();
    }

    public void formContent() {
        StringBuilder s = new StringBuilder("");
        s.append(greeting)
                .append("I hope you are doing well! ")
                .append("Based on ");
        if (providerName.length() > 0) {
            s.append("Dr. ")
                    .append(providerName)
                    .append("'s referral and ");
        }
        s.append(providerName)
                .append("community resources near you, we have put together a list")
                .append("of resources that you may benefit from the most!\n\n");
        s.append("At your convenience, take some time to look over these suggestions, and let us ")
                .append("know what your thoughts are about them!");
        // TODO insert community resource suggestions
        s.append("<insert community resource suggestions here>\n\n");
        s.append(signature);
        content = s.toString();
    }
}
