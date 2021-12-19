package com.example.bb;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder>
{
    private List<DayView> mDayView;
    private OnItemClickListener mClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView dayImage;
        TextView dayNum;

        public ViewHolder(View view){
            super(view);
            dayImage = (ImageView)view.findViewById(R.id.day_image);
            dayNum = (TextView)view.findViewById(R.id.day_num);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mClickListener = onItemClickListener;
    }

    public DayAdapter(List<DayView> DayList){
        mDayView = DayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DayView dayView = mDayView.get(position);
        holder.dayNum.setText(dayView.getName());
        if (mClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDayView.size();
    }
}
