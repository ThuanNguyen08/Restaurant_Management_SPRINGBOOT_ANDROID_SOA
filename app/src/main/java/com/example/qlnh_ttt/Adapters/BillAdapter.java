package com.example.qlnh_ttt.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Activities.PaymentActivity;
import com.example.qlnh_ttt.Entities.Bill;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.example.qlnh_ttt.R;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private List<Bill> bills;

    public BillAdapter(List<Bill> bills){
        this.bills = bills;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        holder.txtBillId.setText("Đơn #" + bill.getBillID());
        holder.txtTableId.setText("Bàn " + bill.getTableID());
        holder.txtAmount.setText(formatter.format(bill.getTotalAmount()));
        holder.txtStatus.setText(bill.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return bills != null ? bills.size() : 0;
    }

    public void updateBills(List<Bill> newBills) {
        this.bills = newBills;
        notifyDataSetChanged();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView txtBillId, txtTableId, txtAmount, txtStatus;

        BillViewHolder(View itemView) {
            super(itemView);
            txtBillId = itemView.findViewById(R.id.txt_bill_id);
            txtTableId = itemView.findViewById(R.id.txt_table_id);
            txtAmount = itemView.findViewById(R.id.txt_amount);
            txtStatus = itemView.findViewById(R.id.txt_status);
        }
    }
}
