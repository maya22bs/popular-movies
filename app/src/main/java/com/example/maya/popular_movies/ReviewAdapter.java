package com.example.maya.popular_movies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by maya on 23/08/16.
 */
public class ReviewAdapter extends BaseAdapter{

    private ArrayList<String> reviews;
    private int positionToExpend;
    private Context c;

    public ReviewAdapter(Context context){
        c=context;
        reviews=new ArrayList<String>();
        positionToExpend=-1;
    }
    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int i) {

        return reviews.get(i);
    }

    @Override
    public long getItemId(int i) {

        return reviews.get(i).hashCode();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view=null;
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater= LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.review_item, null);
            holder=new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.review_text_view);
            view.setTag(holder);
        }

        else{
            view=convertView;
            holder=(ViewHolder) view.getTag();
        }
        if(i!=positionToExpend) {
            holder.textView.setMaxLines(3);
            holder.textView.setBackgroundColor(c.getResources().getColor(R.color.backgroundDetail));
        }
        else{
            holder.textView.setMaxLines(reviews.get(i).length());
            holder.textView.setBackgroundColor(c.getResources().getColor(R.color.reviewItemBack));
        }
        holder.textView.setText(reviews.get(i));



        return view;
    }

    public void addReview(String review) {

        reviews.add(review);
    }

    public void setToExpend(int position){

        positionToExpend=position;
    }
    public int getToExpend(){

        return positionToExpend;
    }
}
