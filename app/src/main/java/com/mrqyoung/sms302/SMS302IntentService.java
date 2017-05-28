package com.mrqyoung.sms302;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.DateFormat;

/**
 * Receive BroadCast and send mails
 */
public class SMS302IntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.mrqyoung.sms302.action.FOO";
    private static final String ACTION_BAZ = "com.mrqyoung.sms302.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.mrqyoung.sms302.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.mrqyoung.sms302.extra.PARAM2";

    public SMS302IntentService() {
        super("SMS302IntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SMS302IntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SMS302IntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // eg. SMS received
        DataHelper d = new DataHelper(this);
        MailSender mailSender = prepareMailSender(d);
        if (mailSender == null) {return;}
        String c = d.isPlainSMS() ? param2 : d.encode(param2);
        boolean r = mailSender.selfSend(param1, c);
        if (!r) {
            retry(mailSender.toStr(), param1, c);
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // eg. Call missed
        DataHelper d = new DataHelper(this);
        MailSender mailSender = prepareMailSender(d);
        if (mailSender == null) {return;}
        boolean r = mailSender.selfSend(param1, param2);
        if (!r) {
            retry(mailSender.toStr(), param1, param2);
        }
    }

    private MailSender prepareMailSender(DataHelper d) {
        String u = d.loadEmail();
        String b = d.loadToken();
        if (u == null || u.isEmpty() || b.isEmpty()) {
            return null;
        }
        MailSender ms = new MailSender(u, b);
        ms.setHOST(d.loadSMTPInfo());
        return ms;
    }

    private void retry(String s0, String s1, String s2) {
        Log.w("SMS302", "##Failed to send email, will try later..");
        Intent intent = new Intent(this, RetryService.class);
        intent.putExtra("Sender", s0);
        intent.putExtra("Title", s1);
        intent.putExtra("Body", s2);
        this.startService(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("SMS302", "SERVICE DISTROY!!!!!!!!!!!!!!!!");
        super.onDestroy();
    }

}
