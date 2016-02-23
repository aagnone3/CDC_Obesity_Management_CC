package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.gatech.johndoe.carecoordinator.Patient;
import edu.gatech.johndoe.carecoordinator.R;

public class PatientAdapter extends ArrayAdapter<Patient> {

    private android.support.v4.app.FragmentManager fragment_manager;

    public PatientAdapter(Context context, int resource, List<Patient> patients, android.support.v4.app.FragmentManager fragment_manager) {
        super(context, resource, patients);
        this.fragment_manager = fragment_manager;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.patient_listview_row, null);
            v.setClickable(false);
            v.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        final Patient p = getItem(position);

        if (p != null) {
            final TextView patient_name = (TextView) v.findViewById(R.id.patient_name);
            final TextView patient_status = (TextView) v.findViewById(R.id.patient_status);
            patient_name.setText(p.getName_first());

            patient_name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //test
                    Log.v("TAG", "CLICKED row number: " + position);
                    FragmentTransaction ft = fragment_manager.beginTransaction();
                    ft.replace(R.id.patient_container, new PatientInfoFragment());
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            patient_status.setText(p.isPending() ? "Pending" : "Closed");
        }
        return v;
    }

}