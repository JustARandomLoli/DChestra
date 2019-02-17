package com.steemit.dchestra.steem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.steemit.dchestra.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class SteemPost {

    private Context context;

    private SteemUser user;
    private int color;

    public SteemPost(Context context, SteemUser user) {
        this.context = context;
        this.user = user;
        this.color = 0xFFF;
    }

    public Context getContext() {
        return context;
    }

    public SteemUser getUser() {
        return user;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
