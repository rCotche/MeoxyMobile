package com.example.exovolley;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private Context myContext;

    PostAdapter(List<Post> postList, Context context) {
        this.posts = postList;
        this.myContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        final View view;
        ImageView image;
        TextView description;
        public TextView name;

        ViewHolder(View v)
        {
            super(v);
            this.view = v;

            name = view.findViewById(R.id.txtTitre);
            image = view.findViewById(R.id.imgCategory);
            description = view.findViewById(R.id.txtBio);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Post post = posts.get(i);

        viewHolder.name.setText(post.getName());
        viewHolder.description.setText(post.getDescription());
        Picasso.with(myContext).load("https://10.0.2.2:44303/assets/"+post.getPicture()).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        if(posts != null)
        {
            return posts.size();
        }
        else
        {
            return 0;
        }
    }
}
