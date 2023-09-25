package vn.edu.usth.twitter;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference userRef;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    TextView textViewUserName,textViewUserTagname;

    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        userRef = database.getReference("Users");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userEmail = currentUser.getEmail();
        textViewUserName = findViewById(R.id.profile_user_name);
        textViewUserTagname = findViewById(R.id.profile_user_tagname);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String dbEmail = dataSnapshot.child("email").getValue(String.class);
                    if (dbEmail != null && dbEmail.equals(userEmail)) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        String userTagname = dataSnapshot.child("tagName").getValue(String.class);

                        // Set the TextViews with user data
                        textViewUserName.setText(userName);
                        textViewUserTagname.setText(userTagname);

                        // You may break out of the loop since you found the user
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        PagerAdapter adapter = new HomeFragmentPagerAdapter(
                getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(5);
        pager.setAdapter(adapter);

        TabLayout tabLayout =  findViewById(R.id.header);
        tabLayout.setupWithViewPager(pager);
    }
    @Override

    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 5;
        private final String[] titles = new String[] { "Post", "Replies", "Highlight", "Media", "Likes" };
        public HomeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return PAGE_COUNT; // number of pages for a ViewPager
        }
        @Override
        public Fragment getItem(int page) {
// returns an instance of Fragment corresponding to the specified page
            switch (page) {
                case 0: return new vn.edu.usth.twitter.PostProfileFragment();
                case 1: return new vn.edu.usth.twitter.RepliesProfileFragment();
                case 2: return new vn.edu.usth.twitter.PostProfileFragment();
                case 3: return new vn.edu.usth.twitter.MediaProfileFragment();
                case 4: return new vn.edu.usth.twitter.LikesProfileFragment();
            }
            return new Fragment(); // failsafe
        }
        @Override
        public CharSequence getPageTitle(int page) {
// returns a tab title corresponding to the specified page
            return titles[page];
        }
    }


}
