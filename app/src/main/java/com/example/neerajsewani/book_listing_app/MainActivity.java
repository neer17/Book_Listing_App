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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "MainActivity";
    
    private static final int LOADER_INIT_KEY = 0;

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

         final ListView listView = findViewById(R.id.list_view_in_main_layout);

         listView.setAdapter(adapter);

         // getting instance of SharedPreference and attaching listener to it
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //  any change in the SharedPreference will get notified
        //  if user tweaks any settings
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        //  Search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  getting value from textView
                query = titleTextView.getText().toString();

                Log.d(TAG, "onClick: "+query);

                //  clearing the adapter
                adapter.clear();

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

                        getSupportLoaderManager().initLoader(LOADER_INIT_KEY, null, MainActivity.this);
                        closeKeyboard();
                        Log.d(TAG, "onClick: ");
                    }
                    else
                        Toast.makeText(MainActivity.this, "Enter a title", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
                }

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

        Log.d(TAG, "onCreateLoader: ");
        //  getting the SharedPreference which contains all the values of the Preference

        //  SharedPreference for maxResults
        SharedPreferences maxResultsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String maxResults = maxResultsSharedPreferences.getString(getString(R.string.settings_default_result_key),
                getString(R.string.settings_default_value_of_result));

        //  SharedPreference for OrderBy
        SharedPreferences orderBySharedPreference = PreferenceManager
        .getDefaultSharedPreferences(this);

        String orderByValue = orderBySharedPreference.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));


        //  passing the minResults with query
        return new CustomLoader(this, query, maxResults, orderByValue);
    }


    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader loader, String s) {
        Log.d(TAG, "onLoadFinished: ");
        
        //  after getting the raw json we are parsing it here
        try {
            JSONObject rootObject = new JSONObject(s);
            JSONArray itemsArray = rootObject.getJSONArray("items");

            for(int i = 0;i < itemsArray.length();i++){
                //  getting each object in the array itemsArray
                JSONObject booksInfo = itemsArray.getJSONObject(i);

                //  getting book id
                id = booksInfo.getString("id");

                //  getting the volumeInfo object
                JSONObject volumeInfo = booksInfo.getJSONObject("volumeInfo");

                //  getting title of the book
                title = volumeInfo.getString("title");
                //  getting the author

                JSONArray author = volumeInfo.getJSONArray("authors");

                //  getting all the members of author array
                for(int j = 0;j < author.length();j++){
                    authorsList.add(author.getString(j));
                }

                if(title == null) {
                    //  if no title matches
                    titleTextView.setText("No title matches");
                }

                //   adding details in the ArrayList bookDetails
                booksDetails.add(new BooksDetails(id, title, authorsList.get(0)));

            }

            adapter.addAll(booksDetails);
        }
        catch(Exception e){
            Log.e(TAG, "onLoadFinished: exception in json parsing", e);
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader loader) {
        Log.d(TAG, "onLoaderReset: ");
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: ");

        if(key.equals(getString(R.string.settings_order_by_key)) || key.equals(getString(R.string.settings_default_result_key))){

            getSupportLoaderManager().restartLoader(LOADER_INIT_KEY, null, this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        adapter.clear();
    }
}
