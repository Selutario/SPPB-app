package com.example.sppb_tfg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.sppb_tfg.Constants.SELECTED_USER;


public class FragmentUsers extends Fragment implements RecyclerUserTouchHelper.RecyclerUserTouchHelperListener, UserAdapter.clickListener {

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView iv_empty_box;
    private TextView et_empty_box;
    private ArrayList<User> usersList;
    private UserAdapter adapter;
    private Long selectedId;

    public FragmentUsers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, null);
        final LinearLayout btn_add_user = (LinearLayout) view.findViewById(R.id.btn_add_user);

        // Get selected user to save future test results, if any
        settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        selectedId = settings.getLong(SELECTED_USER, -1);

        iv_empty_box = (ImageView) view.findViewById(R.id.iv_empty_box);
        et_empty_box = (TextView) view.findViewById(R.id.tv_empty_box);

        // Set recyclerview and link it with adapter, to show list of users saved on DB
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

                if (dy > 0 && btn_add_user.getVisibility() == View.VISIBLE) {
                    btn_add_user.animate().translationY(200);
                } else if (dy < 0) {
                    btn_add_user.animate().translationY(0);
                }
            }
        });


        // Open AlertDialog to write name of new user
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

    // Show alertdialog message to confirm delete of user when swiped, and if confirmed, delete.
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

                            if (toDeleteUser.getId() == selectedId) {
                                editor.putLong(SELECTED_USER, -1);
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

    // Open AlertDialog to write name of new user
    public void addUserAlertDialog() {
        Intent intent = new Intent(getActivity(), AddUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    // Update user list in recyclerview
    public void showUsersList() {
        usersList = User.getUsersList(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new UserAdapter(usersList, selectedId, this);
        mRecyclerView.setAdapter(adapter);

        if (usersList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            iv_empty_box.setVisibility(View.VISIBLE);
            et_empty_box.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            iv_empty_box.setVisibility(View.GONE);
            et_empty_box.setVisibility(View.GONE);
        }
    }

    // Open score fragment and send corresponding user ID
    public void openDetails(Long userId) {
        ScoreFragment scoreFragment = new ScoreFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(SELECTED_USER, userId);
        scoreFragment.setArguments(bundle);
        openFragment(scoreFragment);
    }

    // Open user details when clicked
    @Override
    public void onUserClick(int position) {
        openDetails(usersList.get(position).getId());
    }

    // Select/unselect user to save future tests results.
    @Override
    public boolean onUserLongClick(int position) {
        long newSelectedId = usersList.get(position).getId();

        if (selectedId == newSelectedId) {
            selectedId = (long) -1;
            editor.putLong(SELECTED_USER, -1);
        } else {
            selectedId = newSelectedId;
            editor.putLong(SELECTED_USER, selectedId);
        }

        editor.apply();
        showUsersList();

        return true;
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.main_placeHolder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

