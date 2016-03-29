package edu.gatech.johndoe.carecoordinator.patient.email;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import edu.gatech.johndoe.carecoordinator.patient.Patient;

/**
 * Coordinates the creation of the draft of an email to the patient.
 */
public abstract class PatientEmail {

    // Static members
    protected static final String EMAIL_INTENT_TYPE = "message/rfc822";
    protected static final String CC_RECIPIENT = "cdccoordinator2016@gmail.com";

    // Instance members
    protected Patient patient;
    protected String providerName;
    protected String patientCity;
    protected String coordinatorName; // TODO remove when login functionality is finalized;
    protected String greeting;
    protected String signature;
    protected String subject;
    protected String content;

    Intent emailIntent;

    public PatientEmail() {
        this(null);
    }

    public PatientEmail(Patient patient) {
        this.patient = patient;
        Practitioner provider = patient.getPCP();
        this.providerName = (provider == null ? "" :
                provider.getName().getGivenAsSingleString() + " " +
                        provider.getName().getFamilyAsSingleString());
        this.patientCity = patient.getAddress_second().split(",")[0];
        // TODO have global access to coordinator's name after login functionality is finalized
        this.coordinatorName = "Sally Coordy";
        this.greeting = "Hello " + patient.getFirst_name() + "!\n\n";
        this.signature = "\n\nPlease do not hesitate to respond with any questions or concerns. " +
                "Talk to you soon!\n\nSincerely,\n" + coordinatorName;
    }

    public abstract void formContent();

    public void formIntent() {
        emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType(EMAIL_INTENT_TYPE);

        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{CC_RECIPIENT});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);

        if (patient != null) {
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{patient.getEmail()});
        }
    }

    public Intent getEmailIntent() {
        return emailIntent;
    }

    public void setEmailIntent(Intent emailIntent) {
        this.emailIntent = emailIntent;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getPatientCity() {
        return patientCity;
    }

    public void setPatientCity(String patientCity) {
        this.patientCity = patientCity;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
