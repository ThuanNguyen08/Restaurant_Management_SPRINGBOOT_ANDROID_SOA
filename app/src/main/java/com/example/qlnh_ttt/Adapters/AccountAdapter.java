package com.example.qlnh_ttt.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Activities.UpdateAccountActivity;
import com.example.qlnh_ttt.Entities.DmFood;
import com.example.qlnh_ttt.Entities.InfoUser;
import com.example.qlnh_ttt.R;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder>  {
    private static Context context;
    private List<InfoUser> accounts;
    private boolean isAdmin;
    private OnDeleteClickListener deleteClickListener;

    public AccountAdapter(Context context, List<InfoUser> accounts, boolean isAdmin, OnDeleteClickListener listener) {
        this.context = context;
        this.accounts = accounts;
        this.isAdmin = isAdmin;
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        InfoUser account = accounts.get(position);
        holder.bind(account);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(account, holder.getAdapterPosition());
                }
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UpdateAccountActivity.class);
                intent.putExtra("userInfoId", account.getUserInfoId());
                intent.putExtra("accountId", account.getAccountId());
                intent.putExtra("fullName", account.getFullName());
                intent.putExtra("sex", account.getSex());
                intent.putExtra("email", account.getEmail());
                intent.putExtra("phoneNumber", account.getPhoneNumber());
                context.startActivity(intent);
            }
        });
    }

    // Interface để handle sự kiện xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(InfoUser account, int position);
    }

    public void removeItem(int position) {
        accounts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, accounts.size()); // Cập nhật lại các vị trí
    }


    @Override
    public int getItemCount() {
        return accounts.size();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvPhone, tvEmail;
        private Button btnEdit, btnDelete;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvEmail = itemView.findViewById(R.id.tv_email);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            if (!isAdmin) {
                btnDelete.setVisibility(View.GONE);
            }
        }

        public void bind(InfoUser account) {
            tvName.setText(account.getFullName());
            tvPhone.setText(account.getPhoneNumber());
            tvEmail.setText(account.getEmail());
        }
    }
}
