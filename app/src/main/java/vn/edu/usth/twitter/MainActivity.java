package vn.edu.usth.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "Twitter";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    private FloatingActionButton fab ;
    private int[] tabIcons = {
            R.drawable.home_icon,
            R.drawable.search_icon,
            R.drawable.notification_icon,
            R.drawable.inbox_icon,
    };
    private Adapter pagerAdapter;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*----------------Hooks-----------------*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        /*----------------Tool Bar-----------------*/
        setSupportActionBar(toolbar);

        /*---------Hide app name in tool bar--------*/
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        /*---------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);



        //----------Set MutiView---------------//
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Create an adapter for your ViewPager2
        pagerAdapter = new Adapter(this);

        // Set the adapter to your ViewPager2
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        //---------------Link tabLayout with viewPager--------------//
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Set tab titles here if needed
            tab.setIcon(tabIcons[position]);
        }).attach();
        //------------------listen to item click ----------------//
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //------------------Click on profile image in navigation view to open the profile activity----------//
        View headerView = navigationView.getHeaderView(0); // Get the first (and usually only) header view
        ImageButton imageButton = headerView.findViewById(R.id.profile_image);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                // Start a new activity or perform any desired action
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        //------------------Click on profile image in navigation view to open the profile activity----------//

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TweetActivity.class));
            }
        });


    }


    private void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    //---------------------Open activity after click menu's item---------------//
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