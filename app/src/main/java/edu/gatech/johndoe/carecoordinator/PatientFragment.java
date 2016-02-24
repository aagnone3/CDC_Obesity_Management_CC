package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Patient fragment.
 */
public class PatientFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXAMPLE_ARG_PARAM = "param";

    // TODO: Rename and change types of parameters
    private String mExampleParam;

    private OnFragmentInteractionListener mListener;

    public PatientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param exampleParam Example parameter.
     * @return A new instance of fragment PatientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientFragment newInstance(String exampleParam) {
        PatientFragment fragment = new PatientFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_referral, container, false);
        TextView exampleLabel = (TextView) view.findViewById(R.id.exampleLabel);
        exampleLabel.setText(mExampleParam);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onPatientFragmentInteraction(uri);
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
