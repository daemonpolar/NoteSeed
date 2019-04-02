package com.f0x1d.notes.utils.bottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.List;

public class CustomBottomSheet extends BottomSheetDialogFragment {

    public static CustomBottomSheet newInstance(List<Element> elements) {
        Bundle args = new Bundle();
        args.putSerializable("elems", (Serializable) elements);

        CustomBottomSheet fragment = new CustomBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_with_recycler, null);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);

        LinearLayout layout = v.findViewById(R.id.background);

        if (UselessUtils.ifCustomTheme())
            layout.setBackgroundColor(ThemesEngine.background);
        else if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", true))
            layout.setBackgroundColor(getActivity().getResources().getColor(R.color.statusbar));
        else
            layout.setBackgroundColor(Color.WHITE);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(new BottomSheetItemsAdapter((List<Element>) getArguments().getSerializable("elems")));

        dialog.setContentView(v);
    }
}