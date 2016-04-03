package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CommunityListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private CommunityAdapter communityAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_list, container, false);

        RecyclerView communityList = (RecyclerView) view.findViewById(R.id.communityList);
        communityList.setLayoutManager(new LinearLayoutManager(getContext()));
        communityList.setAdapter(communityAdapter);

        return view;
    }

    public void filterList(CharSequence query) {
        communityAdapter.getFilter().filter(query);
    }

    public void sortList(CommunityAdapter.SortType type) {
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
}
