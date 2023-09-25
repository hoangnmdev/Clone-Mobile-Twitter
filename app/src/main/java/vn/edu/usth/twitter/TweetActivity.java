package vn.edu.usth.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TweetActivity extends AppCompatActivity {
    String TAG= "TweetActivity";
    EditText editText;
    TextView char_count;
    String userName,userTagname,userEmail;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference myRef = database.getReference("Post");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        userEmail = currentUser.getEmail();

        //------create back button------//
        Toolbar toolbar = findViewById(R.id.tweetToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Log.i(TAG,userEmail);

        editText = findViewById(R.id.editTextStatus);
        char_count = findViewById(R.id.textViewCharacterCount);
        editText.addTextChangedListener(mTextEditorWatcher);



    }
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            char_count.setText(s.length() + "/280");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void Tweet(View view) {
        editText = findViewById(R.id.editTextStatus);
        String statusText = editText.getText().toString();

        if (!statusText.isEmpty()) {
            // Create and start a new background thread to add the post to the database
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    addPostToDb(statusText);

                    // After completing the database operation, you can navigate to the MainActivity
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(TweetActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });
            t.start();
        } else {
            editText.setError("Cannot be empty!");
        }
    }

    private void addPostToDb(String statusText) {
        // Create a new post entry
        String key = myRef.push().getKey();
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("UserName", userName);
        postMap.put("UserProfileImage", "user6");
        postMap.put("UserId", userTagname);
        postMap.put("Content", statusText);
        postMap.put("ContentImage", "myuserpost");

        // Set the value in the database and attach listeners
        myRef.child(key).setValue(postMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Database write was successful
                        Intent intent = new Intent(TweetActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error in case of a database write failure
                        Toast.makeText(TweetActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Retrieve user information from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String dbEmail = dataSnapshot.child("email").getValue(String.class);

                    if (dbEmail != null && dbEmail.equals(userEmail)) {
                        userName = dataSnapshot.child("name").getValue(String.class);
                        userTagname = dataSnapshot.child("tagName").getValue(String.class);
                        Log.i(TAG, userName);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }



}