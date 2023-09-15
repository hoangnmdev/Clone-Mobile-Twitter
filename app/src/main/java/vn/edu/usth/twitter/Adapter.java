package vn.edu.usth.twitter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class Adapter extends FragmentStateAdapter {

    public Adapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new Newsfeed();
        } else if (position == 1) {
            return new SearchFragment();
        } else if (position == 2) {
            return new NotificationFragment();
        } else {
            return new MessageFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
