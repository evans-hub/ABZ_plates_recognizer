package com.glowingsoft.carplaterecognizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.glowingsoft.carplaterecognizer.Entity.Model;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    Context context;
    ArrayList<Model> list;

    public Adapter(Context context2, ArrayList<Model> list2) {
        this.context = context2;
        this.list = list2;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.last, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model model = this.list.get(position);
        holder.mname.setText(model.getName());
        holder.mtime.setText(model.getTime_in());
        holder.mplate.setText(model.getCar_plate());
        holder.mmodel.setText(model.getCar_model());
      /* TextView textView = holder.mduration;

        textView.setText(String.valueOf(((System.currentTimeMillis() - Long.parseLong(model.getMillis())) / 1000) / 60/60) + " hrs");
*/
     long Seconds=((System.currentTimeMillis() - Long.parseLong(model.getMillis())) / 1000);

        long h=0;
        long s=Seconds%60;
         h=Seconds/60;
        long m=h%60;
         h=h/60;
         holder.mhrs.setText(String.valueOf(h)+"hrs");
         holder.mmins.setText(String.valueOf(m)+"mins");
         holder.msecs.setText(String.valueOf(s)+"secs");

    }


    public int getItemCount() {
        return this.list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mimage;
        TextView mmodel;
        TextView mname;
        TextView mplate;
        TextView mtime;
        TextView mhrs,mmins,msecs;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.mmodel = (TextView) itemView.findViewById(R.id.last_model);
            this.mplate = (TextView) itemView.findViewById(R.id.last_plate);
            this.mtime = (TextView) itemView.findViewById(R.id.last_date);
            this.mname = (TextView) itemView.findViewById(R.id.last_name);
//            this.mimage = (ImageView) itemView.findViewById(R.id.last_image);
            this.mhrs = (TextView) itemView.findViewById(R.id.last_duration_hrs);
            this.mmins = (TextView) itemView.findViewById(R.id.last_duration_mins);
            this.msecs = (TextView) itemView.findViewById(R.id.last_duration_secs);
        }
    }
}
