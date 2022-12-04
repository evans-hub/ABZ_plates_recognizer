package com.glowingsoft.carplaterecognizer.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.glowingsoft.carplaterecognizer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Activation extends AppCompatActivity {
    TextView name,model,day,status,perday,plate,miles,total;
    Button button;
    ImageView cl;
    EditText ed;
    String plates;
    AlertDialog.Builder builds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        button=findViewById(R.id.pay_buttonl);
        name=findViewById(R.id.pay_namel);
        model=findViewById(R.id.pay_modell);
        plate=findViewById(R.id.pay_platel);
        miles=findViewById(R.id.pay_milesl);
        perday=findViewById(R.id.pay_amount_dayl);
        status=findViewById(R.id.pay_statusl);
        day=findViewById(R.id.todayl);
        cl=findViewById(R.id.click);
        ed=findViewById(R.id.edd);
        this.builds = new AlertDialog.Builder(this);
        this.builds.setMessage("Activation").setTitle("Activation confirmation");
        this.builds.setMessage("Activated Successfully").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
                Intent intent = new Intent(Activation.this.getApplicationContext(), AdminActivity.class);
                Activation.this.startActivity(intent);
                finish();
            }
        });
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference("staff");
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plates=ed.getText().toString();
                DatabaseReference refer = FirebaseDatabase.getInstance().getReference("staff");
                refer.child(plates).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String naming = (String) snapshot.child("name").getValue(String.class);
                        String modell = (String) snapshot.child("car_model").getValue(String.class);
                        String dist = (String) snapshot.child("distance").getValue(String.class);
                        perday.setText("sh.200");
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
                        Date date=new Date();
                        String dd=simpleDateFormat.format(date);
                        day.setText(dd);
                        name.setText(naming);
                        model.setText(modell);
                        miles.setText(dist);
                        plate.setText(plates);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refer.child(plates).child("status").setValue("enabled");
                String nn= String.valueOf(refer.child(plates).child("status").setValue("enabled"));
                AlertDialog alert = Activation.this.builds.create();
                alert.setTitle("Activation confirmation");
                alert.show();
                if (nn.equalsIgnoreCase("enabled")) {
                    button.setText("Activated");

                }

            }
        });
    }
}