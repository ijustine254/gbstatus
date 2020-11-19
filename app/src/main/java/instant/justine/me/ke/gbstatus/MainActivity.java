package instant.justine.me.ke.gbstatus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final int READ_SDCARD_CODE = 1;
    private final int WRITE_SDCARD_CODE = 2;
    private final Context context = MainActivity.this;
    private String app_home;
    private final String TAG = "gbstatus";
    private SharedPreferences preferences;
    private TextView files_holder;
    private InputStream is = null;
    private OutputStream os = null;
    private AllStatus allStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        files_holder = (TextView) findViewById(R.id.files_string);
        preferences = getSharedPreferences("appData", MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!preferences.contains("read") && !preferences.contains("write")) {
                requestForPermission();
            } else {
                app();
            }
        } else {
            app();
        }

    }

    private void app () {
        ActionBar bar = getSupportActionBar();
        Objects.requireNonNull(bar).setTitle(R.string.home);
        app_home = Environment.getExternalStorageDirectory().toString();
        String home = app_home+"/WhatsApp/Media/.Statuses";
        allStatus = new AllStatus(home);
        File file = new File(home);
        files_holder.setText(allStatus.getStatusString());
        Boolean isHomeMade = new HomeDirectory().createHome();
        if (allStatus.getStatus() != null) {
            SharedPreferences.Editor e = preferences.edit();
            e.putInt("count", allStatus.count());
            Set<String> set = new HashSet<>(allStatus.getStatus());
            e.putStringSet("status_set", set);
            e.apply();
        }
        int count = 0;
	    LinearLayout ll = (LinearLayout) findViewById(R.id.container);
	    for (String p:allStatus.getStatus() ) {
		    if (count > 4) break;
		    LinearLayout l1 = new LinearLayout(this);
		    ImageView img = new ImageView(this);
		    LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(
				    ViewGroup.LayoutParams.WRAP_CONTENT,
				    ViewGroup.LayoutParams.WRAP_CONTENT
		    );
		    img.setLayoutParams(img_params);
		    Bitmap bitmap = BitmapFactory.decodeFile(home+"/"+p);
		    img.setImageBitmap(bitmap);
		    l1.addView(img);
		    ll.addView(l1);
		    count++;
	    }

    }

    @SuppressLint("NewApi")
    private void requestForPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showDialog(
                        getResources().getString(R.string.title),
                        getResources().getString(R.string.fetch)
                );
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_SDCARD_CODE);
            }
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showDialog(
                        getResources().getString(R.string.title),
                        getResources().getString(R.string.create)
                );
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_SDCARD_CODE);
            }
        }
    }

    private void showDialog (String title, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestForPermission();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_SDCARD_CODE) {

            if (
                    grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("read", "allowed");
                editor.apply();
            }
        }
        if (requestCode == READ_SDCARD_CODE) {

            if (
                    grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("write", "allowed");
                editor.apply();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                //Intent intent = new Intent(this, Setttings.class);
                //startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
