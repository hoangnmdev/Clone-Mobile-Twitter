package vn.edu.usth.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,DataCommunicationListener{

    private static final String TAG = "Twitter";
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNav;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private List<PostItem> postItems;
    private FloatingActionButton fab ;
    private int[] tabIcons = {
            R.drawable.home_icon,
            R.drawable.search_icon,
            R.drawable.notification_icon,
            R.drawable.inbox_icon,
    };
    //private Adapter pagerAdapter;
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
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        /*----------------Tool Bar-----------------*/
        setSupportActionBar(toolbar);

        /*---------Hide title in tool bar--------*/
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        /*---------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
        /*-------Click on floating action button to open tweet activity---------*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TweetActivity.class));
            }
        });
        bottomNav.setOnItemSelectedListener(navListener);
    }



    private void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
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

    @Override
    public void onDataReceived(String text) {
        // Handle the received text data here
        // You can then update the view in the fragment
        NewsfeedFragment newsfeedFragment = (NewsfeedFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (newsfeedFragment != null) {
            newsfeedFragment.updateViewWithText(text);
            Toast.makeText(MainActivity.this,"Working!",Toast.LENGTH_SHORT);
        }
    }
}