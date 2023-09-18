package vn.edu.usth.twitter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;


public class SearchFragment extends Fragment{

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        return view;

    }


    @Override
    public void onResume(){
        super.onResume();
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        // Get a reference to the ImageView inside the Toolbar
        ImageView logoImage = toolbar.findViewById(R.id.logo_image);

        SearchView searchView = toolbar.findViewById(R.id.search_view);
        // Hide the ImageView
        logoImage.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE); // Show the SearchView
    }

    @Override
    public void onPause(){
        super.onPause();
        // Get a reference to the Toolbar
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        // Get a reference to the ImageView inside the Toolbar
        ImageView logoImage = toolbar.findViewById(R.id.logo_image);

        SearchView searchView = toolbar.findViewById(R.id.search_view);

        // Show the ImageView
        logoImage.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE); // Show the SearchView
    }

    @Override
    public void onStop(){
        super.onStop();
        // Get a reference to the Toolbar
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        // Get a reference to the ImageView inside the Toolbar
        ImageView logoImage = toolbar.findViewById(R.id.logo_image);

        SearchView searchView = toolbar.findViewById(R.id.search_view);
        // Show the ImageView
        logoImage.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE); // Show the SearchView
    }
}