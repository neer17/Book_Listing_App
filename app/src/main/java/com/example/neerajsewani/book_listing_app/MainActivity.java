package com.example.neerajsewani.book_listing_app;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?";

    EditText titleTextView;
    Button searchButton;
    ListView listView;


    String query = "";
    private ArrayList<String> authorsList = new ArrayList<>();
    private ArrayList<BooksDetails> booksDetails = new ArrayList<>();
    private String title = null, id = null;

    private CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        titleTextView = findViewById(R.id.author_textView);
        searchButton = findViewById(R.id.search);
        listView = findViewById(R.id.list_view);

        /**
         * this declaration is needed
         */
          adapter = new CustomAdapter(this, new ArrayList<BooksDetails>());

         ListView listView = findViewById(R.id.list_view_in_main_layout);

         listView.setAdapter(adapter);

        /**
         *  initLoader ensures a loader is initialized and active
         *  same loader will be active when device is rotated
         */
        if(getSupportLoaderManager().getLoader(0) != null)
            getSupportLoaderManager().initLoader(0, null, this);

        //  Search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  getting value from textView
                query = titleTextView.getText().toString();
                Log.d(TAG, "onClick: "+query);

                //  checking the network connection
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                assert connectivityManager != null;
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if(networkInfo != null && networkInfo.isConnected()) {
                    if(query.length() != 0) {
                        /**
                         *  starting a loader here
                         *  restartLoader method starts/restarts a loader and register the callbacks to it
                         *  when we rotate the device a new activity is created and restartLoader starts a new loader
                         *  thats why data is lost on rotation
                         *  */
                        Bundle bundle = new Bundle();
                        bundle.putString("queryString", query);

                        getSupportLoaderManager().restartLoader(0, bundle, MainActivity.this);
                        closeKeyboard();
                    }
                    else
                        Toast.makeText(MainActivity.this, "Enter a title", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "Connect to a network first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void closeKeyboard(){
        //  hiding keyboard after the search
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        View focusedView = this.getCurrentFocus();
        if(focusedView != null) {
            try {
                assert inputMethodManager != null;

            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }catch (AssertionError e){
                Log.e(TAG, "closeKeyboard: Assertion error is thrown inputMethodManager is null", e);
            }
        }
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader onCreateLoader(int id, @Nullable Bundle args) {
        //  getting the SharedPreference which contains all the values of the Preference

        //  SharedPreference for maxResults
        SharedPreferences maxResultsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String maxResults = maxResultsSharedPreferences.getString(getString(R.string.settings_default_result_key),
                getString(R.string.settings_default_value_of_result));

        //  SharedPreference for OrderBy
        SharedPreferences orderBySharedPrference = PreferenceManager
        .getDefaultSharedPreferences(this);

        String orderByValue = orderBySharedPrference.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));


        //  passing the minResults with query
        return new CustomLoader(this, args.getString("queryString"), maxResults, orderByValue);
    }


    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader loader, String s) {

        //  after getting the raw json we are parsing it here
        try {
            JSONObject rootObject = new JSONObject(s);
            JSONArray itemsArray = rootObject.getJSONArray("items");

            for(int i = 0;i < itemsArray.length();i++){
                //  getting each object in the array itemsArray
                JSONObject booksInfo = itemsArray.getJSONObject(i);

                //  getting book id
                id = booksInfo.getString("id");
                Log.d(TAG, "onPostExecute: ID of the book "+id);

                //  getting the volumeInfo object
                JSONObject volumeInfo = booksInfo.getJSONObject("volumeInfo");

                //  getting title of the book
                title = volumeInfo.getString("title");
                Log.d(TAG, "onPostExecute: Title of the book "+title);

                //  getting the author

                JSONArray author = volumeInfo.getJSONArray("authors");

                //  getting all the members of author array
                for(int j = 0;j < author.length();j++){
                    authorsList.add(author.getString(j));
                    Log.d(TAG, "onPostExecute: Author name is "+authorsList.get(i));
                }

                if(title == null) {
                    //  if no title matches
                    titleTextView.setText("No title matches");
                }

                //   adding details in the ArrayList bookDetails
                booksDetails.add(new BooksDetails(id, title, authorsList.get(0)));
            }

            //  using ListView with custom adapter to display the results
            if(!adapter.isEmpty())
                adapter.clear();
            else {
                adapter.addAll(booksDetails);
            }
        }
        catch(Exception e){
            Log.e(TAG, "onPostExecute: exception in json parsing", e);
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader loader) {
        //  clearing data from adapter while resetting the loader
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //  helper method to clear contents of adapter
    private void clearAdapter(CustomAdapter adapter){
        if(adapter != null)
            adapter.clear();
    }
}
