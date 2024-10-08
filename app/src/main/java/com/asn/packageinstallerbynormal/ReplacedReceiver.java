package com.asn.packageinstallerbynormal;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.provider.Settings;

public final class ReplacedReceiver extends BroadcastReceiver {
    private static final String ChannelID = "App Updated";
    private static final int NotificationID = 42;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) return;

        var packageName = context.getPackageName();
        var installer = context.getPackageManager().getInstallerPackageName(packageName);
        if (!packageName.equals(installer)) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || Settings.canDrawOverlays(context)) {
            var i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            var nm = context.getSystemService(NotificationManager.class);
            createNotificationChannel(nm);
            nm.notify(NotificationID, createNotification(context));
        }
    }

    private void createNotificationChannel(NotificationManager nm) {
        var channel = new NotificationChannel(ChannelID, ChannelID, IMPORTANCE_HIGH);
        nm.createNotificationChannel(channel);
    }

    private Notification createNotification(Context context) {
        var flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        var intent = new Intent(context, MainActivity.class);
        var pending = PendingIntent.getActivity(context, 0, intent, flag);
        var icon = Icon.createWithResource(context, android.R.drawable.ic_dialog_info);
        var builder = new Notification.Builder(context, ChannelID);
        return builder.setContentIntent(pending)
                .setContentTitle("app updated")
                .setContentText("tap to open")
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .build();
    }
}
