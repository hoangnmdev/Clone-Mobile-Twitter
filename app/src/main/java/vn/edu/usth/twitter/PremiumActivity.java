package vn.edu.usth.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

public class PremiumActivity extends AppCompatActivity {

    private TextView monthly;
    private TextView yearly;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        Toolbar toolbar = findViewById(R.id.toolbar_premium);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Find your option TextViews by their IDs
        monthly = findViewById(R.id.monthly);
        yearly = findViewById(R.id.yearly);

        // Set click listeners for the options
        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle option 1 selection
                monthly.setSelected(true);
                yearly.setSelected(false);

                // Add your logic for option 1 here

                // Optionally, you can change the text color or background color to indicate selection
                monthly.setTextColor(getResources().getColor(R.color.black));
                yearly.setTextColor(getResources().getColor(R.color.white));
            }
        });

        yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle option 2 selection
                monthly.setSelected(false);
                yearly.setSelected(true);

                // Add your logic for option 2 here

                // Optionally, you can change the text color or background color to indicate selection
                yearly.setTextColor(getResources().getColor(R.color.black));
                monthly.setTextColor(getResources().getColor(R.color.white));
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button click here by starting MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}