package com.example.qlnh_ttt.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Activities.UpdateFoodActivity;
import com.example.qlnh_ttt.Entities.DmFood;
import com.example.qlnh_ttt.Entities.Food;
import com.example.qlnh_ttt.R;

import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewHolder> {
    private Context context;
    private List<Food> foodList;
    private OnDeleteClickListener deleteClickListener;

    public FoodListAdapter(Context context, List<Food> foodList, OnDeleteClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.deleteClickListener = listener;
    }

    //ánh xạ mỗi item trong recyclerView
    public class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtFoodName, txtFoodPrice, txtFoodCategory;
        ImageButton btnUpdateFood, btnDeleteFood;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtFoodPrice = itemView.findViewById(R.id.txtFoodPrice);
            txtFoodCategory = itemView.findViewById(R.id.txtFoodCategory);
            btnUpdateFood = itemView.findViewById(R.id.btnUpdateFood);
            btnDeleteFood = itemView.findViewById(R.id.btnDeleteFood);
        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // lấy food ở vị trí hiện tại
        Food food = foodList.get(position);
        //hiển thị thông tin
        holder.txtFoodName.setText(food.getFoodName());
        holder.txtFoodPrice.setText(food.getPrice() + " VNĐ");

        // Hiển thị ảnh
        if (food.getAvtFood() != null) {
            byte[] imageBytes = food.getAvtFood();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imgFood.setImageBitmap(bitmap);
        } else {
            holder.imgFood.setImageResource(R.drawable.category_image);
        }

        holder.btnUpdateFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateFoodActivity.class);
                intent.putExtra("food_id", food.getFoodID());
                intent.putExtra("food_name", food.getFoodName());
                intent.putExtra("food_price", food.getPrice());
                intent.putExtra("food_category", food.getDmFoodID());
                context.startActivity(intent);
            }
        });

        holder.btnDeleteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(food, position);
                }
            }
        });
    }

    // Interface để handle sự kiện xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(Food food, int position);
    }


    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void removeItem(int position) {
        foodList.remove(position);
        notifyItemRemoved(position);//thông báo vị trí đã bị xóa
        notifyItemRangeChanged(position, foodList.size()); // Cập nhật lại các vị trí
    }


}