package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.patient_fragments.PatientAdapter;


public class ContentListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView contentList;
    private RecyclerView.Adapter adapter;
    private ContentType contentType;

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
                    // TODO: change to Referral type
                    listType = new TypeToken<List<Community>>(){}.getType();
                    data = new Gson().fromJson(savedInstanceState.getString("data"), listType);
                    adapter = new ReferralListAdapter(data, selectedPosition);
                    break;
                case Patient:
                    listType = new TypeToken<List<Patient>>(){}.getType();
                    data = new Gson().fromJson(savedInstanceState.getString("data"), listType);
                    adapter = new PatientAdapter(data, selectedPosition);
                    break;
                case Community:
                    listType = new TypeToken<List<Community>>(){}.getType();
                    data = new Gson().fromJson(savedInstanceState.getString("data"), listType);
                    adapter = new CommunityAdapter(data, selectedPosition);
                    break;
                default:
                    return;
            }

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
        }

        outState.putSerializable("contentType", contentType);
    }

    public void setAdapter(RecyclerView.Adapter adapter, ContentType contentType) {
        if (getView() != null) {
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

    public enum ContentType {
        Referral, Patient, Community
    }
}
