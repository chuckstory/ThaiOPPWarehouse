package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thaiopp.vars.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class SearchPalletListAdapter extends BaseAdapter {

    public List<ItemInfo> itm = new ArrayList<ItemInfo>();

    Context context;
    private static LayoutInflater inflater = null;

    public SearchPalletListAdapter(SearchMainActivity searchMainActivity, List<ItemInfo> lsItem) {
        // TODO Auto-generated constructor stub
        itm = lsItem;
        context = searchMainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return itm.size();
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
        TextView tvQty;
        TextView tvWeight;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.search_pallet_detail, null);

        holder.tvBarcodeId = (TextView) rowView.findViewById(R.id.barcodeId);
        holder.tvDetail1 = (TextView) rowView.findViewById(R.id.detail1);
        holder.tvDetail2 = (TextView) rowView.findViewById(R.id.detail2);
        holder.tvDetail3 = (TextView) rowView.findViewById(R.id.detail3);
        holder.tvQty = (TextView) rowView.findViewById(R.id.qty);
        holder.tvWeight = (TextView) rowView.findViewById(R.id.weight);

        holder.tvBarcodeId.setText(itm.get(position).itemBarcode);
        holder.tvDetail1.setText(itm.get(position).itemDetail);
        holder.tvDetail2.setText("ID : " + itm.get(position).itemDetail2);
        holder.tvDetail3.setText("LOT ID : " + itm.get(position).lotId);
        holder.tvQty.setText(String.valueOf(itm.get(position).itemQty) + " ม้วน");
        holder.tvWeight.setText(itm.get(position).itemWeight + " กก.");

        return rowView;
    }

}