package org.able.core;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import org.able.capled.CapLEDServiceRegistryUpdater;
import org.able.cppp.CPPPServiceRegistryUpdater;
import org.able.myproject.MyProjectServiceRegistryUpdater;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service Registry saves the UUID and Activity Class Tuples which are later used by AbleDeviceScanActivity in checkForKnownServices().
 * Edit the constructor  to add your own UUID Device and Activity class.
 *
 * @author Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
 */

public class AbleServiceRegistry {

    private final Map<UUID, Class<? extends Activity>> registeredServices = new HashMap<>();
    private static final AbleServiceRegistry serviceRegistryINSTANCE = new AbleServiceRegistry();

    /**
     * Returns the singleton of this class.
     *
     * @return the singleton {@link AbleServiceRegistry}
     */
    public static AbleServiceRegistry getInstance() {
        return serviceRegistryINSTANCE;
    }

    /**
     * Private due to singleton pattern
     */
    private AbleServiceRegistry() {
    }

    /**
     * Inserts the pair of UUID and activity into this registry
     *
     * @param serviceUuid the UUID of the service
     * @param activity    the Android activity to launch
     */
    public void registerActivity(final UUID serviceUuid, final Class<? extends Activity> activity) {
        registeredServices.put(serviceUuid, activity);
    }

    public Map<UUID, Class<? extends Activity>> getRegisteredServices() {
        return registeredServices;
    }

    public Class<?> getServiceClass(UUID uuid) {
        if (registeredServices.containsKey(uuid))
            return registeredServices.get(uuid);
        return AbleDeviceControlActivity.class;
    }

    /***
     * Intializes Services that will be mapped to specific Activity classes.
     */
    public void initializeServices(Context context){
        // TODO CUSTOM ABLE PROJECT: Insert your Broadcast receiver here...
        
        BroadcastReceiver brMyProject = new MyProjectServiceRegistryUpdater();
        IntentFilter myProjectFilter = new IntentFilter("org.able.myproject.MyProjectServiceRegistryUpdater");
        myProjectFilter.addAction(AbleServiceRegistryUpdatingBroadcastReceiver.INTENT_ACTION_UPDATE_UUID_MAPPING);
        context.registerReceiver(brMyProject, myProjectFilter);


    }

}
