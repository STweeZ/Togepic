package com.example.myapplication.Utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Tools {

    private static int MIN_W =750;
    private static int MAX_W =4000;

    private static int MIN_H =750;
    private static int MAX_H =4000;


    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        // Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        // return _bmp;
        return output;
    }

    public static Uri getImageUri(ContentResolver cr, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(cr, inImage, "Title", null);
        return Uri.parse(path);
    }


    public static Bitmap getResizedBitmap(Bitmap image) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 75, out);
        image = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));


        int width = image.getWidth();
        int height = image.getHeight();


        if( height > MIN_H && height <MAX_H){
            return image;
        }

        float scaleWidth = ((float) MAX_W - MIN_W) / width;
        float scaleHeight = ((float) MAX_H - MIN_H) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                image, 0, 0, width, height, matrix, false);
        image.recycle();

        return resizedBitmap;
    }

}
