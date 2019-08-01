package com.example.sppb_tfg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class UsersFragment extends Fragment implements RecyclerUserTouchHelper.RecyclerUserTouchHelperListener, UserAdapter.OnUserListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView iv_empty_box;
    private TextView et_empty_box;
    private ArrayList<User> usersList;
    private UserAdapter adapter;
    private Long selectedId;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users,null);
        final LinearLayout btn_add_user = (LinearLayout) view.findViewById(R.id.btn_add_user);

        settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        selectedId = settings.getLong("SelectedUser", -1);

        iv_empty_box = (ImageView) view.findViewById(R.id.iv_empty_box);
        et_empty_box = (TextView) view.findViewById(R.id.tv_empty_box);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_list);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        showUsersList();

        // Swipe users in recyclerview
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerUserTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);


        // Hide "Add user" button when scrolling down
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && btn_add_user.getVisibility() == View.VISIBLE){
                    btn_add_user.animate().translationY(200);
                } else if (dy < 0){
                    btn_add_user.animate().translationY(0);
                }
            }
        });



        btn_add_user.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addUserAlertDialog();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showUsersList();
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        final User toDeleteUser = usersList.get(position);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(toDeleteUser.getName());
        dialogBuilder.setMessage(getString(R.string.delete_user_confirm));
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton(getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (viewHolder instanceof UserAdapter.ViewHolder) {

                            if (toDeleteUser.getId() == selectedId){
                                editor.putLong("SelectedUser", -1);
                                editor.apply();
                            }

                            // remove the item from recycler view
                            toDeleteUser.delete(getActivity());
                            usersList.remove(position);
                            showUsersList();
                        }

                    }
                });
        dialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //pass
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });
        AlertDialog b = dialogBuilder.create();
        b.show();
        b.getButton(b.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorError));
        b.getButton(b.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorText));
    }

    public void addUserAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_user_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setPositiveButton(getActivity().getResources().getString(R.string.done),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                        User user = new User(edt.getText().toString());
                        user.insert(getActivity());

                        selectedId = user.getId();
                        editor.putLong("SelectedUser", selectedId);
                        editor.apply();

                        showUsersList();

            }
        });
        dialogBuilder.setNegativeButton(getActivity().getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

        b.getButton(b.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    public void showUsersList() {
        usersList = User.getUsersList(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new UserAdapter(usersList, selectedId, this);
        mRecyclerView.setAdapter(adapter);

        if (usersList.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            iv_empty_box.setVisibility(View.VISIBLE);
            et_empty_box.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            iv_empty_box.setVisibility(View.GONE);
            et_empty_box.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserClick(int position) {
        long newSelectedId = usersList.get(position).getId();

        if(selectedId == newSelectedId) {
            selectedId = (long) -1;
            editor.putLong("SelectedUser", -1);
        } else {
            selectedId = newSelectedId;
            editor.putLong("SelectedUser", selectedId);
        }

        editor.apply();
        showUsersList();
    }
}

