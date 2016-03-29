package edu.gatech.johndoe.carecoordinator.patient.email;

import edu.gatech.johndoe.carecoordinator.patient.Patient;

/**
 * Factory class to return a specific subclass of PatientEmail, based on the specified EMAIL_TYPE
 */
public class PatientEmailFactory {

    public enum EMAIL_TYPE {INITIAL_CONTACT, INITIAL_SUGGESTIONS, FOLLOW_UP, FINAL_REFERRAL}

    public static PatientEmail getEmailBody(EMAIL_TYPE type, Patient patient) {
        PatientEmail email;
        if (type == EMAIL_TYPE.INITIAL_CONTACT) {
            // Initial contact
            email = new InitialContactEmail(patient);
        } else if (type == EMAIL_TYPE.INITIAL_SUGGESTIONS) {
            // Initial suggestions
            email = new InitialSuggestionsEmail(patient);
        } else if (type == EMAIL_TYPE.FOLLOW_UP) {
            // Follow up
            email = new FollowUpEmail(patient);
        } else if (type == EMAIL_TYPE.FINAL_REFERRAL) {
            // Final referral
            email = new FinalReferralEmail(patient);
        } else {
            email = new BasicEmail(patient);
        }
        return email;
    }
}
