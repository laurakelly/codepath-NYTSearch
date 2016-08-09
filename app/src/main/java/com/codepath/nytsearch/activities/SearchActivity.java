package com.codepath.nytsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.codepath.nytsearch.Article;
import com.codepath.nytsearch.ArticleArrayAdapter;
import com.codepath.nytsearch.R;
import com.codepath.nytsearch.fragments.FilterDialogFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
  GridView gvResults;
  MenuItem actionFilter;

  ArrayList<Article> articles;
  ArticleArrayAdapter adapter;


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

    gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);

        Article article = articles.get(position);
        i.putExtra("article", article);
        startActivity(i);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_search, menu);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

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

  public void onArticleSearch(String query) {
    AsyncHttpClient client = new AsyncHttpClient();

    // TODO REMOVE API KEY
    String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json?";
    RequestParams params = new RequestParams();
    params.put("api-key", "ca637ede91f44c13a9681a003f4e386f");
    params.put("page", 0);
    params.put("q", query);

    client.get(url, params, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d("DEBUG", response.toString());
        JSONArray articleJsonResults = null;

        try {
          articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
          adapter.addAll(Article.fromJSONArray(articleJsonResults));
          Log.d("DEBUG", articles.toString());
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
    FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance();
    filterDialogFragment.show(fm, "filter_fragment");
  }
}
