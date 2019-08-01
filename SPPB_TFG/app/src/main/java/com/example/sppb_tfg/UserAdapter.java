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
    private ImageView iv_deleteIcon;
    private ImageView iv_selected;
    public OnUserListener mOnUserListener;
    Long mSelectedId;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ConstraintLayout viewForeground;
        OnUserListener onUserListener;

        public ViewHolder(View itemView, OnUserListener onUserListener) {
            super(itemView);

            tv_score =(TextView) itemView.findViewById(R.id.tv_result);
            tv_name = (TextView) itemView.findViewById(R.id.tv_username);
            iv_deleteIcon = (ImageView) itemView.findViewById(R.id.iv_delete);
            iv_selected = (ImageView) itemView.findViewById(R.id.iv_selected);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            this.onUserListener = onUserListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onUserListener.onUserClick(getAdapterPosition());
        }
    }

    public UserAdapter(ArrayList<User> users, long mSelectedId, OnUserListener onUserListener) {
        this.mUsers = users;
        this.mOnUserListener = onUserListener;
        this.mSelectedId = mSelectedId;
    }

    public interface OnUserListener {
        void onUserClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.listitem_user, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(userView, mOnUserListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        iv_deleteIcon.setImageResource(R.drawable.ic_delete);
        final User user = mUsers.get(i);

        if (user.getId() == mSelectedId) {
            iv_selected.setVisibility(View.VISIBLE);
        }

        if (user.getScore() != 0) {
            int n_score = user.getScore();

            if (n_score < 4) {
                tv_score.setTextColor(context.getResources().getColor(R.color.severe));
            } else if (n_score < 7) {
                tv_score.setTextColor(context.getResources().getColor(R.color.moderate));
            } else if (n_score < 10) {
                tv_score.setTextColor(context.getResources().getColor(R.color.slight));
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
        mUsers.get(position).delete(context);
        mUsers.remove(position);
        notifyItemRemoved(position);
    }

    public void insertUser(User user, Context context) {
        user.insert(context);
        mUsers.add(user);
        notifyItemInserted(getItemCount());
    }

    public User getUser(int position) {
        return mUsers.get(position);
    }
}
