package vn.edu.usth.twitter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "Twitter";

    private String userName,userTagname,dbEmail;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNav;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private FloatingActionButton fab ;

    private ImageButton imageButton;
    String userEmail;
    private final int[] tabIcons = {
            R.drawable.home_icon,
            R.drawable.search_icon,
            R.drawable.notification_icon,
            R.drawable.inbox_icon,
    };

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewsfeedFragment()).commit();
        /*----------------Hooks-----------------*/
        bottomNav = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0); // Get the first (and usually only) header view
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        String userEmail = currentUser.getEmail();
        TextView nav_userName =  headerView.findViewById(R.id.nav_username);
        TextView nav_userTagname = headerView.findViewById(R.id.nav_usertagname);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("Users");
        new Thread(new Runnable() {
            @Override
            public void run() {
                userRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dbEmail = dataSnapshot.child("email").getValue(String.class);

                            if (dbEmail != null && dbEmail.equals(userEmail)) {
                                userName = dataSnapshot.child("name").getValue(String.class);
                                userTagname = dataSnapshot.child("tagName").getValue(String.class);
                                String profileImageReference = dataSnapshot.child("AvatarImage").getValue(String.class);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Picasso.get().load(profileImageReference).into(imageButton);
                                        nav_userName.setText(userName);
                                        nav_userTagname.setText(userTagname);
                                    }
                                });
                                // Set the TextViews with user data


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
            }
        }).start();

        /*----------------Tool Bar-----------------*/
        setSupportActionBar(toolbar);

        /*---------Hide title in tool bar--------*/
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        /*---------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //------------------listen to item click ----------------//
        navigationView.setNavigationItemSelectedListener(this);


        //------------------Click on profile image in navigation view to open the profile activity----------//

        imageButton = headerView.findViewById(R.id.profile_image_nav);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                // Start a new activity or perform any desired action
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        /*-------Click on floating action button to open tweet activity---------*/
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TweetActivity.class)));
        bottomNav.setOnItemSelectedListener(navListener);

    }


    /*--------Set icon to tablayout(Not used yet)------*/
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }


    //-------------------Open activity by click on menu's item---------------//
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.profile){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }
        if (id == R.id.premium){
            startActivity(new Intent(MainActivity.this,PremiumActivity.class));
        }
        if(id == R.id.bookmarks){
            startActivity(new Intent(MainActivity.this, BookmarksActivity.class));
        }
        if(id == R.id.lists){
            startActivity(new Intent(MainActivity.this, ListsActivity.class));
        }
        if(id == R.id.logout_item){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        return true;
    }

    /*----------------Change fragment when click on bottomnavigationview's item---------------*/
    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {
        //-------- Change display fragment-------//
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.to_newsfeed_btn){
            fab.show();
            selectedFragment = new NewsfeedFragment();
        }
        if (itemId == R.id.to_search_btn) {
            fab.hide();
            selectedFragment = new SearchFragment();
        }
        if (itemId == R.id.to_notification_btn){
            fab.hide();
            selectedFragment = new NotificationFragment();
        }
        if (itemId == R.id.to_message_btn){
            fab.hide();
            selectedFragment = new MessageFragment();
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        return true;
    };

    /*----Click heart button to react to post----*/
    private boolean liked = false;
    public void reactPost(View view){
        ImageButton button = (ImageButton) view;
        int icon;

        if (liked) {
            liked = false;
            icon = R.drawable.heart;
        }
        else {
            liked = true;
            icon = R.drawable.red_heart;
        }

        button.setBackgroundResource(icon);

    }

    /*----Click bookmark to react to save to bookmark----*/
    private boolean bookmark = false;
    public void bookmarkPost(View view){
        ImageButton button = (ImageButton) view;
        int icon;

        if (bookmark) {
            bookmark = false;
            icon = R.drawable.bookmark;
        }
        else {
            bookmark = true;
            icon = R.drawable.bookmarked;
        }

        button.setBackgroundResource(icon);

    }


}