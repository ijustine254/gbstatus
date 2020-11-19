package instant.justine.me.ke.gbstatus;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import java.util.HashSet;


public class FileChecker extends Service {

    private final String TAG = "myService";
    private Handler handler = new Handler();
    private SharedPreferences preferences;
    private HomeDirectory home;
    private AllStatus allStatus;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startWatching();
        home = new HomeDirectory();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = getSharedPreferences("appData", MODE_PRIVATE);
        HomeDirectory homeDirectory = new HomeDirectory();
        AllStatus allStatus1 = new AllStatus(homeDirectory.getAppHome());
        for (String status : preferences.getStringSet("status_set", new HashSet<String>())) {
            if (!allStatus1.getStatus().contains(status)) {
                FileEngine fileEngine = new FileEngine();
                fileEngine.copy(homeDirectory.getStatusPage()+status, homeDirectory.getAppHome()+status);
            }
        }
        return START_STICKY;
    }

    private void startWatching() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, 4000);
            }
        });
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (preferences.contains("count")) {
                new HomeDirectory().createHome();
                int count = preferences.getInt("count", 0);
                allStatus = new AllStatus(home.getStatusPage());
                if (count !=0 && allStatus.count() > count) {
                    // download all the status that have not been download
                    for (String status : preferences.getStringSet("status_set", new HashSet<String>())) {
                        if (!allStatus.getStatus().contains(status)) {
                            FileEngine fileEngine = new FileEngine();
                            fileEngine.copy(home.getStatusPage()+status, home.getAppHome()+status);
                        }
                    }
                    SharedPreferences.Editor e = preferences.edit();
                    e.putInt("count", allStatus.count());
                    e.putStringSet("status_set", new HashSet<>(allStatus.getStatus()));
                    e.apply();
                }
            }
            handler.postDelayed(runnable, 4000);
        }
    };

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
