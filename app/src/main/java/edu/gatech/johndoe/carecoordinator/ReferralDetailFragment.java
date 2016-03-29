package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.firebase.client.Firebase;
import com.google.gson.Gson;

import edu.gatech.johndoe.carecoordinator.patient.EHR;

/**
 * Created by rakyu012 on 3/17/2016.
 */
public class ReferralDetailFragment extends Fragment {

    private static final String ARG_COMMUNITY = "referral";

    private EHR referral;

    private OnFragmentInteractionListener mListener;

    private TextView listName, listDetails, name, date, details;
    private Button reviewedButton;

    /**
     *
     * @param referral Community.
     * @return A new instance of fragment CommunityDetailFragment.
     */
    public static ReferralDetailFragment newInstance(EHR referral) {
        ReferralDetailFragment fragment = new ReferralDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMMUNITY, new Gson().toJson(referral));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (referral == null) {
                referral = new Gson().fromJson(savedInstanceState.getString("referral"), EHR.class);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            referral = new Gson().fromJson(getArguments().getString(ARG_COMMUNITY), EHR.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_referral_detail, container, false);

        name = (TextView) view.findViewById(R.id.textviewname);
        date = (TextView) view.findViewById(R.id.textviewdate);
        details = (TextView) view.findViewById(R.id.textviewdetails);
        reviewedButton = (Button) view.findViewById(R.id.buttonreviewed);
        reviewedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (lastClicked >= 0) {
//                    listviewReferral.setSelection(lastClicked);
//                }

//                Firebase myFirebaseRef = new Firebase("https://cdccoordinator2.firebaseio.com/referral");
////                myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");
//                System.out.println("Button clicked");
//
//                myFirebaseRef.child(ref.getName()).setValue(ref);
//                retrieveReferral();

//                if (!rec) {
//                    rec = true;
//                    recreation.setImageResource(R.drawable.recreation_selected);
//                    foo = false;
//                    food.setImageResource(R.drawable.food);
//                    stu = false;
//                    study.setImageResource(R.drawable.study);
//                    ViewRec();
//                }
//                displayListView();
            }
        });;

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

        outState.putString("referral", new Gson().toJson(referral));
    }
}
