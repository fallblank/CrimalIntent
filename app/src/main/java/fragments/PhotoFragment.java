package fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import utils.PhotoUtils;

/**
 * Created by fallb on 2015/9/8.
 */
public class PhotoFragment extends DialogFragment{
    private static final String TAG = "PhotoFragment";
    private static final String EXTRA_IMAGE_PATH = "fragment.PhotoFragment.image_path";

    public static PhotoFragment newInstance(String imagePath){
        Bundle args = new Bundle();
        args.putString(EXTRA_IMAGE_PATH, imagePath);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);
        return fragment;
    }

    private ImageView mCrimePhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCrimePhoto = new ImageView(getActivity());
        String photoPath = getArguments().getString(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PhotoUtils.getScaleDrawable(getActivity(),photoPath);
        mCrimePhoto.setImageDrawable(image);
        mCrimePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoFragment.this.dismiss();
            }
        });
        return mCrimePhoto;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"check invoke dimiss whether this method be invoke or not");
        PhotoUtils.cleanImageView(mCrimePhoto);
    }
}
