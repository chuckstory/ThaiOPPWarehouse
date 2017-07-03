package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.thaiopp.utils.BadgeView;
import com.thaiopp.vars.ListWorkOrder;

import java.util.HashMap;
import java.util.List;

public class ExpandableRcvListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<ListWorkOrder>> _listDataChild;

    public ExpandableRcvListAdapter(Context context, List<String> listDataHeader,
                                    HashMap<String, List<ListWorkOrder>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        //final String childText = (String) getChild(groupPosition, childPosition);
        ListWorkOrder listItem;
        listItem = (ListWorkOrder) getChild(groupPosition, childPosition);
        final String itemText = (String) listItem.transId;
        final String itemDate = (String) listItem.transDate;
        //final String itemStatus = (String) listItem.stockSeq;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exp_list_item, null);
        }

        TextView txtListItem = (TextView) convertView.findViewById(R.id.lblListItem);
        TextView txtListItemDate = (TextView) convertView.findViewById(R.id.lblListItemDate);

        txtListItem.setText(itemText);
        txtListItemDate.setText(itemDate);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        ViewHolder holder;
        String headerTitle = (String) getGroup(groupPosition);

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.exp_list_group, null);


        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        // set badge
        int badgeBGColor;
        badgeBGColor = _context.getResources().getColor(R.color.color_badge_green);
        holder = new ViewHolder();
        holder.text = lblListHeader;
        holder.badge = new BadgeView(_context, holder.text);
        holder.badge.setBadgeBackgroundColor(badgeBGColor);
        holder.badge.setTextColor(Color.WHITE);
        holder.badge.setTextSize(12);
        holder.badge.setText(" " + String.valueOf(getChildrenCount(groupPosition)) + "");
        holder.badge.show();
        convertView.setTag(holder);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewHolder {
        TextView text;
        BadgeView badge;
    }
}