package models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by fallb on 2015/8/27.
 * Description:this is a model class,each instance can stand for a crime event
 */
public class Crime {
    public static final String JSON_ID = "id";
    public static final String JSON_TITLE = "title";
    public static final String JSON_SOLVED = "solved";
    public static final String JSON_DATE = "date";


    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime(){
        //use Universally Unique Identifier creating a id,which unique identity a crime event
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(JSONObject object) throws JSONException {
        mId = UUID.fromString(object.getString(JSON_ID));
        if(object.has(JSON_TITLE)){
            mTitle = object.getString(JSON_TITLE);
        }
        mSolved = object.getBoolean(JSON_SOLVED);
        mDate = new Date(object.getLong(JSON_DATE));
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(JSON_ID,mId.toString());
        object.put(JSON_TITLE,mTitle);
        object.put(JSON_DATE,mDate.getTime());
        object.put(JSON_SOLVED,mSolved);
        return object;
    }
}
