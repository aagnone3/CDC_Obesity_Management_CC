package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.patient.EHR;
import edu.gatech.johndoe.carecoordinator.util.Utility;

/**
 * Created by rakyu012 on 3/18/2016.
 */
public class ReferralListFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private ReferralListAdapter referralAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_referral_list, container, false);
//        ArrayList<EHR> communities = new ArrayList<>(Arrays.asList(
//                new Community("YMCA", 120), new Community("Farmer's Market", 54),
//                new Community("YMCA", 120), new Community("Farmer's Market", 54),
//                new Community("YMCA", 120), new Community("Farmer's Market", 54),
//                new Community("YMCA", 120), new Community("Farmer's Market", 54),
//                new Community("YMCA", 120), new Community("Farmer's Market", 54)
//        ));  // FIXME: replace with real data


        List<EHR> referrals = Utility.referral_list;
        for (EHR ehr: referrals) {
            System.out.println(ehr.getPatientID());
        }

        referralAdapter = new ReferralListAdapter(Utility.referral_list);
        RecyclerView referralList = (RecyclerView) view.findViewById(R.id.listviewreferral);
        referralList.setLayoutManager(new LinearLayoutManager(getContext()));
        referralList.setAdapter(referralAdapter);

        return view;
    }

//    public void filterList(CharSequence query) {
//        referralAdapter.getFilter().filter(query);
//    }
//
//    public void sortList(CommunityAdapter.SortType type) {
//        referralAdapter.sort(type);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//            mListener.onReferralFragmentInteraction(this);
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
}
