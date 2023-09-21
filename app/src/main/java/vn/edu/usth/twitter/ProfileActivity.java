package vn.edu.usth.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private boolean liked = false;
    public void reactPost(View view){
        ImageButton button = (ImageButton) view;
        int icon;

        if (liked) {
            liked = false;
            icon = R.drawable.heart;
        }
        else {
            liked = true;
            icon = R.drawable.red_heart;
        }

        button.setBackgroundResource(icon);

    }
    private boolean bookmark = false;
    public void bookmarkPost(View view){
        ImageButton button = (ImageButton) view;
        int icon;

        if (bookmark) {
            bookmark = false;
            icon = R.drawable.bookmark;
        }
        else {
            bookmark = true;
            icon = R.drawable.bookmarked;
        }

        button.setBackgroundResource(icon);

    }
}