package com.glowingsoft.carplaterecognizer.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.Entity.Model;
import com.glowingsoft.carplaterecognizer.Entity.Pay;
import com.glowingsoft.carplaterecognizer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class PActivity extends AppCompatActivity {
    PAdapter adapter;
    ArrayList<Pay> list;
    RecyclerView listView;
    private Button pickDateBtn;
    DatabaseReference reference;
    EditText search;
    private TextView selectedDateTV;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_pactivity);
        this.search = (EditText) findViewById(R.id.seay);
        this.listView = (RecyclerView) findViewById(R.id.lv);
        this.reference = FirebaseDatabase.getInstance().getReference("staff");
        this.listView.setHasFixedSize(true);
        this.listView.setLayoutManager(new LinearLayoutManager(this));
        this.list = new ArrayList<>();
        PAdapter search_adapter = new PAdapter(this, this.list);
        this.adapter = search_adapter;
        this.listView.setAdapter(search_adapter);
        Button button = (Button) findViewById(R.id.idBtnPickDate);
        this.pickDateBtn = button;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(PActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        PActivity.this.search.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, c.get(1), c.get(2), c.get(5)).show();
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(67108864);
            window.setStatusBarColor(getResources().getColor(R.color.startblue));
        }
        this.search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                PActivity.this.reference.addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        PActivity.this.list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Pay model_data = (Pay) dataSnapshot.getValue(Pay.class);
                            if (String.valueOf(model_data.getCar_plate()).toLowerCase().contains(s) || String.valueOf(model_data.getName()).toLowerCase().contains(s)) {
                                PActivity.this.list.add(model_data);
                            }
                        }
                        Collections.reverse(PActivity.this.list);
                        PActivity.this.adapter.notifyDataSetChanged();
                    }

                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(PActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }
}
