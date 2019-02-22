package com.tirtakapps.instasave;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by gamew on 8.06.2017.
 */

public class autostart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, FollowingService.class);
        context.startService(serviceIntent);
        PendingIntent alarmIntent = PendingIntent.getService(context, 0 , serviceIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR, alarmIntent);
    }
}
