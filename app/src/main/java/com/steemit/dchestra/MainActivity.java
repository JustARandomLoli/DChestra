package com.steemit.dchestra;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;

import com.steemit.dchestra.post.PostAdapter;
import com.steemit.dchestra.steem.SteemPost;
import com.steemit.dchestra.post.PostList;
import com.steemit.dchestra.steem.SteemUser;
import com.steemit.dchestra.steem.Steemd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        // Get the ListView & fill it.
        final ListView listView = (PostList) findViewById(R.id.list_post);

        try {
            // Get newest posts
            Steemd.sendAPIRequest(this, "https://api.steemit.com", "tags_api.get_discussions_by_created", new JSONObject().put("limit", 16), new Steemd.Receiver() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        ArrayList<String> usernames = new ArrayList<>();
                        final JSONArray result = response.getJSONArray("result");
                        for(int i = 0; i < result.length(); i++) {
                            JSONObject post = result.getJSONObject(i);
                            System.out.println(post.toString(2));
                            if(!usernames.contains(post.getString("author")))
                                usernames.add(post.getString("author"));
                        }

                        // Create a list of Posts and display them.
                        SteemUser.getSteemUser(MainActivity.this, new SteemUser.SteemUserCallback() {
                            @Override
                            public void onResult(SteemUser[] users) {
                                ArrayList<SteemPost> items = new ArrayList<>();
                                for(int i = 0; i < result.length(); i++) {
                                    try {
                                        JSONObject post = result.getJSONObject(i);
                                        for(SteemUser user : users) {
                                            if(user.getUsername().equalsIgnoreCase(post.getString("author"))) {
                                                System.out.println("@" + user.getUsername() + " - " + user.getProfilePictureURL());
                                                items.add(new SteemPost(MainActivity.this, user));
                                            }
                                        }
                                    } catch(JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                final PostAdapter postAdapter = new PostAdapter(MainActivity.this, items);
                                listView.setAdapter(postAdapter);
                            }
                        }, usernames.toArray(new String[0]));

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

}
