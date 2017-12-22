package us.mifeng.administrator.loadfile;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/12/21.
 */

public class FileUtils {
    public static boolean isSD(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    public static File getFile(){
        File rootFile=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file=new File(rootFile,"mkt");
        if (!file.exists()){
            file.mkdirs();
        }
        return file;
    }
}
