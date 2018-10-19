package com.example.asus.instagram.Upload;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.example.asus.instagram.Profile.AccountSettingsActivity;
import com.example.asus.instagram.Profile.ProfileActivity;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.FirebaseMethods;
import com.example.asus.instagram.Utils.Permissions;

/**
 * Created by Yiqun
 */
public class PhotoFragment extends Fragment{
    private static final String TAG = "PhotoFragment";

    //constant
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int CAMERA_REQUEST_CODE = 5;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private String mSelectedImage;
    private boolean flashOn = false;

    private CapturePreview cPreview;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_photo_from_camera,container,false);

        // Create a container that will hold a SurfaceView for camera previews
        cPreview = new CapturePreview(getActivity());

        // Add CapturePreview object to the FrameLayout.
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.SurfaceView);
        preview.addView(cPreview);

        // Associates setFlashOn method to the flash button in the layout.
        RelativeLayout flashoptions = (RelativeLayout) rootView.findViewById(R.id.flash_op);
        flashoptions.bringToFront();
        cPreview.setFlashOn(flashOn);

        // Associates button press to invoking takePicture method in CapturePreview class.
        RelativeLayout takepic = (RelativeLayout) rootView.findViewById(R.id.take_pic_button);
        takepic.bringToFront();
        Button captureButton = (Button) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cPreview.takePicture();


                    }
                }
        );

        // Listener for the flash togglebutton calls FlashToggle method in CapturePreview class.
        ToggleButton flashButton = (ToggleButton) rootView.findViewById(R.id.btn_flash);
        flashButton.setOnCheckedChangeListener(

                new CompoundButton.OnCheckedChangeListener() {

                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // get an image from the camera
                        flashOn = isChecked;
                        cPreview.FlashToggle();
                    }
                }
        );
        return rootView;
    }


    // Whenever fragment is resumed check if current CapturePreview object is still active.
    // If not create new CapturePreview object and arrange buttons as per onCreateView.
    @Override
    public void onResume() {
        super.onResume();
        if (!cPreview.active) {
            cPreview = new CapturePreview(getActivity());
            FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.SurfaceView);
            preview.addView(cPreview);
        }

        RelativeLayout flashoptions = (RelativeLayout) rootView.findViewById(R.id.flash_op);
        flashoptions.bringToFront();
        cPreview.setFlashOn(flashOn);

        RelativeLayout takepic = (RelativeLayout) rootView.findViewById(R.id.take_pic_button);
        takepic.bringToFront();
    }

    // onPause
    @Override
    public void onPause() {
        super.onPause();
        cPreview.photoFragmentPause();          // release the camera immediately on pause event
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
}















