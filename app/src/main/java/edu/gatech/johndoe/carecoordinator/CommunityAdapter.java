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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.community.Community;


public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityHolder> implements Filterable, Restorable {

    private static final Comparator<Community> NAME_COMPARATOR = new Comparator<Community>() {
        @Override
        public int compare(Community lhs, Community rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };

    private static final Comparator<Community> POPULARITY_COMPARATOR = new Comparator<Community>() {
        @Override
        public int compare(Community lhs, Community rhs) {
            return rhs.getPatientCount() - lhs.getPatientCount();
        }
    };

    private static final Comparator<Community> DISTANCE_COMPARATOR = new Comparator<Community>() {
        @Override
        public int compare(Community lhs, Community rhs) {
            // TODO: compare by distance?
            return 0;
        }
    };

    private Context context;
    private List<Community> communities;
    private List<Community> filteredCommunities;
    private int selectedPosition;
    public static Community currentCommunity;
    public static int currentPosition;

    public CommunityAdapter(List<Community> communities, int selectedPosition) {
        this.communities = communities;
        this.filteredCommunities = new ArrayList<>(communities);
        this.selectedPosition = selectedPosition;
    }

    public CommunityAdapter(List<Community> communities) {
        this(communities, -1);
    }

    @Override
    public CommunityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.community_list_item, parent, false);
        return new CommunityHolder(view);
    }

    @Override
    public void onBindViewHolder(CommunityHolder holder, int position) {
        Community community = filteredCommunities.get(position);
        holder.bindCommunity(context, community);

        if (MainActivity.isInExpandedMode) {
            holder.itemView.setSelected(position == selectedPosition);
        }
    }

    @Override
    public int getItemCount() {
        return filteredCommunities.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Community> filtered = new ArrayList<>();

                constraint = constraint.toString().toLowerCase().trim();
                for (Community community : communities) {
                    if (community.getName().toLowerCase().startsWith(constraint.toString())) {
                        filtered.add(community);
                    }
                }

                results.count = filtered.size();
                results.values = filtered;

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredCommunities.clear();
                filteredCommunities.addAll((ArrayList<Community>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void sort(SortType type) {
        switch (type) {
            case NAME:
                Collections.sort(filteredCommunities, NAME_COMPARATOR);
                break;
            case POPULARITY:
                Collections.sort(filteredCommunities, POPULARITY_COMPARATOR);
                break;
            case DISTANCE:
                Collections.sort(filteredCommunities, DISTANCE_COMPARATOR);
                break;
        }
        notifyDataSetChanged();
    }

    @Override
    public List<Community> getDataSet() {
        return communities;
    }

    @Override
    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class CommunityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView communityImageView;
        private final TextView communityNameTextView;
        private final TextView patientCountTextView;
        private Community community;

        public CommunityHolder(View itemView) {
            super(itemView);

            communityImageView = (ImageView) itemView.findViewById(R.id.communityImage);
            communityNameTextView = (TextView) itemView.findViewById(R.id.communityName);
            patientCountTextView = (TextView) itemView.findViewById(R.id.patientCount);
            itemView.setOnClickListener(this);
        }

        public void bindCommunity(Context context, Community community) {
            this.community = community;
            communityImageView.setImageResource(R.mipmap.ic_launcher);   // FIXME: set to an actual image
            communityNameTextView.setText(community.getName());
            patientCountTextView.setText(context.getString(R.string.patient_count, community.getPatientCount()));
        }

        @Override
        public void onClick(View v) {
            if (community != null) {
                Fragment detailFragment = CommunityDetailFragment.newInstance(community);
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
                currentCommunity = community;
                currentPosition = selectedPosition;
            }
        }
    }

    public enum SortType {
        NAME, POPULARITY, DISTANCE
    }
}
