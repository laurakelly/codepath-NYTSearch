package com.codepath.nytsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.codepath.nytsearch.Article;
import com.codepath.nytsearch.ArticleArrayAdapter;
import com.codepath.nytsearch.EndlessScrollListener;
import com.codepath.nytsearch.FilterSettings;
import com.codepath.nytsearch.R;
import com.codepath.nytsearch.fragments.FilterDialogFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.OnClickFilterSaveListener {
  GridView gvResults;
  MenuItem actionFilter;

  ArrayList<Article> articles;
  ArticleArrayAdapter adapter;

  FilterSettings filterSettings;
  SearchView searchView;

  private AsyncHttpClient client;
  private String url;
  // TODO REMOVE API KEY
  private String apiKey = "ca637ede91f44c13a9681a003f4e386f";

  public void onClickFiltered(boolean arts, boolean fashion, boolean sports, String order, int day, int month, int year, boolean hasChanged) {
    // preserve selected filters
    filterSettings.setArts(arts);
    filterSettings.setFashion(fashion);
    filterSettings.setSports(sports);
    filterSettings.setOrder(order);
    filterSettings.setDay(day);
    filterSettings.setMonth(month);
    filterSettings.setYear(year);

    if (hasChanged && !searchView.getQuery().equals("")) {
      articles.clear();
      onArticleSearch(searchView.getQuery().toString());
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    setupViews();
  }

  public void setupViews() {
    gvResults = (GridView) findViewById(R.id.gvResults);
    articles = new ArrayList<>();
    adapter = new ArticleArrayAdapter(this, articles);
    gvResults.setAdapter(adapter);
    filterSettings = new FilterSettings();
    client = new AsyncHttpClient();
    url = "http://api.nytimes.com/svc/search/v2/articlesearch.json?";

    gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);

        Article article = articles.get(position);
        i.putExtra("article", Parcels.wrap(article));
        startActivity(i);
      }
    });

    gvResults.setOnScrollListener(new EndlessScrollListener() {
      @Override
      public boolean onLoadMore(int page, int totalItemCount) {
        customLoadMoreDataFromApi(page, searchView.getQuery().toString());
        return true;
      }
    });
  }

  public void customLoadMoreDataFromApi(int offset, String query) {
    RequestParams params = makeRequestParams(offset, query);
    client.get(url, params, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        JSONArray articleJsonResults = null;

        try {
          articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
          adapter.addAll(Article.fromJSONArray(articleJsonResults));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_search, menu);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        onArticleSearch(query);
        searchView.clearFocus();
        return true;
      }

      public boolean onQueryTextChange(String query) {
        return false;
      }
    });

    actionFilter = menu.findItem(R.id.action_filter);
    actionFilter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        onArticleFilter();
        return true;
      }
    });

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_search) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public String formatDate(int day, int month, int year) {
    String formattedDate = String.valueOf(year);

    if (month < 10) {
      formattedDate = formattedDate + "0" + String.valueOf(month);
    } else {
      formattedDate += String.valueOf(month);
    }

    if (day < 10) {
      formattedDate = formattedDate + "0" + String.valueOf(day);
    } else {
      formattedDate += String.valueOf(day);
    }

    return formattedDate;
  }

  public RequestParams makeRequestParams(int page, String query) {
    RequestParams params = new RequestParams();
    params.put("api-key", apiKey);
    params.put("page", String.valueOf(page));
    params.put("q", query);

    if (filterSettings.getDay() != 0) {
      params.put("begin_date", formatDate(filterSettings.getDay(), filterSettings.getMonth(), filterSettings.getYear()));
    }

    ArrayList<String> newsDeskFilters = filterSettings.getNewsDeskFilters();
    if (newsDeskFilters.size() > 0) {
      params.put("fq", "news_desk:(\"" + TextUtils.join("\" \"", newsDeskFilters) + "\")");
    }

    params.put("sort", filterSettings.getOrder());

    return params;
  }

  public void onArticleSearch(String query) {
    RequestParams params = makeRequestParams(0, query);

    client.get(url, params, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d("DEBUG", response.toString());
        JSONArray articleJsonResults = null;

        try {
          articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
          adapter.clear();
          adapter.addAll(Article.fromJSONArray(articleJsonResults));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
      }
    });
  }

  public void onArticleFilter() {
    FragmentManager fm = getSupportFragmentManager();
    FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance(filterSettings);
    filterDialogFragment.show(fm, "filter_fragment");
  }
}
