package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thaiopp.vars.ItemStockInfo;

import java.util.ArrayList;
import java.util.List;

public class SearchLocationListAdapter extends BaseAdapter {
    public List<ItemStockInfo> checkItem = new ArrayList<ItemStockInfo>();

    Context context;
    private static LayoutInflater inflater = null;

    public SearchLocationListAdapter(SearchMainActivity searchMainActivity, List<ItemStockInfo> lsCheckItem) {
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
        TextView tvItemDetail;
        TextView tvRollNo;
        TextView tvLotId;
        TextView tvStockQty;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.search_location_detail, null);

        holder.tvItemId = (TextView) rowView.findViewById(R.id.barcodeId);
        holder.tvItemDetail = (TextView) rowView.findViewById(R.id.itemDetail);
        holder.tvRollNo = (TextView) rowView.findViewById(R.id.rollNo);
        holder.tvLotId = (TextView) rowView.findViewById(R.id.lotId);
        holder.tvStockQty = (TextView) rowView.findViewById(R.id.stockQty);

        holder.tvItemId.setText(checkItem.get(position).itemBarcode + " (" + checkItem.get(position).itemType + ")");
        holder.tvItemDetail.setText(checkItem.get(position).itemName);
        holder.tvRollNo.setText("ROLL NO: " + checkItem.get(position).rollNo);
        holder.tvLotId.setText("LOT ID: " + checkItem.get(position).lotId);
        holder.tvStockQty.setText(String.valueOf(checkItem.get(position).itemQty));

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
        return rowView;
    }

}