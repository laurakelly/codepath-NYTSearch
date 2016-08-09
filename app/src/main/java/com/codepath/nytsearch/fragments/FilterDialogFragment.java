package com.codepath.nytsearch.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.codepath.nytsearch.R;

/**
 * Created by laura_kelly on 8/8/16.
 */
public class FilterDialogFragment extends DialogFragment{
  public FilterDialogFragment() {}
  public CheckBox cbArts;

  public static FilterDialogFragment newInstance() {
    FilterDialogFragment frag = new FilterDialogFragment();
    Bundle args = new Bundle();
    frag.setArguments(args);
    return frag;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.filter_dialog, container);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    cbArts = (CheckBox) view.findViewById(R.id.cbArts);

  }

  @Override
  public void onResume() {
    // Make dialog the 75% of the screen size
    Window window = getDialog().getWindow();
    Point size = new Point();
    Display display = window.getWindowManager().getDefaultDisplay();
    display.getSize(size);
    window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
    window.setGravity(Gravity.CENTER);

    super.onResume();
  }
}
