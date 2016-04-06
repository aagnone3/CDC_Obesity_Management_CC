package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

import edu.gatech.johndoe.carecoordinator.patient.EHR;
import edu.gatech.johndoe.carecoordinator.util.Utility;

/**
 * Created by rakyu012 on 3/18/2016.
 */
public class ReferralListFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private ReferralListAdapter referralAdapter;
    private List<EHR> referral_lists = new ArrayList<>();
    private List<EHR> referral_pending = new ArrayList<>();
    private List<EHR> referral_Npending = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-mm-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_referral_list, container, false);
        //String id, String patientID, String title, String detail, boolean pending, Date issueDate
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d = parseDate("2014-02-14");
//        ArrayList<EHR> ehr = new ArrayList<>(Arrays.asList(
//                new EHR("1", "1878496", "Referral 1", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878497", "Referral 2", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878498", "Referral 3", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878499", "Referral 4", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878500", "Referral 5", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878501", "Referral 6", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878502", "Referral 7", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878503", "Referral 8", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878504", "Referral 9", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878505", "Referral 10", "None", false, parseDate("2014-02-14")),
//                new EHR("1", "1878506", "Referral 11", "None", false, parseDate("2014-02-14"))
//        ));  // FIXME: replace with real data

        final List<EHR> referrals = new ArrayList<EHR>();
        Firebase ref = new Firebase("https://cdccoordinator2.firebaseio.com/referrals");
        // Attach an listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " Referrances posts");
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    EHR post = postSnapshot.getValue(EHR.class);
                    referrals.add(post);
                    System.out.println(post.getPatientID() + " - " + post.getId());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

//        List<EHR> referrals = Utility.referral_list;
//        System.out.println("size of EHR is " + referrals.size());
//        for (EHR eh: ehr) {
//            System.out.println(eh.getPatientID());
//        }
//        referral_lists = Utility.referral_list;
//        referral_pending.clear();
//        referral_Npending.clear();
//        for (EHR ehr: referral_lists) {
//            if (ehr.isPending()) {
//                referral_pending.add(ehr);
//            } else {
//                referral_Npending.add(ehr);
//            }
//        }
//
//        referral_lists.clear();
//        referral_lists.addAll(referral_pending);
//        referral_lists.addAll(referral_Npending);
//
//
//        Collections.sort(referral_lists, new Comparator<EHR>() {
//            @Override
//            public int compare(EHR lhs, EHR rhs) {
//
//                return rhs.getDateOfimport().compareTo(lhs.getDateOfimport());
//            }
//        });
        Utility.getAllReferrals();
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
