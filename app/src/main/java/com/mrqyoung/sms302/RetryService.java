package com.mrqyoung.sms302;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

public class RetryService extends Service {

    private MailSender sender;
    private String title, body;

    private String TAG = "RetryService";

    public RetryService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            String s0 = intent.getStringExtra("Sender");
            this.title = intent.getStringExtra("Title");
            this.body = intent.getStringExtra("Body");
            saveAndRetryLater(s0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        cancelTheReceiver();
        super.onDestroy();
    }


    private void saveAndRetryLater(String s0) {
        updateBadCount(1);
        this.sender = new MailSender(s0);
        setUpTheReceiver();
    }

    private void updateBadCount(int i) {
        SharedPreferences settings = this.getSharedPreferences("Cache2", 0);
        int count = settings.getInt("BAD", 0);
        count += i;
        settings.edit().putInt("BAD", count).apply();
    }

    private void reSend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sender.selfSend(title, body)) {
                    updateBadCount(-1);
                    Log.d(TAG, "##Resend OK");
                    stopSelf();
                } else {
                    Log.d(TAG, "##Resend ERROR");
                }
            }
        }).start();
    }


    /* ******************************************************
    * Listening network state and try send again if failed.
    */
    private static BroadcastReceiver networkChangedReceiver = null;
    private void setUpTheReceiver() {
        networkChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    Log.d(TAG, "##Network changed");
                    ConnectivityManager cm= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    if(info != null && info.isAvailable()) {
                        Log.d("##Network", info.getTypeName());
                        // load cache then send
                        reSend();
                    } else {
                        Log.d(TAG, "##No network");
                    }
                }
            }
        };
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(networkChangedReceiver, mFilter);
    }

    private void cancelTheReceiver() {
        if (networkChangedReceiver != null) {
            Log.d(TAG, "##cancel the network receiver");
            unregisterReceiver(networkChangedReceiver);
            networkChangedReceiver = null;
        }
    }

}
