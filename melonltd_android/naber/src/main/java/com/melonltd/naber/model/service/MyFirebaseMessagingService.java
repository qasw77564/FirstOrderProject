package com.melonltd.naber.model.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.common.collect.Lists;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.melonltd.naber.R;
import com.melonltd.naber.model.type.Identity;
import com.melonltd.naber.view.common.BaseActivity;
import com.melonltd.naber.view.seller.page.SellerOrdersFragment;

import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private int numMessages = 0;

    private static List<Identity> USER_IDENTITY = Identity.getUserValues();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> map = remoteMessage.getData();
        Identity identity = Identity.of(map.get("identity"));
        Identity currentIdentity = Identity.of(SPService.getIdentity());
        if (USER_IDENTITY.containsAll(Lists.newArrayList(identity, currentIdentity))) {
            sendNotification(remoteMessage.getNotification());
        } else if (Lists.newArrayList(identity, currentIdentity).contains(Identity.SELLERS)) {
            sendNotification(remoteMessage.getNotification());
            if (SellerOrdersFragment.FRAGMENT != null){
                SellerOrdersFragment.loadLiveData();
            }
        }
    }


    private void sendNotification(RemoteMessage.Notification notify) {

        Intent intent = new Intent(this, BaseActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (notify != null ){
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "1")
                    .setContentTitle(notify.getTitle())
                    .setContentText(notify.getBody())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.naber_icon_logo_reverse))
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setLights(Color.RED, 1000, 300)
                    .setDefaults( Notification.DEFAULT_VIBRATE )
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setNumber(++numMessages)
                    .setSmallIcon(R.drawable.ic_notif_eca_small);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(notify.getTitle()).bigText(notify.getBody()));
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.notify(numMessages, notificationBuilder.build());
        }

    }
}
