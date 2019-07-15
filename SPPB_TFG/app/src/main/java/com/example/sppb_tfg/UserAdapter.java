package com.example.sppb_tfg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> mUsers;
    public TextView tv_score;
    public TextView tv_name;
    private LinearLayout list_item;
    private LinearLayout swipe_item;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout viewBackground, viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_score =(TextView) itemView.findViewById(R.id.tv_score);
            tv_name = (TextView) itemView.findViewById(R.id.tv_username);
            list_item = (LinearLayout) itemView.findViewById(R.id.main_list);
            swipe_item = (LinearLayout) itemView.findViewById(R.id.swipe_layout);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            //viewBackground = itemView.findViewById(R.id.viewBackground);
        }
    }

    public UserAdapter(ArrayList<User> users) {
        mUsers = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.listitem_user, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        User user = mUsers.get(i);

        if (!user.getLastScore().equals("0")) {
            int n_score = Integer.parseInt(user.getLastScore());

            if (n_score < 4) {
                tv_score.setTextColor(context.getResources().getColor(R.color.severe));
            } else if (n_score < 7) {
                tv_score.setTextColor(context.getResources().getColor(R.color.moderate));
            } else if (n_score < 10) {
                tv_score.setTextColor(context.getResources().getColor(R.color.light));
            } else if (n_score <= 12) {
                tv_score.setTextColor(context.getResources().getColor(R.color.minimum));
            }
        }


        tv_score.setText(user.getLastScore());
        tv_name.setText(user.getName());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if(mUsers != null){
            return mUsers.size();
        } else {
            return 0;
        }
    }

    public void removeUser(int position) {
        mUsers.remove(position);

        // Notify removed position in order to let Recyclerview  perform
        // delete animation
        notifyItemRemoved(position);
        //notifyItemRangeChanged(position, mUsers.size());
    }

    public void restoreUser(User user, int position) {
        mUsers.add(position, user);
        notifyItemInserted(position);
    }
}
