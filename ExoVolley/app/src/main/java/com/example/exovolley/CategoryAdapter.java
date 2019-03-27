package com.example.exovolley;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private List<Category> categories;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Titre, Bio, id;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            Titre = view.findViewById(R.id.txtTitre);
            Bio = view.findViewById(R.id.txtBio);
            id = view.findViewById(R.id.txtId);
            imageView = view.findViewById(R.id.imgCategory);
        }
    }

    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categories = categoryList;
        this.mContext = context;
    }

    public CategoryAdapter() {
        super();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        Category category = categories.get(i);

        myViewHolder.Titre.setText(category.getName());
        myViewHolder.Bio.setText(category.getDescription());
        myViewHolder.id.setText(String.valueOf(category.getId()));
        Picasso.with(mContext).load("https://10.0.2.2:44303/assets/"+category.getPicture()).into(myViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
