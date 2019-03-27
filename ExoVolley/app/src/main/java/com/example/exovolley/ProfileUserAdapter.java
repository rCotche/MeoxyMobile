package com.example.exovolley;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProfileUserAdapter extends RecyclerView.Adapter<ProfileUserAdapter.MyViewHolder> {
    private List<Category> categories;
    private Context context;
    private String pictureName;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView description;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            description = view.findViewById(R.id.lblDescription);
            title = view.findViewById(R.id.lblTitle);
            imageView = view.findViewById(R.id.ivCategory);
        }
    }

    public ProfileUserAdapter(List<Category> categoryList, Context context){
        this.categories = categoryList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_profile_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        Category category= categories.get(i);
        pictureName = category.getPicture();

        int resid = this.context.getResources().getIdentifier(pictureName, "drawable", this.context.getPackageName());

        myViewHolder.description.setText(category.getDescription());
        myViewHolder.title.setText(category.getName());
        myViewHolder.imageView.setImageResource(resid);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
