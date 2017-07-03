package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thaiopp.vars.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class TransferListAdapter extends BaseAdapter {
    public List<ItemInfo> checkItem = new ArrayList<ItemInfo>();

    Context context;
    private static LayoutInflater inflater = null;

    public TransferListAdapter(TransferMainActivity transferMainActivity, List<ItemInfo> lsCheckItem) {
        // TODO Auto-generated constructor stub
        checkItem = lsCheckItem;
        context = transferMainActivity;
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
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.transfer_itemdetail, null);

        holder.tvItemId = (TextView) rowView.findViewById(R.id.checkItemId);
        holder.tvItemType = (TextView) rowView.findViewById(R.id.checkItemType);
        holder.tvItemName = (TextView) rowView.findViewById(R.id.checkItemName);
        holder.tvItemDetail = (TextView) rowView.findViewById(R.id.checkItemDetail);
        holder.tvItemStatus = (TextView) rowView.findViewById(R.id.checkItemStatus);

        holder.tvItemId.setText(checkItem.get(position).itemId);
        holder.tvItemType.setText(checkItem.get(position).itemType);
        holder.tvItemName.setText(checkItem.get(position).itemLocation + " [ " + String.valueOf(checkItem.get(position).itemQty) + " ]");
        holder.tvItemDetail.setText(checkItem.get(position).itemDetail);
        holder.tvItemStatus.setText(String.valueOf(checkItem.get(position).itemCount));

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
        return rowView;
    }
}