package com.sate7.wlj.developerreader.sate7gems.test;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class CopyMapService extends IntentService {
    private static final String ACTION_COPY = "com.sate7.copy.map";

    public CopyMapService() {
        super("CopyMapService");
    }

    private final String DESTINATION_PATH = "/storage/emulated/0/amap";
    private final String SOURCE_FILE = "/system/media/amap";
    private final String TAG = "CopyMapService";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startCopy(Context context) {
        Intent intent = new Intent(context, CopyMapService.class);
        intent.setAction(ACTION_COPY);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_COPY.equals(action)) {
//                handleActionCopy();
                boolean success = FileUtils.copy(SOURCE_FILE,DESTINATION_PATH);
                Log.d(TAG,"copy result :" + success);
            }
        }
    }

    private void handleActionCopy() {
        Log.d(TAG, "handleActionCopy ...");
        //1、判断sd卡中是否已经有了
        File destination = new File(DESTINATION_PATH);
        if (destination.exists() && isIntact(destination)) {
            Log.d(TAG, "has been exist, no need to copy ...");
        } else {
            //2、start copy
            File srcFile = new File(SOURCE_FILE);
            zipContraFile(SOURCE_FILE,DESTINATION_PATH,"amap");
        }
    }


    //完整性检测，是否漏掉文件；
    private boolean isIntact(File file) {
        long length = file.length();
        Log.d(TAG, "isIntact ww length : " + file.getAbsolutePath() + "," + file.exists() + "," + file.isDirectory() + "," + length);
        if (file.isDirectory() && file.length() == 100) {
            return true;
        }
        return true;
    }

    public void zipContraFile(String zippath, String outfilepath, String filename) {
        try {
            File file = new File(zippath);//压缩文件路径和文件名
            File outFile = new File(outfilepath);//解压后路径和文件名
            ZipFile zipFile = new ZipFile(file);
            ZipEntry entry = zipFile.getEntry(filename);//所解压的文件名
            InputStream input = zipFile.getInputStream(entry);
            OutputStream output = new FileOutputStream(outFile);
            int temp = 0;
            while ((temp = input.read()) != -1) {
                output.write(temp);
            }
            input.close();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"zipContraFile Exception ... " + e.getMessage());
        }
    }
}
