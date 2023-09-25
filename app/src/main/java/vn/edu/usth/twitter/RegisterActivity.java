package vn.edu.usth.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextUserName, editTextTagname;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    User user;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference mDatabase = database.getReference("Users");
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.registerEmail);
        editTextPassword = findViewById(R.id.registerPassword);
        editTextUserName = findViewById(R.id.registerUserName);
        editTextTagname = findViewById(R.id.registerTagName);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.loginNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, userName, tagName;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf((editTextPassword.getText()));
                userName = String.valueOf(editTextUserName.getText());
                tagName = String.valueOf(editTextTagname.getText());

                if(TextUtils.isEmpty(email)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                    if(TextUtils.isEmpty(userName)){
                    userName = "user_name";
                }
                if(TextUtils.isEmpty(tagName)){
                    tagName = "tag_name";
                }
                user = new User(email,password,userName,tagName);
                registerUser(email,password);
            }
        });
    }

    public void  registerUser(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            editTextEmail.setText("");
                            editTextPassword.setText("");
                            editTextUserName.setText("");
                            editTextTagname.setText("");
                            Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Failed to create account.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }


                });
    }
    public  void updateUI(FirebaseUser currentUser){
        String keyId = mDatabase.push().getKey();
        mDatabase.child(keyId).setValue(user);
        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
    }
}