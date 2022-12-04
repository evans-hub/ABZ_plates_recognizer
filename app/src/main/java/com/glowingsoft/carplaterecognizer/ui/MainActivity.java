package com.glowingsoft.carplaterecognizer.ui;
import static com.google.firebase.database.core.ServerValues.NAME_OP_TIMESTAMP;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import cz.msebera.android.httpclient.Header;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.Entity.Model;
import com.glowingsoft.carplaterecognizer.Entity.staff;
import com.glowingsoft.carplaterecognizer.R;
import com.glowingsoft.carplaterecognizer.api.WebRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.ServerValues;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity  implements IPickResult,View.OnClickListener {
    String DB;
    String model;
    String DB_path;
    String Database_Path = "Employees";
    String SHARED_PREF_NAME = "user_pref";
    AlertDialog.Builder build;
    AlertDialog.Builder buildd;
    AlertDialog.Builder builder;
    AlertDialog.Builder bu;
    String car_type = "";
    String cc;
    Button check_in;
    Button check_out;
    Context context;
    String countrycode = "";
    Date date;
    DateFormat df;
    ImageButton editResult;
    ImageView emptyImage;
    FloatingActionButton floatingActionButton;
    ImageView imageView;
    String imagepath;
    String last_digits = "";
    ProgressDialog load;
    Button nextImage;
    CardView plateCard;
    TextView plate_txt;
    String plate_type = "";
    ProgressBar progressBar;
    CardView regionCard;
    TextView region_txt;
    String region_type = "";
    PickSetup setup = new PickSetup().setCancelText("Cancel").setFlip(true).setMaxSize(50).setWidth(50).setHeight(50).setProgressText("Loading Image").setPickTypes(EPickType.CAMERA).setCameraButtonText("Camera").setIconGravity(Gravity.TOP).setButtonOrientation(LinearLayout.HORIZONTAL).setSystemDialog(false).setCameraIcon(R.drawable.cam);
    SharedPreferences sharedPreferences;
    String timeStamp = "";
    String token = "";
    String type;
    CardView vihicalCard;
    TextView vihicle_txt;
    String dista;

    public static String getFilename(Context context2) {
        File file = new File(context2.getFilesDir().getPath(), ".Foldername/PlateRecognizerHistory");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round(((float) height) / ((float) reqHeight));
            int widthRatio = Math.round(((float) width) / ((float) reqWidth));
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        while (((float) (width * height)) / ((float) (inSampleSize * inSampleSize)) > ((float) (reqWidth * reqHeight * 2))) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView((int) R.layout.activity_main);
        this.sharedPreferences = getSharedPreferences(this.SHARED_PREF_NAME, 0);
        this.date = new Date();
        this.df = new SimpleDateFormat("MM/dd/");
        this.df.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        this.nextImage = (Button) findViewById(R.id.next_image);
        this.check_in = (Button) findViewById(R.id.check_in_image);
        this.check_out = (Button) findViewById(R.id.check_out_image);
        this.nextImage.setOnClickListener(this);
        this.check_in.setOnClickListener(this);
        this.check_out.setOnClickListener(this);
        this.floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        this.floatingActionButton.setOnClickListener(this);
        this.progressBar = (ProgressBar) findViewById(R.id.homeprogress);
        this.plate_txt = (TextView) findViewById(R.id.car_plate);
        this.region_txt = (TextView) findViewById(R.id.region_code);
        this.vihicle_txt = (TextView) findViewById(R.id.vihicle_type);
        this.emptyImage = (ImageView) findViewById(R.id.empty_image);
        this.plateCard = (CardView) findViewById(R.id.cardView);
        this.vihicalCard = (CardView) findViewById(R.id.cardView3);
        this.regionCard = (CardView) findViewById(R.id.cardView2);
        this.editResult = (ImageButton) findViewById(R.id.setting_edit_btn);
        this.editResult.setOnClickListener(this);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        this.imageView.setOnClickListener(this);
        this.load = new ProgressDialog(this);
        this.DB_path = getIntent().getExtras().getString("database_path");
        this.bu = new AlertDialog.Builder(this);
        this.bu.setMessage("Check out").setTitle("Fine Payment");
        this.bu.setMessage("You should pay your fine to check out you overstayed").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
                dialog.dismiss();
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), DashBoard.class));
            }
        });
        this.builder = new AlertDialog.Builder(this);
        this.builder.setMessage("Register").setTitle("user unavailable");
        this.builder.setMessage("Do you want to register this number plate ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
                SharedPreferences.Editor myEdit = MainActivity.this.getSharedPreferences("Prof", 0).edit();
                myEdit.putString("plate", MainActivity.this.cc);
                myEdit.commit();
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), Registration.class);
                intent.putExtra("database_path", MainActivity.this.DB_path);
                intent.putExtra("dd", MainActivity.this.cc);
                MainActivity.this.startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
                dialog.dismiss();
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), DashBoard.class));
            }
        });
        this.buildd = new AlertDialog.Builder(this);
        this.buildd.setMessage("Register").setTitle("Staff not activated");
        this.buildd.setMessage("Visit admin to activate your account ?").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
                dialog.dismiss();
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), DashBoard.class));
            }
        });
        this.build = new AlertDialog.Builder(this);
        this.build.setMessage("Check in").setTitle("Checking in Progress");
        this.build.setMessage("Check in success").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
                dialog.dismiss();
                MainActivity.this.nextImage.setVisibility(View.VISIBLE);
                MainActivity.this.check_in.setVisibility(View.GONE);
                MainActivity.this.check_out.setVisibility(View.GONE);
                MainActivity.this.plate_txt.setText((CharSequence) null);
                MainActivity.this.region_txt.setText((CharSequence) null);
                MainActivity.this.vihicle_txt.setText((CharSequence) null);
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), DashBoard.class));
            }
        });
        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_admin /*2131361835*/:
                        if (!MainActivity.this.getSharedPreferences("Pref", 0).getString("username", "").contains("admin")) {
                            Toast.makeText(MainActivity.this.context, "User not authorized", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), AdminActivity.class));
                            MainActivity.this.finish();
                            break;
                        }
                    case R.id.action_home /*2131361846*/:
                        MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), DashBoard.class));
                        MainActivity.this.finish();
                        break;
                    case R.id.action_notification /*2131361853*/:
                        MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), NotificationActivity.class));
                        MainActivity.this.finish();
                        break;
                    case R.id.action_settings /*2131361854*/:
                        MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), SettingActivity.class));
                        MainActivity.this.finish();
                        break;
                }
                return false;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (!this.sharedPreferences.contains("checked") || !this.sharedPreferences.getBoolean("checked", false)) {
            this.editResult.setVisibility(View.GONE);
        } else {
            this.editResult.setVisibility(View.VISIBLE);
        }
        this.last_digits = this.sharedPreferences.getString("LastDigits", "");
        this.token = this.sharedPreferences.getString("CarToken", "");
        if (this.token.equals("")) {
            Toast.makeText(this.context, "Token Not Found", Toast.LENGTH_SHORT).show();
            return;
        }
        AsyncHttpClient asyncHttpClient = WebRequest.client;
        asyncHttpClient.addHeader("Authorization", "Token " + this.token);
    }

    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            RequestParams params = new RequestParams();
            String file = r.getPath();
            String compressed = compressImage(file);
            this.countrycode = this.sharedPreferences.getString("RegionCode", "");
            String baseurl = this.sharedPreferences.getString("BaseUrl", "https://api.platerecognizer.com/v1/plate-reader/");
            Log.d("response", "filepath: " + file + " ");
            try {
                params.put("upload", new File(compressed));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            params.put("regions", this.countrycode);
            Log.d("response", "image to upload: " + params + " ");
            WebRequest.post(this.context, baseurl, params, new JsonHttpResponseHandler() {
                public void onStart() {
                    MainActivity.this.progressBar.setVisibility(View.VISIBLE);
                    MainActivity.this.region_txt.setText((CharSequence) null);
                    MainActivity.this.plate_txt.setText((CharSequence) null);
                    MainActivity.this.vihicle_txt.setText((CharSequence) null);
                    MainActivity.this.imageView.setImageResource(R.drawable.upload);
                    Log.d("response", "onStart: ");
                    super.onStart();
                }

                @SuppressLint("RestrictedApi")
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("response ", response.toString() + " ");
                    try {
                        MainActivity mainActivity = MainActivity.this;
                        mainActivity.imagepath = "https://us-east-1.linodeobjects.com/platerec-api/uploads/" + MainActivity.this.df.format(MainActivity.this.date) + response.getString("filename");
                        JSONArray Jsresults = response.getJSONArray("results");
                        if (Jsresults.length() > 0) {
                            for (int i = 0; i < Jsresults.length(); i++) {
                                MainActivity.this.plate_txt.setText(Jsresults.getJSONObject(i).getString("plate"));
                                MainActivity.this.timeStamp = response.getString(NAME_OP_TIMESTAMP);
                                Picasso.get().load(MainActivity.this.imagepath).into(MainActivity.this.imageView, new Callback() {
                                    public void onSuccess() {
                                        String format = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                        MainActivity.this.progressBar.setVisibility(View.GONE);
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MainActivity.this.DB_path);
                                        final String county = MainActivity.this.plate_txt.getText().toString();
                                        MainActivity.this.cc = county;
                                        reference.child(county).addListenerForSingleValueEvent(new ValueEventListener() {
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    MainActivity.this.DB = (String) snapshot.child("name").getValue(String.class);
                                                    MainActivity.this.type = (String) snapshot.child("type").getValue(String.class);
                                                    SharedPreferences.Editor myEdit = MainActivity.this.getSharedPreferences("stating", 0).edit();
                                                    myEdit.putString(NotificationCompat.CATEGORY_STATUS, (String) snapshot.child(NotificationCompat.CATEGORY_STATUS).getValue(String.class));
                                                    myEdit.commit();
                                                    MainActivity.this.regionCard.setVisibility(View.VISIBLE);
                                                    MainActivity.this.plateCard.setVisibility(View.VISIBLE);
                                                    MainActivity.this.vihicalCard.setVisibility(View.VISIBLE);
                                                    MainActivity.this.check_in.setVisibility(View.GONE);
                                                    MainActivity.this.check_out.setVisibility(View.GONE);
                                                    MainActivity.this.nextImage.setVisibility(View.GONE);
                                                    MainActivity.this.floatingActionButton.setVisibility(View.GONE);
                                                    MainActivity.this.emptyImage.setVisibility(View.GONE);
                                                    MainActivity.this.vihicle_txt.setText((String) snapshot.child("car_model").getValue(String.class));
                                                    MainActivity.this.region_txt.setText((String) snapshot.child("id_number").getValue(String.class));
                                                    FirebaseDatabase.getInstance().getReference("allcars").child(county).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        public void onDataChange(DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                MainActivity.this.check_out.setVisibility(View.VISIBLE);
                                                            } else {
                                                                MainActivity.this.check_in.setVisibility(View.VISIBLE);
                                                            }
                                                        }

                                                        public void onCancelled(DatabaseError error) {
                                                        }
                                                    });
                                                    return;
                                                }
                                                AlertDialog alert = MainActivity.this.builder.create();
                                                alert.setTitle("User unavailable in Database");
                                                alert.show();
                                            }

                                            public void onCancelled(DatabaseError error) {
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        MainActivity.this.regionCard.setVisibility(View.VISIBLE);
                                        MainActivity.this.plateCard.setVisibility(View.VISIBLE);
                                        MainActivity.this.vihicalCard.setVisibility(View.VISIBLE);
                                        MainActivity.this.nextImage.setVisibility(View.VISIBLE);
                                        MainActivity.this.check_in.setVisibility(View.VISIBLE);
                                        MainActivity.this.check_out.setVisibility(View.VISIBLE);
                                        MainActivity.this.floatingActionButton.setVisibility(View.GONE);
                                        MainActivity.this.emptyImage.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("response1", "onFailure: " + errorResponse + " ");
                    MainActivity.this.progressBar.setVisibility(View.GONE);
                    MainActivity.this.editResult.setVisibility(View.GONE);
                    MainActivity.this.regionCard.setVisibility(View.GONE);
                    MainActivity.this.plateCard.setVisibility(View.GONE);
                    MainActivity.this.vihicalCard.setVisibility(View.GONE);
                    MainActivity.this.nextImage.setVisibility(View.GONE);
                    MainActivity.this.check_in.setVisibility(View.GONE);
                    MainActivity.this.check_out.setVisibility(View.GONE);
                    MainActivity.this.floatingActionButton.setVisibility(View.GONE);
                    MainActivity.this.emptyImage.setVisibility(View.VISIBLE);
                    MainActivity mainActivity = MainActivity.this;
                    Toast.makeText(mainActivity, errorResponse + "", Toast.LENGTH_SHORT).show();
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("response2", "onFailure: " + errorResponse + " ");
                    MainActivity.this.progressBar.setVisibility(View.GONE);
                    MainActivity.this.editResult.setVisibility(View.GONE);
                    MainActivity.this.regionCard.setVisibility(View.GONE);
                    MainActivity.this.plateCard.setVisibility(View.GONE);
                    MainActivity.this.vihicalCard.setVisibility(View.GONE);
                    MainActivity.this.nextImage.setVisibility(View.GONE);
                    MainActivity.this.check_in.setVisibility(View.GONE);
                    MainActivity.this.check_out.setVisibility(View.GONE);
                    MainActivity.this.emptyImage.setVisibility(View.VISIBLE);
                    MainActivity mainActivity = MainActivity.this;
                    Toast.makeText(mainActivity, errorResponse.toString() + "", Toast.LENGTH_LONG).show();
                }

                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("response3", "onFailure: " + responseString + " ");
                    MainActivity.this.progressBar.setVisibility(View.GONE);
                    MainActivity.this.editResult.setVisibility(View.GONE);
                    MainActivity.this.regionCard.setVisibility(View.GONE);
                    MainActivity.this.plateCard.setVisibility(View.GONE);
                    MainActivity.this.vihicalCard.setVisibility(View.GONE);
                    MainActivity.this.nextImage.setVisibility(View.GONE);
                    MainActivity.this.check_in.setVisibility(View.GONE);
                    MainActivity.this.check_out.setVisibility(View.GONE);
                    MainActivity.this.emptyImage.setVisibility(View.VISIBLE);
                    MainActivity mainActivity = MainActivity.this;
                    Toast.makeText(mainActivity, responseString + "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == -1) {
            String plate = data.getStringExtra("car_plate");
            String region = data.getStringExtra("region_code");
            String car = data.getStringExtra("car_type");
            Log.d("response", "onActivityResult: " + plate + " ");
            this.plate_txt.setText(plate);
            this.region_txt.setText(region);
            this.vihicle_txt.setText(car);
            Toast.makeText(this, "Results saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.imageView) {
            if (this.token.isEmpty()) {
                Toast.makeText(this, "Go to Settings to Set Your Token", Toast.LENGTH_LONG).show();
                return;
            }
            PickImageDialog.build(this.setup).show((FragmentActivity) this);
        }
        this.plate_type = this.plate_txt.getText().toString();
        this.region_type = this.region_txt.getText().toString();
        this.car_type = this.vihicle_txt.getText().toString();
        if (v.getId() == R.id.setting_edit_btn) {
            if (this.plate_type.isEmpty()) {
                Toast.makeText(this, "Nothing to Edit Now", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("car_plate", this.plate_type);
                intent.putExtra("region_code", this.region_type);
                intent.putExtra("car_type", this.car_type);
                startActivityForResult(intent, 123);
            }
        }
        if (v.getId() == R.id.next_image) {
            PickImageDialog.build(this.setup).show((FragmentActivity) this);
        }
        if (v.getId() == R.id.check_in_image) {

            String dd = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            String millis = String.valueOf(System.currentTimeMillis());
            this.load.setTitle("Sign in");
            this.load.setMessage("Checking in please wait..");
            this.load.setCanceledOnTouchOutside(true);
            this.load.show();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("allcars");
            this.plate_type = this.plate_txt.getText().toString();
            this.region_type = this.region_txt.getText().toString();
            this.car_type = this.vihicle_txt.getText().toString();
            reference.child(this.plate_type).setValue(new Model(this.DB, this.plate_type, this.car_type, dd, this.type, millis));
            DatabaseReference referrrr = FirebaseDatabase.getInstance().getReference("staff").child(plate_type).child("distance");
            referrrr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dista=snapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            DatabaseReference refer = FirebaseDatabase.getInstance().getReference("staff");
      if (DB_path.equalsIgnoreCase("staff")){
          refer.child(plate_type).child("times").addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  long em = (long) snapshot.getValue(long.class);

                  refer.child(plate_type).child("times").setValue(em+1);
                  long ema = (long) snapshot.getValue(long.class);
                 long amount=0;
                 long dist=Long.parseLong(dista);
                 amount=dist*100*(ema+1);
//                  amount=(ema+1)*200;
                  refer.child(plate_type).child("amount").setValue(String.valueOf(amount));
                  refer.child(plate_type).child("payment").setValue("unpaid");
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
        /*  refer.child(plate_type).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  long em=snapshot.child("times").getValue(long.class);
                  refer.child(plate_type).child("times").setValue(em+1);
                  long ema=snapshot.child("times").getValue(long.class);
                  long amount=0;
                  amount=(ema+1)*200;
                  refer.child(plate_type).child("amount").setValue(String.valueOf(amount));
                  refer.child(plate_type).child("payment").setValue("unpaid");
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });*/
      }

            this.load.dismiss();
            AlertDialog alert = this.build.create();
            alert.setTitle("Success");
            alert.show();
        }
        if (v.getId() == R.id.check_out_image) {
            if (getSharedPreferences("stating", 0).getString(NotificationCompat.CATEGORY_STATUS, "").equalsIgnoreCase("temporary")) {
                AlertDialog alert2 = this.buildd.create();
                alert2.setTitle("Success");
                alert2.show();
            } else {
                String format = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                String format2 = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                this.load.setTitle("Check out");
                this.load.setMessage("Checking out please wait..");
                this.load.setCanceledOnTouchOutside(true);
                this.load.show();
                    FirebaseDatabase.getInstance().getReference("allcars").child(this.plate_type).removeValue();
                this.load.dismiss();
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Check Out").setMessage("Checking Out Success").create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    private static final int AUTO_DISMISS_MILLIS = 1000;

                    public void onShow(DialogInterface dialog) {
                        CharSequence text = ((AlertDialog) dialog).getButton(-2).getText();
                        final DialogInterface dialogInterface = dialog;
                        new CountDownTimer(1000, 100) {
                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                if (((AlertDialog) dialogInterface).isShowing()) {
                                    dialogInterface.dismiss();
                                    MainActivity.this.nextImage.setVisibility(View.VISIBLE);
                                    MainActivity.this.check_in.setVisibility(View.GONE);
                                    MainActivity.this.check_out.setVisibility(View.GONE);
                                    MainActivity.this.plate_txt.setText((CharSequence) null);
                                    MainActivity.this.region_txt.setText((CharSequence) null);
                                    MainActivity.this.vihicle_txt.setText((CharSequence) null);
                                    MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), DashBoard.class));
                                    MainActivity.this.finish();
                                }
                            }
                        }.start();
                    }
                });
                dialog.show();
            }
        }
        if (v.getId() == R.id.fab) {
            String plate = this.plate_txt.getText().toString();
            String region = this.region_txt.getText().toString();
            String car = this.vihicle_txt.getText().toString();
            Uri bmpUri = getLocalBitmapUri(this.imageView);
            String share = "Date & TimeStamp: " + this.timeStamp + "\nCar Plate: " + plate + "\nRegion Code: " + region + "\nVihicle Type: " + car + "\nToken Code: " + this.last_digits;
            Log.d("response", "onActivityResult: " + plate + " ");
            if (bmpUri != null) {
                Uri parse = Uri.parse("android.resource://" + getPackageName() + "/drawable/ic_launcher");
                Intent shareIntent = new Intent();
                shareIntent.setAction("android.intent.action.SEND");
                shareIntent.putExtra("android.intent.extra.TEXT", share);
                shareIntent.putExtra("android.intent.extra.STREAM", bmpUri);
                shareIntent.setType("image/jpeg");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "send"));
                return;
            }
            Log.d("response", "onFailure:");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.settings) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(new Intent(this, SettingActivity.class));
        return true;
    }

    public Uri getLocalBitmapUri(ImageView imageView2) {
        if (!(imageView2.getDrawable() instanceof BitmapDrawable)) {
            return null;
        }
        Bitmap bmp = ((BitmapDrawable) imageView2.getDrawable()).getBitmap();
        try {
            String path = Environment.getExternalStorageDirectory().getPath();
            File file = new File(path, ".Foldername/PlateRecognizer" + System.currentTimeMillis() + ".jpeg");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String compressImage(String filePath) {
        String str = filePath;
        int resized = this.sharedPreferences.getInt("Resize", -1);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(str, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = ((float) resized) * 7.0f;
        float maxWidth = ((float) resized) * 12.0f;
        float imgRatio = (float) (actualWidth / actualHeight);
        float maxRatio = maxWidth / maxHeight;
        if (((float) actualHeight) > maxHeight || ((float) actualWidth) > maxWidth) {
            if (imgRatio < maxRatio) {
                actualWidth = (int) (((float) actualWidth) * (maxHeight / ((float) actualHeight)));
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                actualHeight = (int) (((float) actualHeight) * (maxWidth / ((float) actualWidth)));
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16384];
        try {
            bmp = BitmapFactory.decodeFile(str, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        float ratioX = ((float) actualWidth) / ((float) options.outWidth);
        float ratioY = ((float) actualHeight) / ((float) options.outHeight);
        BitmapFactory.Options options2 = options;
        float middleX = ((float) actualWidth) / 2.0f;
        int i = actualHeight;
        float middleY = ((float) actualHeight) / 2.0f;
        int i2 = actualWidth;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        float f = ratioY;
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        Matrix matrix = scaleMatrix;
        float f2 = middleX;
        float f3 = middleY;
        canvas.drawBitmap(bmp, middleX - ((float) (bmp.getWidth() / 2)), middleY - ((float) (bmp.getHeight() / 2)), new Paint(2));
        try {
            int orientation = new ExifInterface(str).getAttributeInt("Orientation", 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix2 = new Matrix();
            if (orientation == 6) {
                matrix2.postRotate(90.0f);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix2.postRotate(180.0f);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix2.postRotate(270.0f);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix2, true);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        String filename = getFilename(this);
        try {
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, resized, new FileOutputStream(filename));
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
        }
        return filename;
    }
}