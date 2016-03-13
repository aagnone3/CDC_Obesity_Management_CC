package edu.gatech.johndoe.carecoordinator;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.location.LocationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Referral fragment.
 */
public class ReferralFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // Update
    private static final String EXAMPLE_ARG_PARAM = "param";
    private ListView listviewReferral;
    private ArrayAdapter<String> adapter;
    private ArrayList<Referral> referralPatientsName = new ArrayList<Referral>();
    private ArrayList<String> dummyList = new ArrayList<String>();
    private TextView listName, listDetails, name, date, details;
    private Button reviewedButton;
    private int lastClicked = -1;


    // TODO: Rename and change types of parameters
    private String mExampleParam;

    private OnFragmentInteractionListener mListener;

    public ReferralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param exampleParam Example parameter.
     * @return A new instance of fragment ReferralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReferralFragment newInstance(String exampleParam) {
        ReferralFragment fragment = new ReferralFragment();
        Bundle args = new Bundle();
        args.putString(EXAMPLE_ARG_PARAM, exampleParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mExampleParam = getArguments().getString(EXAMPLE_ARG_PARAM);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        referralPatientsName.add(new Referral("John", "He is sick"));
        referralPatientsName.add(new Referral("David", "No idea"));
        referralPatientsName.add(new Referral("Kesha"));
        referralPatientsName.add(new Referral("Tom", "I'm not creative enough."));
        dummyList.add("John");
        dummyList.add("David");
        dummyList.add("Kesha");
        dummyList.add("Tom");
        adapter = new ReferralListAdapter(getActivity(), R.layout.listview_referral, dummyList);
        listviewReferral = (ListView) getView().findViewById(R.id.listviewreferral);
        listviewReferral.setAdapter(adapter);
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.d("ID", Integer.toString(position));
                lastClicked = position;
                Referral ref = referralPatientsName.get(position);
                name.setText("Name: " +ref.getName());
                date.setText("Date: " + "2015/1/2");
                details.setText("Details: " + ref.getDetail());

            }
        };
        listviewReferral.setOnItemClickListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_referral, container, false);
        name = (TextView) view.findViewById(R.id.textviewname);
        date = (TextView) view.findViewById(R.id.textviewdate);
        details = (TextView) view.findViewById(R.id.textviewdetails);
        reviewedButton = (Button) view.findViewById(R.id.buttonreviewed);
        reviewedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClicked >= 0) {
                    listviewReferral.setSelection(lastClicked);
                }


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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onReferralFragmentInteraction(uri);
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

    private class ReferralListAdapter extends ArrayAdapter<String> {

        public ReferralListAdapter(Context c, int r, List<String> o) {

            super(c, r, o);

        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            View vi = view;
            if (vi == null) {

                vi = getActivity().getLayoutInflater().inflate(R.layout.listview_referral, parent, false);
            }
            final Referral patient = referralPatientsName.get(position);
            listName = (TextView) vi.findViewById(R.id.patientname);
            listName.setText(patient.getName());
            listDetails = (TextView) vi.findViewById(R.id.referralsummary);
            listDetails.setText(patient.getDetail());
            final ImageView iv = (ImageView) vi.findViewById(R.id.clickfavourite);
            iv.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    if (patient.getClicked()) {
                        patient.setClicked();
                        iv.setImageResource(R.drawable.unfavourite);
                    } else {
                        patient.setClicked();
                        iv.setImageResource(R.drawable.favourite_selected);
                    }
                }
            });
            return vi;
        }

        class ViewHolder{
            TextView textOne;
            TextView textTwo;
            ImageView imageView;
        }
    }

}
