package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.patient.EHR;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.util.Utility;

public class PatientListFragment extends Fragment {

    private ListView list;
    private List<Patient> patients;
    private PatientAdapter adapter;
    private int threshold = 10;
    private int dummy_cnt;

    public PatientListFragment() {
        if (patients == null) {
            patients = new ArrayList<>();
            for (int i = 1; i <= 1; i++) {
                patients.add(new Patient(Utility.get_patient_info_by_id(i)));
                for (int j = 0; j < 1 + (int) (Math.random() * 10); j++) {
                    patients.get(i - 1).addEHR(new EHR());
                }
            }
            dummy_cnt = 2;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);
        list = (ListView)view.findViewById(R.id.listView_patient);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (list.getLastVisiblePosition() >= list.getCount() - threshold) {
                        new DummyGenerator().execute("");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) { }
        });
        adapter = new PatientAdapter(getActivity(), R.id.patient_list_view_row,
                patients, getActivity().getSupportFragmentManager());
        list.setAdapter(adapter);
        return view;
    }


    private class DummyGenerator extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 5; i++) {
                patients.add(new Patient(Utility.get_patient_info_by_id(dummy_cnt + i)));
                for (int j = 0; j < (int) (Math.random() * 10); j++)
                    patients.get(dummy_cnt + i - 1).addEHR(new EHR());
            }
            dummy_cnt += 5;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }
    }

}
