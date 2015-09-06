package utils;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by fallb on 2015/8/30.
 */
public class UtilsHelper {


    public static boolean isXiaomi() {
        if (Build.MANUFACTURER.equals("Xiaomi")) return true;
        return false;
    }

    public static boolean isMiui() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            if (properties.getProperty("ro.miui.ui.version.name") != null){
                Log.i("UtilsHelper",properties.getProperty("ro.miui.ui.version.name"));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

