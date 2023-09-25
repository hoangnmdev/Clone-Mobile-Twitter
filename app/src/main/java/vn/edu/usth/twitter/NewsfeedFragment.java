package vn.edu.usth.twitter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class NewsfeedFragment extends Fragment {

    private final List<PostItem> postItems = new ArrayList<>();
    private String[] listName;
    
    private int[] listUserProfileImage;
    private String[] listUserId;
    private RecyclerView recycleView;
    private String[] listContent;
    private int[] listContentImage;


    PostAdapter postAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference myRef = database.getReference("Post"); // Replace with your Firebase node
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
        recycleView = view.findViewById(R.id.recyclerview);
        postAdapter = new PostAdapter(postItems, getContext());
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setHasFixedSize(true);
        recycleView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing data
                postItems.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String name = postSnapshot.child("UserName").getValue(String.class);
                    String userId = postSnapshot.child("UserId").getValue(String.class);
                    String content = postSnapshot.child("Content").getValue(String.class);
                    int profileDrawableResourceId = 0; // Default value indicating no image
                    int contentDrawableResourceId = 0; // Default value indicating no image

                    String profileImageReference = postSnapshot.child("UserProfileImage").getValue(String.class);
                    String contentImageReference = postSnapshot.child("ContentImage").getValue(String.class);


                    profileDrawableResourceId = getResources().getIdentifier(profileImageReference, "drawable", getContext().getPackageName());


                    contentDrawableResourceId = getResources().getIdentifier(contentImageReference, "drawable", getContext().getPackageName());


// Now, create the PostItem with default or resolved drawable resource IDs
                    PostItem postItem = new PostItem(name, profileDrawableResourceId, userId, content, contentDrawableResourceId);

                    // Add the postItem to your list
                    postItems.add(0,postItem);
                }
                datainitialize();
                postAdapter.notifyDataSetChanged();
                // Update your adapter and notify data set changed to refresh the UI

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur during the database read operation
            }
        });
    }

    private void datainitialize(){
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