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

/*
* Adapter to display user list in Recyclerview.
*/
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> mUsers;
    public TextView tv_score;
    public TextView tv_name;
    private ImageView iv_deleteIcon;
    private ImageView iv_selected;
    public clickListener mClickListener;
    Long mSelectedId;
    Context context;

    public interface clickListener {
        public void onUserClick(int position);
        public boolean onUserLongClick(int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public ConstraintLayout viewForeground;
        clickListener clickListener;

        public ViewHolder(View itemView, clickListener clickListener) {
            super(itemView);

            // Get listItemView views
            tv_score =(TextView) itemView.findViewById(R.id.tv_score_history);
            tv_name = (TextView) itemView.findViewById(R.id.tv_username);
            iv_deleteIcon = (ImageView) itemView.findViewById(R.id.iv_delete);
            iv_selected = (ImageView) itemView.findViewById(R.id.iv_selected);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            this.clickListener = clickListener;

            // Obtain position of clicked user
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onUserClick(getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            clickListener.onUserLongClick(getAdapterPosition());
            return true;
        }
    }


    public UserAdapter(ArrayList<User> users, long mSelectedId, clickListener clickListener) {
        this.mUsers = users;
        this.mClickListener = clickListener;
        this.mSelectedId = mSelectedId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.listitem_user, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(userView, mClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        iv_deleteIcon.setImageResource(R.drawable.ic_delete);
        final User user = mUsers.get(i);

        // Show a blue line if the user is selected to save future tests results
        if (user.getId() == mSelectedId) {
            iv_selected.setVisibility(View.VISIBLE);
        }

        // Print score with different color depending on result
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

        // Set name and score
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
}
