package com.example.qlnh_ttt.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Entities.BillDetail;
import com.example.qlnh_ttt.Entities.Food;
import com.example.qlnh_ttt.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class PaymentDetailAdapter extends RecyclerView.Adapter<PaymentDetailAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BillDetail> billDetails;
    private Map<Integer, Food> foodMap;

    public PaymentDetailAdapter(Context context, ArrayList<BillDetail> billDetails) {
        this.context = context;
        this.billDetails = billDetails;
    }

    public void updateData(ArrayList<BillDetail> billDetails, Map<Integer, Food> foodMap) {
        this.billDetails = billDetails;
        this.foodMap = foodMap;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BillDetail detail = billDetails.get(position);
        Food food = foodMap.get(detail.getFoodID());

        if (food != null) {
            holder.txtFoodName.setText(food.getFoodName());
        } else {
            holder.txtFoodName.setText("Món " + detail.getFoodID());
        }

        holder.txtQuantity.setText("Số lượng: " + detail.getQuantity());

        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(detail.getPrice() * detail.getQuantity()) + " VNĐ";
        holder.txtAmount.setText(formattedPrice);
    }

    @Override
    public int getItemCount() {
        return billDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFoodName, txtQuantity, txtAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtAmount = itemView.findViewById(R.id.txtAmount);
        }
    }
}