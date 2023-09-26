package vn.edu.usth.twitter;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;

public class TweetActivity extends AppCompatActivity {
    private static final String TAG = "TweetActivity";
    private static final int SELECT_PICTURE = 200;

    private EditText editText;
    private TextView char_count;
    private String userName, userTagname;
    private StorageReference storageRef;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private DatabaseReference myRef;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String userEmail;
    private Button chooseImage;
    private ImageView imageChoseView;
    Uri selectedImageUri;
    private StorageReference fileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        // Initialize Firebase components
        database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        userRef = database.getReference("Users");
        myRef = database.getReference("Post");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userEmail = currentUser.getEmail();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        //------create back button------//
        Toolbar toolbar = findViewById(R.id.tweetToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Log.i(TAG, userEmail);
        chooseImage = findViewById(R.id.chooseImageButton);
        editText = findViewById(R.id.editTextStatus);
        imageChoseView = findViewById(R.id.tweetImageView);
        char_count = findViewById(R.id.textViewCharacterCount);
        editText.addTextChangedListener(mTextEditorWatcher);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
    }

    void imageChooser() {
        // create an instance of the intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        // pass the constant to compare it with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    imageChoseView.setImageURI(selectedImageUri);
                    imageChoseView.setBackgroundColor(R.drawable.black_image);
                }
            }
        }
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // This sets a textview to the current length
            char_count.setText(s.length() + "/280");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void Tweet(View view) {
        editText = findViewById(R.id.editTextStatus);
        String statusText = editText.getText().toString();

        if (!statusText.isEmpty()) {
            // Check if an image is selected
            if (selectedImageUri != null) {
                // Upload the selected image to Firebase Storage
                uploadImageToStorage(statusText);
            } else {
                // No image selected, proceed with adding the post without an image
                addPostToDb(statusText, null);
            }
        } else {
            editText.setError("Cannot be empty!");
        }
    }

    private void uploadImageToStorage(String statusText) {
        // Create a reference to the Firebase Storage location where you want to store the image
        fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(selectedImageUri));

        // Upload the selected image to Firebase Storage
        fileRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // Once you have the image URL, add the post to the database
                        addPostToDb(statusText, imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle the error if image upload fails
                    Toast.makeText(TweetActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addPostToDb(String statusText, String imageUrl) {
        // Retrieve user information from the database
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String dbEmail = dataSnapshot.child("email").getValue(String.class);
                    Log.i(TAG, dbEmail);
                    if (dbEmail.equals(userEmail)) {
                        userName = dataSnapshot.child("name").getValue(String.class);
                        userTagname = dataSnapshot.child("tagName").getValue(String.class);
                        break;
                    }
                }

                // Create a new post entry
                String key = myRef.push().getKey();
                HashMap<String, Object> postMap = new HashMap<>();
                postMap.put("UserName", userName);
                postMap.put("UserProfileImage", "user6");
                postMap.put("UserId", userTagname);
                postMap.put("Content", statusText);
                postMap.put("ContentImage", imageUrl);

                // Set the value in the database and attach listeners
                myRef.child(key).setValue(postMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Database write was successful
                                runOnUiThread(() -> {
                                    Intent intent = new Intent(TweetActivity.this, MainActivity.class);
                                    startActivity(intent);
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error in case of a database write failure
                                runOnUiThread(() -> {
                                    Toast.makeText(TweetActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
                runOnUiThread(() -> {
                    Toast.makeText(TweetActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
