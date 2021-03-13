package com.ip.smslockdown;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ip.smslockdown.databinding.UserItemBinding;
import com.ip.smslockdown.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static final String TAG = "UserAdapter";
    private List<User> users = new ArrayList<>();
    private UserClickListener listener;

    public UserAdapter(UserClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UserItemBinding userItemBinding = UserItemBinding.inflate(inflater, parent, false);

        return new UserViewHolder(userItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.userNameTv.setText(users.get(position).getFullName());
        holder.userAddressTv.setText(users.get(position).getAddress());
        holder.linearLayout.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setData(List<User> users) {

        this.users = users;
        notifyDataSetChanged();
    }

    public void setUserListener(UserClickListener listener) {

        this.listener = listener;
    }

    public interface UserClickListener {

        void onUserClickListener(List<User> users, int position);
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView userNameTv;
        private final TextView userAddressTv;
        private final View view;
        private final LinearLayout linearLayout;

        private UserViewHolder(UserItemBinding binding) {
            super(binding.getRoot());

            view = binding.deco;
            userNameTv = binding.userNameRec;
            userAddressTv = binding.userAddressRec;
            linearLayout = binding.linearItem;

            userNameTv.setOnClickListener(this);
            userAddressTv.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onUserClickListener(users, getAdapterPosition());
        }

    }

    public List<User> getData(){
        return users;
    }

}