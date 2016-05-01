package edu.gatech.johndoe.carecoordinator.patient.email;

import android.util.Log;

import java.lang.reflect.Array;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.util.Utility;

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
        Set<Double> keys = patient.getDistanceSortedCommunities().keySet();
        String carePlanType = "NONE";
        String workingCarePlan = "NONE";
        Integer resourceCount = 0;
        Integer loopCount = 0;
        ArrayList<String> suggestedCommunities = new ArrayList<>();

        // find the type of the first "open" care plan
        if (patient.getReferralList() != null) {
            for (String carePlanID : patient.getReferralList()) {
                if (!carePlanType.equals("NONE"))
                    break;
                for (CarePlan carePlan : Utility.carePlan_list) {
                    if (carePlan.getId().equals(carePlanID)) {
                        if (carePlan.getStatus().equals("OPENED")) {
                            carePlanType = carePlan.getType();
                            workingCarePlan = carePlan.getId();
                        }
                    }
                }
            }
        }

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
                .append("know what your thoughts are about them!\n\n");
        // TODO insert community resource suggestions

        if (!carePlanType.equals("NONE")) {
            for (Object key : keys) {
                Log.e("key", key.toString());
                if (loopCount >= patient.getDistanceSortedCommunities().size() || resourceCount >= 5)
                    break;

                Object o = patient.getDistanceSortedCommunities().get(key);
                for (Community community : Utility.community_list) {
                    if (community.getId().equals(o.toString())) {
                        if (carePlanType.equals("Diet Plan") && community.getCommunityType().equals("nutritionist")) {
                            s.append(community.getName() + "\n");
                            s.append(community.getStreetAddress() + "\n");
                            s.append(community.getCity() + ", " + community.getState() + "   " + community.getZipcode() + "\n");
                            s.append(community.getPhoneNumber() + "\n");
                            s.append(community.getEmailAddress() + "\n\n");
                            suggestedCommunities.add(community.getId());
                            resourceCount++;
                            loopCount++;
                        } else if (carePlanType.equals("Exercise Plan") && community.getCommunityType().equals("physical")) {
                            s.append(community.getName() + "\n");
                            s.append(community.getStreetAddress() + "\n");
                            s.append(community.getCity() + ", " + community.getState() + "   " + community.getZipcode() + "\n");
                            s.append(community.getPhoneNumber() + "\n");
                            s.append(community.getEmailAddress() + "\n\n");
                            suggestedCommunities.add(community.getId());
                            resourceCount++;
                            loopCount++;
                        } else {
                            loopCount++;
                        }
                        break;
                    }
                }
            }
            if (!patient.getSuggestedCommunities().containsKey(workingCarePlan)) {
                patient.addSuggestedCommunities(workingCarePlan, suggestedCommunities);
            }
        }
        else
            s.append("! NO OPEN CARE PLAN !");

        s.append(signature);
        content = s.toString();
    }
}
