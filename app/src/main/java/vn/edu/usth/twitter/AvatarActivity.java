package vn.edu.usth.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class AvatarActivity extends AppCompatActivity {
    private static final String TAG = "AvatarActivity";
    private static final int SELECT_PICTURE = 200;
    private StorageReference storageRef;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        Toolbar toolbar = findViewById(R.id.avatarToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Initialize Firebase components
            database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
            userRef = database.getReference("Users");
            auth = FirebaseAuth.getInstance();
            currentUser = auth.getCurrentUser();
            userEmail = currentUser.getEmail();
            storageRef = FirebaseStorage.getInstance().getReference("uploads_avatar");

            // Create back button
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            chooseImage = findViewById(R.id.chooseImageAvatarButton);
            imageChoseView = findViewById(R.id.avatarImageView);
            takePhoto = findViewById(R.id.takePhotoAvatarButton);

            if (ContextCompat.checkSelfPermission(AvatarActivity.this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AvatarActivity.this, new String[]{
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



        private String getFileExtension(Uri uri) {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }

        public void ChangeAvatar(View view) {
            imageChoseView = findViewById(R.id.avatarImageView);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (imageChoseView.getDrawable() != null) {

                        // Check if an image is selected
                        if (selectedImageUri != null) {
                            // Upload the selected image to Firebase Storage
                            uploadImageToStorage();
                        } else if (imageData != null) {
                            uploadImageToStorageBitmap();
                        }
                    } else {
                        Toast.makeText(AvatarActivity.this, "Warning: There is no image!", Toast.LENGTH_SHORT).show();

                    }
                }


            }).start();

        }

        private void uploadImageToStorageBitmap() {
            selectedImageUri = null;
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
                    addAvatarToDb( imageUrl);

                });
            }).addOnProgressListener(taskSnapshot -> {
                // Calculate and display the upload progress here if needed
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload progress: " + progress + "%");
            });
        }

        private void uploadImageToStorage() {
            // Create a reference to the Firebase Storage location where you want to store the image
            fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(selectedImageUri));

            // Upload the selected image to Firebase Storage
            fileRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded image
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            // Once you have the image URL, add the post to the database
                            addAvatarToDb( imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error if image upload fails
                        Toast.makeText(AvatarActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

    private void addAvatarToDb(String imageUrl) {
        userRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class); // Change dataSnapshot to userSnapshot here
                    Log.i(TAG, dbEmail);
                    if (dbEmail.equals(userEmail)) {
                        HashMap<String, Object> postMap = new HashMap<>();
                        postMap.put("AvatarImage", imageUrl);
                        userSnapshot.getRef().updateChildren(postMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(AvatarActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AvatarActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AvatarActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



}