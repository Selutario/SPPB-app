package com.example.sppb_tfg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/*
 * Adapter to display user list in Recyclerview.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private User mUser;
    private TextView tv_score_history;
    private ProgressBar pb_history;

    private ImageView iv_calendar;
    private ImageView iv_balance;
    private ImageView iv_gait;
    private ImageView iv_chair;

    private TextView tv_date;
    private TextView tv_balance;
    private TextView tv_gait;
    private TextView tv_chair;

    Context context;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);

            // Get listItemView views
            tv_score_history =(TextView) itemView.findViewById(R.id.tv_score_history);
            pb_history = (ProgressBar) itemView.findViewById(R.id.pb_history);

            iv_calendar = (ImageView) itemView.findViewById(R.id.iv_calendar);
            iv_balance = (ImageView) itemView.findViewById(R.id.iv_balance);
            iv_gait = (ImageView) itemView.findViewById(R.id.iv_gait);
            iv_chair = (ImageView) itemView.findViewById(R.id.iv_chair);

            tv_date =(TextView) itemView.findViewById(R.id.tv_date);
            tv_balance =(TextView) itemView.findViewById(R.id.tv_balance);
            tv_gait =(TextView) itemView.findViewById(R.id.tv_gait);
            tv_chair =(TextView) itemView.findViewById(R.id.tv_chair);
        }
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.listitem_history, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(userView);

        return viewHolder;
    }

    public HistoryAdapter(User user) {
        this.mUser = user;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        iv_calendar.setImageResource(R.drawable.ic_date_range_black_24dp);
        tv_date.setText(mUser.getTestDate(i));

        iv_balance.setImageResource(R.color.colorBalance);
        tv_balance.setText(String.valueOf(mUser.getBalanceScore(i)));

        iv_gait.setImageResource(R.color.colorGaitSpeed);
        tv_gait.setText(String.valueOf(mUser.getSpeedScore(i)));

        iv_chair.setImageResource(R.color.colorChairStand);
        tv_chair.setText(String.valueOf(mUser.getChairScore(i)));

        tv_score_history.setText(String.valueOf(mUser.getScore(i)));
        pb_history.setProgress(mUser.getScore(i));
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
        return mUser.getHistorySize();
    }
}
