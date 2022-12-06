package com.glowingsoft.carplaterecognizer.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.Entity.Pay;
import com.glowingsoft.carplaterecognizer.Entity.staff;
import com.glowingsoft.carplaterecognizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Paying extends AppCompatActivity {
TextView name,model,day,status,perday,plate,miles,total;
Button button;
EditText am;
    AlertDialog.Builder builds,build;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paying);
        button=findViewById(R.id.pay_button);
        name=findViewById(R.id.pay_name);
        model=findViewById(R.id.pay_model);
        plate=findViewById(R.id.pay_plate);
        total=findViewById(R.id.total_pay);
        miles=findViewById(R.id.pay_miles);
        perday=findViewById(R.id.pay_amount_day);
        status=findViewById(R.id.pay_status);
        day=findViewById(R.id.today);

        String names = getIntent().getStringExtra("name");
        String plates = getIntent().getStringExtra("plate");
        String models = getIntent().getStringExtra("model");
        String statuss = getIntent().getStringExtra("status");
        String amounts = getIntent().getStringExtra("amount");
        String miless = getIntent().getStringExtra("miles");
        String limit = getIntent().getStringExtra("state");

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Date date=new Date();
        String dd=simpleDateFormat.format(date);
        day.setText(dd);
        status.setText(statuss);
        total.setText("Sh."+amounts);
        model.setText(models);
        plate.setText(plates);
        name.setText(names);
        perday.setText("sh."+200);
        miles.setText(miless +" miles");
        this.build = new AlertDialog.Builder(this);
        this.build.setMessage("Payment").setTitle("Payment confirmation");
        this.build.setMessage("Payment Success").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
                Intent intent = new Intent(Paying.this.getApplicationContext(), Payment.class);
                startActivity(intent);
                finish();
            }
        });
        this.builds = new AlertDialog.Builder(this);
        this.builds.setMessage("Payment").setTitle("Payment confirmation");
        this.builds.setMessage("Do you want to continue with payment ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
                SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
                Date date =new Date();
                String dd= formatter.format(date);
                DatabaseReference refer = FirebaseDatabase.getInstance().getReference("staff");
                DatabaseReference referer = FirebaseDatabase.getInstance().getReference("payment").child(plates);
                refer.child(plates).child("amount").setValue(String.valueOf(0));
                refer.child(plates).child("payment").setValue("paid");
                refer.child(plates).child("state").setValue("enabled");
                refer.child(plates).child("times").setValue(0);
                String pp="paid";
                String rr="enabled";
//
                Pay staffModel = new Pay(models,plates,dd, pp,amounts,names,miless,rr);

                referer.child(dd).setValue(staffModel);
                button.setText("paid");
                total.setText("0.0");
                AlertDialog alert = Paying.this.build.create();
                alert.setTitle("Payment confirmation");
                alert.show();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
                Paying.this.startActivity(new Intent(Paying.this.getApplicationContext(), Payment.class));
                finish();

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int aa=Integer.parseInt(amounts);
                int bb=Integer.parseInt(miless)*100;
                long cc=aa/bb;
                if (aa <= 100){
                    Toast.makeText(Paying.this, "Insufficient amount,You must have more than 100", Toast.LENGTH_LONG).show();
                }
                else{
                    if (cc<4 && limit.equalsIgnoreCase("disabled")){
                        Toast.makeText(Paying.this, "You must have checked in 4 times to be paid", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AlertDialog alert = Paying.this.builds.create();
                        alert.setTitle("Payment confirmation");
                        alert.show();


                    }
                }



            }
        });

    }
}