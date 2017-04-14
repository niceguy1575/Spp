package tcp;

import java.io.*;
import java.util.zip.*;

public class CRC32get {
   String Filepath;
   long CRC32Value;

   public long getCRC32(String Filepath,byte[] filesize){
      System.out.println(filesize);
      return getCRC32Value(Filepath,filesize);
   }

    public long getCRC32Value(String filename,byte[] filesize) {
      Checksum crc = (Checksum) new CRC32();

      try {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
        byte[] buffer = filesize;
        int length = 0;

        while ((length = in.read(buffer)) >= 0)
          crc.update(buffer, 0, length);

        in.close();
      } catch (IOException e) {
          System.err.println(e);
          System.exit(2);
      }
      return crc.getValue();
    }
 }