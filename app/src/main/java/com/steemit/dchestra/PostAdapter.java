package com.steemit.dchestra;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class PostAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PostItem> items;

    public PostAdapter(Context context, ArrayList<PostItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(PostItem item) {
        items.add(item);
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    public void removeItem(PostItem item) {
        items.remove(item);
    }

    public void clearItems() {
        items.clear();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        }

        final View view = convertView;
        PostItem currentItem = (PostItem) getItem(position);

        // Make it fullscreen
        view.getLayoutParams().height = parent.getMeasuredHeight();
        view.requestLayout();

        // Change text, pictures, etc...
        ((TextView) view.findViewById(R.id.text_username)).setText(currentItem.getUsername());
        currentItem.getProfilePicture(new PostItem.BitmapCallback() {
            @Override
            public void onDone(Bitmap bitmap) {
                ((ImageView)view.findViewById(R.id.btn_profile)).setImageBitmap(bitmap);
            }
        });
        view.findViewById(R.id.btn_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rnd = new Random();
                ((ImageView)v).setColorFilter(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),rnd.nextInt(256)));
            }
        });


        return convertView;
    }
}
