package com.example.neerajsewani.book_listing_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<BooksDetails> {

    CustomAdapter(Context context, List<BooksDetails> booksDetails){
        super(context, 0, booksDetails);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rootView = convertView;

        if(rootView == null){
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.for_the_list_view, parent, false);
        }

        BooksDetails currentBookDetails = getItem(position);

        TextView id = rootView.findViewById(R.id.id_textView);
        id.setText(currentBookDetails.getId());

        TextView author = rootView.findViewById(R.id.author_textView);
        author.setText(currentBookDetails.getAuthor());

        TextView title = rootView.findViewById(R.id.title_textView);
        title.setText(currentBookDetails.getTitle());

        return rootView;
    }
}
