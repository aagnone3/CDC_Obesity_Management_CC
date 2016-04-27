package edu.gatech.johndoe.carecoordinator.patient.email;

import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.patient.Patient;

/**
 * Factory class to return a specific subclass of PatientEmail, based on the specified EMAIL_TYPE
 */
public class PatientEmailFactory {

    public enum EMAIL_TYPE {INITIAL_CONTACT, INITIAL_SUGGESTIONS, FOLLOW_UP, FINAL_REFERRAL}

    public static PatientEmail getEmailBody(int selectedMenuId, Patient patient) {
        PatientEmail email;
        if (selectedMenuId == R.id.menu_contact) {
            // Initial contact
            email = new InitialContactEmail(patient);
        } else if (selectedMenuId == R.id.menu_suggestions) {
            // Initial suggestions
            email = new InitialSuggestionsEmail(patient);
        } else if (selectedMenuId == R.id.menu_followup) {
            // Follow up
            email = new FollowUpEmail(patient);
        }  else if (selectedMenuId == R.id.menu_referral) {
            // Final referral
            email = new FinalReferralEmail(patient);
        } else {
            // Basic email to be filled by the coordinator
            email = new BasicEmail(patient);
        }
        return email;
    }
}
