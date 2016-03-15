package edu.gatech.johndoe.carecoordinator.patient;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Coordinates the creation of the draft of an email to the patient.
 */
public class PatientEmail {

    enum EMAIL_TYPE {INITIAL_CONTACT, INITIAL_SUGGESTIONS, FOLLOW_UP, FINAL_REFERRAL}
    static final String EMAIL_INTENT_TYPE = "message/rfc822";
    static final String CC_RECIPIENT = "cdccoordinator2016@gmail.com";
    static final Map<EMAIL_TYPE, String> SUBJECTS;
    static {
        SUBJECTS = new HashMap<>();
        SUBJECTS.put(EMAIL_TYPE.INITIAL_CONTACT,
                "Greetings from your CDC Care Coordinator!");
        SUBJECTS.put(EMAIL_TYPE.INITIAL_SUGGESTIONS,
                "Community Resource Suggestions from your CDC Care Coordinator!");
        SUBJECTS.put(EMAIL_TYPE.FOLLOW_UP,
                "Follow-up from your CDC Care Coordinator!");
        SUBJECTS.put(EMAIL_TYPE.FINAL_REFERRAL,
                "Final Referral from your CDC Care Coordinator!");
    }

    Intent emailIntent;
    private PatientEmailBody emailBody;

    public PatientEmail() {
        this(null);
    }

    public PatientEmail(Patient p) {
        // Pass instance of this to callback to finalizeEmailContent() when database responds
        //     with the cooridnator's name
        emailBody = new PatientEmailBody(EMAIL_TYPE.INITIAL_CONTACT, p, this);

        emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType(EMAIL_INTENT_TYPE);

        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{CC_RECIPIENT});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECTS.get(EMAIL_TYPE.INITIAL_CONTACT));
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody.getContent());

        if (p != null) {
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{p.getEmail()});
        }
    }

    public Intent getEmailIntent() {
        return emailIntent;
    }

    public void setEmailIntent(Intent emailIntent) {
        this.emailIntent = emailIntent;
    }
}
