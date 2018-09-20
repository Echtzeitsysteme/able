package org.able.core;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Scans through all the services of a GATT server and saves them with their data the into a List<List<BluetoothGattCharacteristic>>.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
 */

public class AbleCharacteristicSorter {

    private List<HashMap<String, String>> gattServiceData = null;
    private List<ArrayList<HashMap<String, String>>> gattCharacteristicData = null;
    List<List<BluetoothGattCharacteristic>> characteristicList = null;

    public AbleCharacteristicSorter(List<HashMap<String, String>> gattServiceData,
                                    List<ArrayList<HashMap<String, String>>> gattCharacteristicData,
                                    List<List<BluetoothGattCharacteristic>> characteristicList) {
        this.gattServiceData = gattServiceData;
        this.gattCharacteristicData = gattCharacteristicData;
        this.characteristicList = characteristicList;
    }


    /**
     * Constructor of the class, that scans all services and puts all data sorted in the lists
     * gattServiceData, gattCharacteristicData and characteristicList.
     *
     * @param gattServices       GATT services discovered by the ABLE scan.
     * @param listName           name of list
     * @param listUUID           name of UUID list
     * @param characteristicList here all the characteristics are added
     * @param context            Android Java context of the app
     * @return
     */
    static AbleCharacteristicSorter settingUpServices(List<BluetoothGattService> gattServices, String listName, String listUUID,
                                                      List<List<BluetoothGattCharacteristic>> characteristicList, Context context) {
        String uuid = null;
        String unknownServiceString = context.getResources().getString(R.string.unknown_service);
        String unknownCharaString = context.getResources().getString(R.string.unknown_characteristic);
        List<HashMap<String, String>> gattServiceData = new ArrayList<>();
        List<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<>();
        characteristicList = new ArrayList<>();

        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(listName, unknownServiceString);
            currentServiceData.put(listUUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(listName, unknownCharaString);
                currentCharaData.put(listUUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            characteristicList.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        AbleCharacteristicSorter objectToReturn = new AbleCharacteristicSorter(gattServiceData, gattCharacteristicData, characteristicList);
        return objectToReturn;
    }

    /**
     * @return characteristicList
     */
    public List<List<BluetoothGattCharacteristic>> getCharacteristicList() {
        return characteristicList;
    }

    /**
     * @return gattServiceData
     */
    public List<HashMap<String, String>> getGattServiceData() {
        return gattServiceData;
    }

    /**
     * Sets the value gattServiceData
     *
     * @param gattServiceData new value for this.gattServiceData
     */
    public void setGattServiceData(List<HashMap<String, String>> gattServiceData) {
        this.gattServiceData = gattServiceData;
    }

    /**
     * @return gattCharacteristicData
     */
    public List<ArrayList<HashMap<String, String>>> getGattCharacteristicData() {
        return gattCharacteristicData;
    }

    /**
     * Sets gattCharacteristicData
     *
     * @param gattCharacteristicData new value for this.gattCharacteristicData
     */
    public void setGattCharacteristicData(List<ArrayList<HashMap<String, String>>> gattCharacteristicData) {
        this.gattCharacteristicData = gattCharacteristicData;
    }
}