package edu.gatech.johndoe.carecoordinator.care_plan.UI;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.ContentListFragment;
import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.util.Utility;

/**
 * Created by rakyu012 on 4/28/2016.
 */
public class InnerCommunityAdapter extends RecyclerView.Adapter<InnerCommunityAdapter.InnerCommunityHolder> {
    private android.support.v4.app.FragmentManager fragment_manager;
    private ArrayList<String> communityList;

    public InnerCommunityAdapter(ArrayList<String> communityList, android.support.v4.app.FragmentManager fragment_manager) {
        this.fragment_manager = fragment_manager;
        this.communityList = communityList;
    }

    @Override
    public InnerCommunityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.care_plan_inner_list, parent, false);
        return new InnerCommunityHolder(view);
    }
    @Override
    public void onBindViewHolder(InnerCommunityHolder holder, int position) {
        String communityName = communityList.get(position);
        holder.bindInnerCommunity(communityName);
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public class InnerCommunityHolder extends RecyclerView.ViewHolder {
        private final TextView communityName;
        private String commuinty;
        private Boolean clicked;

        public InnerCommunityHolder(View itemView) {
            super(itemView);
            this.communityName = (TextView) itemView.findViewById(R.id.inner_community_name);
//            this.communityStatus = (TextView) itemView.findViewById(R.id.community_status);
//            this.communityStatusImg = (ImageView) itemView.findViewById(R.id.community_status_image);
            clicked = false;
        }

        public void bindInnerCommunity(String cm) {
            this.commuinty = cm;
            communityName.setText(cm);
//            communityStatusImg.setBackgroundResource(android.R.drawable.ic_menu_info_details);
//            communityStatus.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    if (!clicked) {
//                        communityStatus.setText("Clicked");
//                        System.out.println("true");
//                        communityStatusImg.setBackgroundResource(android.R.drawable.presence_online);
//
//                        clicked = true;
//                    } else {
//                        communityStatus.setText("Unclicked");
//                        System.out.println("false");
//                        communityStatusImg.setBackgroundResource(android.R.drawable.ic_menu_info_details);
//                        clicked = false;
//                    }
//                    ContentListFragment contentListFragment =
//                            (ContentListFragment) fragment_manager.findFragmentById(R.id.contentListFragment);
//                    contentListFragment.getAdapter().notifyDataSetChanged();
//                }
//            });
        }
    }
}
