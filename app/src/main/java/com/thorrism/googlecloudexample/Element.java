package com.thorrism.googlecloudexample;
import android.graphics.Bitmap;

/**
 * Created by Lucas Crawford on 3/29/2015.
 */
public class Element {
    private int type;
    private String path;
    private Bitmap bitmap;

    public Element(int type, Bitmap bitmap){
        this.type   = type;
        this.bitmap = bitmap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }
}
