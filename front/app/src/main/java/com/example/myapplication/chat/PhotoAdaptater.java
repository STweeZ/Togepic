package com.example.myapplication.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdaptater extends BaseAdapter {

    Context context;
    List<Photo> data;
    private static LayoutInflater inflater = null;



    public PhotoAdaptater(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        Photo p = (Photo) getItem(position);

        if (vi == null){
            vi = inflater.inflate(R.layout.item_photo_closable, null);
        }
        //((ImageView)vi.findViewById(R.id.photo)).setImageBitmap(p.getBitmap());
        ((ImageView)vi.findViewById(R.id.photo)).setImageURI(p.getUri());



        return vi;
    }

    public void add(Photo p){
        this.data.add(p);
        notifyDataSetChanged();
    }
}
