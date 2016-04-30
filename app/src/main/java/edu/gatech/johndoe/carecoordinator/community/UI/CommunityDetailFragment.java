package edu.gatech.johndoe.carecoordinator.community.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.util.Utility;


public class CommunityDetailFragment extends Fragment {

    private static final String ARG_COMMUNITY = "community";

    private Community community;

    /**
     *
     * @param community Community.
     * @return A new instance of fragment CommunityDetailFragment.
     */
    public static CommunityDetailFragment newInstance(Community community) {
        CommunityDetailFragment fragment = new CommunityDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMMUNITY, new Gson().toJson(community));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (community == null) {
                community = new Gson().fromJson(savedInstanceState.getString("community"), Community.class);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            community = new Gson().fromJson(getArguments().getString(ARG_COMMUNITY), Community.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_detail, container, false);

        Glide
            .with(this)
            .load(CommunityAdapter.IMAGE_URL + community.getId())
            .placeholder(R.drawable.community_icon)
            .fitCenter()
            .dontAnimate()
            .into((ImageView) view.findViewById(R.id.communityImage));

        TextView communityName = (TextView) view.findViewById(R.id.communityName);
        communityName.setText(community.getName());

        TextView patientCount = (TextView) view.findViewById(R.id.patientCount);
        int numPatients = community.getPatientCount();
        String plurality = (numPatients > 1 ? "s" : "");
        patientCount.setText(numPatients == 0 ? getString(R.string.no_patient_description) : getString(R.string.patient_count, numPatients, plurality));

        TextView address = (TextView) view.findViewById(R.id.address);
        address.setText(community.fullAddress());

        TextView phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
        phoneNumber.setText(community.getPhoneNumber());
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_menu_call)
                        .setTitle(community.getPhoneNumber())
                        .setMessage("Do you want to call this number?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + community.getPhoneNumber()));
                                startActivity(in);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        TextView email = (TextView) view.findViewById(R.id.email);
        email.setText(community.getEmailAddress());

        TextView hours = (TextView) view.findViewById(R.id.hours);
        hours.setText(community.hoursAsString());

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(community.getDescription());

        if (community.getPatientCount() > 0) {
            ArrayList<Patient> patients = new ArrayList<>(community.getPatientCount());

            for (String patientId : community.getPatientList()) {
                for (Patient patient : Utility.patient_list) {
                    if (patient.getId().equals(patientId)) {
                        patients.add(patient);
                    }
                }
            }

            RecyclerView patientList = (RecyclerView) view.findViewById(R.id.connectedPatientsList);
            patientList.setLayoutManager(new LinearLayoutManager(getContext()));
            patientList.setAdapter(new CommunityPatientAdapter(community, patients));
            patientList.setHasFixedSize(true);
            patientList.setVisibility(View.VISIBLE);
        } else {
            TextView noPatientTextView = (TextView) view.findViewById(R.id.noPatientTextView);
            noPatientTextView.setVisibility(View.VISIBLE);
        }

        ImageView mapImageView = (ImageView) view.findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_map_black)
                        .setTitle(community.getName())
                        .setMessage("Do you want locate this place in Google Maps?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + community.fullAddress());
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        try {
            String fullAddress = URLEncoder.encode(community.fullAddress(), "UTF-8");
            new Utility.ImageDownloadTask(mapImageView).execute(
                    "https://maps.googleapis.com/maps/api/staticmap?center="
                    + fullAddress + "&zoom=16&size=500x500&markers=color:blue|"
                    + fullAddress + "&maptype=roadmap");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("community", new Gson().toJson(community));
    }
}
