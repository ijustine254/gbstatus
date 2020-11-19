package instant.justine.me.ke.gbstatus;
import android.os.Environment;

import java.io.File;

class HomeDirectory {
    private String appHome;
    private String whatsappHome;
    HomeDirectory() {
        String dir = Environment.getExternalStorageDirectory().toString();
        this.appHome = dir+"/Pictures/gbstatus/";
        this.whatsappHome = dir+"/WhatsApp/Media/.Statuses/";
    }
    Boolean createHome() {
        File file = new File(this.appHome);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return false;
        }
    }
    String getStatusPage() {
        return this.whatsappHome;
    }

    public String getAppHome() {
        return appHome;
    }
}
