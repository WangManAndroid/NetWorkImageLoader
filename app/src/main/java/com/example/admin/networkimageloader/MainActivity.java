package com.example.admin.networkimageloader;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

public class MainActivity extends AbsSingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ListImgsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_single_fragment;
    }

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
