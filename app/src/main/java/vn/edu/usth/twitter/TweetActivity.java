package vn.edu.usth.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class TweetActivity extends AppCompatActivity {
    Button tweetbtn;
    EditText editText;
    TextView char_count;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        //------create back button------//

        Toolbar toolbar = findViewById(R.id.tweetToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Button buttonPost = findViewById(R.id.tweetButton);
       /* buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });*/

        editText = findViewById(R.id.editTextStatus);
        char_count = findViewById(R.id.textViewCharacterCount);
        editText.addTextChangedListener(mTextEditorWatcher);


    }
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            char_count.setText(String.valueOf(s.length()) + "/280");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void Tweet(View view) {
        editText = findViewById(R.id.editTextStatus);
        String statusText = editText.getText().toString(); // Get the text from the EditText field

        if (!statusText.isEmpty()) {
            // Initialize Firebase with your Firebase Realtime Database URL
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");

            // Reference to your specific Firebase node
            DatabaseReference myRef = database.getReference("Post"); // Replace with your Firebase node

            // Push data to Firebase with a unique key
            addPostToDb();
            // Clear the EditText field after sending
            Intent intent = new Intent(TweetActivity.this,MainActivity.class);
            startActivity(intent);
        }else {
            editText.setError("Cannot be empty!");
        }
    }
    private void addPostToDb(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("User",R.string.user_name);
        map.put("UserId",R.string.profile_user_tagname);
        map.put("Content",editText.getText().toString());

    }

}