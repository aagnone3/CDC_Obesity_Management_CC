package edu.gatech.johndoe.carecoordinator.care_plan.UI;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.Restorable;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;

/**
 * Created by rakyu012 on 3/17/2016.
 */
//extends RecyclerView.Adapter<CommunityAdapter.CommunityHolder> implements Filterable, Restorable
public class CarePlanListAdapter extends RecyclerView.Adapter<CarePlanListAdapter.ReferralListAdpaterHolder> implements Filterable, Restorable {

    private Context context;
    private List<CarePlan> carePlen;
    private List<CarePlan> filteredCarePlan;
    private int selectedPosition;
    public static CarePlan currentCarePlan;
    public static int currentPosition;

    public CarePlanListAdapter(List<CarePlan> carePlan, int selectedPosition) {
        this.carePlen = carePlan;
        this.filteredCarePlan = new ArrayList<>(carePlan);
        this.selectedPosition = selectedPosition;
    }

    public CarePlanListAdapter(List<CarePlan> carePlan) {
        this(carePlan, -1);
    }

    @Override
    public ReferralListAdpaterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.listview_care_plan, parent, false);
        return new ReferralListAdpaterHolder(view);
    }

    @Override
    public void onBindViewHolder(ReferralListAdpaterHolder holder, int position) {
//        System.out.println("Pos" + position);
//        if (position != null) {
        CarePlan carePlan = filteredCarePlan.get(position);
//        System.out.println("CarePlan " + carePlan.getTitle());
        holder.bindReferral(context, carePlan);

        if (MainActivity.isInExpandedMode && currentCarePlan != null) {
            holder.itemView.setSelected(carePlan.equals(currentCarePlan));
        }
    }

    @Override
    public int getItemCount() {
        return filteredCarePlan.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<CarePlan> filtered = new ArrayList<>();

                constraint = constraint.toString().toLowerCase().trim();
                for (CarePlan carePlan : carePlen) {
//                    if (carePlan.) {
                    filtered.add(carePlan);
//                    }
                }

                results.count = filtered.size();
                results.values = filtered;

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredCarePlan.clear();
                filteredCarePlan.addAll((ArrayList<CarePlan>) results.values);
                currentPosition = filteredCarePlan.indexOf(currentCarePlan);
                notifyDataSetChanged();
            }
        };
    }

//    public void sort(SortType type) {
//        switch (type) {
//            case NAME:
//                Collections.sort(filteredCarePlan, NAME_COMPARATOR2);
//                break;
//            case POPULARITY:
//                Collections.sort(filteredCarePlan, DATE_COMPARATOR2);
//                break;
//        }
//        notifyDataSetChanged();
//    }

    @Override
    public List<CarePlan> getDataSet() {
        return carePlen;
    }

    @Override
    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class ReferralListAdpaterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView listTitle;
        private final TextView listPatientID;
        private final TextView listPending;
        private CarePlan carePlan;



        public ReferralListAdpaterHolder(View itemView) {
            super(itemView);
            listTitle = (TextView) itemView.findViewById(R.id.referraltitle);
            listPatientID = (TextView) itemView.findViewById(R.id.referralpatientid);
            listPending = (TextView) itemView.findViewById(R.id.referralpending);
            itemView.setOnClickListener(this);
        }

        public void bindReferral(Context context, CarePlan carePlan) {
            this.carePlan = carePlan;
            listTitle.setText(carePlan.getTitle());
            listPatientID.setText(carePlan.getPatientID());
            if (carePlan.isPending()) {
                listPending.setText("Pending");
            } else {
                listPending.setText("Not Pending");
            }
        }

        @Override
        public void onClick(View v) {
            if (carePlan != null) {
                Fragment detailFragment = CarePlanDetailFragment.newInstance(carePlan);
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                if (MainActivity.isInExpandedMode) {
                    //noinspection ResourceType
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.replace(R.id.detailFragmentContainer, detailFragment, "detail");
                } else {
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(R.id.contentContainer, detailFragment, "detail").addToBackStack(null);
                }

                transaction.commit();

                if (MainActivity.isInExpandedMode) {
                    notifyItemChanged(currentPosition);
                    currentPosition = getLayoutPosition();
                    notifyItemChanged(currentPosition);
                } else {
                    selectedPosition = getLayoutPosition();
                    currentPosition = getLayoutPosition();
                }
                currentCarePlan = carePlan;

            }
        }
    }

//    public enum SortType {
//        NAME, POPULARITY, DISTANCE
//    }
}
