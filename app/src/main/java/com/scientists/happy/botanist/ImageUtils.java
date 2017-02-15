// Rotate the image
// @author: Chia George Washington
package com.scientists.happy.botanist;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.IOException;
public class ImageUtils
{
    /**
     * Load scaled image
     * @param photoPath - path to image
     * @param width - width of image
     * @param height - height of image
     * @return Returns the scaled Bitmap
     */
    public static Bitmap loadScaledImage(String photoPath, int width, int height)
    {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / width, photoH / height);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        return ImageUtils.correctRotation(photoPath, bitmap);
    }

    /**
     * Rotate the image if need be
     * @param photoPath - string path to photo
     * @param bitmap - the image
     * @return Returns the angle to rotate the image
     */
    private static Bitmap correctRotation(String photoPath, Bitmap bitmap)
    {
        try
        {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
            {
                return rotateImage(bitmap, 90);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
            {
                return rotateImage(bitmap, 180);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
            {
                return rotateImage(bitmap, 270);
            }
        }
        catch (IOException e)
        {
        }
        return bitmap;
    }

    /**
     * Rotate the image
     * @param source - the image
     * @param angle - angle to rotate
     * @return Returns the rotated image
     */
    private static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}