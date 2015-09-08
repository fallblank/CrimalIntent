package utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.example.fallb.criminalintent.R;

import java.io.File;
import java.io.IOException;


/**
 * Created by fallb on 2015/9/8.
 */
public class PhotoUtils {

    private static final String TAG = "PhotoUtils";

    public static File createImageFile(Activity activity, String fileName) throws IOException {
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                activity.getString(R.string.app_name));
        Log.d(TAG, storageDir.getAbsolutePath());
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(TAG, "error in making dirs");
                return null;
            }
        }

        File imageFile = new File(storageDir, fileName);
        if (!imageFile.exists()) {
            imageFile.createNewFile();
        }
        Log.d(TAG, imageFile.getAbsolutePath());
        return imageFile;
    }

    public static BitmapDrawable getScaleDrawable(Activity activity, String path) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if(srcHeight>destHeight||srcWidth>destWidth){
            if(srcWidth>srcHeight){
                inSampleSize = Math.round(srcHeight/destHeight);
            }else {
                inSampleSize = Math.round(srcWidth/destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return new BitmapDrawable(activity.getResources(),bitmap);
    }

    public static  void cleanImageView(ImageView imageView){
        if(!(imageView.getDrawable() instanceof BitmapDrawable))   return;
        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
        return;
    }
}
