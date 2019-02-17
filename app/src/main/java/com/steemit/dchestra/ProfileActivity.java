package com.steemit.dchestra;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.steemit.dchestra.steem.SteemUser;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle b = getIntent().getExtras();

        SteemUser steemUser = SteemUser.getSteemUser(b.getInt("userIndex", 0));

        ((TextView)findViewById(R.id.profile_title)).setText(steemUser.getDisplayName());
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        steemUser.getProfilePicture(new SteemUser.BitmapCallback() {
            @Override
            public void onDone(Bitmap bitmap) {
                ((ImageView)findViewById(R.id.profile_picture)).setImageBitmap(bitmap);
            }
        });

    }
}
