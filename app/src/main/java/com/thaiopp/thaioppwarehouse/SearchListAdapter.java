package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thaiopp.vars.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends BaseAdapter {
    public List<ItemInfo> checkItem = new ArrayList<ItemInfo>();

    Context context;
    private static LayoutInflater inflater = null;

    public SearchListAdapter(SearchMainActivity searchMainActivity, List<ItemInfo> lsCheckItem) {
        // TODO Auto-generated constructor stub
        checkItem = lsCheckItem;
        context = searchMainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return checkItem.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tvItemId;
        TextView tvItemType;
        TextView tvItemName;
        TextView tvItemDetail;
        TextView tvItemStatus;
        ImageView ivItemAlert;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.check_itemdetail, null);

        holder.tvItemId = (TextView) rowView.findViewById(R.id.checkItemId);
        holder.tvItemType = (TextView) rowView.findViewById(R.id.checkItemType);
        holder.tvItemName = (TextView) rowView.findViewById(R.id.checkItemName);
        holder.tvItemDetail = (TextView) rowView.findViewById(R.id.checkItemDetail);
        holder.tvItemStatus = (TextView) rowView.findViewById(R.id.checkItemStatus);
        holder.ivItemAlert = (ImageView) rowView.findViewById(R.id.checkAlert);

        holder.tvItemId.setText(checkItem.get(position).itemBarcode);
        holder.tvItemType.setText(checkItem.get(position).itemType);
        holder.tvItemName.setText(checkItem.get(position).itemName);
        holder.tvItemDetail.setText(checkItem.get(position).itemDetail);
        holder.tvItemStatus.setText(checkItem.get(position).itemQty);
        if (checkItem.get(position).itemQty == 0) {
            holder.ivItemAlert.setVisibility(View.VISIBLE);
        } else {
            holder.ivItemAlert.setVisibility(View.INVISIBLE);
        }

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
        return rowView;
    }

}