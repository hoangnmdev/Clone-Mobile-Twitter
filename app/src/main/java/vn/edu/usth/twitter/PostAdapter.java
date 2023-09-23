package vn.edu.usth.twitter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<PostItem> postItems;
    private Context context;

    public PostAdapter(List<PostItem> postItems, Context context) {
        this.postItems = postItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostItem postItem = postItems.get(position);
        holder.userImage.setImageResource(postItem.getProfileImg());
        holder.userName.setText(postItem.getName());
        holder.userId.setText(postItem.getId());
        holder.postContent.setText((postItem.getContent()));
        holder.imageContent.setImageResource(postItem.getImageContent());
    }

    @Override
    public int getItemCount() {
        return postItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView userImage;
        public TextView userName;
        public TextView userId;
        public TextView postContent;

        public ImageView imageContent;
        public ViewHolder(View postView) {
            super(postView);

            userImage = postView.findViewById(R.id.user_profile_image);
            userName = postView.findViewById(R.id.user_name);
            userId = postView.findViewById(R.id.user_id);
            postContent = postView.findViewById(R.id.content);
            imageContent = postView.findViewById(R.id.content_image);
        }
    }
}
