package com.example.asus.instagram.Upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.instagram.Interface.EditImageFragmentListener;
import com.example.asus.instagram.Interface.FiltersListFragmentListener;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.ImageManager;
import com.example.asus.instagram.Utils.ViewPagerAdapter;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.IOException;

/**
 * @author : Yujie Lyu
 * @date : 28-09-2018
 * @time : 16:24
 */

public class FilterActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener {

    public static final String pictureName = "626939的副本.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;
    private static final String TAG = FilterActivity.class.getName();
    public static Bitmap originalBitmap, filteredBitmap, finalBitmap;

    //load native image filters lib
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    ImageView img_preview;
    TabLayout tabLayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;
    FilterListFragment filterListFragment;
    EditImageFragment editImageFragment;
    CardView btn_crop;
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;
    Uri image_selected_uri;//TODO:新加
    private String imgUrl;
    private String mSelectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Instagram Filter");


        //view
        img_preview = findViewById(R.id.image_preview);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        coordinatorLayout = findViewById(R.id.coordinator);
        btn_crop = findViewById(R.id.btn_crop);

        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onCropStart(Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), originalBitmap, null, null)));

            }
        });

        try {
            loadImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        TextView nextScreen = (TextView) findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG, "onClick: navigating to the share screen");
                //todo:filter pager
                Intent intent = new Intent(FilterActivity.this, ShareActivity.class);
//                intent.putExtra(getString(R.string.selected_bitmap),finalBitmap);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadImage() throws IOException {
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.selected_image))) {
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "loadImage: " + imgUrl);
            originalBitmap = ImageManager.getBitmap(imgUrl);
        } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
            originalBitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));

        }

//        originalBitmap=BitmapUtils.getBitmapFromAssets(this,pictureName,300,300);
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        //todo

        img_preview.setImageBitmap(originalBitmap);

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter((getSupportFragmentManager()));
        filterListFragment = new FilterListFragment();
        filterListFragment.setListener(this);

        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filterListFragment, "FILTERS");
        adapter.addFragment(editImageFragment, "EDIT");

        viewPager.setAdapter(adapter);

    }

    public void onCropStart(Uri uri) {//TODO:新加

        UCrop ucrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), "目标文件.jpeg")));

        ucrop.start(this);
    }

    //adjust the brightness
    @Override
    public void onBrightnessChanged(int brightness) {

        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        img_preview.setImageBitmap(myFilter.processFilter(filteredBitmap.copy(Bitmap.Config.ARGB_8888, true)));


    }

    //adjust the saturation
    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        img_preview.setImageBitmap(myFilter.processFilter(filteredBitmap.copy(Bitmap.Config.ARGB_8888, true)));

    }

    //adjust the contrast
    @Override
    public void onContrastChanged(float contrast) {

        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        img_preview.setImageBitmap(myFilter.processFilter(filteredBitmap.copy(Bitmap.Config.ARGB_8888, true)));

    }

    @Override
    public void onEditStarted() {


    }

    @Override
    public void onEditCompleted() {

        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));

        finalBitmap = myFilter.processFilter(bitmap);

    }

    @Override
    public void onFilterSelected(Filter filter) {

        resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        img_preview.setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//TODO:新加

//        image_selected_uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), originalBitmap, null, null));
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            try {
                handleCropResult(data);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }



    private void handleCropError(Intent intent) {//TODO:新加

        final Throwable cropError = UCrop.getError(intent);
        if (cropError != null) {
            Log.e(TAG, "Crop Error：" + cropError.getMessage());
            Toast.makeText(this, "" + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropResult(Intent intent) throws IOException {//TODO:新加
        final Uri resultUri = UCrop.getOutput(intent);
        if (resultUri != null) {
            img_preview.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri));
        } else {
            Toast.makeText(this, "cannot retrieve crop image", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetControl() {
        if (editImageFragment != null)
            editImageFragment.resetControls();
        brightnessFinal = 0;
        contrastFinal = 1.0f;
        saturationFinal = 1.0f;
    }


}
