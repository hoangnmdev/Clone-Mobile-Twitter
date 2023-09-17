package vn.edu.usth.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "Twitter";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private TabLayout tabLayout;

    private int[] tabIcons = {
            R.drawable.home_icon,
            R.drawable.search_icon,
            R.drawable.notification_icon,
            R.drawable.inbox_icon,
    };
    private Adapter pagerAdapter;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*----------------Hooks-----------------*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        /*----------------Tool Bar-----------------*/
        setSupportActionBar(toolbar);





            /* Hide app name in tool bar */
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
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    //---------------------Open activity after click menu's item---------------//
    private void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
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


}