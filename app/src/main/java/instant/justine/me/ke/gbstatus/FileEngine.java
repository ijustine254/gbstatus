package instant.justine.me.ke.gbstatus;

import java.io.*;

class FileEngine {
    private InputStream is;
    private OutputStream os;
    void copy(String source, String dest) {
        Copier copier = new Copier(source, dest);
        copier.start();
    }
    class Copier extends Thread  {
        private String source;
        private String dest;
        Copier(String source, String dest) {
            this.source = source;
            this.dest = dest;
        }
        @Override
        public void run() {
            try {
                is = new FileInputStream(source);
                os = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                 try {
                     is.close();
                     os.close();
                 }  catch (IOException e) {
                     e.printStackTrace();
                 }
            }
        }

    }
}
