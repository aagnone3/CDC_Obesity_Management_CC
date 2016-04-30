package edu.gatech.johndoe.carecoordinator.community.UI;

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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.Restorable;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.util.Utility;


public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityHolder> implements Filterable, Restorable {

    public static final String IMAGE_URL = "https://api.hyunseochung.com/cdcimage/";

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
        holder.communityImageView.setImageDrawable(null);
        holder.bindCommunity(context, community);

        if (MainActivity.isInExpandedMode && currentCommunity != null) {
            holder.itemView.setSelected(community.equals(currentCommunity));
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

                if (constraint.toString().startsWith("=")) {
                    List<String> filters = Arrays.asList(constraint.toString().substring(1).split(","));
                    for (Community community : communities) {
                        if (filters.contains(community.getCommunityType())) {
                            filtered.add(community);
                        }
                    }
                } else {
                    constraint = constraint.toString().toLowerCase().trim();

                    for (Community community : communities) {
                        if (community.getName().toLowerCase().contains(constraint.toString())) {
                            filtered.add(community);
                        }
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
                currentPosition = filteredCommunities.indexOf(currentCommunity);
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

            Glide
                .with(context)
                .load(IMAGE_URL + community.getId())
                .placeholder(R.drawable.community_icon)
                .fitCenter()
                .dontAnimate()
                .into(communityImageView);

            communityNameTextView.setText(community.getName());
            int numPatients = community.getPatientCount();
            String plurality = (numPatients > 1 ? "s" : "");
            patientCountTextView.setText(numPatients == 0 ? context.getString(R.string.no_patient_description) : context.getString(R.string.patient_count, numPatients, plurality));
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
                    notifyItemChanged(currentPosition);
                    currentPosition = getLayoutPosition();
                    notifyItemChanged(currentPosition);
                } else {
                    currentPosition = getLayoutPosition();
                }
                currentCommunity = community;

                Utility.updateCommunityLatLong(community, null);
            }
        }
    }

    public enum SortType {
        NAME, POPULARITY, DISTANCE
    }
}
