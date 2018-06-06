package toluog.femoji;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CameraActivity extends AppCompatActivity implements CameraFragment.OnFragmentInteractionListener,
        EditImageFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Fragment cameraFragment = CameraFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.frame, cameraFragment).commit();
    }

    @Override
    public void onPictureTaken(byte[] jpeg) {
        Fragment editBitmapFragment = EditImageFragment.newInstance(jpeg);
        fragmentManager.beginTransaction().replace(R.id.frame, editBitmapFragment).commit();
    }
}
