package com.example.maya.popular_movies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by maya on 22/08/16.
 */
public class TrailersAdapter  extends BaseAdapter {
    ArrayList<String> trailers;
    private Context mContext;

    public TrailersAdapter(Context c){
        mContext = c;
        trailers=new ArrayList<String>();
    }
    @Override
    public int getCount() {

        return trailers.size();
    }

    @Override
    public Object getItem(int i) {

        return trailers.get(i);
    }

    @Override
    public long getItemId(int i) {

        return trailers.get(i).hashCode();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view=null;
        ViewHolder holder;
        if(convertView==null){
            LayoutInflater inflater= LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.trailer_item, null);

            holder=new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.trailer_list_item_title);
            view.setTag(holder);
        }else{
            view=convertView;
            holder=(ViewHolder) view.getTag();
        }

        holder.textView.setText("Trailer "+ (i+1));

        return view;
    }

    public void addTrailer(String trailer){
        trailers.add(trailer);
    }


    public void clear(){ trailers.clear();
    }
}
