package com.codepath.nytsearch;

import org.parceler.Parcel;

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

  private boolean arts;
  private boolean fashion;
  private boolean sports;

  public FilterSettings() {
    this.arts = false;
    this.fashion = false;
    this.sports = false;
  }
}
