package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.thaiopp.vars.ProdRcv;

import java.util.ArrayList;
import java.util.List;

public class ProdRcvListAdapter extends BaseAdapter {
    public List<ProdRcv> prodRcv = new ArrayList<>();

    Context context;
    private static LayoutInflater inflater = null;

    public ProdRcvListAdapter(ProdRcvMainActivity prodRcvMainActivity, List<ProdRcv> lsProdRcv) {
        // TODO Auto-generated constructor stub
        prodRcv = lsProdRcv;
        context = prodRcvMainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return prodRcv.size();
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
        TextView tvBarcodeId;
        TextView tvDetail1;
        TextView tvDetail2;
        TextView tvDetail3;
        TextView tvDetail4;
        TextView tvQty;
        TextView tvWeight;
        TextView tvDate;
        CheckBox cbCheck;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.prodrcv_itemdetail, null);

        holder.tvBarcodeId = (TextView) rowView.findViewById(R.id.barcodeId);
        holder.tvDetail1 = (TextView) rowView.findViewById(R.id.detail1);
        holder.tvDetail2 = (TextView) rowView.findViewById(R.id.detail2);
        holder.tvDetail3 = (TextView) rowView.findViewById(R.id.detail3);
        holder.tvDetail4 = (TextView) rowView.findViewById(R.id.detail4);
        holder.tvQty = (TextView) rowView.findViewById(R.id.qty);
        holder.tvWeight = (TextView) rowView.findViewById(R.id.weight);
        holder.tvDate = (TextView) rowView.findViewById(R.id.date);
        holder.cbCheck = (CheckBox) rowView.findViewById(R.id.check);

        if (prodRcv.get(position).palletId.equals("")) {
            holder.tvBarcodeId.setText(prodRcv.get(position).barcode + " [" + prodRcv.get(position).locationId + "]");
        } else {
            holder.tvBarcodeId.setText(prodRcv.get(position).barcode + " [" + prodRcv.get(position).palletId + "]" + " [" + prodRcv.get(position).locationId + "]");
        }
        holder.tvDetail1.setText(prodRcv.get(position).cpSpec);
        holder.tvDetail2.setText("ID : " + prodRcv.get(position).rollId);
        holder.tvDetail3.setText("LOT ID : " + prodRcv.get(position).lotId + "    JOB ID : " + prodRcv.get(position).jobId);
        holder.tvDetail4.setText("LOCATION : " + prodRcv.get(position).itemLocation);
        holder.tvQty.setText(String.valueOf(prodRcv.get(position).qty) + " ม้วน");
        holder.tvWeight.setText(prodRcv.get(position).weight + " กก.");
        holder.tvDate.setText(prodRcv.get(position).crDate);
        holder.cbCheck.setChecked(prodRcv.get(position).check);


        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
        return rowView;
    }

}