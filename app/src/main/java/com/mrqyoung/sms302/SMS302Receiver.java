package com.mrqyoung.sms302;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SMS302Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        // DEBUG only
        debug("===========");
        for (String s : bundle.keySet()) {
            debug(s + "=>" + bundle.get(s).toString());
        }
        debug("-----------");

        if (bundle.containsKey("pdus")) { // SMS
            Object pdus[] = (Object[])bundle.get("pdus");

            if (pdus == null) return;
            SmsMessage[] message = new SmsMessage[pdus.length];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pdus.length; i++) {
                message[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                sb.append(message[i].getDisplayMessageBody());
            }
            String address = message[0].getDisplayOriginatingAddress();

            String msg = sb.toString();

            debug("ADDR=" + address);
            debug("MSG=" + msg.length());

            SMS302IntentService.startActionFoo(context, address, msg);
        } else if (bundle.containsKey("incoming_number")) {  // CALL
            int i = bundle.getInt("subscription");
            String s = bundle.getString("state");
            // State = IDLE or RINGING
            if (!"IDLE".equalsIgnoreCase(s)) {
                debug("RINGING, ignore--");
                return;
            }
            String number = bundle.getString("incoming_number");
            debug("CALL=" + number);
            // TODO send call mail
            String now = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
            SMS302IntentService.startActionBaz(context, now, number);
        } else {
            debug("UNKNOWN");
        }
    }

    private void debug(String s) {
        Log.d("Receiver", s);
    }
}
