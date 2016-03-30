package edu.gatech.johndoe.carecoordinator;

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

import edu.gatech.johndoe.carecoordinator.patient.EHR;

/**
 * Created by rakyu012 on 3/17/2016.
 */
//extends RecyclerView.Adapter<CommunityAdapter.CommunityHolder> implements Filterable, Restorable
public class ReferralListAdapter extends RecyclerView.Adapter<ReferralListAdapter.ReferralListAdpaterHolder> implements Filterable, Restorable {
//    private static final Comparator<EHR> NAME_COMPARATOR2 = new Comparator<Referral>() {
//        @Override
//        public int compare(Referral lhs, Referral rhs) {
//            return lhs.getName().compareTo(rhs.getName());
//        }
//    };
//
//    private static final Comparator<Referral> POPULARITY_COMPARATOR2 = new Comparator<Referral>() {
//        @Override
//        public int compare(Referral lhs, Referral rhs) {
////            return rhs.getName() - lhs.getName();
//            return 0;
//        }
//    };
//
//    private static final Comparator<Community> DISTANCE_COMPARATOR = new Comparator<Community>() {
//        @Override
//        public int compare(Community lhs, Community rhs) {
//            // TODO: compare by distance?
//            return 0;
//        }
//    };

    private Context context;
    private List<EHR> referrals;
    private List<EHR> filteredReferral;
    private int selectedPosition;
    public static EHR currentReferral;
    public static int currentPosition;

    public ReferralListAdapter(List<EHR> referral, int selectedPosition) {
        this.referrals = referral;
        this.filteredReferral = new ArrayList<>(referral);
        this.selectedPosition = selectedPosition;
    }

    public ReferralListAdapter(List<EHR> referral) {
        this(referral, -1);
    }

    @Override
    public ReferralListAdpaterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.listview_referral, parent, false);
        return new ReferralListAdpaterHolder(view);
    }

    @Override
    public void onBindViewHolder(ReferralListAdpaterHolder holder, int position) {
        System.out.println("Pos" + position);
//        if (position != null) {
            EHR referral = filteredReferral.get(position);
            holder.bindCommunity(context, referral);
//        }

        if (MainActivity.isInExpandedMode) {
            holder.itemView.setSelected(position == selectedPosition);
        }
    }

    @Override
    public int getItemCount() {
        return filteredReferral.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<EHR> filtered = new ArrayList<>();

                constraint = constraint.toString().toLowerCase().trim();
                for (EHR referral : referrals) {
//                    if (referral.) {
                        filtered.add(referral);
//                    }
                }

                results.count = filtered.size();
                results.values = filtered;

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredReferral.clear();
                filteredReferral.addAll((ArrayList<EHR>) results.values);
                notifyDataSetChanged();
            }
        };
    }

//    public void sort(SortType type) {
//        switch (type) {
//            case NAME:
//                Collections.sort(filteredCommunities, NAME_COMPARATOR2);
//                break;
//            case POPULARITY:
//                Collections.sort(filteredCommunities, POPULARITY_COMPARATOR2);
//                break;
//            case DISTANCE:
//                Collections.sort(filteredCommunities, DISTANCE_COMPARATOR);
//                break;
//        }
//        notifyDataSetChanged();
//    }

    @Override
    public List<EHR> getDataSet() {
        return referrals;
    }

    @Override
    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class ReferralListAdpaterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView listName;
        private final TextView listDetails;
        private EHR referral;



        public ReferralListAdpaterHolder(View itemView) {
            super(itemView);
            listName = (TextView) itemView.findViewById(R.id.patientname);
            listDetails = (TextView) itemView.findViewById(R.id.referralsummary);
            itemView.setOnClickListener(this);
        }

        public void bindCommunity(Context context, EHR referral) {
            this.referral = referral;
            listName.setText(referral.getPatientID());
            listDetails.setText(referral.getDetail());
        }

        @Override
        public void onClick(View v) {
            if (referral != null) {
                Fragment detailFragment = ReferralDetailFragment.newInstance(referral);
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
                    notifyItemChanged(selectedPosition);
                    selectedPosition = getLayoutPosition();
                    notifyItemChanged(selectedPosition);
                } else {
                    selectedPosition = getLayoutPosition();
                }
                currentReferral = referral;
                currentPosition = selectedPosition;
            }
        }
    }

    public enum SortType {
        NAME, POPULARITY, DISTANCE
    }
}
