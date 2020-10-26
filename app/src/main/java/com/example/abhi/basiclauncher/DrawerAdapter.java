package com.example.abhi.basiclauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DrawerAdapter extends BaseAdapter implements Filterable {

    Context mContext;
    List<Pac> originalList;


    public DrawerAdapter(Context c, List<Pac> pacs) {
        mContext = c;
        originalList = pacs;
    }

    @Override
    public int getCount() {
        return originalList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.values = originalList;
                    filterResults.count = originalList.size();
                } else {
                    ArrayList<Pac> filterResultsData = new ArrayList<>();
                    for (Pac data : originalList) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.name.contains(charSequence)) {
                            filterResultsData.add(data);
                        }

                    }
                    filterResults.values = filterResultsData;
                    filterResults.count = filterResultsData.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                // update the data with the new set of suggestions
                originalList = (List<Pac>) filterResults.values;

                if (filterResults != null && filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }

                notifyDataSetChanged();
            }
        };


        return filter;
    }


    static class ViewHolder {
        TextView label, packagename, main_activity, versioncode, versionname;
        ImageView icon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = li.inflate(R.layout.drawer_items, null);

            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon_image);
            viewHolder.label = (TextView) convertView.findViewById(R.id.icon_label);
            viewHolder.packagename = (TextView) convertView.findViewById(R.id.packagename);
            viewHolder.main_activity = (TextView) convertView.findViewById(R.id.main_activity);
            viewHolder.versioncode = (TextView) convertView.findViewById(R.id.versioncode);
            viewHolder.versionname = (TextView) convertView.findViewById(R.id.versionname);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.icon.setImageDrawable(originalList.get(position).icon);
        viewHolder.label.setText(originalList.get(position).label);

        viewHolder.packagename.setText(originalList.get(position).packageName);
        viewHolder.main_activity.setText(originalList.get(position).mainActivity);
        viewHolder.versionname.setText(/*originalList.get(position).packageName*/  "packagename ");
        viewHolder.versioncode.setText(/*originalList.get(position).mainActivity*/ "packagecode");
        return convertView;
    }
}
