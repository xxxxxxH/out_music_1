package net.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;
import java.text.DecimalFormat;


public class VideoUtils {
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        if (totalSeconds / 3600 > 0) {
            return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(totalSeconds / 3600), Integer.valueOf(minutes), Integer.valueOf(seconds)});
        }
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
    }


    public static int getScreenWidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static boolean removeMedia(Context ctx, String trackId, String data) {
        Uri uri = null;
        String idStr = null;
        boolean result = false;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        idStr = MediaStore.Video.Media._ID;

        result = deleteFile(data);
        if(result) {
            result = ctx.getContentResolver().delete(uri, idStr + "=" + trackId, null) > 0;
        }
        return result ;
    }

    private static boolean deleteFile(String path){
        boolean result = false;
        if(!TextUtils.isEmpty(path)){
            File file = new File(path);
            if(file.exists()){
                result = file.delete();
            }
        }
        return result;
    }


    public static String filesize(String file_path) {

        File file = new File(file_path);
        double length = file.length();

        //   length = length / 1024;
        return formatFileSize(length);
    }

    public static String formatFileSize(double size) {
        String hrSize = null;

        double b = size;
        double k = b / 1024.0;
        double m = k / 1024.0;
        double g = m / 1024.0;
        double t = g / 1024.0;

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

}
