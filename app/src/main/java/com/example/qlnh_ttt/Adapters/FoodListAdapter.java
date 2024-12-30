package com.example.qlnh_ttt.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Entities.Food;
import com.example.qlnh_ttt.R;

import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewHolder> {
    private Context context;
    private List<Food> foodList;

    public FoodListAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.txtFoodName.setText(food.getFoodName());
        holder.txtFoodPrice.setText(food.getPrice() + " VNĐ");
        holder.txtFoodCategory.setText("Danh mục: " + food.getDmFoodID());

        // Hiển thị ảnh
        if (food.getAvtFood() != null) {
            byte[] imageBytes = food.getAvtFood();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imgFood.setImageBitmap(bitmap);
        } else {
            holder.imgFood.setImageResource(R.drawable.category_image);
        }
    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtFoodName, txtFoodPrice, txtFoodCategory;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtFoodPrice = itemView.findViewById(R.id.txtFoodPrice);
            txtFoodCategory = itemView.findViewById(R.id.txtFoodCategory);
        }
    }

    public void updateData(List<Food> newFoodList) {
        this.foodList = newFoodList;
        notifyDataSetChanged();
    }
}