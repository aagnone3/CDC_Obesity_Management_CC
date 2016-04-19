package edu.gatech.johndoe.carecoordinator.patient.UI;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.gatech.johndoe.carecoordinator.ContentListFragment;
import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanDetailFragment;
import edu.gatech.johndoe.carecoordinator.util.Utility;

public class InnerCarePlanAdapter extends ArrayAdapter<CarePlan> {

    private android.support.v4.app.FragmentManager fragment_manager;
    private List<CarePlan> carePlanList;

    public InnerCarePlanAdapter(Context context, int resource, List<CarePlan> carePlanList, android.support.v4.app.FragmentManager fragment_manager) {
        super(context, resource, carePlanList);
        this.fragment_manager = fragment_manager;
        this.carePlanList = carePlanList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.patient_care_plan_list_item, parent, false);
        }
        final CarePlan e = getItem(position);

        if (e != null) {
            final TextView ehr_title = (TextView) v.findViewById(R.id.ehr_title);
            final ImageView ehr_status_image = (ImageView) v.findViewById(R.id.ehr_status_image);
            final TextView ehr_status = (TextView) v.findViewById(R.id.ehr_status);
            ehr_title.setText(e.getTitle());
            ehr_title.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Fragment detailFragment = CarePlanDetailFragment.newInstance(e);
                    FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    if (MainActivity.isInExpandedMode) {
                        //noinspection ResourceType
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.replace(R.id.detailFragmentContainer, detailFragment, "detail").addToBackStack(null);
                    } else {
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.add(R.id.contentContainer, detailFragment, "detail").addToBackStack(null);
                    }

                    transaction.commit();
                }
            });

            ehr_status.setText(e.isPending() ? "Pending" : "Closed");
            ehr_status_image.setBackgroundResource(e.isPending() ? android.R.drawable.ic_menu_info_details : android.R.drawable.presence_online);
            ehr_status.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (e.isPending()) {
                        ehr_status.setText("Closed");
                        ehr_status_image.setBackgroundResource(android.R.drawable.presence_online);
                        e.setPending(false);
                    } else {
                        ehr_status.setText("Pending");
                        ehr_status_image.setBackgroundResource(android.R.drawable.ic_menu_info_details);
                        e.setPending(true);
                    }
                    Utility.updateReferralStatus(e.getId(), e.isPending());
                    ContentListFragment contentListFragment =
                            (ContentListFragment) fragment_manager.findFragmentById(R.id.contentListFragment);
                    contentListFragment.updatePatientStatus(isClosed());
                    contentListFragment.getAdapter().notifyDataSetChanged();
                }
            });
        }
        return v;
    }

    private boolean isClosed() {
        for (CarePlan e : carePlanList) {
            if (e.isPending()) {
                return false;
            }
        }
        return true;
    }

}