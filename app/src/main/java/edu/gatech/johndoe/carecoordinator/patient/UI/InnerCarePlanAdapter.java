package edu.gatech.johndoe.carecoordinator.patient.UI;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.gatech.johndoe.carecoordinator.ContentListFragment;
import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanDetailFragment;
import edu.gatech.johndoe.carecoordinator.util.Utility;

public class InnerCarePlanAdapter extends RecyclerView.Adapter<InnerCarePlanAdapter.InnerCarePlanHolder> {

    private android.support.v4.app.FragmentManager fragment_manager;
    private List<CarePlan> carePlanList;

    public InnerCarePlanAdapter(List<CarePlan> carePlanList, android.support.v4.app.FragmentManager fragment_manager) {
        this.fragment_manager = fragment_manager;
        this.carePlanList = carePlanList;
    }

    private boolean isClosed() {
        for (CarePlan e : carePlanList) {
            if (e.isPending()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public InnerCarePlanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_care_plan_list_item, parent, false);
        return new InnerCarePlanHolder(view);
    }

    @Override
    public void onBindViewHolder(InnerCarePlanHolder holder, int position) {
        CarePlan carePlan = carePlanList.get(position);
        holder.bindInnerCarePlan(carePlan);
    }

    @Override
    public int getItemCount() {
        return carePlanList.size();
    }

    public class InnerCarePlanHolder extends RecyclerView.ViewHolder {
        private final TextView care_plan_title;
        private final ImageView care_plan_status_image;
        private final TextView care_plan_status;
        private CarePlan carePlan;

        public InnerCarePlanHolder(View itemView) {
            super(itemView);

            this.care_plan_title = (TextView) itemView.findViewById(R.id.inner_care_plan_title);
            this.care_plan_status_image = (ImageView) itemView.findViewById(R.id.care_plan_status_image);
            this.care_plan_status = (TextView) itemView.findViewById(R.id.care_plan_status);
        }

        public void bindInnerCarePlan(CarePlan cp) {
            this.carePlan = cp;

            care_plan_title.setText(carePlan.getType());
            care_plan_title.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Fragment detailFragment = CarePlanDetailFragment.newInstance(carePlan);
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

            care_plan_status.setText(carePlan.getStatus());
            care_plan_status_image.setBackgroundResource(carePlan.isPending() ? android.R.drawable.ic_menu_info_details : android.R.drawable.presence_online);
            care_plan_status.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (carePlan.isPending()) {
                        care_plan_status.setText("Closed");
                        care_plan_status_image.setBackgroundResource(android.R.drawable.presence_online);
                        carePlan.setPending(false);
                    } else {
                        care_plan_status.setText("Pending");
                        care_plan_status_image.setBackgroundResource(android.R.drawable.ic_menu_info_details);
                        carePlan.setPending(true);
                    }
                    Utility.updateReferralStatus(carePlan.getId(), carePlan.isPending());
                    ContentListFragment contentListFragment =
                            (ContentListFragment) fragment_manager.findFragmentById(R.id.contentListFragment);
                    contentListFragment.updatePatientStatus(isClosed());
                    contentListFragment.getAdapter().notifyDataSetChanged();
                }
            });
        }
    }
}