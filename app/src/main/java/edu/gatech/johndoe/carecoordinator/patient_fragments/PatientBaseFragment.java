package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.gatech.johndoe.carecoordinator.R;

public class PatientBaseFragment extends Fragment {

    private static final String TAG = "PatientBaseFragment";

    public static PatientBaseFragment newInstance(String param) {
        PatientBaseFragment fragment = new PatientBaseFragment();
        Bundle args = new Bundle();
        args.putString(TAG, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_fragment_base, container, false);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.patient_container, new PatientListFragment());
        transaction.commit();
        return view;
    }

}