package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.thaiopp.vars.ListItemDetail;

import java.util.ArrayList;
import java.util.List;

public class PickItemDetailAdapter extends BaseAdapter {

    List<ListItemDetail> result = new ArrayList<ListItemDetail>();
    Context context;
    int[] imgItemStatus;
    private static LayoutInflater inflater = null;

    public PickItemDetailAdapter(PickDetailActivity pickDetailActivity, List<ListItemDetail> lsItem) {
        // TODO Auto-generated constructor stub
        result = lsItem;
        context = pickDetailActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
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
        TextView tvItemBarcode;
        TextView tvItemDetail1;
        TextView tvItemDetail2;
        TextView tvItemDetail3;
        TextView tvItemDetail4;
        TextView tvItemQty;
        TextView tvItemWeight;
        CheckBox cbItemFin;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.pick_list_itemdetail, null);

        holder.tvItemBarcode = (TextView) rowView.findViewById(R.id.barcodeId);
        holder.tvItemDetail1 = (TextView) rowView.findViewById(R.id.itemDetail1);
        holder.tvItemDetail2 = (TextView) rowView.findViewById(R.id.itemDetail2);
        holder.tvItemDetail3 = (TextView) rowView.findViewById(R.id.itemDetail3);
        holder.tvItemDetail4 = (TextView) rowView.findViewById(R.id.itemDetail4);
        holder.tvItemQty = (TextView) rowView.findViewById(R.id.itemQty);
        holder.tvItemWeight = (TextView) rowView.findViewById(R.id.itemWeight);
        holder.cbItemFin = (CheckBox) rowView.findViewById(R.id.itemCheckBox);

        holder.tvItemBarcode.setText(result.get(position).itemBarcode);
        holder.tvItemDetail1.setText(result.get(position).matName);
        holder.tvItemDetail2.setText("ID : " + result.get(position).itemDetail);
        holder.tvItemDetail3.setText("LOT ID : " + result.get(position).lotId);
        holder.tvItemDetail4.setText("LOCATION : " + result.get(position).itemLocation);
        holder.tvItemQty.setText(result.get(position).itemQty);
        holder.tvItemWeight.setText(result.get(position).itemWeight);

        if (result.get(position).itemFin == result.get(position).itemAll) {
            holder.cbItemFin.setChecked(true);
        } else {
            holder.cbItemFin.setChecked(false);
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