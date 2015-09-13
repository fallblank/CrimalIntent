package models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import utils.PhotoUtils;

/**
 * Created by fallb on 2015/8/28.
 */
public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static final String FILE_NAME = "crime.json";

    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private CriminalIntentJSONSerializer mSerializer;

    private ArrayList<Crime> mCrimes;

    private CrimeLab(Context appContext) {
        mAppContext = appContext;
        mSerializer = new CriminalIntentJSONSerializer(mAppContext,FILE_NAME);
        try {
            mCrimes = mSerializer.loadCrimes();
        }catch (FileNotFoundException e){
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG,"Error: ",e);
        }

    }

    public static CrimeLab get(Context c) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public Crime getCrime(UUID id) {
        for (Crime c : mCrimes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public void addCrime(Crime crime){
        mCrimes.add(crime);
        return;
    }

    public boolean removeCrime(UUID id){
        Crime crime = getCrime(id);
        if(crime!=null){
            try {
                File file = PhotoUtils.getImageFile(mAppContext,crime.getPhoto().getFileName());
                if(file!=null && file.exists()){
                    file.delete();
                }
            } catch (Exception e) {
                Log.e(TAG,"error in getting image location",e);
            }
            mCrimes.remove(crime);
            return true;
        }
        return false;
    }

    public boolean savaCrimes(){
        try {
            mSerializer.savaCrimes(mCrimes);
            Log.i(TAG,"crimes saved to file");
            return true;
        }catch (Exception e){
            Log.e(TAG,"Error saving crimes: ",e);
            return false;
        }
    }
}
