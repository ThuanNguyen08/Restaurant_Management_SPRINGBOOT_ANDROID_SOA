package com.example.qlnh_ttt.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Activities.FoodListActivity;
import com.example.qlnh_ttt.Activities.ItemCategoryActivity;
import com.example.qlnh_ttt.Activities.UpdateCategoryActivity;
import com.example.qlnh_ttt.Entities.DmFood;
import com.example.qlnh_ttt.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<DmFood> categoryList;
    private OnDeleteClickListener deleteClickListener;

    public CategoryAdapter(Context context, ArrayList<DmFood> categoryList, OnDeleteClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.deleteClickListener = listener;
    }

    //ánh xạ mỗi item trong recyclerView
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;
        ImageButton btnDeleteCategory, btnUpdateCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);
            btnUpdateCategory = itemView.findViewById(R.id.btnUpdateCategory);
        }
    }

    //tao mot item(view con) lay tu layout_category_item
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    //gán dữ liệu từ 1 mục vào trongg danh sách
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        DmFood category = categoryList.get(position);
        holder.txtCategoryName.setText(category.getCategoryName());

        //bắt sự kiện khi nhấn vào 1 item category trong danh sách
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemCategoryActivity.class);
            intent.putExtra("category_name", category.getCategoryName());
            intent.putExtra("category_id", category.getDmFoodId());
            context.startActivity(intent);

        });

        //Xử lý sự kiện click nút update
        holder.btnUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateCategoryActivity.class);
                intent.putExtra("category_id", category.getDmFoodId());
                intent.putExtra("category_name", category.getCategoryName());
                context.startActivity(intent);
            }
        });

        // Xử lý sự kiện click nút xóa
        holder.btnDeleteCategory.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(category, position);
            }
        });
    }

    // Interface để handle sự kiện xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(DmFood category, int position);
    }


    //láy số lượng trong danh sách
    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void removeItem(int position) {
        categoryList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, categoryList.size()); // Cập nhật lại các vị trí
    }

}
