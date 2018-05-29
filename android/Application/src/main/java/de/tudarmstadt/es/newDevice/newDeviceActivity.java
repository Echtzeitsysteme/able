package de.tudarmstadt.es.newDevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import de.tudarmstadt.es.able.BLEServiceListener;

public class newDeviceActivity extends Activity implements BLEServiceListener{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    public void gattConnected() {

    }

    @Override
    public void gattDisconnected() {

    }

    @Override
    public void gattServicesDiscovered() {

    }

    @Override
    public void dataAvailable(Intent intent) {

    }
}
