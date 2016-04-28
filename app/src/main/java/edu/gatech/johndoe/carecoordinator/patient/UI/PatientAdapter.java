package edu.gatech.johndoe.carecoordinator.patient.UI;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.Restorable;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.util.OnLatLongUpdateListener;
import edu.gatech.johndoe.carecoordinator.util.Utility;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientHolder> implements Filterable, Restorable {

    private static final Comparator<Patient> FIRST_NAME_COMPARATOR = new Comparator<Patient>() {
        @Override
        public int compare(Patient lhs, Patient rhs) {
            return lhs.getFirst_name().compareTo(rhs.getFirst_name());
        }
    };

    private static final Comparator<Patient> LAST_NAME_COMPARATOR = new Comparator<Patient>() {
        @Override
        public int compare(Patient lhs, Patient rhs) {
            return lhs.getLast_name().compareTo(rhs.getLast_name());
        }
    };

    private static final Comparator<Patient> LATEST_IMPORT_DATE_COMPARATOR = new Comparator<Patient>() {
        @Override
        public int compare(Patient lhs, Patient rhs) {
            return lhs.getDateOfimport().compareTo(rhs.getDateOfimport());
        }
    };

    private Context context;
    private List<Patient> patients;
    private List<Patient> filteredPatients;
    private int selectedPosition;
    public static Patient currentPatient;
    public static int currentPosition;

    public PatientAdapter(List<Patient> patients, int selected) {
        this.patients = patients;
        this.filteredPatients = new ArrayList<>(patients);
        this.selectedPosition = selected;
    }

    public PatientAdapter(List<Patient> patients) {
        this(patients, -1);
    }

    @Override
    public PatientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.patient_list_item, parent, false);
        return new PatientHolder(view);
    }

    @Override
    public void onBindViewHolder(PatientHolder holder, int position) {
        Patient patient = filteredPatients.get(position);
        holder.bindPatient(context, patient);

        if (MainActivity.isInExpandedMode && currentPatient != null) {
            holder.itemView.setSelected(patient.equals(currentPatient));
        }
    }

    @Override
    public int getItemCount() {
        return filteredPatients.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Patient> filtered = new ArrayList<>();

                constraint = constraint.toString().toLowerCase().trim();
                for (Patient patient : patients) {
                    if (patient.getFull_name_first().toLowerCase().startsWith(constraint.toString())) {
                        filtered.add(patient);
                    }
                }

                results.count = filtered.size();
                results.values = filtered;

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPatients.clear();
                filteredPatients.addAll((ArrayList<Patient>) results.values);
                currentPosition = filteredPatients.indexOf(currentPatient);
                notifyDataSetChanged();
            }
        };
    }

    public void sort(SortType type) {
        switch(type) {
            case FIRST_NAME:
                Collections.sort(filteredPatients, FIRST_NAME_COMPARATOR);
                break;
            case LAST_NAME:
                Collections.sort(filteredPatients, LAST_NAME_COMPARATOR);
                break;
            case LATEST_IMPORT_DATE:
                Collections.sort(filteredPatients, LATEST_IMPORT_DATE_COMPARATOR);
                break;
        }
        notifyDataSetChanged();
    }

    @Override
    public List<Patient> getDataSet() {
        return patients;
    }

    @Override
    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class PatientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView patientImage;
        private final TextView patientNameTextView;
        private final TextView numCarePlansTextView;
        private Patient patient;
        private List<CarePlan> carePlanList;

        public PatientHolder(View itemView) {
            super(itemView);
            patientImage = (ImageView) itemView.findViewById(R.id.patientSmallImage);
            patientNameTextView = (TextView) itemView.findViewById(R.id.patient_name);
            numCarePlansTextView = (TextView) itemView.findViewById(R.id.patient_num_care_plans);
            itemView.setOnClickListener(this);
        }

        public void bindPatient(Context context, Patient patient) {
            this.patient = patient;
            this.carePlanList = Utility.getAllRelatedReferrals(patient.getReferralList());
            boolean isPending = false;
            for (CarePlan carePlan : carePlanList) {
                if (carePlan.isPending()) {
                    isPending = true;
                    break;
                }
            }
            patientNameTextView.setText(patient.getFull_name_last());
            if (patient.getImageName() != null) {
                int imageId = context.getResources().getIdentifier(patient.getImageName(), "drawable", context.getPackageName());
                patientImage.setImageResource(imageId);
            } else {
                patientImage.setImageResource(R.drawable.randomchild);
            }

            // Display the number of care plans the patient has
            Resources res = context.getResources();
            int numCarePlans = patient.getNumCarePlans();
            String plurality = (numCarePlans == 1 ? "" : "s");
            String patient_num_care_plan_text = String.format(
                    res.getString(R.string.patient_num_care_plans_text),
                    numCarePlans, plurality);
            numCarePlansTextView.setText(patient_num_care_plan_text);
        }

        @Override
        public void onClick(View v) {
            if (patient != null) {

                Fragment detailFragment = PatientDetailFragment.newInstance(patient, carePlanList);
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                if (MainActivity.isInExpandedMode) {
                    //noinspection ResourceType
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.replace(R.id.detailFragmentContainer, detailFragment, "detail");
                } else {
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(R.id.contentContainer, detailFragment, "detail").addToBackStack(null);
                }

                transaction.commit();

                if (MainActivity.isInExpandedMode) {
                    notifyItemChanged(currentPosition);
                    currentPosition = getLayoutPosition();
                    notifyItemChanged(currentPosition);
                } else {
                    selectedPosition = getLayoutPosition();
                    currentPosition = getLayoutPosition();
                }
                currentPatient = patient;

                if (currentPatient.getLatitude() != 0 && currentPatient.getLongitude() != 0) {
                    Utility.sortCommunitiesByDistance(currentPatient);
                }
                else {
                    Utility.updatePatientLatLong(currentPatient, new OnLatLongUpdateListener() {
                        @Override
                        public void onUpdate(double[] coordinates) {
                            Utility.sortCommunitiesByDistance(currentPatient);
                        }
                    });
                }

            }
        }
    }

    public enum SortType {
        FIRST_NAME, LAST_NAME, LATEST_IMPORT_DATE
    }

}