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
import com.glowingsoft.carplaterecognizer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    Adapter adapter;
    ImageView imageView;
    ArrayList<Model> list;
    ProgressDialog loading;
    private RecyclerView recyclerView;
    DatabaseReference reference;
    TextView textView;
    TextView tt;

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.textView.setText(getSharedPreferences("Pref", 0).getString("username", ""));
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loading = new ProgressDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.lv);
        final BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        reference = FirebaseDatabase.getInstance().getReference("allcars");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        textView = (TextView) findViewById(R.id.profilename_home);
        tt = (TextView) findViewById(R.id.home_number);
        imageView = (ImageView) findViewById(R.id.profile_image_home);
      adapter = new Adapter(this,list);
        recyclerView.setAdapter(adapter);
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
                   list.add((Model) dataSnapshot.getValue(Model.class));
                }
                adapter.notifyDataSetChanged();
                if (Home.this.list.size() == 0) {
                    Toast.makeText(Home.this, "No cars in the compound", Toast.LENGTH_SHORT).show();
                }
                int total = 0;
                for (int i = 0; i < Home.this.list.size(); i++) {
                    total++;
                }
                TextView textView = Home.this.tt;
                textView.setText(String.valueOf(total) + " cars");
            }

            public void onCancelled(DatabaseError error) {
                Home.this.loading.dismiss();
                Toast.makeText(Home.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_admin /*2131361835*/:
                        Home.this.startActivity(new Intent(Home.this.getApplicationContext(), AdminActivity.class));
                        Home.this.finish();
                        return false;
                    case R.id.action_home /*2131361846*/:
                        Home.this.startActivity(new Intent(Home.this.getApplicationContext(), DashBoard.class));
                        Home.this.finish();
                        return false;
                    case R.id.action_notification /*2131361853*/:
                        Home.this.startActivity(new Intent(Home.this.getApplicationContext(), NotificationActivity.class));
                        Home.this.finish();
                        return false;
                    case R.id.action_settings /*2131361854*/:
                        Home.this.startActivity(new Intent(Home.this.getApplicationContext(), SettingActivity.class));
                        Home.this.finish();
                        return false;
                    default:
                        return false;
                }
            }
        });
    }
}
