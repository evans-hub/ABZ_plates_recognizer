package com.glowingsoft.carplaterecognizer.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.glowingsoft.carplaterecognizer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    CardView all;
    CardView register;
CardView pay;
CardView admin;
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_admin);
        this.all = (CardView) findViewById(R.id.admin_view);
        this.register = (CardView) findViewById(R.id.admin_registration);
        pay=findViewById(R.id.admin_registration2);
        admin=findViewById(R.id.admin_view2);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ine=new Intent(AdminActivity.this,Activation.class);
                startActivity(ine);
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminActivity.this.startActivity(new Intent(AdminActivity.this.getApplicationContext(), Payment.class));

            }
        });
        this.all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AdminActivity.this.startActivity(new Intent(AdminActivity.this.getApplicationContext(), Home.class));
            }
        });
        this.register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this.getApplicationContext(), Registration.class);
                intent.putExtra("database_path", "staff");
                AdminActivity.this.startActivity(intent);
            }
        });
        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home /*2131361846*/:
                        AdminActivity.this.startActivity(new Intent(AdminActivity.this.getApplicationContext(), DashBoard.class));
                        AdminActivity.this.finish();
                        return false;
                    case R.id.action_notification /*2131361853*/:
                        AdminActivity.this.startActivity(new Intent(AdminActivity.this.getApplicationContext(), NotificationActivity.class));
                        AdminActivity.this.finish();
                        return false;
                    case R.id.action_settings /*2131361854*/:
                        AdminActivity.this.startActivity(new Intent(AdminActivity.this.getApplicationContext(), SettingActivity.class));
                        AdminActivity.this.finish();
                        return false;
                    default:
                        return false;
                }
            }
        });
    }
}
