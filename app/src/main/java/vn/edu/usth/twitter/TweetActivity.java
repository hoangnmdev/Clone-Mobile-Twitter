package vn.edu.usth.twitter;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


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

    private Button takePhoto;
    Bitmap bitmap;
    ByteArrayOutputStream baos;
    byte[] imageData;
    private ImageView imageChoseView;
    Uri selectedImageUri;
    private StorageReference fileRef;

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

        // Create back button
        Toolbar toolbar = findViewById(R.id.tweetToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        chooseImage = findViewById(R.id.chooseImageButton);
        editText = findViewById(R.id.editTextStatus);
        imageChoseView = findViewById(R.id.tweetImageView);
        char_count = findViewById(R.id.textViewCharacterCount);
        editText.addTextChangedListener(mTextEditorWatcher);
        takePhoto = findViewById(R.id.takePhotoButton);

        if (ContextCompat.checkSelfPermission(TweetActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TweetActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
    }

    void imageChooser() {
        // Create an instance of the intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        // Pass the constant to compare it with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (requestCode == 100) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageChoseView.setImageBitmap(bitmap);
                        }
                    });

                    baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageData = baos.toByteArray();
                }
                if (resultCode == RESULT_OK) {
                    // Compare the resultCode with the SELECT_PICTURE constant
                    if (requestCode == SELECT_PICTURE) {
                        // Get the URL of the image from data
                        selectedImageUri = data.getData();
                        imageChoseView.setImageURI(null);
                        if (selectedImageUri != null) {
                            // Update the preview image in the layout
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageChoseView.setImageURI(selectedImageUri);
                                    imageChoseView.setBackgroundColor(R.drawable.black_image);
                                }
                            });

                        }
                    }
                }
            }
        }).start();
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Set a textview to the current length
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!statusText.isEmpty()) {
                    // Check if an image is selected
                    if (selectedImageUri != null) {
                        // Upload the selected image to Firebase Storage
                        uploadImageToStorage(statusText);
                    } else if (imageData != null) {
                        uploadImageToStorageBitmap(statusText);
                    } else {
                        // No image selected, proceed with adding the post without an image
                        addPostToDb(statusText, null);
                    }
                } else {
                    editText.setError("Cannot be empty!");
                }
            }


        }).start();

    }

    private void uploadImageToStorageBitmap(String statusText) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageData = baos.toByteArray();

        // Create a reference to the Firebase Storage location where you want to store the image
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        // Upload the image data to Firebase Storage
        UploadTask uploadTask = fileRef.putBytes(imageData);

        uploadTask.addOnFailureListener(e -> {
            // Handle the error, e.g., show a toast message
            Log.e(TAG, "Image upload failed: " + e.getMessage());
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully, get the download URL
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                // Now you can use 'imageUrl' to store in your database or display the image
                Log.d(TAG, "Image URL: " + imageUrl);
                addPostToDb(statusText, imageUrl);

            });
        }).addOnProgressListener(taskSnapshot -> {
            // Calculate and display the upload progress here if needed
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            Log.d(TAG, "Upload progress: " + progress + "%");
        });
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
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(TweetActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error in case of a database write failure
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TweetActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TweetActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}