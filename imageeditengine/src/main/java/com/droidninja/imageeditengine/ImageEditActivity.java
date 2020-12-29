package com.droidninja.imageeditengine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.droidninja.imageeditengine.utils.FragmentUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.droidninja.imageeditengine.ImageEditor.EXTRA_EDITED_PATH;
import static com.droidninja.imageeditengine.ImageEditor.EXTRA_IMAGE_PATH;

public class ImageEditActivity extends BaseImageEditActivity
    implements PhotoEditorFragment.OnFragmentInteractionListener,
    CropFragment.OnFragmentInteractionListener {
  private Rect cropRect;

  //private View touchView;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_edit);

    String imagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
    if (imagePath != null) {
      FragmentUtil.addFragment(this, R.id.fragment_container,
          PhotoEditorFragment.newInstance(imagePath));
    }
  }

  @Override public void onCropClicked(Bitmap bitmap) {
    FragmentUtil.replaceFragment(this, R.id.fragment_container,
        CropFragment.newInstance(bitmap, cropRect));
  }

  ///data/user/0/com.droidninja.photoeditengine/cacheedited_1609175426268.jpg
  @Override public void onDoneClicked(String imagePath) {

    Intent intent = new Intent();
    intent.putExtra(EXTRA_EDITED_PATH, imagePath);
    setResult(Activity.RESULT_OK, intent);
    finish();
  }

  @Override public void onImageCropped(Bitmap bitmap, Rect cropRect) {
    this.cropRect = cropRect;
    PhotoEditorFragment photoEditorFragment =
        (PhotoEditorFragment) FragmentUtil.getFragmentByTag(this,
            PhotoEditorFragment.class.getSimpleName());
    if (photoEditorFragment != null) {
      photoEditorFragment.setImageWithRect(cropRect);
      photoEditorFragment.reset();
      FragmentUtil.removeFragment(this,
          (BaseFragment) FragmentUtil.getFragmentByTag(this, CropFragment.class.getSimpleName()));
//      FragmentUtil.replaceFragment(this, R.id.fragment_container,
//              CropFragment.newInstance(bitmap, cropRect)); //todo added by suraksha : need to create a new imageeditor view with this new bitmap


      storeImage(bitmap); //here i am storing my bitmap image to internal storage
//      new ProcessingImage(bitmap, Utility.getCacheFilePath(this),
//              new TaskCallback<String>() {
//                @Override public void onTaskDone(String data) {
//                  //
//                  onDoneClicked("/data/user/0/com.droidninja.photoeditengine/cacheedited_1609175426268.jpg");
//                }
//              }).execute();


      //new ImageEditor.Builder(this,"/data/user/0/com.droidninja.photoeditengine/cacheedited_1609175426268.jpg")
       new ImageEditor.Builder(this,"/storage/emulated/0/Pictures/IMG_20201228_142503.jpg")
              .open();
    }
    //todo actually this bitmap has to go one screen back only try to do that
  }


  @Override public void onCancelCrop() {
    FragmentUtil.removeFragment(this,
        (BaseFragment) FragmentUtil.getFragmentByTag(this, CropFragment.class.getSimpleName()));
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
  }

  private void storeImage(Bitmap image) //todo suraksha
  {
    File pictureFile = new File("/storage/emulated/0/Pictures/IMG_20201228_142503.jpg");
    if (pictureFile == null) {
      Log.d("permission",
              "Error creating media file, check storage permissions: ");// e.getMessage());
      return;
    }
    try {
      FileOutputStream fos = new FileOutputStream(pictureFile);
      image.compress(Bitmap.CompressFormat.PNG, 90, fos);
      fos.close();
    } catch (FileNotFoundException e) {
      Log.d("permission", "File not found: " + e.getMessage());
    } catch (IOException e) {
      Log.d("permission", "Error accessing file: " + e.getMessage());
    }
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

  }
}
