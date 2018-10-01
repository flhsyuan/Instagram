package com.example.asus.instagram.Upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.instagram.Interface.EditImageFragmentListener;
import com.example.asus.instagram.Interface.FiltersListFragmentListener;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.ViewPagerAdapter;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * @author : Yujie Lyu
 * @date : 28-09-2018
 * @time : 16:24
 */

public class FilterActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener {

    public static final String pictureName = "626939的副本.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;
    private static final String TAG = "FilterActivity";

    ImageView img_preview;
    TabLayout tabLayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;

    public static Bitmap originalBitmap, filteredBitmap, finalBitmap;
    FilterListFragment filterListFragment;
    EditImageFragment editImageFragment;
    private String imgUrl;
    private Intent intent;
    private String mSelectedImage;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    //load native image filters lib
    static {
        System.loadLibrary("NativeImageProcessor");
    }

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
                intent.putExtra(getString(R.string.selected_bitmap), finalBitmap);
                startActivity(intent);
            }
        });
    }

    //通过路径将图片转化为Bitmap
    public static Bitmap getBitmapFromUri(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
                BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }


    private void loadImage() throws IOException {
        intent = getIntent();
        if (intent.hasExtra(getString(R.string.selected_image))) {
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "loadImage: " + imgUrl);
            originalBitmap = getBitmapFromUri(imgUrl,300,300);
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

    private void resetControl() {
        if (editImageFragment != null)
            editImageFragment.resetControls();
        brightnessFinal = 0;
        contrastFinal = 1.0f;
        saturationFinal = 1.0f;
    }


}
