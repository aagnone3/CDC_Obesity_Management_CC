package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


class CommunityAdapter extends ArrayAdapter<Community> {

    public CommunityAdapter(Context context, List<Community> communities) {
        super(context, R.layout.community_list_item, communities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Community community = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.community_list_item, parent, false);
            viewHolder.communityImage = (ImageView) convertView.findViewById(R.id.communityImage);
            viewHolder.communityName = (TextView) convertView.findViewById(R.id.communityName);
            viewHolder.patientCount = (TextView) convertView.findViewById(R.id.patientCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.communityImage.setImageResource(R.mipmap.ic_launcher);   // FIXME: set to an actual image
        viewHolder.communityName.setText(community.name);
        viewHolder.patientCount.setText(getContext().getString(R.string.patient_count, community.patientCount));

        return convertView;
    }

    private class ViewHolder {
        ImageView communityImage;
        TextView communityName;
        TextView patientCount;
    }
}
