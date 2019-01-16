package com.example.user.apfiber.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.ImagePickerViewHolder> {

    private Context mContext;

    private ArrayList<Uri> images;

    private Picasso picasso = Picasso.get();

    public ImagePickerAdapter(Context mContext, ArrayList<Uri> images) {
        this.mContext = mContext;
        this.images = images;
    }

    public void addAll(ArrayList<Uri> images) {
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    public void clear() {
        if (images.size() > 0) {
            this.images.clear();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ImagePickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(mContext);
        imageView.setPadding(0, 0, 8, 0);
        return new ImagePickerViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePickerViewHolder holder, int position) {
        picasso.load(this.images.get(position)).centerCrop().resize(300, 300).into(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return this.images.size();
    }

    class ImagePickerViewHolder extends RecyclerView.ViewHolder {

        ImageView itemView;

        ImagePickerViewHolder(ImageView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
