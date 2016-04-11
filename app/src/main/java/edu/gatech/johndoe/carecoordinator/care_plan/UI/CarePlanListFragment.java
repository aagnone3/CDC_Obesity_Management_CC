package edu.gatech.johndoe.carecoordinator.care_plan.UI;

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
import java.util.List;
import java.util.Date;

import edu.gatech.johndoe.carecoordinator.OnFragmentInteractionListener;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanListAdapter;
import edu.gatech.johndoe.carecoordinator.util.Utility;

/**
 * Created by rakyu012 on 3/18/2016.
 */
public class CarePlanListFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private CarePlanListAdapter referralAdapter;
    private List<CarePlan> carePlan_lists = new ArrayList<>();
    private List<CarePlan> carePlan_pending = new ArrayList<>();
    private List<CarePlan> carePlan_Npending = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_care_plan_list, container, false);
        //String id, String patientID, String title, String detail, boolean pending, Date issueDate
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d = parseDate("2014-02-14");
//        ArrayList<CarePlan> ehr = new ArrayList<>(Arrays.asList(
//                new CarePlan("1", "1878496", "CarePlan 1", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878497", "CarePlan 2", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878498", "CarePlan 3", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878499", "CarePlan 4", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878500", "CarePlan 5", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878501", "CarePlan 6", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878502", "CarePlan 7", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878503", "CarePlan 8", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878504", "CarePlan 9", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878505", "CarePlan 10", "None", false, parseDate("2014-02-14")),
//                new CarePlan("1", "1878506", "CarePlan 11", "None", false, parseDate("2014-02-14"))
//        ));  // FIXME: replace with real data

        final List<CarePlan> carePlen = new ArrayList<>();
        Firebase ref = new Firebase("https://cdccoordinator2.firebaseio.com/carePlen");
        // Attach an listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " Referrances posts");
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    CarePlan post = postSnapshot.getValue(CarePlan.class);
                    carePlen.add(post);
                    System.out.println(post.getPatientID() + " - " + post.getId());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

//        List<CarePlan> carePlen = Utility.carePlan_list;
//        System.out.println("size of CarePlan is " + carePlen.size());
//        for (CarePlan eh: ehr) {
//            System.out.println(eh.getPatientID());
//        }
//        carePlan_lists = Utility.carePlan_list;
//        carePlan_pending.clear();
//        carePlan_Npending.clear();
//        for (CarePlan ehr: carePlan_lists) {
//            if (ehr.isPending()) {
//                carePlan_pending.add(ehr);
//            } else {
//                carePlan_Npending.add(ehr);
//            }
//        }
//
//        carePlan_lists.clear();
//        carePlan_lists.addAll(carePlan_pending);
//        carePlan_lists.addAll(carePlan_Npending);
//
//
//        Collections.sort(carePlan_lists, new Comparator<CarePlan>() {
//            @Override
//            public int compare(CarePlan lhs, CarePlan rhs) {
//
//                return rhs.getDateOfimport().compareTo(lhs.getDateOfimport());
//            }
//        });
        Utility.getAllCarePlans();
        referralAdapter = new CarePlanListAdapter(Utility.carePlan_list);
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
