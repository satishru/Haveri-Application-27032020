package com.example.myapplication.ui.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.BR;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityHomeBinding;
import com.example.myapplication.ui.activity.setting.SettingActivity;
import com.example.myapplication.ui.base.BaseActivity;
import com.example.myapplication.ui.fragment.home.HomeFragment;
import com.example.myapplication.utils.AppConstants;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Display all Taluk, Places, Image, Events, Video, Map and Other Content
 */
public class HomeActivity extends BaseActivity<ActivityHomeBinding, HomeActivityViewModel> implements
        HasSupportFragmentInjector, iHomeActivityContract.iHomeActivityNavigator,
        HomeFragment.HomeFragmentCallBack,
        LocationListener {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    private ActivityHomeBinding mActivityHomeBinding;
    private HomeActivityViewModel mHomeActivityViewModel;

    public static Intent newIntent(Activity activity) {
        return new Intent(activity, HomeActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public HomeActivityViewModel getViewModel() {
        mHomeActivityViewModel = new ViewModelProvider(this, factory).get(
                HomeActivityViewModel.class);
        return mHomeActivityViewModel;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityHomeBinding = getViewDataBinding();
        setToolBar(mActivityHomeBinding.layoutToolbar.toolbar, 0, false);
        mHomeActivityViewModel.setNavigator(this);
        checkPermissionAndGetLocation();
        loadFragment(HomeFragment.newInstance(),
                mActivityHomeBinding.contentHome.fragmentContainer.getId(), true, true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mHomeActivityViewModel.getDataManager().isScreenReloadRequired()) {
            mHomeActivityViewModel.getDataManager().setIsScreenReloadRequired(false);
            reCreateActivityWithAnimation();
            return;
        }
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivityWithAnimation(SettingActivity.newIntent(this));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * HomeFragment.HomeFragmentCallBack Call Backs
     *
     * @param visibility View.VISIBLE / View.GONE
     */
    @Override
    public void showHideHomeButton(boolean visibility) {
        mActivityHomeBinding.layoutToolbar.ivBack.setVisibility(
                visibility ? View.VISIBLE : View.GONE);
    }
    /* HomeFragment.HomeFragmentCallBack Call Backs */

    @Override
    public void onBackPressed() {
        if (isBottomSheetOpened()) {
            closeBottomSheet();
            return;
        } else if (getBackStackEntryCount() == 1) {
            exitActivityWithAnimation();
            return;
        }
        super.onBackPressed();
        exitActivityWithAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showLoading(false);
    }

    @Override
    public void onBackIconClicked() {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION_TURN_ON) {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION_PERMISSION) {
            checkLocationOnAndGetLocation();
        }
    }
}
