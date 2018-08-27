package de.tudarmstadt.es.myProject;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import de.tudarmstadt.es.able.BLEServiceListener;
import de.tudarmstadt.es.able.R;

public class myProjectActivity extends FragmentActivity implements BLEServiceListener, ActionBar.TabListener {
    private final static String TAG = myProjectActivity.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static String mDeviceName;
    public static String mDeviceAddress;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    /**
     * Initializes activity and GUI objects.
     * @param savedInstanceState
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
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
    }

    /**
     * Called after onCreate(..) and initializes BLE variables.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
    }

    /**
     * Called if the application is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
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
     * Called when GATT connection starts.
     */
    @Override
    public void gattConnected() {

    }

    /**
     * Called when GATT connection ends.
     */
    @Override
    public void gattDisconnected() {
    }

    /**
     * Called when a GATT Service is discovered.
     */
    @Override
    public void gattServicesDiscovered() {
    }

    /**
     * This method is called if data is available for the CapLED Service.
     * Then the LED switch button GUI is refreshed and the CapSense GUI View refreshed.
     * @param intent
     */
    @Override
    public void dataAvailable(Intent intent) {
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position) {
                case 0: return myProjectViewTab.newInstance(mDeviceName, mDeviceAddress);
                case 1: return myProjectSettingsTab.newInstance(mDeviceName, mDeviceAddress);
                // TODO: Add a case, if a tab is added ...

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
            // TODO: Increment return if a tab is added
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "View";
                case 1:
                    return "Settings";
                    // TODO: You can insert your own tab here ...
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof myProjectViewTab) {
            }
            return super.getItemPosition(object);
        }
    }

}
