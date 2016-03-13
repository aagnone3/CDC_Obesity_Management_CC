package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class ContentListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView.Adapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Type listType = new TypeToken<List<Community>>(){}.getType();
            List<Community> data = new Gson().fromJson(savedInstanceState.getString("data"), listType);
            adapter = new CommunityAdapter(data);
            setAdapter(adapter);
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
        RecyclerView contentList = (RecyclerView) view.findViewById(R.id.list);
        contentList.addItemDecoration(new ListDividerItemDecoration(getContext()));
        contentList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (adapter instanceof DataRecyclable) {
            outState.putString("data", new Gson().toJson(((DataRecyclable) adapter).getDataSet()));
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (getView() != null) {
            ((RecyclerView) getView().findViewById(R.id.list)).setAdapter(adapter);
            this.adapter = adapter;
        }
    }
}
