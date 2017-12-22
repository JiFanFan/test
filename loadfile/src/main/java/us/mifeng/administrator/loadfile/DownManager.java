package us.mifeng.administrator.loadfile;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/12/21.
 */

public class DownManager {
    private String path;
    private final ThreadPoolExecutor pool;
    private final File rootFile;
    private final String fileName;
    private RandomAccessFile raf;
    private int totalLength;
    private long downedSize=0;
    private MyThread myThread;
    public boolean isDowned=true;
    public DownManager(String path) {
        this.path = path;
        pool = new ThreadPoolExecutor(5,5,50, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(2000));
        rootFile = FileUtils.getFile();
        fileName = path.substring(path.lastIndexOf("/")+1);
    }
    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            //下载数据
            loaddownFile();
        }
    }

    private void loaddownFile() {
        try {
            URL url=new URL(path);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            //获取文件存储对象
            File file=new File(rootFile,fileName);
            if (!file.exists()){
                raf = new RandomAccessFile(file,"rwd");
            }else {
                if (raf==null){
                    raf=new RandomAccessFile(file,"rwd");
                    //获取下载长度
                    totalLength = conn.getContentLength();
                }else {
                    //获取文件的长度
                    downedSize = file.length();
                    raf.seek(downedSize);
                    //设置网络数据的下载位置
                    conn.setRequestProperty("Range","bytes="+downedSize+"-"+totalLength);
                    conn.connect();
                }
            }
            InputStream in=conn.getInputStream();
            int len=0;
            byte[]by=new byte[1024];
            long endTime=System.currentTimeMillis();
            while ((len=in.read(by))!=-1&&isDowned){
                raf.write(by,0,len);
                downedSize+=len;
                if (System.currentTimeMillis()-endTime>500){
                    DecimalFormat format=new DecimalFormat("#0.00");
                    double dvalue=downedSize/(totalLength*1.0);
                    String svalue=format.format(dvalue)+"%";
                    Log.i("tag",svalue);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        if (myThread==null){
            this.isDowned=true;
            myThread=new MyThread();
            pool.execute(myThread);
        }
    }
    public void pause(){
        if (myThread!=null){
            this.isDowned=false;
            pool.remove(myThread);
            myThread=null;
        }
    }
}
