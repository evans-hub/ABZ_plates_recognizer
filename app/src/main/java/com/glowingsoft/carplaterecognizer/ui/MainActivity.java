package com.glowingsoft.carplaterecognizer.ui;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.R;
import com.glowingsoft.carplaterecognizer.api.WebRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity  implements IPickResult,View.OnClickListener {
    ImageView imageView,emptyImage;
    TextView plate_txt,region_txt,vihicle_txt;
    Context context;
    ImageButton editResult;
    Button nextImage,check_in,check_out;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    String SHARED_PREF_NAME ="user_pref";
    String token = "";
    String countrycode="";
    Date date;
    DateFormat df;
    String plate_type="",region_type="",car_type="",last_digits="",timeStamp="";
    CardView plateCard,regionCard,vihicalCard;
    FloatingActionButton floatingActionButton;
    String imagepath;
    String Database_Path ="Employees";
    String DB;
    ProgressDialog load;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        date = new Date();
        df = new SimpleDateFormat("MM/dd/");
        // Use London time zone to format the date in
        df.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        nextImage=findViewById(R.id.next_image);
        check_in=findViewById(R.id.check_in_image);
        check_out=findViewById(R.id.check_out_image);
        nextImage.setOnClickListener(this);
        check_in.setOnClickListener(this);
        check_out.setOnClickListener(this);
        floatingActionButton=findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        progressBar=findViewById(R.id.homeprogress);
        plate_txt = findViewById(R.id.car_plate);
        region_txt = findViewById(R.id.region_code);
        vihicle_txt = findViewById(R.id.vihicle_type);
        emptyImage=findViewById(R.id.empty_image);
        plateCard=findViewById(R.id.cardView);
        vihicalCard=findViewById(R.id.cardView3);
        regionCard=findViewById(R.id.cardView2);
        editResult=findViewById(R.id.setting_edit_btn);
        editResult.setOnClickListener(this);
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        load=new ProgressDialog(this);
    }
    //dialoag box setup
    @SuppressLint("WrongConstant")
    PickSetup setup = new PickSetup()
            .setCancelText("Cancel")
            .setFlip(true)
            .setMaxSize(50)
            .setWidth(50)
            .setHeight(50)
            .setProgressText("Loading Image")
            .setPickTypes(EPickType.CAMERA)
            .setCameraButtonText("Camera")
            .setIconGravity(Gravity.TOP)
            .setButtonOrientation(LinearLayoutCompat.HORIZONTAL)
            .setSystemDialog(false)
            .setCameraIcon(R.drawable.cam);

    //to change token value
    @Override
    protected void onResume() {
        super.onResume();
//        String editvisibility=plate_txt.getText().toString();
        if( sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked", false)) {
            editResult.setVisibility(View.VISIBLE);
        }
        else {
            editResult.setVisibility(View.GONE);
        }
        last_digits=sharedPreferences.getString("LastDigits", "");
//        Toast.makeText(MainActivity.this, last_digits, Toast.LENGTH_SHORT).show();
        token=sharedPreferences.getString("CarToken", "");
        if (token.equals("")){
            Toast.makeText(context, "Token Not Found", Toast.LENGTH_SHORT).show();
        }else {
            WebRequest.client.addHeader("Authorization","Token "+token);
        }
    }
    //pick result method to get image after getting image form gallary or camera
    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            RequestParams params=new RequestParams();
            String file=r.getPath();
            String compressed=compressImage(file);
            countrycode=sharedPreferences.getString("RegionCode","");
            String baseurl=sharedPreferences.getString("BaseUrl","https://api.platerecognizer.com/v1/plate-reader/");


            Log.d("response", "filepath: "+file+" ");
            try {
                params.put("upload", new File(compressed));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            params.put("regions",countrycode);
            Log.d("response", "image to upload: "+params+" ");
            WebRequest.post(context,baseurl,params,new JsonHttpResponseHandler()
            {
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    region_txt.setText(null);
                    plate_txt.setText(null);
                    vihicle_txt.setText(null);
                    imageView.setImageResource(R.drawable.upload);

                    Log.d("response", "onStart: ");
                    super.onStart();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    Log.d("response ",response.toString()+" ");
                    try {
                        //image path
                        imagepath="https://us-east-1.linodeobjects.com/platerec-api/uploads/"+df.format(date)+response.getString("filename");
                        //json array or results
                        JSONArray Jsresults = response.getJSONArray("results");
                        if (Jsresults.length()>0)
                        {
                            for (int i = 0; i < Jsresults.length(); i++) {
                                JSONObject tabObj = Jsresults.getJSONObject(i);
                                plate_txt.setText(tabObj.getString("plate"));
                                //region_txt.setText(tabObj.getJSONObject("region").getString("code"));
                               // vihicle_txt.setText(tabObj.getJSONObject("vehicle").getString("type"));
                                timeStamp=response.getString("timestamp");
                                //Toast.makeText(MainActivity.this, "plate"+plate_txt.getText().toString(), Toast.LENGTH_LONG).show();
                                    Picasso.with(context)
                                            .load(imagepath)
                                            .into(imageView, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    Date date=new Date();
                                                    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
                                                    String day=sdf.format(date);
                                                    progressBar.setVisibility(View.GONE);
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Database_Path);
                                                    String county=plate_txt.getText().toString();
                                                    reference.child(county).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                                            if (snapshot.exists()){
                                                                DB = snapshot.child("name").getValue(String.class);
                                                                String idno = snapshot.child("id").getValue(String.class);
                                                               String mode = snapshot.child("model").getValue(String.class);
                                                                //Toast.makeText(MainActivity.this, "name is "+DB, Toast.LENGTH_LONG).show();
                                                                regionCard.setVisibility(View.VISIBLE);
                                                                plateCard.setVisibility(View.VISIBLE);
                                                                vihicalCard.setVisibility(View.VISIBLE);
                                                                check_in.setVisibility(View.VISIBLE);
                                                                check_out.setVisibility(View.VISIBLE);
                                                                nextImage.setVisibility(View.GONE);
                                                                floatingActionButton.setVisibility(View.GONE);
                                                                emptyImage.setVisibility(View.GONE);
                                                                vihicle_txt.setText(mode);
                                                                region_txt.setText(idno);
                                                                if (mode.isEmpty()){
                                                                    Toast.makeText(MainActivity.this, "Car plate not available", Toast.LENGTH_SHORT).show();
                                                                }
                                                                if (idno.isEmpty()){
                                                                    Toast.makeText(MainActivity.this, "Car plate not available", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull  DatabaseError error) {

                                                        }
                                                    });
                                                }
                                                @Override
                                                public void onError() {
                                                    regionCard.setVisibility(View.VISIBLE);
                                                    plateCard.setVisibility(View.VISIBLE);
                                                    vihicalCard.setVisibility(View.VISIBLE);
                                                    nextImage.setVisibility(View.VISIBLE);
                                                    check_in.setVisibility(View.VISIBLE);
                                                    check_out.setVisibility(View.VISIBLE);
                                                    floatingActionButton.setVisibility(View.GONE);
                                                    emptyImage.setVisibility(View.GONE);
                                                }
                                            });

                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("response1", "onFailure: " + errorResponse + " ");
                    progressBar.setVisibility(View.GONE);
                    editResult.setVisibility(View.GONE);
                    regionCard.setVisibility(View.GONE);
                    plateCard.setVisibility(View.GONE);
                    vihicalCard.setVisibility(View.GONE);
                    nextImage.setVisibility(View.GONE);
                    check_in.setVisibility(View.GONE);
                    check_out.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                    emptyImage.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, errorResponse+"", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("response2", "onFailure: "+errorResponse+" ");
                    progressBar.setVisibility(View.GONE);
                    editResult.setVisibility(View.GONE);
                    regionCard.setVisibility(View.GONE);
                    plateCard.setVisibility(View.GONE);
                    vihicalCard.setVisibility(View.GONE);
                    nextImage.setVisibility(View.GONE);
                    check_in.setVisibility(View.GONE);
                    check_out.setVisibility(View.GONE);
                    emptyImage.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,errorResponse.toString()+"",Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("response3", "onFailure: "+responseString+" ");
                    progressBar.setVisibility(View.GONE);
                    editResult.setVisibility(View.GONE);
                    regionCard.setVisibility(View.GONE);
                    plateCard.setVisibility(View.GONE);
                    vihicalCard.setVisibility(View.GONE);
                    nextImage.setVisibility(View.GONE);
                    check_in.setVisibility(View.GONE);
                    check_out.setVisibility(View.GONE);
                    emptyImage.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,responseString+"No Internet Connection",Toast.LENGTH_LONG).show();
                }
            });
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check if request code is for inserting new list then perform insertion
        if (requestCode == 123 && resultCode == RESULT_OK) {
            String plate = data.getStringExtra("car_plate");
            String region = data.getStringExtra("region_code");
            String car = data.getStringExtra("car_type");
            Log.d("response", "onActivityResult: "+plate+" ");
            plate_txt.setText(plate);
            region_txt.setText(region);
            vihicle_txt.setText(car);
            Toast.makeText(this, "Results saved", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.imageView)
        {
            if (token.isEmpty()) {
                Toast.makeText(this,"Go to Settings to Set Your Token", Toast.LENGTH_LONG).show();
                return;
            }
            PickImageDialog.build(setup).show(MainActivity.this);
        }
        plate_type = plate_txt.getText().toString();
        region_type=region_txt.getText().toString();
        car_type=vihicle_txt.getText().toString();
        if (v.getId()==R.id.setting_edit_btn)
        {


            if (plate_type.isEmpty())
            {
                Toast.makeText(MainActivity.this,"Nothing to Edit Now",Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("car_plate", plate_type);
                intent.putExtra("region_code", region_type);
                intent.putExtra("car_type", car_type);
                startActivityForResult(intent, 123);
            }
        }
        if (v.getId()==R.id.next_image)
        {
            PickImageDialog.build(setup).show(MainActivity.this);

        }
        if (v.getId()==R.id.check_in_image){
            Date date=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
            String day=sdf.format(date);
            load.setTitle("Sign in");
            load.setMessage("Checking in please wait..");
            load.setCanceledOnTouchOutside(true);
            load.show();

            String time= String.valueOf(System.currentTimeMillis());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Database_Path);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    reference.child(plate_type).child("time in").setValue(time);
                    load.dismiss();

                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Check In")
                            .setMessage("Checking in Success")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: Add positive button action code here
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        private static final int AUTO_DISMISS_MILLIS = 2000;
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                            final CharSequence negativeButtonText = defaultButton.getText();
                            new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }
                                @Override
                                public void onFinish() {
                                    if (((AlertDialog) dialog).isShowing()) {
                                        dialog.dismiss();
                                        nextImage.setVisibility(View.VISIBLE);
                                        check_in.setVisibility(View.GONE);
                                        check_out.setVisibility(View.GONE);
                                        plate_txt.setText(null);
                                        region_txt.setText(null);
                                        vihicle_txt.setText(null);
                                    }
                                }
                            }.start();
                        }
                    });
                    dialog.show();
                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });
        }
        if (v.getId()==R.id.check_out_image){
            Date date=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
            String day=sdf.format(date);
            load.setTitle("Sign out");
            load.setMessage("Checking out please wait..");
            load.setCanceledOnTouchOutside(true);
            load.show();

            String time= String.valueOf(System.currentTimeMillis());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Database_Path);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    reference.child(plate_type).child("time out").setValue(time);
                    load.dismiss();

                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Check Out")
                            .setMessage("Checking Out Success")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: Add positive button action code here
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        private static final int AUTO_DISMISS_MILLIS = 2000;
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                            final CharSequence negativeButtonText = defaultButton.getText();
                            new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }
                                @Override
                                public void onFinish() {
                                    if (((AlertDialog) dialog).isShowing()) {
                                        dialog.dismiss();
                                        nextImage.setVisibility(View.VISIBLE);
                                        check_in.setVisibility(View.GONE);
                                        check_out.setVisibility(View.GONE);
                                        plate_txt.setText(null);
                                        region_txt.setText(null);
                                        vihicle_txt.setText(null);
                                    }
                                }
                            }.start();
                        }
                    });
                    dialog.show();
                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });
        }
        if (v.getId()==R.id.fab)
        {
            String plate = plate_txt.getText().toString();
            String region = region_txt.getText().toString();
            String car = vihicle_txt.getText().toString();
            Uri bmpUri = getLocalBitmapUri(imageView);
            //set it as current date.
            String share="Date & TimeStamp: "+timeStamp+"\nCar Plate: "+plate+"\nRegion Code: "+region+"\nVihicle Type: "+car+"\nToken Code: "+last_digits;
            Log.d("response", "onActivityResult: "+plate+" ");
            if (bmpUri != null) {
                Uri imageUri = Uri.parse("android.resource://" + getPackageName()
                        + "/drawable/" + "ic_launcher");
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, share);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/jpeg");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "send"));
            }
            else {
                Log.d("response", "onFailure:");
            }


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.settings)
        {
            Intent intent =new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStorageDirectory().getPath(),
              ".Foldername/PlateRecognizer" + System.currentTimeMillis() + ".jpeg");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public String compressImage(String filePath) {

        int resized=sharedPreferences.getInt("Resize", -1);

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight =resized*7.0f;
        float maxWidth = resized*12.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth)
        {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;

            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth,
                actualHeight);
        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth,
                    actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        //      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename(this);
        try {
            out = new FileOutputStream(filename);
            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, resized, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }
    public static String getFilename(Context context) {
        File file = new File(context.getFilesDir().getPath(), ".Foldername/PlateRecognizerHistory");

        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

        return uriSting;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;

        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height/ (float)
                    reqHeight);

            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        }       final float totalPixels = width * height;

        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }
}
