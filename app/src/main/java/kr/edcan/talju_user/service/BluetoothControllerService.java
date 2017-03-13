package kr.edcan.talju_user.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import kr.edcan.talju_user.activity.CallActivity;

/**
 * Created by Chad on 2017-02-04.
 */

public class BluetoothControllerService extends Service {
    BluetoothSPP bt;

    @Override
    public void onCreate() {
        super.onCreate();
        setBluetoothConfig();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void setBluetoothConfig() {
        bt = new BluetoothSPP(this);
        if (!bt.isBluetoothAvailable()) {
            return;
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "연결되었습니다", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "연결이끊겼습니다", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
            }
        });

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.e("asdf", message + "");
                if(message.equals("start") && !CallActivity.isLaunched){
                    startActivity(new Intent(getApplicationContext(), CallActivity.class));
                }
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                bt.autoConnect("applepie");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }



    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

}
