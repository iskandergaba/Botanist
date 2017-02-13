// Rotate the image
// @author: Chia George Washington
package com.scientists.happy.botanist;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.IOException;
public class ImageUtils
{
    /**
     * Rotate the image if need be
     * @param photoPath - string path to photo
     * @param bitmap - the image
     * @return Returns the angle to rotate the image
     */
    public static Bitmap correctRotation(String photoPath, Bitmap bitmap)
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