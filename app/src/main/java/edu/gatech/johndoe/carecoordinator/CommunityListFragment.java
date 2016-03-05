package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CommunityListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private CommunityAdapter communityAdapter;
    private static final String ARG_COMMUNITY = "community";
    private Community community;
    private ImageView communityImage;
    private TextView communityName,patientCount, address, phoneNumber, email, description;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_list, container, false);

        ArrayList<Community> communities = new ArrayList<>(Arrays.asList(
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54)
        ));  // FIXME: replace with real data

        communityAdapter = new CommunityAdapter(communities);
        RecyclerView communityList = (RecyclerView) view.findViewById(R.id.communityList);
        communityList.setLayoutManager(new LinearLayoutManager(getContext()));
        communityList.setAdapter(communityAdapter);

        communityImage = (ImageView) view.findViewById(R.id.communityImage);
        communityName = (TextView) view.findViewById(R.id.communityName);
        patientCount = (TextView) view.findViewById(R.id.patientCount);
        address = (TextView) view.findViewById(R.id.address);
        phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
        email = (TextView) view.findViewById(R.id.email);
        description = (TextView) view.findViewById(R.id.description);

        return view;
    }

    public void filterList(CharSequence query) {
        communityAdapter.getFilter().filter(query);
    }

    public void sortList(SortType type) {
        communityAdapter.sort(type);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onCommunityFragmentInteraction(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityHolder> implements Filterable {

        private final Comparator<Community> NAME_COMPARATOR = new Comparator<Community>() {
            @Override
            public int compare(Community lhs, Community rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        };

        private final Comparator<Community> POPULARITY_COMPARATOR = new Comparator<Community>() {
            @Override
            public int compare(Community lhs, Community rhs) {
                return rhs.patientCount - lhs.patientCount;
            }
        };

        private final Comparator<Community> DISTANCE_COMPARATOR = new Comparator<Community>() {
            @Override
            public int compare(Community lhs, Community rhs) {
                // TODO: compare by distance?
                return 0;
            }
        };

        private Context context;
        private List<Community> communities;
        private List<Community> filteredCommunities;

        public CommunityAdapter(List<Community> communities) {
            this.communities = communities;
            this.filteredCommunities = new ArrayList<>(communities);
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
                        if (community.name.toLowerCase().startsWith(constraint.toString())) {
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
                communityNameTextView.setText(community.name);
                patientCountTextView.setText(context.getString(R.string.patient_count, community.patientCount));

            }

            @Override
            public void onClick(View v) {
                if (community != null) {
//                    Fragment detailFragment = CommunityDetailFragment.newInstance(community);
//                    FragmentTransaction transaction = ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.community_content, detailFragment, "detail");
//                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    transaction.addToBackStack(null);
//                    transaction.commit();
                    communityImage.setImageResource(R.mipmap.ic_launcher);  // FIXME: replace with real data
                    communityName.setText(community.name);
                    patientCount.setText(getString(R.string.patient_count, community.patientCount));
                    address.setText("450 Madison Court, Decatur, GA 30030");    // FIXME: replace with real data
                    phoneNumber.setText("(678) 148 - 4606");    // FIXME: replace with real data
                    email.setText("johndoe@gmail.com"); // FIXME: replace with real data
                    description.setText("This is a random description of a random community resource. " +
                            "This is a random description of a random community resource. " +
                            "This is a random description of a random community resource.");
                }
            }
        }


    }
    public enum SortType {
        NAME, POPULARITY, DISTANCE
    }

}
