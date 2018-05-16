package com.example.neerajsewani.book_listing_app;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

/**
 * @param "AsyncTaskLoader" takes a parameter to operate upon
 *                        in this case we are passing a QUERY from which we are getting raw json response
 *                        from the loadInBackground method
 */
public class CustomLoader extends AsyncTaskLoader<String> {

    private String query;
    private String minResults;
    CustomLoader(Context context, String query, String minResults){
        super(context);
        this.query = query;
        this.minResults = minResults;

    }
    @Nullable
    @Override
    public String loadInBackground() {
        return Utils.getInfo(query, minResults);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }
}
