package edu.gatech.johndoe.carecoordinator.care_plan.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.gatech.johndoe.carecoordinator.R;


/**
 * CarePlan fragment.
 */
public class CarePlanFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_care_plan, container, false);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        System.out.println("CarePlanFragment");
        transaction.replace(R.id.referral_content, new CarePlanListFragment(), "list");
        transaction.commit();
        return view;
    }
}
