package edu.gatech.johndoe.carecoordinator.community.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.patient.Patient;

public class CommunityPatientAdapter extends RecyclerView.Adapter<CommunityPatientAdapter.CommunityPatientHolder> {

    private Context context;
    private Community community;
    private List<Patient> patients;

    public CommunityPatientAdapter(Community community, List<Patient> patients) {
        this.community = community;
        this.patients = patients;
    }

    @Override
    public CommunityPatientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.community_patient_list_item, parent, false);
        return new CommunityPatientHolder(view);
    }

    @Override
    public void onBindViewHolder(CommunityPatientHolder holder, int position) {
        Patient patient = patients.get(position);
        holder.bindPatient(patient);
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public class CommunityPatientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView patientNameView;
        private Patient patient;

        public CommunityPatientHolder(View itemView) {
            super(itemView);

            patientNameView = (TextView) itemView.findViewById(R.id.patientName);

            itemView.setOnClickListener(this);
        }

        public void bindPatient(Patient patient) {
            this.patient = patient;
            patientNameView.setText(patient.getFull_name_last());
        }

        @Override
        public void onClick(View v) {
            // TODO
        }
    }
}
