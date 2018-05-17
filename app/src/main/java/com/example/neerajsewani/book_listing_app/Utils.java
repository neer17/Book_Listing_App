package com.example.neerajsewani.book_listing_app;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

public class Utils {

    private static final String TAG = "Utils";

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q";   //  params for search string
    private static final String MAX_RESULT = "maxResults";   //  param for max results
    private static final String PRINT_TYPE = "printType";    //  param for printType
    private static final String ORDER_BY = "orderBy";   //  param for orderBy


    //  When we pass the quey it will return the json response
    static String getInfo(String queryString, String minResults, String orderByValue){
        HttpURLConnection connection = null;
        String jsonString = null;
        InputStream inputStream = null;
            try{
            //  Building our Uri
            Uri builtUri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULT, minResults)
                    .appendQueryParameter(PRINT_TYPE, "books")
                  //  .appendQueryParameter(ORDER_BY, orderByValue)
                    .build();

            URL requestURL = new URL(builtUri.toString());

                 //  setting up the connection
                 connection = (HttpURLConnection) requestURL.openConnection();
                 connection.setRequestMethod("GET");
                 connection.connect();

                 if (connection.getResponseCode() == 200) {
                     //  Reading the response from the connection
                     inputStream = connection.getInputStream();
                     jsonString = readFromStream(inputStream);
                 }
            }catch (Exception e){
                e.printStackTrace();
            }finally {

                //  closing inputStream and connection
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(connection != null)
                    connection.disconnect();
            }

            //  json string that contains all data
            Log.d(TAG, "jsonResponse  "+jsonString);

            System.out.print(jsonString);
            //  returning the jsonResponse
            return jsonString;
        }


    public static String readFromStream(InputStream inputStream) throws IOException{
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

        String line = null;
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(inputStreamReader);
        line = reader.readLine();

        //  reading the whole json response
        while (line != null) {
            builder.append(line);
            line = reader.readLine();
        }

        if (builder.length() == 0)
            return null;

        return builder.toString();
}
}
