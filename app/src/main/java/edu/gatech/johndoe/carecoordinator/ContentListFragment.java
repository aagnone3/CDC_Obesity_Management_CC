package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanListAdapter;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.community.UI.CommunityAdapter;
import edu.gatech.johndoe.carecoordinator.patient.*;
import edu.gatech.johndoe.carecoordinator.patient.UI.PatientAdapter;


public class ContentListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView contentList;
    private RecyclerView.Adapter adapter;
    private ContentType contentType;

    private boolean[] communityFilters;
    private String query;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            contentType = (ContentType) savedInstanceState.getSerializable("contentType");
            int selectedPosition = savedInstanceState.getInt("selectedPosition");
            List data;
            Type listType;

            switch (contentType) {
                case Referral:
                    listType = new TypeToken<List<CarePlan>>(){}.getType();
                    data = new Gson().fromJson(savedInstanceState.getString("data"), listType);
                    adapter = new CarePlanListAdapter(data);
                    break;
                case Patient:
                    listType = new TypeToken<List<Patient>>(){}.getType();
                    data = new Gson().fromJson(savedInstanceState.getString("data"), listType);
                    adapter = new PatientAdapter(data);
                    break;
                case Community:
                    listType = new TypeToken<List<Community>>(){}.getType();
                    data = new Gson().fromJson(savedInstanceState.getString("data"), listType);
                    adapter = new CommunityAdapter(data);
                    break;
                default:
                    return;
            }

            communityFilters = savedInstanceState.getBooleanArray("communityFilters");
            query = savedInstanceState.getString("query");

            setAdapter(adapter, contentType);

            if (MainActivity.isInExpandedMode) {
                if (selectedPosition != -1) {
                    mListener.onShouldUpdateDetail(data.get(selectedPosition));
                    contentList.scrollToPosition(selectedPosition);
                }
            } else {
                closeDetailView();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_list, container, false);
        contentList = (RecyclerView) view.findViewById(R.id.list);
        contentList.addItemDecoration(new ListDividerItemDecoration(getContext()));
        contentList.setLayoutManager(new LinearLayoutManager(getContext()));
        contentList.setItemAnimator(null);

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

        if (adapter instanceof Restorable) {
            outState.putString("data", new Gson().toJson(((Restorable) adapter).getDataSet()));
            outState.putInt("selectedPosition", ((Restorable) adapter).getSelectedPosition());
            outState.putBooleanArray("communityFilters", communityFilters);
            outState.putString("query", query);
        }

        outState.putSerializable("contentType", contentType);
    }

    public void updatePatientStatus(boolean isClosed){
        PatientAdapter pa = (PatientAdapter) adapter;
        System.out.println("get selected2 " + pa.getSelectedPosition());
        View v = contentList.getChildAt(pa.getSelectedPosition());
        if (v != null) {
            System.out.println("here2");
            ImageView patientStatusImage = (ImageView) v.findViewById(R.id.patientSmallImage);
            TextView patientStatusTextView = (TextView) v.findViewById(R.id.patient_num_care_plans);
            if (isClosed) {
                patientStatusImage.setImageResource(R.drawable.closed);
                patientStatusTextView.setText(R.string.closed);
            } else {
                patientStatusImage.setImageResource(R.drawable.pending);
                patientStatusTextView.setText(R.string.pending);
            }
        }
    }

    ////////////// update this
//    public void updateCarePlanStatus(){
//        CarePlanListAdapter ca = (CarePlanListAdapter) adapter;
//        System.out.println("get selected " + ca.getSelectedPosition() + " " + ca.getItemCount());
//        View v = contentList.getChildAt(ca.getSelectedPosition());
//        System.out.println("even?");
//        if (v != null) {
//            System.out.println("here");
//            TextView listTitle = (TextView) v.findViewById(R.id.referraltitle);
//            TextView listPatientID = (TextView) v.findViewById(R.id.referralpatientid);
//            TextView listPending = (TextView) v.findViewById(R.id.referralpending);
//            TextView listDate = (TextView) v.findViewById(R.id.referraldate);
//            ImageView carePlanStatusImage = (ImageView) v.findViewById(R.id.carePlanStatusImage);
//            carePlanStatusImage.setImageResource(R.drawable.closed);
//            listTitle.setTypeface(null, Typeface.NORMAL);
//            listPatientID.setTypeface(null, Typeface.NORMAL);
//            listPending.setTypeface(null, Typeface.NORMAL);
//            listDate.setTypeface(null, Typeface.NORMAL);
//        }
//    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter, ContentType contentType) {
        if (getView() != null) {
            if (adapter instanceof CommunityAdapter && query != null) {
                ((CommunityAdapter) adapter).getFilter().filter(query);
            }

            contentList.setAdapter(adapter);
            this.adapter = adapter;
            this.contentType = contentType;
        }
    }

    public void closeDetailView() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    public void sortList(CommunityAdapter.SortType type) {
        if (adapter instanceof CommunityAdapter) {
            ((CommunityAdapter) adapter).sort(type);
        }
    }

    public void searchList(CharSequence query) {
        if (adapter instanceof CommunityAdapter) {
            ((CommunityAdapter) adapter).getFilter().filter(query);
        } else if (adapter instanceof PatientAdapter) {
            ((PatientAdapter) adapter).getFilter().filter(query);
        }
    }

    public void filterList(SparseBooleanArray filters) {
        if (adapter instanceof CommunityAdapter) {
            query = "=";
            communityFilters = new boolean[3];
            for (int i = 0; i < filters.size(); i++) {
                if (filters.valueAt(i)) {
                    if (query.length() > 1) {
                        query += ",";
                    }
                    query += Community.CommunityType.values[filters.keyAt(i)].toString();
                    communityFilters[filters.keyAt(i)] = true;
                }
            }
            ((CommunityAdapter) adapter).getFilter().filter(query);
        }
    }

    public boolean[] getCommunityFilters() {
        if (communityFilters == null) {
            communityFilters = new boolean[3];
            Arrays.fill(communityFilters, true);
        }
        return communityFilters;
    }

    public enum ContentType {
        Referral, Patient, Community
    }
}
