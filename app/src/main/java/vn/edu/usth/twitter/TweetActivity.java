package vn.edu.usth.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TweetActivity extends AppCompatActivity {
    Button tweetbtn;
    EditText textbox;
    ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        Toolbar toolbar = findViewById(R.id.tweetToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Button buttonPost = findViewById(R.id.buttonPostStatus);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TweetActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });
    }

}