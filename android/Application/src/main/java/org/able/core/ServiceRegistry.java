package org.able.core;

import android.app.Activity;

import org.able.capled.CapLEDActivity;
import org.able.capled.CapLEDConstants;

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

public class ServiceRegistry {

    private final Map<UUID, Class<? extends Activity>> registeredServices = new HashMap<>();
    private static final ServiceRegistry serviceRegistryINSTANCE = new ServiceRegistry();

    /**
     * Returns the singleton of this class.
     * @return the singleton {@link ServiceRegistry}
     */
    public static ServiceRegistry getInstance() {
        return serviceRegistryINSTANCE;
    }

    /**
     * Private due to singleton pattern
     */
    private ServiceRegistry(){
    }

    /**
     * Inserts the pair of UUID and activity into this registry
     * @param serviceUuid the UUID of the service
     * @param activity the Android activity to launch
     */
    public void registerActivity(final UUID serviceUuid, final Class<? extends Activity> activity) {
        registeredServices.put(serviceUuid, activity);
    }

    public Map<UUID, Class<? extends Activity>> getRegisteredServices () {
        return registeredServices;
    }

    public Class<?> getServiceClass(UUID uuid){
        if(registeredServices.containsKey(uuid))
            return registeredServices.get(uuid);
        return AbleDeviceControlActivity.class;
    }

}
