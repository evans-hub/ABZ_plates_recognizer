package com.glowingsoft.carplaterecognizer.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.glowingsoft.carplaterecognizer.Entity.Pay;
import com.glowingsoft.carplaterecognizer.PaymentAdapter;
import com.glowingsoft.carplaterecognizer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PAdapter extends RecyclerView.Adapter<PAdapter.MyViewHolder> {
        Context context;
        ArrayList<Pay> listing;

public PAdapter(Context context2, ArrayList<Pay> list2) {
        this.context = context2;
        this.listing = list2;
        }

    @NonNull
    @Override
    public PAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(this.context).inflate(R.layout.draw,parent,false);
        return new PAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PAdapter.MyViewHolder holder, int position) {
        Pay model=listing.get(position);
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        Date date=new Date();
        String day= sdf.format(date);
        holder.mname.setText(model.getName());
        holder.mplate.setText(model.getCar_plate());
        holder.mmodel.setText(model.getCar_model());
        holder.mamount.setText("Sh."+model.getAmount());
        holder.mstatus.setText(model.getPayment());
        holder.mtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(), Paying.class);
                intent.putExtra("name",model.getName());
                intent.putExtra("plate",model.getCar_plate());
                intent.putExtra("model",model.getCar_model());
                intent.putExtra("amount",model.getAmount());
                intent.putExtra("state",model.getState());
                intent.putExtra("status",model.getPayment());
                intent.putExtra("miles",model.getDistance());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listing.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mmodel;
        TextView mname;
        TextView mplate;
        Button mtime;
        TextView mstatus;
        TextView mamount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mmodel = (TextView) itemView.findViewById(R.id.llast_model);
            this.mplate = (TextView) itemView.findViewById(R.id.llast_plate);
            this.mtime = (Button) itemView.findViewById(R.id.llast_date);
            this.mname = (TextView) itemView.findViewById(R.id.llast_name);
            this.mstatus = (TextView) itemView.findViewById(R.id.llast_status);
            this.mamount = (TextView) itemView.findViewById(R.id.llast_amount);
        }
    }


}
