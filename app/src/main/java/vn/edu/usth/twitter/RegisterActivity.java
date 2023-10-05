package vn.edu.usth.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;

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
    CheckBox showPasswordCheckbox;

    User user;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference mDatabase = database.getReference("Users");

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
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
        // Initialize CheckBox
        showPasswordCheckbox = findViewById(R.id.passwordCheckbox);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle password visibility
                if (isChecked) {
                    editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Move cursor to the end of the text
                editTextPassword.setSelection(editTextPassword.getText().length());
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
                tagName = "@" + String.valueOf(editTextTagname.getText());

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(userName)) {
                    userName = "user_name";
                }

                if (TextUtils.isEmpty(tagName)) {
                    tagName = "@tag_name";
                }

                user = new User(email, password, userName, tagName);
                registerUser(email, password);
            }
        });
    }

    private void registerUser(String email, String password) {
        String passwordError = validatePassword(password);

        // Check if the password is valid
        if (passwordError != null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(RegisterActivity.this, passwordError, Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            editTextEmail.setText("");
                            editTextPassword.setText("");
                            editTextUserName.setText("");
                            editTextTagname.setText("");
                            Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to create account.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password should be at least 8 characters long.";
        }

        if (!containsUppercase(password) || !containsLowercase(password) || !containsDigit(password)) {
            return "Password should contain at least one uppercase letter, one lowercase letter, and one digit.";
        }

        return null; // Password is valid
    }

    private boolean containsUppercase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLowercase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDigit(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public void updateUI(FirebaseUser currentUser) {
        String keyId = mDatabase.push().getKey();
        mDatabase.child(keyId).setValue(user);
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }
}
