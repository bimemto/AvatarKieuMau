package com.duyota.standardavatar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by admin on 5/12/16.
 */
public class AvatarActivity extends AppCompatActivity {

    private TextView tvBig;
    private TextView tvSmall;
    private RelativeLayout root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar_maker);
        tvBig = (TextView) findViewById(R.id.tvBig);
        tvSmall = (TextView) findViewById(R.id.tvSmall);
        root = (RelativeLayout) findViewById(R.id.root);
        tvBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInput();
            }
        });
        tvSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInput();
            }
        });
        showInput();
    }

    private void showInput() {
        View view = LayoutInflater.from(this).inflate(R.layout.input, null);
        final EditText editText1 = (EditText) view.findViewById(R.id.edit1);
        final EditText editText2 = (EditText) view.findViewById(R.id.edit2);
        editText1.setText(tvBig.getText());
        editText2.setText(tvSmall.getText());
        new MaterialDialog.Builder(this).customView(view, true).positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                tvBig.setText(editText1.getText().toString());
                tvSmall.setText(editText2.getText().toString());
            }
        }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Dexter.checkPermission(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            saveInBackground();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    saveInBackground();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveInBackground() {
        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                takeScreenshot();
                return null;
            }
        }).onSuccess(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                new MaterialDialog.Builder(AvatarActivity.this).content(R.string.finished).positiveText(android.R.string.ok).show();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    private void takeScreenshot() {
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + "avatarkieumau_" + System.currentTimeMillis() + ".jpg";
            // create bitmap screen capture
            root.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(root.getDrawingCache());
            root.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "avatarkieumau_" + System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DESCRIPTION, "Avatar kiểu mẫu");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, imageFile.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, imageFile.getName().toLowerCase(Locale.US));
            values.put("_data", imageFile.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
