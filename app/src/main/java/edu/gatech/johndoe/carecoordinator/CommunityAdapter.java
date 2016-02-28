package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


class CommunityAdapter extends ArrayAdapter<Community> implements Filterable {

    private List<Community> communities;

    public CommunityAdapter(Context context, List<Community> communities) {
        super(context, R.layout.community_list_item, communities);
        this.communities = new ArrayList<>(communities);
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Community> filtered = new ArrayList<>();

                constraint = constraint.toString().toLowerCase().trim();
                for (Community community : communities) {
                    if (community.name.toLowerCase().startsWith(constraint.toString())) {
                        filtered.add(community);
                    }
                }

                results.count = filtered.size();
                results.values = filtered;

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
                clear();
                addAll((ArrayList<Community>) results.values);
                notifyDataSetInvalidated();
            }
        };
    }

    private class ViewHolder {
        ImageView communityImage;
        TextView communityName;
        TextView patientCount;
    }
}
