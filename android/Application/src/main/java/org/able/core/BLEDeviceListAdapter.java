package org.able.core;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for holding devices found through scanning.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
 */

class BLEDeviceListAdapter extends BaseAdapter {
    private List<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;

    /**
     * Initializes the list and the inflater.
     * @param inflater
     */
    public BLEDeviceListAdapter(LayoutInflater inflater) {
        super();
        mLeDevices = new ArrayList<>();
        mInflator = inflater;
    }

    /**
     * Adds the item device to the list, if it is not inside the list.
     * @param device is added to the list
     */
    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    /**
     * Returns the device on the a specific position of the list.
     * @param position of the device which will be returned
     * @return device on position
     */
    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    /**
     * Clears the list.
     */
    public void clear() {
        mLeDevices.clear();
    }

    /**
     * @return size of the list.
     */
    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    /**
     * Gets the device with the id i.
     * @param i id of device.
     * @return device with id i.
     */
    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    /**
     * Converts int to long.
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    /**
     * Sets the list View of the GUI inside AbleDeviceScanActivity.
     * @param i
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AbleDeviceScanActivity.ViewHolder viewHolder;
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new AbleDeviceScanActivity.ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (AbleDeviceScanActivity.ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }

    public List<BluetoothDevice> getListOfDevices(){
        return mLeDevices;
    }
}