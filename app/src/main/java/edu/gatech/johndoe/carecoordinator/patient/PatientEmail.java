package edu.gatech.johndoe.carecoordinator.patient;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aagnone3 on 2/26/2016.
 */
public class PatientEmail {

    enum SUBJECT_TYPES {INITIAL_CONTACT, FOLLOW_UP, FINAL_REFERRAL};
    static final String EMAIL_INTENT_TYPE = "message/rfc822";
    static final String CC_RECIPIENT = "a_agnone123@yahoo.com";
    static final Map<SUBJECT_TYPES, String> SUBJECTS;
    static {
        SUBJECTS = new HashMap<>();
        SUBJECTS.put(SUBJECT_TYPES.INITIAL_CONTACT, "Greetings from your CDC Care Coordinator!");
        SUBJECTS.put(SUBJECT_TYPES.FOLLOW_UP, "Follow-up from your CDC Care Coordinator!");
        SUBJECTS.put(SUBJECT_TYPES.FINAL_REFERRAL, "Final Referral from your CDC Care Coordinator!");
        // TODO Defaults for email body contents
    }

    Intent emailIntent;

    public PatientEmail() {
        this(null);
    }

    public PatientEmail(Patient p) {
        emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType(EMAIL_INTENT_TYPE);

        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{CC_RECIPIENT});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECTS.get(SUBJECT_TYPES.INITIAL_CONTACT));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Sent from my Android app");

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
