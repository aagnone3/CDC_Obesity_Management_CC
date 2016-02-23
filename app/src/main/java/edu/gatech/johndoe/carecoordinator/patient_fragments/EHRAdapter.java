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

import edu.gatech.johndoe.carecoordinator.EHR;
import edu.gatech.johndoe.carecoordinator.R;

public class EHRAdapter extends ArrayAdapter<EHR> {

    private android.support.v4.app.FragmentManager fragment_manager;

    public EHRAdapter(Context context, int resource, List<EHR> ehr, android.support.v4.app.FragmentManager fragment_manager) {
        super(context, resource, ehr);
        this.fragment_manager = fragment_manager;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.patient_ehr_listview_row, null);
            v.setClickable(false);
            v.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        final EHR e = getItem(position);

        if (e != null) {
            final TextView ehr_title = (TextView) v.findViewById(R.id.ehr_title);
            final TextView ehr_suggestion = (TextView) v.findViewById(R.id.ehr_suggestion);
            final TextView ehr_status = (TextView) v.findViewById(R.id.ehr_status);
            ehr_title.setText("EHR" + position);
            ehr_title.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //test
                    Log.v("TAG", "EHR CLICKED row number: " + position);
                    FragmentTransaction ft = fragment_manager.beginTransaction();
                    ft.replace(R.id.patient_container, new EHRInfoFragment());
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            ehr_suggestion.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.v("TAG", "Suggestion CLICKED row number: " + position);
                }
            });

            ehr_status.setText(e.isPending() ? "Pending" : "Closed");
            ehr_status.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (e.isPending() == true) {
                        ehr_status.setText("Closed");
                        e.setClosed();
                    } else {
                        ehr_status.setText("Pending");
                        e.setPending();
                    }
                }
            });
        }
        return v;
    }

}