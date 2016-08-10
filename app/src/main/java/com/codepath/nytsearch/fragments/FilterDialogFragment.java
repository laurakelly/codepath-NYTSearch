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
import android.widget.Button;
import android.widget.CheckBox;

import com.codepath.nytsearch.FilterSettings;
import com.codepath.nytsearch.R;

/**
 * Created by laura_kelly on 8/8/16.
 */
public class FilterDialogFragment extends DialogFragment implements View.OnClickListener {
  public FilterDialogFragment() {}
  public Button btnFilter;
  public CheckBox cbArts;
  public CheckBox cbFashion;
  public CheckBox cbSports;

  public boolean arts;
  public boolean fashion;
  public boolean sports;

  public interface OnClickFilterSaveListener {
    void onClickFiltered(boolean arts, boolean fashion, boolean sports);
  }

  public static FilterDialogFragment newInstance(FilterSettings filterSettings) {
    FilterDialogFragment frag = new FilterDialogFragment();
    Bundle args = new Bundle();
    args.putBoolean("arts", filterSettings.getArts());
    args.putBoolean("sports", filterSettings.getSports());
    args.putBoolean("fashion", filterSettings.getFashion());
    frag.setArguments(args);
    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.filter_dialog, container);
    return view;
  }

  @Override
  public void onClick(View view) {
    OnClickFilterSaveListener listener = (OnClickFilterSaveListener) this.getActivity();

    // Update filter settings based on checkbox values
    cbArts = (CheckBox) getView().findViewById(R.id.cbArts);
    cbFashion = (CheckBox) getView().findViewById(R.id.cbFashion);
    cbSports = (CheckBox) getView().findViewById(R.id.cbSports);

    arts = cbArts.isChecked();
    fashion = cbArts.isChecked();
    sports = cbSports.isChecked();

    listener.onClickFiltered(arts, fashion, sports);
    dismiss();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    btnFilter = (Button) view.findViewById(R.id.btnFilter);
    btnFilter.setOnClickListener(this);
    applySettings(view);
  }

  public void applySettings(View v) {
    arts = getArguments().getBoolean("arts");
    fashion = getArguments().getBoolean("fashion");
    sports = getArguments().getBoolean("sports");

    cbArts = (CheckBox) getView().findViewById(R.id.cbArts);
    cbFashion = (CheckBox) getView().findViewById(R.id.cbFashion);
    cbSports = (CheckBox) getView().findViewById(R.id.cbSports);

    cbArts.setChecked(arts);
    cbFashion.setChecked(fashion);
    cbSports.setChecked(sports);
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
