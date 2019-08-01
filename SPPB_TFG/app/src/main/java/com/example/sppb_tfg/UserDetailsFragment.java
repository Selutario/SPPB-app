package com.example.sppb_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.sppb_tfg.Constants.SELECTED_USER;

public class UserDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_score, null);
        Long id = getActivity().getIntent().getLongExtra(SELECTED_USER, -1);

        User user = User.getUser(getActivity(), id);


        return view;
    }
}
