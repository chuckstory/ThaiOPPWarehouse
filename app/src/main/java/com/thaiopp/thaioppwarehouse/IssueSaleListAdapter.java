package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thaiopp.vars.ListSaleOrder;

import java.util.ArrayList;
import java.util.List;

public class IssueSaleListAdapter extends BaseAdapter {
    public List<ListSaleOrder> lsSo = new ArrayList<>();

    Context context;
    private static LayoutInflater inflater = null;

    public IssueSaleListAdapter(IssueSaleMainActivity issueSaleMainActivity, List<ListSaleOrder> lsSaleOrder) {
        // TODO Auto-generated constructor stub
        lsSo = lsSaleOrder;
        context = issueSaleMainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lsSo.size();
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
        TextView tvSoId;
        TextView tvPoId;
        TextView tvCustId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.issuesale_list, null);

        holder.tvSoId = (TextView) rowView.findViewById(R.id.soId);
        holder.tvPoId = (TextView) rowView.findViewById(R.id.poId);
        holder.tvCustId = (TextView) rowView.findViewById(R.id.custId);

        holder.tvSoId.setText(lsSo.get(position).soId);
        holder.tvPoId.setText("TF DOC : " + lsSo.get(position).tfLotDocId);
        holder.tvCustId.setText("Date : " + lsSo.get(position).deliDate);


        return rowView;
    }

}