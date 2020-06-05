package com.ltb.laer.waterview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class ImageUtils {

    public static Bitmap rotateBitmap(ImageView imageView, int orientationDegree) {


        Bitmap bm =((BitmapDrawable)imageView.getDrawable()).getBitmap();

        //方便判断，角度都转换为正值
        int degree = orientationDegree;
        if( degree < 0){
            degree = 360 + orientationDegree;
        }

        int srcW = bm.getWidth();
        int srcH = bm.getHeight();

        Matrix m = new Matrix();
        m.setRotate(degree, (float) srcW / 2, (float) srcH / 2);
        float targetX, targetY;

        int destH = srcH;
        int destW = srcW;

        //根据角度计算偏移量，原理不明
        if (degree == 90 ) {
            targetX = srcH;
            targetY = 0;
            destH = srcW;
            destW = srcH;
        } else if( degree == 270){
            targetX = 0;
            targetY = srcW;
            destH = srcW;
            destW = srcH;
        }else if(degree == 180){
            targetX = srcW;
            targetY = srcH;
        }else {
            return bm;
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        //注意destW 与 destH 不同角度会有不同
        Bitmap bm1 = Bitmap.createBitmap(destW, destH, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);
        return bm1;
    }

}
