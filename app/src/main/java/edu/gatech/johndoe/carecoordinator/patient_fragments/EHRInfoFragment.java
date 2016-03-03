package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.patient.EHR;

public class EHRInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ehr_info, container, false);
        Bundle extras = getArguments();
        EHR e = extras.getParcelable("EHR");
        int pos = extras.getInt("EHR_NUM");
        TextView temp = (TextView) view.findViewById(R.id.temp_label);
        temp.setText("EHR " + pos);
        return view;
    }
}
