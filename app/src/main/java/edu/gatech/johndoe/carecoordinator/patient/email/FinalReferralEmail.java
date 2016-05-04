package edu.gatech.johndoe.carecoordinator.patient.email;

import android.content.Intent;

import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.util.Utility;

/**
 * Handles forming the content for a final referral email with the patient.
 */
public class FinalReferralEmail extends PatientEmail {

    private Community finalCommunity;
    private CarePlan carePlan;

    public FinalReferralEmail() {
        this(null, null, null);
    }

    public FinalReferralEmail(Patient patient, Community finalCommunity, CarePlan carePlan) {
        super(patient);
        this.finalCommunity = finalCommunity;
        this.carePlan = carePlan;
        subject = "Finalized Care Plan and E-referral from your CDC Care Coordinator!";
        formContent();
        formIntent();
        patient.setWorkingCommunity(finalCommunity);
    }

    public void formContent() {
        finalCommunity = patient.getWorkingCommunity();

        finalCommunity.addPatient(patient);
        finalCommunity.setPatientCount(finalCommunity.getPatientCount() + 1);
        Utility.saveCommunityResource(finalCommunity);
        //
        StringBuilder s = new StringBuilder("");
        s.append(greeting)
                .append("Dr. ")
                .append(carePlan.getPhysicianNameLast())
                .append(" and I are excited to let you know that we have completed your ")
                .append(carePlan.getType().toLowerCase())
                .append(" referral to ")
                .append(finalCommunity.getName() + "!\n\n");
        s.append("At your convenience, take some time to look over the details below, and give ")
                .append(finalCommunity.getName())
                .append(" a call when you are are ready to get started!");
        s.append(carePlan.formattedString());
        s.append(finalCommunity);
        s.append(signature);
        content = s.toString();
    }

    public Community getFinalCommunity() {
        return finalCommunity;
    }

    public void setFinalCommunity(Community finalCommunity) {
        this.finalCommunity = finalCommunity;
    }

    public CarePlan getCarePlan() {
        return carePlan;
    }

    public void setCarePlan(CarePlan carePlan) {
        this.carePlan = carePlan;
    }
}
