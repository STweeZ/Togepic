package com.example.myapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.activities.Chat;
import com.example.myapplication.chat.Message;
import com.example.myapplication.objects.Groupe;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private Context context;

    private List<Groupe> groupList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView vName;
        TextView vDesc;
        Switch vIsPublic;
        ImageView vImg;
        ImageView vLock;
        TextView vDate;
        TextView vNbUser;
        TextView vMessageListView;
        View vDivider;

        public ViewHolder(View itemView) {
            super(itemView);

            vName = (TextView) itemView.findViewById(R.id.grpNameField);
            vDesc = (TextView) itemView.findViewById(R.id.groupDesc);
            vDate = (TextView) itemView.findViewById(R.id.dateField);
            vImg = (ImageView) itemView.findViewById(R.id.grpImgField);
            vNbUser = (TextView) itemView.findViewById(R.id.nbUser);
            vLock = (ImageView) itemView.findViewById(R.id.lock);
            vMessageListView = (TextView) itemView.findViewById(R.id.msgListView);
            vDivider = (View) itemView.findViewById(R.id.divider);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

    public GroupAdapter(List<Groupe> listData, Context context) {
        this.groupList = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grouplist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Groupe groupe = this.groupList.get(position);

        holder.vName.setText(groupe.getName());
        holder.vNbUser.setText(groupe.getNbUsers());

        StringBuilder messages = new StringBuilder();
        for (Message m : groupe.getMessages()) {
            messages.append(m.getOwner() + ": " + m.getContent() + "\n");
        }
        holder.vMessageListView.setText(messages);

        if (groupe.getPicture() != null)
            holder.vImg.setImageBitmap(Converter.stringToBitmap(groupe.getPicture()));
        else
            holder.vImg.setImageResource(R.drawable.togepic);

        if (groupe.isPrivate())
            holder.vLock.setImageResource(R.drawable.baseline_lock_24);

        if (groupe.getMessages().size() > 0)
            holder.vDate.setText(groupe.getMessages().get(0).getDate().split(" ")[0]);
        else {
            holder.vDivider.setVisibility(View.INVISIBLE);
            holder.vDate.setText(R.string.no_message_yet);

        }

        holder.itemView.setOnClickListener(l -> {
            Intent intent = new Intent(context, Chat.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle b = new Bundle();
            b.putInt("id", groupe.getId());
            b.putString("role", groupe.getRole());
            intent.putExtras(b);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}
