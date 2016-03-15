package edu.gatech.johndoe.carecoordinator.patient;

import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import edu.gatech.johndoe.carecoordinator.util.DatabaseCallback;

/**
 * Created by aagnone3 on 3/14/2016.
 */
public class PatientEmailBody {

    private PatientEmail.EMAIL_TYPE emailType;
    private PatientEmail emailCallback;
    private String patientFirstName;
    private String patientLastName;
    private String providerName;
    private String patientCity;
    private String coordinatorName;
    private String content;

    public PatientEmailBody(PatientEmail.EMAIL_TYPE emailType, Patient patient, PatientEmail emailCallback) {
        this.emailType = emailType;
        this.emailCallback = emailCallback;
        this.patientFirstName = patient.getFirst_name();
        this.patientLastName = patient.getLast_name();
        Practitioner provider = patient.getPCP();
        this.providerName = (provider == null ? "" :
                provider.getName().getGivenAsSingleString() + " " +
                        provider.getName().getFamilyAsSingleString());
        this.patientCity = patient.getAddress().getCity();
        // TODO have global access to coordinator's name after login functionality is finalized
        this.coordinatorName = "Sally Coordy";
        formEmailBody();
    }

    // TODO handle case where coordinatorName has not yet been populated by the callback
    public void formEmailBody() {
        if (emailType == PatientEmail.EMAIL_TYPE.INITIAL_CONTACT) {
            // Initial contact email
            StringBuilder s = new StringBuilder("");
            s.append("Hello ")
                    .append(patientFirstName)
                    .append("!\n\n")
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
                    .append("Please do not hesitate to response with any questions or concerns. ")
                    .append("Talk to you soon!\n\n")
                    .append("Sincerely,\n")
                    .append(coordinatorName);
            content = s.toString();
        } else if (emailType == PatientEmail.EMAIL_TYPE.INITIAL_SUGGESTIONS) {
            // Initial suggestions email
            // TODO
        } else if (emailType == PatientEmail.EMAIL_TYPE.FOLLOW_UP) {
            //  Follow-up email
            // TODO
        } else {
            // Final Referral
            // TODO
        }
    }

    public PatientEmail.EMAIL_TYPE getEmailType() {
        return emailType;
    }

    public void setEmailType(PatientEmail.EMAIL_TYPE emailType) {
        this.emailType = emailType;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
