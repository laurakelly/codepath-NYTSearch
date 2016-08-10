package com.codepath.nytsearch;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by laura_kelly on 8/10/16.
 */
@Parcel
public class FilterSettings {
  public boolean getArts() {
    return arts;
  }

  public boolean getFashion() {
    return fashion;
  }

  public boolean getSports() {
    return sports;
  }

  public boolean arts;
  public boolean fashion;
  public boolean sports;

  public void setSports(boolean sports) {
    this.sports = sports;
  }

  public ArrayList<String> getNewsDeskFilters() {
    ArrayList newsDeskFilters = new ArrayList<String>();

    // TODO make constants
    if (this.arts) newsDeskFilters.add("Arts");
    if (this.fashion) newsDeskFilters.add("Fashion & Style");
    if (this.sports) newsDeskFilters.add("Sports");

    return newsDeskFilters;
  }

  public void setArts(boolean arts) {
    this.arts = arts;
  }

  public void setFashion(boolean fashion) {
    this.fashion = fashion;
  }

  public FilterSettings() {
    this.arts = false;
    this.fashion = false;
    this.sports = false;
  }
}
