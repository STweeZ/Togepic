package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.activities.Chat;
import com.example.myapplication.chat.Message;
import com.example.myapplication.fragments.CreateGroupDialog;
import com.example.myapplication.fragments.MapFragment;
import com.example.myapplication.objects.Groupe;
import com.example.myapplication.objects.Picture;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    private Context context;
    private List<Picture> pictureList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView vUserPicture;
        TextView vUserName;
        ImageView vPicture;
        ImageButton vMap;

        public ViewHolder(View itemView) {
            super(itemView);
            vUserPicture = (ImageView) itemView.findViewById(R.id.userPicture);
            vUserName = (TextView) itemView.findViewById(R.id.userName);
            vPicture = (ImageView) itemView.findViewById(R.id.showPicture);
            vMap = (ImageButton) itemView.findViewById(R.id.pictureLocation);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

    public PictureAdapter(List<Picture> listData, Context context) {
        this.pictureList = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.picturelist_item, parent, false);
        return new PictureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureAdapter.ViewHolder holder, int position) {
        Picture picture = this.pictureList.get(position);
        if(picture.getUser().getPicture() != null)
            holder.vUserPicture.setImageBitmap(Converter.stringToBitmap(picture.getUser().getPicture()));
        holder.vUserName.setText(picture.getUser().getName());
        holder.vPicture.setImageBitmap(Converter.stringToBitmap(picture.getText()));

        holder.vMap.setOnClickListener(l -> {
            MapFragment frag = new MapFragment();
            Bundle args = new Bundle();
            args.putInt("x", picture.getxGeolocate());
            args.putInt("y", picture.getyGeolocate());
            new MapFragment().show(((AppCompatActivity)context).getSupportFragmentManager(), MapFragment.TAG);
        });
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }
}
