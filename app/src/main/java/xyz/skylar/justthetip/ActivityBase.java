package xyz.skylar.justthetip;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
/**
 * Created by Skylar on 5/28/2015.
 */
public class ActivityBase extends FragmentActivity{
    public static final String TAG = "SampleActivityBase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void onStart() {
        super.onStart();
    }
}
