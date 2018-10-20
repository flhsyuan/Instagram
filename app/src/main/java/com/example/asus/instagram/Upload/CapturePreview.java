package com.example.asus.instagram.Upload;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.asus.instagram.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CapturePreview extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = "CapturePreview";

    // Attributes for each CaptuerPreview instance.
    public File pictureFile;
    SurfaceHolder holder;   // Holders are meant for controlling a surface size and format.
    static Camera mCamera;    // Camera class deprecated in much later API 21.
    public static final int MEDIA_TYPE_IMAGE = 1;   // Only want photo (not video).
    public boolean active = false;
    public boolean flashOn;    // This value is set by the persistent value stored in fragment.
    static String myApp = "InstagramApp"; // Folder for storing this apps photo files.

    // Attributes for drawing camera preview and gridlines to canvas.
    int width;
    int height;
    int vmid1;
    int vmid2;
    int hmid1;
    int hmid2;
    Paint paint;

    Context context;

    // CaptrurePreview constructor.
    public CapturePreview(Context context) {
        super(context);

        holder = getHolder();         // Allows access to the SurfaceHolder for SurfaceView.
        holder.addCallback(this);     // Adds a Callback interface (itself) to the holder object.
        this.setWillNotDraw(false);   // Required to ensure camera preview is updated.

        active = true;
        this.context = context;
    }

    // This method is from SurfaceHolder.Callback interface.
    // Overriding the surfaceChanged method. Called whenever change to format or size of surface.
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.getSupportedPreviewSizes();
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }catch (Exception err){
            Toast.makeText(context,"Camera Failure",Toast.LENGTH_LONG).show();
        }
    }

    // Setting up Camera object, flash status and grid line paint when new surface created.
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mCamera = Camera.open(); // attempt to get a Camera instance
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            updateFlash(flashOn);
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Failed to open Camera");
            e.printStackTrace();
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    // Releases all necessary resources when the CapturePreview object is destroyed.
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //trying to fix the camera crash
        this.getHolder().removeCallback(this);
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        active = false;
    }

    // Draws gridlines to canvas as camera preview updates with image stream.
    @Override
    protected void onDraw(Canvas canvas)
    {
        width = canvas.getWidth();
        height = canvas.getHeight();
        vmid1 = width/3;
        vmid2 = 2*width/3;
        hmid1 = height/3;
        hmid2 = 2*height/3;


        canvas.drawLine(vmid1, 0, vmid1, height, paint);
        canvas.drawLine(vmid2, 0, vmid2, height, paint);
        canvas.drawLine(0, hmid1, width, hmid1, paint);
        canvas.drawLine(0, hmid2, width, hmid2, paint);
    }

    // Method for taking picture.
    // Uses Camera object PictureCallback to capture image then saves to File object, before
    // passing this file path to the filter activity for post-processing.
    public void takePicture(){

        Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null){
                    Log.d(TAG, "Error creating media file, check storage permissions: " );
                    return;
                }

                // Writing to file.
                try {
                    Log.i(TAG, "Writing to file");
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Log.i(TAG, "File closed.");
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }

                //Call filter activity.
                Intent intent = new Intent(getContext(),FilterActivity.class);
                Log.i("photo", pictureFile.getAbsolutePath());
                intent.putExtra("selected_image",pictureFile.getAbsolutePath());
                getContext().startActivity(intent);

            }

        };

        // Uses takePicture method from Camera class to trigger PictureCallback defined above.
        mCamera.takePicture(null, null, mPictureCallback);
    }
    // Flash status setter. This is called when a new CapturePreview object is instantiated
    // and gets persistent flash status from PhotoFromCameraFragment.
    public void  setFlashOn(Boolean flashState) {
        flashOn = flashState;
    }

    // This is called by flashButton toggle listener in PhotoFromCameraFragment to update the
    // flashOn attribute in this object based.
    public void FlashToggle() {
        Log.i(TAG, "Before flash toggle launched: "+ flashOn);
        flashOn = !flashOn;

        Log.i(TAG, "After flash toggle launched: "+ flashOn);

        updateFlash(flashOn);
    }

    // Method invoked to set flash parameter in Camera object.
    public void updateFlash(Boolean flash) {

        Camera.Parameters param = mCamera.getParameters();

        if (flashOn) {
            param.setFlashMode("on");
            mCamera.setParameters(param);
            Log.i(TAG, "Get flash mode (expect on): " + mCamera.getParameters().getFlashMode());}
        else {
            param.setFlashMode("off");
            mCamera.setParameters(param);
            Log.i(TAG, "Get flash mode (expect off): " + mCamera.getParameters().getFlashMode()); }
    }

    // Release camera when fragment is paused.
    public void photoFragmentPause() {
        this.getHolder().removeCallback(this);
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        active = false;
    }


    // Creates file on external storage.
    public static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), myApp);

        // Create directory if it doesn't exist.
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Creates a unique filename based current time/date.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
            Log.i(TAG, "Filename:"+"IMG_"+ timeStamp + ".jpg");
            Log.i(TAG, "Path:"+mediaStorageDir.getPath());
        } else {
            return null;
        }
        return mediaFile;
    }
}