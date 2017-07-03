package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thaiopp.vars.ListLocation;

import java.util.ArrayList;
import java.util.List;

public class SearchRollListAdapter extends BaseAdapter {
    public List<ListLocation> lsLoc = new ArrayList<>();

    Context context;
    private static LayoutInflater inflater = null;

    public SearchRollListAdapter(SearchMainActivity searchMainActivity, List<ListLocation> lsLocation) {
        // TODO Auto-generated constructor stub
        lsLoc = lsLocation;
        context = searchMainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lsLoc.size();
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
        TextView tvLocationId;
        TextView tvPalletId;
        TextView tvStockQty;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.search_roll_detail, null);

        holder.tvLocationId = (TextView) rowView.findViewById(R.id.locationId);
        holder.tvPalletId = (TextView) rowView.findViewById(R.id.palletId);
        holder.tvStockQty = (TextView) rowView.findViewById(R.id.stockQty);

        holder.tvLocationId.setText("Location ID : " + lsLoc.get(position).locationId);
        holder.tvPalletId.setText("Pallet ID : " + lsLoc.get(position).palletId);
        holder.tvStockQty.setText(String.valueOf(lsLoc.get(position).locationQty));

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
        return rowView;
    }

}