package vn.edu.usth.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UpdateProfile extends AppCompatActivity {
    EditText editName, editTagName;
//    Button saveButton;
    String nameUser, tagNameUser;
    DatabaseReference reference;

    private DatabaseReference userRef;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userEmail = currentUser.getEmail();

        // Create back button
        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        editName = findViewById(R.id.editTextName);
        editTagName = findViewById(R.id.editTextTagName);
//        saveButton = findViewById(R.id.saveButton);

        showData();

        // Add a button or event listener to trigger the update
        Button updateNameButton = findViewById(R.id.saveButton);
        updateNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = editName.getText().toString();
                String newTagName = editTagName.getText().toString();
                if (!newName.isEmpty() || !newTagName.isEmpty()) {
                    // Call a method to update the name in the database
                    updateNameInDatabase(newName);
                    updateTagNameInDatabase(newTagName);
                } else {
                    Toast.makeText(UpdateProfile.this, "Cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    public boolean isNameChanged(){
//        if (!nameUser.equals(editName.getText().toString())){
//            reference.child(userId).child("name").setValue(editName.getText().toString());
//            nameUser = editName.getText().toString();
//            return true;
//        }else{
//            return false;
//        }
//    }
//
//    public boolean isTagNameChanged(){
//        if (!tagNameUser.equals(editTagName.getText().toString())){
//            reference.child(userId).child("tagName").setValue(editTagName.getText().toString());
//            tagNameUser = editTagName.getText().toString();
//            return true;
//        }else{
//            return false;
//        }
//    }

    public void showData() {
        // Initialize userRef with the correct reference
        userRef = database.getReference("Users");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String dbEmail = dataSnapshot.child("email").getValue(String.class);
                            if (dbEmail != null && dbEmail.equals(userEmail)) {
                                String userName = dataSnapshot.child("name").getValue(String.class);
                                String userTagname = dataSnapshot.child("tagName").getValue(String.class);
                                // Set the TextViews with user data
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        editName.setText(userName);
                                        editTagName.setText(userTagname);
                                    }
                                });
                                // You may break out of the loop since you found the user
                                break;
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    private void updateNameInDatabase(String newName) {
        userRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    if (dbEmail.equals(userEmail)) {
                        HashMap<String, Object> updateMap = new HashMap<>();
                        updateMap.put("name", newName);
                        userSnapshot.getRef().updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Handle the update success
                                Toast.makeText(UpdateProfile.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the update failure
                                Toast.makeText(UpdateProfile.this, "Error updating name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the database error if needed
                Toast.makeText(UpdateProfile.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTagNameInDatabase(String newTagName) {
        userRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    if (dbEmail.equals(userEmail)) {
                        HashMap<String, Object> updateMap = new HashMap<>();
                        updateMap.put("tagName", newTagName);
                        userSnapshot.getRef().updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Handle the update success
                                Toast.makeText(UpdateProfile.this, "Tag name updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the update failure
                                Toast.makeText(UpdateProfile.this, "Error updating tag name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the database error if needed
                Toast.makeText(UpdateProfile.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
