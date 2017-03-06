// Rotate the image
// @author: Chia George Washington
package com.scientists.happy.botanist;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
public class ImageUtils {

    /**
     * Rotate the image if need be
     * @return Returns the angle to rotate the image
     */
    public static Bitmap correctRotation(Context context, Uri photoUri) {
        try {
            String photoPath = getPath(context, photoUri);
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return rotateImage(bitmap, 90);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return rotateImage(bitmap, 180);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return rotateImage(bitmap, 270);
            }
        }
        catch (IOException ignored) {
        }
        return null;
    }

    /**
     * Rotate the image
     * @param source - the image
     * @param angle - angle to rotate
     * @return Returns the rotated image
     */
    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static String getPath(Context context, Uri uri) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}