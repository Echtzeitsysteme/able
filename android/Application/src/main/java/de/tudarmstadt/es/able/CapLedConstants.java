package de.tudarmstadt.es.able;

import java.util.UUID;

/**
 * Created by user on 01.03.18.
 */

public interface CapLedConstants {

    final static String baseUUID = ("00000000-0000-1000-8000-00805f9b34f");
    final static String capsenseLedServiceUUID = ("00000000-0000-1000-8000-00805f9b34f0");
    final static String ledCharacteristicUUID = ("00000000-0000-1000-8000-00805f9b34f1");
    final static String capsenseCharacteristicUUID = ("00000000-0000-1000-8000-00805f9b34f2");

    UUID BASE_UUID = UUID.fromString(baseUUID);
    UUID CAPLED_SERVICE_UUID = UUID.fromString(BASE_UUID.toString() + "0");
    UUID CAPLED_LED_CHARACTERISTIC_UUID = UUID.fromString(BASE_UUID.toString() + "1");
    UUID CAPLED_CAP_CHARACTERISTIC_UUID = UUID.fromString(BASE_UUID.toString() + "2");
    UUID CccdUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

}
