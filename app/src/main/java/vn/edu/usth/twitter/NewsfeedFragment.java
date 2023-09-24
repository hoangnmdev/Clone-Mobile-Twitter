package vn.edu.usth.twitter;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NewsfeedFragment extends Fragment {

    private List<PostItem> postItems;
    private String[] listName;
    
    private int[] listUserProfileImage;
    private String[] listUserId;
    private RecyclerView recycleView;
    private String[] listContent;
    private int[] listContentImage;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference myRef = database.getReference(); // Replace with your Firebase node
    public NewsfeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        datainitialize();


        recycleView = view.findViewById(R.id.recyclerview);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setHasFixedSize(true);
        PostAdapter postAdapter = new PostAdapter(postItems, getContext());
        recycleView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
    }

    private void datainitialize(){
        postItems = new ArrayList<>();
        /*String name, String id, String content, int profileImg, int comment, int rt, int like*/
        listName = new String[]{
                getString(R.string.rose_user),
                "GPT",
                "Adobe Photoshop",
                getString(R.string.app_name),
                getString(R.string.app_name)
        };
        listUserProfileImage = new int[]{
                R.drawable.user1,
                R.drawable.user2,
                R.drawable.user3,
                R.drawable.user_4,
                R.drawable.user5,

        };

        listUserId = new String[]{
                "@rosé pics",
                getString(R.string.gpt_user),
                "@adobepts",
                "animewibu",
                "randomuser"
        };
        listContent = new String[]{
                "princess",
            getString(R.string.noti_content2),
                "Hello",
                "Nice to meet you",
                "I'm new here",
        };
        listContentImage = new int[]{
                R.drawable.user1_post,
                R.drawable.user2,
                R.drawable.user3,
                R.drawable.user_4,
                R.drawable.user5,
        };

        for (int i = 0; i < listName.length; i++){
            PostItem postItem = new PostItem(listName[i],listUserProfileImage[i],listUserId[i],listContent[i],listContentImage[i]);
            postItems.add(postItem);
            /*HashMap<String,Object> map = new HashMap<>();
            map.put("Name",listName[i]);
            map.put("UserId",listUserId[i]);
            map.put("Profile Image",listUserProfileImage);
            map.put("Content",listContent[i]);
            map.put("Image",listContentImage[i]);*/

        }
    }
}