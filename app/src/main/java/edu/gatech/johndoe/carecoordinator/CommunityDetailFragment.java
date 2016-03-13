package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;


public class CommunityDetailFragment extends Fragment {

    private static final String ARG_COMMUNITY = "community";

    private Community community;

    private OnFragmentInteractionListener mListener;

    /**
     *
     * @param community Community.
     * @return A new instance of fragment CommunityDetailFragment.
     */
    public static CommunityDetailFragment newInstance(Community community) {
        CommunityDetailFragment fragment = new CommunityDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMMUNITY, new Gson().toJson(community));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (community == null) {
                community = new Gson().fromJson(savedInstanceState.getString("community"), Community.class);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            community = new Gson().fromJson(getArguments().getString(ARG_COMMUNITY), Community.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_detail, container, false);

        ImageView communityImage = (ImageView) view.findViewById(R.id.communityImage);
        communityImage.setImageResource(R.mipmap.ic_launcher);  // FIXME: replace with real data

        TextView communityName = (TextView) view.findViewById(R.id.communityName);
        communityName.setText(community.name);

        TextView patientCount = (TextView) view.findViewById(R.id.patientCount);
        patientCount.setText(getString(R.string.patient_count, community.patientCount));

        TextView address = (TextView) view.findViewById(R.id.address);
        address.setText("450 Madison Court, Decatur, GA 30030");    // FIXME: replace with real data

        TextView phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
        phoneNumber.setText("(678) 148 - 4606");    // FIXME: replace with real data

        TextView email = (TextView) view.findViewById(R.id.email);
        email.setText("johndoe@gmail.com"); // FIXME: replace with real data

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText("This is a random description of a random community resource. " +
                "This is a random description of a random community resource. " +
                "This is a random description of a random community resource.");    // FIXME: replace with real data

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("community", new Gson().toJson(community));
    }
}