package com.example.lance.btcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Lance on 2017/7/27.
 */

public class NoneFileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View noneFileFragment = inflater.inflate(R.layout.none_file_fragment_layout, container, false);
        noneFileFragment.findViewById(R.id.none_file_fragment_text_view)
                .setOnClickListener(((MainActivity) getActivity()));
        return noneFileFragment;
    }

}
