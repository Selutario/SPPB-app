package com.example.sppb_tfg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> mUsers;
    public TextView tv_score;
    public TextView tv_name;
    private ImageView mDeleteIcon;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout viewBackground, viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_score =(TextView) itemView.findViewById(R.id.tv_result);
            tv_name = (TextView) itemView.findViewById(R.id.tv_username);
            mDeleteIcon = (ImageView) itemView.findViewById(R.id.iv_delete);
            viewForeground = itemView.findViewById(R.id.viewForeground);
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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        mDeleteIcon.setImageResource(R.drawable.ic_delete);
        final User user = mUsers.get(i);

        if (user.getScore() != 0) {
            int n_score = user.getScore();

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

        tv_score.setText(Integer.toString(user.getScore()));
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
