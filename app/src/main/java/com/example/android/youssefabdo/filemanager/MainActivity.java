package com.example.android.youssefabdo.filemanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        checkAndroidVersion();
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            MainCode();
        }
    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission
                            .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_MULTIPLE_REQUEST);
        } else {
            MainCode();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean writeExternalFile = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (writeExternalFile && readExternalFile) {
                        MainCode();
                    }
                    else {
                        // permission denied, boo!
                        MainActivity.this.finish();
                    }
                    return;
                }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.main_activity);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentFolderLand fragmentFolderLand=(FragmentFolderLand)getSupportFragmentManager().findFragmentByTag("fragmentFolderLand");
            if(fragmentFolderLand==null) {
                fragmentFolderLand = new FragmentFolderLand();
                FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
                transact.replace(R.id.container_view, fragmentFolderLand);
                transact.commit();
            }
        }
        else {
            FragmentFolder fragmentFolder=(FragmentFolder)getSupportFragmentManager().
                    findFragmentByTag("fragmentFolder1");
            if(fragmentFolder==null) {
                fragmentFolder = new FragmentFolder();
                FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
                transact.replace(R.id.container_view, fragmentFolder);
                transact.commit();
            }
        }
    }

        public void MainCode () {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //code for portrait mode
            FragmentFolder fragmentFolder=(FragmentFolder)getSupportFragmentManager()
                    .findFragmentByTag("fragmentFolder");
            if(fragmentFolder==null){
                fragmentFolder=new FragmentFolder();
                FragmentTransaction transact=getSupportFragmentManager().beginTransaction();
                transact.replace(R.id.container_view,fragmentFolder,"fragmentFolder");
                transact.commit();
            }
        }
else {
            FragmentFolderLand fragmentFolderLand=(FragmentFolderLand)getSupportFragmentManager()
                    .findFragmentByTag("fragmentFolderLand1");
            if(fragmentFolderLand==null) {
                fragmentFolderLand = new FragmentFolderLand();
                FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
                transact.replace(R.id.container_view, fragmentFolderLand, "fragmentFolderLand1");
                transact.commit();
            }
        }
    }
}

