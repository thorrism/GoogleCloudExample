package com.thorrism.googlecloudexample;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private Button mImageUploadBtn;
    private Button mVideoUploadBtn;
    private TextView mEmptyView;
    private File mDirectory;
    private ImageAdapter mAdapter;
    private GridView mGridView;
    private List<Element> mData;
    private List<String> mFileNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        mImageUploadBtn = (Button) findViewById(R.id.imageUploadBtn);
        mVideoUploadBtn = (Button) findViewById(R.id.videoUploadBtn);
        mEmptyView      = (TextView) findViewById(R.id.empty_text_view);
        mGridView       = (GridView) findViewById(R.id.image_gridview);
        mFileNames      = new ArrayList<String>();
        mData           = new ArrayList<Element>();

        mGridView.setColumnWidth(getScreenWidth());
        mGridView.setStretchMode(GridView.NO_STRETCH);
        addListeners();
        new FillTask().execute();
    }

    public void addListeners(){
        mImageUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(Utils.TAG, "Selecting image...");
                selectImage();
            }
        });

        mVideoUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(Utils.TAG, "Selecting Video...");
                selectVideo();
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createViewDialog(mData.get(position));
            }
        });
    }

    /**
     * Get the target width for the size of the bitmaps we want to display in the GridView.
     * Checks which orientation we are in and ensures we retain the same size for the bitmaps.
     * @return int representing GridView item size
     */
    public int getScreenWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //Always make the images the same size, regardless of landscape vs. portrait
        if(metrics.widthPixels < metrics.heightPixels)
            return (metrics.widthPixels / 3) - 15;
        else
            return (metrics.heightPixels / 3) - 15;
    }

    /**
     * Select an image from the gallery via an implicit intent
     */
    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    /**
     * Select a video from the gallery via an implicit intent
     */
    public void selectVideo(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, 0);
    }

    /**
     * Once the user has returned from choosing a video / image, upload it to the cloud.
     * @param reqCode - request made from calling an intent
     * @param resCode - result from the intent being called.
     * @param data    - Data the user has selected from their intent
     */
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == Activity.RESULT_OK && data != null){
            Uri uri = data.getData();
            if(uri != null)
                new UploadTask().execute(Utils.getPath(MainActivity.this, uri));
        }
    }

    /**
     * Fill the grid view with our new data. Initializes the GridView's adapter if need be,
     * otherwise updates the adapter with the new data.
     * @param data
     */
    public void fillData(final List<Element> data){
        mEmptyView.setVisibility(View.GONE);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mAdapter == null){
                    mAdapter = new ImageAdapter(getApplicationContext(), data);
                    mGridView.setAdapter(mAdapter);
                }
                else mAdapter.updateData(data);
            }
        });
    }

    /**
     * For every file in our directory, get a bitmap to display in our GridView.
     */
    public void populateGrid(){
        List<Element> paths = mData;
        int targetWidth     = getScreenWidth();
        Element element     = null; //Re-use object to improve java's GC

        //Go through each file in the directory and create new elements
        for(File file : mDirectory.listFiles()){
            String path = file.getAbsolutePath();
            String mimeType = path.substring(path.length()-4, path.length());
            if(!mFileNames.contains(path)){
                if(mimeType.equals(".jpg") || mimeType.equals("jpeg")) {
                    element = new Element(Utils.IMAGE_ELEM, Utils.getImageBitmap(path, targetWidth, true));
                }
                else if (mimeType.equals(".mp4")){
                    element = new Element(Utils.VIDEO_ELEM, Utils.getVideoBitmap(path, targetWidth));
                }
                if(element != null){
                    element.setPath(path);
                    paths.add(element);
                    fillData(paths);
                    mFileNames.add(path);
                }
            }
        }
        mData = paths;
    }

    /**
     * Create a dialog to view an image or video element. A video element allows the user to
     * watch the video, and the image element just displays the image larger.
     * @param element - Element the user wishes to view
     */
    public void createViewDialog(Element element){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View mHolderView        = inflater.inflate(R.layout.view_dialog, null);
        final Dialog dialog = new Dialog(this);

        //Check the type of our element (video or image)
        if(element.getType() == Utils.VIDEO_ELEM){
            Intent intent = new Intent(MainActivity.this, ShowVideo.class);
            intent.putExtra("PATH", element.getPath());
            startActivity(intent);
        }else{
            ImageView mImageView = (ImageView) mHolderView.findViewById(R.id.image_element_view);
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(Utils.getImageBitmap(element.getPath(), getScreenWidth(), false));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(mHolderView);
            dialog.show();
        }
    }

    /**
     * AsyncTask to fill our GridView with the files in the current directory for our application.
     */
    private class FillTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(Void... params){
            mDirectory = Utils.getApplicationDirectory();
            populateGrid();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            setProgressBarIndeterminateVisibility(false);
            if(mData.size() == 0) mEmptyView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * AsyncTask to upload a media element to the cloud.
     */
    private class UploadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute(){
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params){
            StorageConstants.CONTEXT = getApplicationContext();
            try{
                StorageUtils.uploadFile(StorageConstants.BUCKET_NAME, params[0]);
            }catch(Exception e){
                Log.d("Failure", "Exception: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Utils.createToast("Upload complete!", MainActivity.this);
            setProgressBarIndeterminateVisibility(false);
            new DownloadTask().execute();
        }
    }

    /**
     * AsyncTask to download all media elements we don't already have in our directory.
     */
    private class DownloadTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String outputPath = mDirectory.getAbsolutePath();
            try{
                List<String> fileNames = StorageUtils.listBucket(StorageConstants.BUCKET_NAME);
                for(String file : fileNames){

                    //Download the file only if it isn't already in our directory.
                    if(!Utils.inDirectory(mDirectory, file))
                        StorageUtils.downloadFile(StorageConstants.BUCKET_NAME, file, outputPath);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            populateGrid();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            setProgressBarIndeterminateVisibility(false);
            Utils.createToast("Download complete!", MainActivity.this);
        }
    }
}
