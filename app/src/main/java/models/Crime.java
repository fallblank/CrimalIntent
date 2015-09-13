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
    public static final String JSON_PHOTO = "photo";
    public static final String JSON_SUSPECT = "suspect";


    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Photo mPhoto;
    private String mSuspect;

    public Crime() {
        //use Universally Unique Identifier creating a id,which unique identity a crime event
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(JSONObject object) throws JSONException {
        mId = UUID.fromString(object.getString(JSON_ID));
        if (object.has(JSON_TITLE)) {
            mTitle = object.getString(JSON_TITLE);
        }
        mSolved = object.getBoolean(JSON_SOLVED);
        mDate = new Date(object.getLong(JSON_DATE));
        if (object.has(JSON_PHOTO)) {
            mPhoto = new Photo(object.getJSONObject(JSON_PHOTO));
        }
        if (object.has(JSON_SUSPECT)) {
            mSuspect = object.getString(JSON_SUSPECT);
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(JSON_ID, mId.toString());
        object.put(JSON_TITLE, mTitle);
        object.put(JSON_DATE, mDate.getTime());
        object.put(JSON_SOLVED, mSolved);
        if (mPhoto != null) {
            object.put(JSON_PHOTO, mPhoto.toJSON());
        }
        object.put(JSON_SUSPECT, mSuspect);
        return object;
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

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    @Override
    public String toString() {
        return mTitle;
    }


}
