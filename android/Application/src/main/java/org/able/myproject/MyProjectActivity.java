package org.able.myproject;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


import org.able.core.AbleDeviceScanActivity;
import org.able.core.BLEBroadcastReceiver;
import org.able.core.BLEService;
import org.able.core.BLEServiceListener;
import org.able.core.R;

public class MyProjectActivity extends FragmentActivity implements BLEServiceListener, ActionBar.TabListener {
    private final static String TAG = MyProjectActivity.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static String sDeviceName;
    private static String sDeviceAddress;
    private static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    private static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private BLEService mAbleBLEService;

    private MyProjectViewTab myProjectViewTab;
    private MyProjectSettingsTab myProjectSettingsTab;
    private BLEBroadcastReceiver thisReceiver;

    /**
     * Initializes activity and GUI objects.
     * @param savedInstanceState instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myproject_activity);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Tab settings
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.myProjectActivity);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        final Intent intent = getIntent();
        sDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        sDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        if (mAbleBLEService != null) {
            final boolean result = mAbleBLEService.connect(sDeviceAddress);
        }
    }

    /**
     * Called after onCreate(..) and initializes BLE variables.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        thisReceiver = new BLEBroadcastReceiver(this);
        this.registerReceiver(thisReceiver, thisReceiver.makeGattUpdateIntentFilter());
    }

    /**
     * Called if the application is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(thisReceiver);
    }

    /**
     * Called if the application is closed.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    /**
     * Called if back button was pressed. In this scenario the BLE connection will be disconnected.
     */
    @Override
    public void onBackPressed(){
        AbleDeviceScanActivity.getmBluetoothLeService().disconnect();
        super.onBackPressed();
    }

    /**
     * Called when GATT connection starts.
     */
    @Override
    public void gattConnected() {
        myProjectViewTab.gattConnected();
        myProjectSettingsTab.gattConnected();
    }

    /**
     * Called when GATT connection ends.
     */
    @Override
    public void gattDisconnected() {
        myProjectViewTab.gattDisconnected();
        myProjectSettingsTab.gattDisconnected();
    }

    /**
     * Called when a GATT Service is discovered.
     */
    @Override
    public void gattServicesDiscovered() {
        myProjectViewTab.gattServicesDiscovered();
        myProjectSettingsTab.gattServicesDiscovered();
    }

    /**
     * This method is called if data is available for the CapLED Service.
     * Then the LED switch button GUI is refreshed and the CapSense GUI View refreshed.
     * @param intent the intent that triggered this callback
     */
    @Override
    public void dataAvailable(Intent intent) {
        myProjectViewTab.dataAvailable(intent);
        myProjectSettingsTab.dataAvailable(intent);
    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            myProjectViewTab = MyProjectViewTab.newInstance(sDeviceName, sDeviceAddress);
            myProjectSettingsTab = MyProjectSettingsTab.newInstance(sDeviceName, sDeviceAddress);
            // TODO CUSTOM ABLE PROJECT: Add a case, if a tab is added ...

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position) {
                case 0: return myProjectViewTab;
                case 1: return myProjectSettingsTab;
                // TODO CUSTOM ABLE PROJECT: Add a case, if a tab is added ...
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
            // TODO CUSTOM ABLE PROJECT: Increment return if a tab is added
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "View";
                case 1:
                    return "Settings";
                    // TODO CUSTOM ABLE PROJECT: You can insert your own tab here ...
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof MyProjectViewTab) {
                // TODO CUSTOM ABLE PROJECT: You can insert your own code here ...
            }
            return super.getItemPosition(object);
        }
    }

}
