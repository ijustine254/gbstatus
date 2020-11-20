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
import android.support.annotation.NonNull;
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
import android.graphics.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.Toolbar.*;
import android.util.*;
import java.util.*;
import java.text.*;
import android.provider.*;

import static instant.justine.me.ke.gbstatus.AllStatus.vid_exts;
import android.media.*;
import android.app.*;
import android.net.*;

public class MainActivity extends AppCompatActivity {

    private final int READ_SDCARD_CODE = 1;
    private final int WRITE_SDCARD_CODE = 2;
    private final Context context = MainActivity.this;
    private String app_home;
    private final String TAG = "gbstatus";
    private SharedPreferences preferences;
    private InputStream is = null;
    private OutputStream os = null;
    private AllStatus allStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
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
		final HomeDirectory home_dir = new HomeDirectory();
		if (!home_dir.hasStatus()) {
			Toast toast = Toast.makeText(this, "Check if WhatsApp is Installed", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		home_dir.createHome();
		allStatus = new AllStatus(home_dir.getStatusPage());
		LinearLayout ll = (LinearLayout) findViewById(R.id.container);
	    for (final String p:allStatus.getStatus() ) {
		    LinearLayout l1 = new LinearLayout(this);
		    ImageView img = new ImageView(this);
		    LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(
				    ViewGroup.LayoutParams.MATCH_PARENT,
				    ViewGroup.LayoutParams.WRAP_CONTENT
		    );
			img_params.setMargins(10,15, 10, 15);
			img_params.setLayoutDirection(LinearLayout.VERTICAL);
		    img.setLayoutParams(img_params);
			final String f = home_dir.getStatusPage()+"/"+p;
		    Bitmap bitmap = BitmapFactory.decodeFile(f);
			String ex = f.substring(f.lastIndexOf(".")+1);
			boolean isVideo = false;
			for (String ext: vid_exts) {
				if (ext.equals(ex)) {
					isVideo = true;
					int kind = MediaStore.Video.Thumbnails.MINI_KIND;
					bitmap = ThumbnailUtils.createVideoThumbnail(f,kind);
					break;
				}
			}
			if (isVideo) {
				img.setOnClickListener(new View.OnClickListener() {
					@Override 
					public void onClick(View view) {
						final Dialog dialog = new Dialog(context);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.vid);
						dialog.setCanceledOnTouchOutside(true);
						dialog.show();
						WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
							LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						lp.copyFrom(dialog.getWindow().getAttributes());
						dialog.getWindow().setAttributes(lp);
						final VideoView videoview = (VideoView) dialog.findViewById(R.id.vid_container);
						videoview.setVideoPath(f);
						videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer p1){
								dialog.dismiss();
							}
						});
						dialog.findViewById(R.id.vidButton).setOnClickListener(new OnClickListener(){
							@Override 
							public void onClick(View view) {
								save(f, home_dir);
							}
						});
						videoview.setKeepScreenOn(true);
						videoview.setZOrderOnTop(true);
						videoview.start();
					}
				});
			}
		    img.setImageBitmap(bitmap);
			img.setTag(f);
			img.setBackground(getResources().getDrawable(R.drawable.shape));
			img.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick(View view) {
					save(f, home_dir);
					return true;
				}
			});
			img.setPadding(10,10,10,10);
			l1.setBackgroundColor(Color.rgb(209,226,210));
			l1.setGravity(Gravity.CENTER);
		    l1.addView(img);
		    ll.addView(l1);
	    }

    }
	
	private void save(String src, HomeDirectory home_dir) {
		String t = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date());
		String ext = src.substring(src.lastIndexOf("."));
		new FileEngine().copy(src, home_dir.getAppHome()+"/GBSTATUS-"+t+ext);
		Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_LONG).show();
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
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
		System.exit(0);
	}
}
