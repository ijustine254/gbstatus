package instant.justine.me.ke.gbstatus;

import java.io.File;
import java.util.ArrayList;

class AllStatus {
    private ArrayList<String> statusArrayList;
    private String statusString = "";
	private String[] img_exts = {"jpg","jpeg","png"};
	static String[] vid_exts = {"mkv", "3gp", "mp4"};
    AllStatus (String path) {
        File dir = new File(path);
        File[] flist = dir.listFiles();
        statusArrayList = new ArrayList<>();
        for (File file : flist) {
            if (file.isFile()) {
				String name = file.getName();
				if (!hasExtension(name)) continue;
                statusString += name + " \n ";
                statusArrayList.add(name);
            }
        }
    }
	
	private boolean hasExtension(String f) {
		String ex = f.substring(f.lastIndexOf(".")+1);
		for (String e: img_exts) {
			if (e.equals(ex)) return true;
		}
		for (String e: vid_exts) {
			if (e.equals(ex)) return true;
		}
		return false;
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
