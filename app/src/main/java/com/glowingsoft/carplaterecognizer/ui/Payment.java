package com.glowingsoft.carplaterecognizer.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.Adapter;
import com.glowingsoft.carplaterecognizer.Entity.Model;
import com.glowingsoft.carplaterecognizer.Entity.Pay;
import com.glowingsoft.carplaterecognizer.PaymentAdapter;
import com.glowingsoft.carplaterecognizer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Payment extends AppCompatActivity {
RecyclerView recyclerView;
    PaymentAdapter adapter;
    ImageView imageView;
    ArrayList<Pay> list;
    ProgressDialog loading;
    DatabaseReference reference;
    TextView tt;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        recyclerView=findViewById(R.id.paymentrecyclerview);
        loading = new ProgressDialog(this);
        final BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        reference = FirebaseDatabase.getInstance().getReference("staff");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new PaymentAdapter(this,list);
        recyclerView.setAdapter(adapter);
        tt = (TextView) findViewById(R.id.home_number);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    navigationView.setVisibility(View.VISIBLE);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || (dy < 0 && navigationView.isShown())) {
                    navigationView.setVisibility(View.GONE);
                }
            }
        });
        this.loading.show();
        this.reference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    loading.dismiss();
                    list.add((Pay) dataSnapshot.getValue(Pay.class));
                }
                adapter.notifyDataSetChanged();
                if (Payment.this.list.size() == 0) {
                    Toast.makeText(Payment.this, "No cars", Toast.LENGTH_SHORT).show();
                }
                int total = 0;
                for (int i = 0; i < Payment.this.list.size(); i++) {
                    total++;
                }
                TextView textView = Payment.this.tt;
                textView.setText(String.valueOf(total) + " cars");
            }

            public void onCancelled(DatabaseError error) {
                loading.dismiss();
                Toast.makeText(Payment.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_admin /*2131361835*/:
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        finish();
                        return false;
                    case R.id.action_home /*2131361846*/:
                       startActivity(new Intent(getApplicationContext(), DashBoard.class));
                        finish();
                        return false;
                    case R.id.action_notification /*2131361853*/:
                        startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                        finish();
                        return false;
                    case R.id.action_settings /*2131361854*/:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        finish();
                        return false;
                    default:
                        return false;
                }
            }
        });
    }
}