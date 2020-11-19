package instant.justine.me.ke.gbstatus;

import java.io.File;
import java.util.ArrayList;

class AllStatus {
    private ArrayList<String> statusArrayList;
    private String statusString = "";

    AllStatus (String path) {
        File dir = new File(path);
        File[] flist = dir.listFiles();
        statusArrayList = new ArrayList<>();
        for (File file : flist) {
            if (file.isFile()) {
                statusString += file.getName() + " \n ";
                statusArrayList.add(file.getName());
            }
        }
    }

    String getStatusString() {
        return statusString;
    }

    ArrayList<String> getStatus() {
        return statusArrayList;
    }

    int count() {
        return statusArrayList.size();
    }
}
