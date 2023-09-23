package vn.edu.usth.twitter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NewsfeedFragment extends Fragment {

    private List<PostItem> postItems;
    private String[] listName;
    
    private int[] listUserImageId;
    private String[] listUserId;
    private RecyclerView recycleView;
    private String[] listContent;
    private int[] listImageContent;

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
        listUserImageId = new int[]{
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
        listImageContent = new int[]{
                R.drawable.user1_post,
                R.drawable.user2,
                R.drawable.user3,
                R.drawable.user_4,
                R.drawable.user5,
        };
        for (int i = 0; i < listName.length; i++){
            PostItem postItem = new PostItem(listName[i],listUserImageId[i],listUserId[i],listContent[i],listImageContent[i]);
            postItems.add(postItem);
        }
    }
    public void updateViewWithText(String content) {
        String postContent = content;
        PostItem myPost = new PostItem(getString(R.string.profile_user_name) ,R.drawable.user6, getString(R.id.user_id),postContent, R.drawable.user1_post);
        postItems.add(myPost);
        Toast.makeText(getActivity(),"Text!",Toast.LENGTH_SHORT);
    }
}