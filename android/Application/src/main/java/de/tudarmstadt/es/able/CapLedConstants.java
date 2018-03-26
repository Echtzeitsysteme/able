package de.tudarmstadt.es.able;

import java.util.UUID;

/**
 * For a given specific BLE device, this interface contains all known UUIDs.
 */

public interface CapLedConstants {

    UUID BASE_UUID = UUID.fromString("00000000-0000-1000-8000-00805f9b34f");
    UUID CAPLED_SERVICE_UUID = UUID.fromString(BASE_UUID.toString() + "0");
    UUID CAPLED_LED_CHARACTERISTIC_UUID = UUID.fromString(BASE_UUID.toString() + "1");
    UUID CAPLED_CAP_CHARACTERISTIC_UUID = UUID.fromString(BASE_UUID.toString() + "2");
    UUID CccdUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

}
