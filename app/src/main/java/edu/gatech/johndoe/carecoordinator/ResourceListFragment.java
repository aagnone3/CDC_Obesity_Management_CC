package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;


public class ResourceListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    CommunityAdapter communityAdapter;

    public ResourceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        ArrayList<Community> communities = new ArrayList<>(Arrays.asList(
//                new Community("YMCA", 120), new Community("Farmer's Market", 54),
//                new Community("YMCA", 120), new Community("Farmer's Market", 54),
//                new Community("YMCA", 120), new Community("Farmer's Market", 54),
//                new Community("YMCA", 120), new Community("Farmer's Market", 54),
//                new Community("YMCA", 120), new Community("Farmer's Market", 54)
//        ));  // FIXME: replace with real data
//
//        communityAdapter = new CommunityAdapter(communities);
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null) {

        }

        ArrayList<Community> communities = new ArrayList<>(Arrays.asList(
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54)
        ));  // FIXME: replace with real data

        communityAdapter = new CommunityAdapter(communities);

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView communityList = (RecyclerView) view.findViewById(R.id.list);
        communityList.setLayoutManager(new LinearLayoutManager(getContext()));
        communityList.setAdapter(communityAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setAdapter(CommunityAdapter adapter) {
        if (getView() != null) {
            ((RecyclerView) getView().findViewById(R.id.list)).setAdapter(adapter);
        }
    }
}
