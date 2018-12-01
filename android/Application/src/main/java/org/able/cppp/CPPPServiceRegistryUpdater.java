package org.able.cppp;

import android.app.Activity;

import java.util.UUID;

import org.able.core.AbleServiceRegistryUpdatingBroadcastReceiver;


/**
 * This class registers the UUID-to-activity mapping for the CPPP device.
 */
public class CPPPServiceRegistryUpdater extends AbleServiceRegistryUpdatingBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivityClass() {
        return CPPPActivity.class;
    }

    @Override
    protected UUID getUuid() {
        return CPPPConstants.CPPP_SERVICE_UUID;
    }
}
