package com.steemit.dchestra.post;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.steemit.dchestra.ProfileActivity;
import com.steemit.dchestra.R;
import com.steemit.dchestra.steem.SteemPost;
import com.steemit.dchestra.steem.SteemUser;

import java.util.ArrayList;
import java.util.Random;

public class PostAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SteemPost> items;

    public PostAdapter(Context context, ArrayList<SteemPost> items) {
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

    public void addItem(SteemPost item) {
        items.add(item);
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    public void removeItem(SteemPost item) {
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
        final SteemPost steemPost = (SteemPost) getItem(position);

        // Make it fullscreen
        view.getLayoutParams().height = parent.getMeasuredHeight();
        view.requestLayout();

        // Making Variables for Views that still need change
        final TextView username = view.findViewById(R.id.text_username);
        final ImageButton profilePicture = view.findViewById(R.id.btn_profile);
        final ImageButton likeButton = view.findViewById(R.id.btn_like);

        // Change text, pictures, etc... //TODO: If a Profile Picture takes long to load, it sometimes get set into the first/currently active/every 2nd? View instead of its own. (Changes back to normal once you swipe out and back into the View)
        username.setText("@" + steemPost.getUser().getUsername());
        profilePicture.setImageResource(R.drawable.circle);
        steemPost.getUser().getProfilePicture(new SteemUser.BitmapCallback() {
            @Override
            public void onDone(Bitmap bitmap) {
                profilePicture.setImageBitmap(bitmap);
            }
        });

        // Clicked on Profile Picture
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                Bundle b = new Bundle();
                b.putInt("userIndex", steemPost.getUser().getIndex());
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });

        likeButton.setColorFilter(steemPost.getColor());
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Random rnd = new Random();
                final int newColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),rnd.nextInt(256));
                final int prevColor = steemPost.getColor();
                ValueAnimator animator = new ValueAnimator();
                animator.setFloatValues(0f, 1f);
                animator.setDuration(500L);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ((ImageView)v).setColorFilter(ColorUtils.blendARGB(prevColor, newColor, animation.getAnimatedFraction()));
                    }
                });
                animator.start();
                steemPost.setColor(newColor);

            }
        });


        return convertView;
    }
}
