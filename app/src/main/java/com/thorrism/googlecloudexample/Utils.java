package com.thorrism.googlecloudexample;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;


/**
 * Created by Lucas Crawford on 3/4/2015.
 */
public class Utils {
    public static final String TAG     = "CLOUD_EXAMPLE";
    public static final int IMAGE_ELEM = 0;
    public static final int VIDEO_ELEM = 1;

    /**
     * Create toast for an activity based on the context
     */
    public static void createToast(String s, Context ctx){
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }


    /**
     * Get the file directory for our application where all media elements are stored. Creates
     * the directory if it doesn't already exist.
     * @return - File representing the applications directory
     */
    public static File getApplicationDirectory() {
        File directory = Environment.getExternalStorageDirectory();
        File path = new File(directory.getAbsolutePath() + File.separator +
                StorageConstants.APPLICATION_NAME_PROPERTY);
        if (!path.exists()) {
            if (!path.mkdirs()) {
                Log.d("Filmstrip", "failed to create directory");
                return null;
            }
        }
        return path;
    }

    /**
     * Get the string path for a file located from our media gallery.
     * @param activity - Current activity using the function
     * @param uri      - URI for the media file
     * @return         - Path of the URI file
     */
    public static String getPath(Activity activity, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        activity.startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Generate a bitmap for the media element we want to display. Re-sizes the bitmap to
     * fit in our target width. Also performs a center crop on the bitmap
     * @param path
     * @param targetWidth
     * @return
     */
    public static Bitmap getImageBitmap(String path, int targetWidth, boolean isOffset){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        //Create the bitmap options
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);

        //Create the bitmap
        bmOptions.inSampleSize = Math.min(bmOptions.outWidth/targetWidth, bmOptions.outHeight/targetWidth);
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        //Center crop offsets
        if(isOffset) {
            int offsetWidth = (bitmap.getWidth() - targetWidth) / 2;
            int offsetHeight = (bitmap.getHeight() - targetWidth) / 2;

            //Create a new bitmap cropped around the center of the bitmap
            return Bitmap.createBitmap(bitmap, offsetWidth, offsetHeight, targetWidth, targetWidth);
        }else{
            return bitmap;
        }
    }

    public static Bitmap getVideoBitmap(String path, int targetWidth){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path,
                MediaStore.Images.Thumbnails.MINI_KIND);

        //Center crop offsets
        int offsetWidth  = (bitmap.getWidth() - targetWidth)/2;
        int offsetHeight = (bitmap.getHeight() - targetWidth)/2;

        //Create a new bitmap cropped around the center of the bitmap
        return Bitmap.createBitmap(bitmap, offsetWidth, offsetHeight, targetWidth, targetWidth);
    }

    /**
     * Check if a file path exists in a directory.
     * @param directory - Directory we are checking
     * @param path      - path of the file we want to determine is in the directory or not.
     * @return          - True if exists, false if the path doesn't
     */
    public static boolean inDirectory(File directory, String path){
        for(File file : directory.listFiles()){
            String fileName = file.getAbsolutePath().split(directory.getAbsolutePath())[1];
            if(fileName.equals(File.separator + path)){
                return true;
            }
        }
        return false;
    }

}