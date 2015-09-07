package models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fallb on 2015/9/7.
 */
public class Photo {
    private static final String JSON_FILE_NAME = "fileName";
    private String mFileName;

    public Photo(String fileName){
        mFileName = fileName;
    }

    public Photo(JSONObject json) throws JSONException {
        mFileName = json.getString(JSON_FILE_NAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILE_NAME,mFileName);
        return json;
    }

    public String getFileName(){
        return mFileName;
    }
}
