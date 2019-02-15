package com.steemit.dchestra;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    ListView listView;

    @SuppressLint("ClickableViewAccessibility") // Because that blind comment I made
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        // Im trying to comment my stuff properly, but it's just too hard D:
        listView = (PostList) findViewById(R.id.list_post);
        ArrayList<PostItem> items = new ArrayList<>();

        for(int i = 0; i < 16; i++) {
            items.add(new PostItem(this, "@User-" + Math.round(Math.random() * 1000000), "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/06/06cf1771ee270ed5476cfe42c6e172fbd5e2770a_full.jpg"));
        }

        final PostAdapter postAdapter = new PostAdapter(this, items);
        listView.setAdapter(postAdapter);


    }

}
