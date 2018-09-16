package org.able.capled;

import java.util.UUID;

/**
 * This interfaces contains all the relevant UUIDs for the CapLED example.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.1
 */

public interface CapLEDConstants {
    UUID CAPLED_SERVICE_UUID = UUID.fromString("00000000-0000-1000-8000-00805f9b34f0");
    UUID CAPLED_LED_CHARACTERISTIC_UUID = UUID.fromString("00000000-0000-1000-8000-00805f9b34f1");
    UUID CAPLED_GREEN_LED_CHARACTERISTIC_UUID = UUID.fromString("00000000-0000-1000-8000-00805f9b34f3");
    UUID CAPLED_CAP_CHARACTERISTIC_UUID = UUID.fromString("00000000-0000-1000-8000-00805f9b34f2");
    UUID CAPLED_CAP_NOTIFICATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

}
