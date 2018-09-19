package org.able.myproject;

import android.app.Activity;

import java.util.UUID;

import org.able.core.AbleServiceRegistryUpdatingBroadcastReceiver;

/**
 * This class registers the UUID-to-activity mapping for the Capled device.
 */
public class MyProjectServiceRegistryUpdater extends AbleServiceRegistryUpdatingBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivityClass() {
        return MyProjectActivity.class;
    }

    @Override
    protected UUID getUuid() {
        // TODO CUSTOM ABLE PROJECT: return your Service UUID here ...
        // EXAMPLE: return MyProjectConstants.MYPROJECT_SERVICE_UUID;
        return null;
    }
}
