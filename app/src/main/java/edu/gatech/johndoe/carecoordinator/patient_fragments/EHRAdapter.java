package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.patient.EHR;
import edu.gatech.johndoe.carecoordinator.patient.Patient;

public class EHRAdapter extends ArrayAdapter<EHR> {

    private android.support.v4.app.FragmentManager fragment_manager;
    private Patient p;

    public EHRAdapter(Context context, int resource, Patient p, android.support.v4.app.FragmentManager fragment_manager) {
        super(context, resource, p.getEHR_by_import());
        this.fragment_manager = fragment_manager;
        this.p = p;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.patient_ehr_list_item, null);
            v.setClickable(false);
        }
        final EHR e = getItem(position);

        if (e != null) {
            final TextView ehr_title = (TextView) v.findViewById(R.id.ehr_title);
            final ImageView ehr_status_image = (ImageView) v.findViewById(R.id.ehr_status_image);
            final TextView ehr_status = (TextView) v.findViewById(R.id.ehr_status);
            ehr_title.setText("EHR" + position);
            ehr_title.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    EHRDetailFragment ehr = new EHRDetailFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelable("EHR", e);
//                    bundle.putInt("EHR_NUM", position);
//                    ehr.setArguments(bundle);
                    FragmentTransaction ft = fragment_manager.beginTransaction();
                    ft.replace(R.id.patient_container, ehr);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });


            ehr_status.setText(e.isPending() ? "Pending" : "Closed");
            ehr_status.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (e.isPending() == true) {
                        ehr_status.setText("Closed");
                        ehr_status_image.setBackgroundResource(R.drawable.closed);
                        e.setClosed();
                    } else {
                        ehr_status.setText("Pending");
                        ehr_status_image.setBackgroundResource(R.drawable.pending);
                        e.setPending();
                    }
                }
            });
        }
        return v;
    }

}