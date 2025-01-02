package com.example.qlnh_ttt.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Entities.Bill;
import com.example.qlnh_ttt.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RevenueAdapter extends RecyclerView.Adapter<RevenueAdapter.BillViewHolder> {

    private List<Bill> billList;

    public RevenueAdapter(List<Bill> billList) {
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_revenue_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billList.get(position);
        if (bill == null) return;

        holder.tvBillID.setText("Mã hóa đơn: " + bill.getBillID());
        holder.tvBillDate.setText("Ngày: " + bill.getBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        holder.tvTotalAmount.setText("Tổng tiền: " + bill.getTotalAmount() + " VND");
        holder.tvStatus.setText("Trạng thái: " + bill.getStatus());
    }

    @Override
    public int getItemCount() {
        return billList != null ? billList.size() : 0;
    }

    public void updateData(List<Bill> newBillList) {
        this.billList = newBillList;
        notifyDataSetChanged();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillID, tvBillDate, tvTotalAmount, tvStatus;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillID = itemView.findViewById(R.id.tvBillID);
            tvBillDate = itemView.findViewById(R.id.tvBillDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
