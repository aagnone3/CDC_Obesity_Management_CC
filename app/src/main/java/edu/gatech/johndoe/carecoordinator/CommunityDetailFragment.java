package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        TextView placeholder = (TextView) view.findViewById(R.id.placeholder);
        placeholder.setText(community.name);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCommunityDetailFragmentInteraction(uri);
        }
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
}
