package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.thaiopp.utils.DataUtil;
import com.thaiopp.vars.ListItemDetail;

import java.util.ArrayList;
import java.util.List;

public class IssueSaleItemDetailAdapter extends BaseAdapter {


    List<ListItemDetail> result = new ArrayList<ListItemDetail>();
    //String [] result;
    Context context;
    int[] imgItemStatus;
    private static LayoutInflater inflater = null;

    public IssueSaleItemDetailAdapter(IssueSaleDetailActivity issueSaleDetailActivity, List<ListItemDetail> lsItem) {
        // TODO Auto-generated constructor stub
        result = lsItem;
        context = issueSaleDetailActivity;
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
        TextView tvItemId;
        TextView tvDetail1;
        TextView tvDetail2;
        TextView tvDetail3;
        TextView tvItemLocation;
        TextView tvItemStatus;
        ImageView ivItemAlert;
        CheckBox cbItemFin;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.issue_list_itemdetail, null);
        String bc = result.get(position).itemBarcode;
        String loc = "LOCATION : ";
        holder.tvItemId = (TextView) rowView.findViewById(R.id.barcodeId);
        holder.tvDetail1 = (TextView) rowView.findViewById(R.id.itemDetail1);
        holder.tvDetail2 = (TextView) rowView.findViewById(R.id.itemDetail2);
        holder.tvDetail3 = (TextView) rowView.findViewById(R.id.itemDetail3);
        holder.tvItemLocation = (TextView) rowView.findViewById(R.id.location);
        //holder.tvItemQtyWgt = (TextView) rowView.findViewById(R.id.itemDetailQtyWgt);
        holder.tvItemStatus = (TextView) rowView.findViewById(R.id.itemDetailStatus);
        holder.cbItemFin = (CheckBox) rowView.findViewById(R.id.itemCheckBox);

        holder.tvItemId.setText(bc);
        holder.tvDetail1.setText(result.get(position).matName);
        holder.tvDetail2.setText("DATE : " + result.get(position).itemDetail);
        holder.tvDetail3.setText("GRADE : " + result.get(position).grade + "    LOT ID : " + result.get(position).lotId);
        DataUtil data = new DataUtil(this.context);
        holder.tvItemLocation.setText(result.get(position).itemLocation);
        if (result.get(position).itemFin == result.get(position).itemAll) {
            holder.cbItemFin.setChecked(true);
        } else {
            holder.cbItemFin.setChecked(false);
        }
        holder.tvItemStatus.setText(String.valueOf(result.get(position).itemFin) + "/" + String.valueOf(result.get(position).itemAll));
        holder.ivItemAlert = (ImageView) rowView.findViewById(R.id.itemAlert);
        if (result.get(position).itemAlert == 1) {
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