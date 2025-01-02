package com.example.qlnh_ttt.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlnh_ttt.Entities.Food;
import com.example.qlnh_ttt.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderFoodAdapter extends RecyclerView.Adapter<OrderFoodAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Food> foodList;
    private OnQuantityChangeListener quantityChangeListener;
    private final Map<Integer, Integer> foodQuantities = new HashMap<>(); // lưu số lương món ăn theo billid

    public interface OnQuantityChangeListener {
        void onQuantityChanged(Food food, int quantity);
    }

    public void setQuantity(Food food, int quantity) {
        foodQuantities.put(food.getFoodID(), quantity);
    }

    public OrderFoodAdapter(Context context, ArrayList<Food> foodList, OnQuantityChangeListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.quantityChangeListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.txtFoodName.setText(food.getFoodName());
        holder.txtFoodPrice.setText(food.getPrice() + " VNĐ");

        if (food.getAvtFood() != null) {
            holder.imgFood.setImageBitmap(
                    BitmapFactory.decodeByteArray(food.getAvtFood(), 0, food.getAvtFood().length)
            );
        }

        int quantity = foodQuantities.getOrDefault(food.getFoodID(), 0);
        holder.txtQuantity.setText(String.valueOf(quantity));

        holder.btnDecrease.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.txtQuantity.getText().toString());
            if (currentQty > 0) {
                currentQty--;
                holder.txtQuantity.setText(String.valueOf(currentQty));
                foodQuantities.put(food.getFoodID(), currentQty);
                quantityChangeListener.onQuantityChanged(food, currentQty);
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.txtQuantity.getText().toString());
            currentQty++;
            holder.txtQuantity.setText(String.valueOf(currentQty));
            foodQuantities.put(food.getFoodID(), currentQty);
            quantityChangeListener.onQuantityChanged(food, currentQty);
        });
    }

    public void clearQuantities() {
        foodQuantities.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtFoodName, txtFoodPrice, txtQuantity;
        ImageButton btnDecrease, btnIncrease;

        public ViewHolder(View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtFoodPrice = itemView.findViewById(R.id.txtFoodPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }
    }
}