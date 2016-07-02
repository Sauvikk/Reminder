package com.example.souvik.remindertemplate.Alarm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.souvik.remindertemplate.Activities.NotificationOnClickActivity;
import com.example.souvik.remindertemplate.R;
import com.example.souvik.remindertemplate.RemindMe;
import com.example.souvik.remindertemplate.model.Alarm;
import com.example.souvik.remindertemplate.model.AlarmMsg;


public class AlarmReceiver extends BroadcastReceiver {

    NotificationManager manager;
    Notification myNotication;

    private static String TAKE ="take";
    private static String SKIP ="skip";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        long alarmMsgId = intent.getLongExtra(AlarmMsg.COL_ID, -1);
        long alarmId = intent.getLongExtra(AlarmMsg.COL_ALARMID, -1);
        Intent buttonIntent;
        AlarmMsg alarmMsg = new AlarmMsg(alarmMsgId);

        if (TAKE.equals(action)) {
            Log.d("I am here", "take");
            Log.d("alarmMsgId take", alarmMsgId + "");
            Log.d("alarmId take", alarmId + "");
            String  s = Context.NOTIFICATION_SERVICE;
            NotificationManager mNM = (NotificationManager) context.getSystemService(s);
            mNM.cancel((int)alarmMsgId);
            alarmMsg.setStatus(AlarmMsg.TAKEN);
        } else if (SKIP.equals(action)) {
            Log.d("I am here", "skip");
            Log.d("alarmMsgId skip", alarmMsgId + "");
            Log.d("alarmId skip", alarmId + "");
            String  s = Context.NOTIFICATION_SERVICE;
            NotificationManager mNM = (NotificationManager) context.getSystemService(s);
            mNM.cancel((int) alarmMsgId);
            alarmMsg.setStatus(AlarmMsg.SKIPPED);
        }



        alarmMsg.persist(RemindMe.db);


        Alarm alarm = new Alarm(alarmId);
        alarm.load(RemindMe.db);

        if (!TAKE.equals(action) && !SKIP.equals(action)) {
            Log.d("I am here", "lol");
            Log.d("alarmMsgId", alarmMsgId + "");
            Log.d("alarmId", alarmId + "");


//        Notification n = new Notification(R.drawable.ic_profile, alarm.getName(), System.currentTimeMillis());
            Intent onClickIntent =  new Intent(context, NotificationOnClickActivity.class);
            onClickIntent.putExtra(AlarmMsg.COL_ID, alarmMsgId);
            onClickIntent.putExtra(AlarmMsg.COL_ALARMID, alarmId);
            PendingIntent pi = PendingIntent.getActivity(context, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            builder.setContentTitle("Prescription");
            builder.setContentText(alarm.getName());
            builder.setSmallIcon(R.drawable.tablet);
            builder.setContentIntent(pi);

            buttonIntent = new Intent(context, AlarmReceiver.class);
            buttonIntent.setAction(SKIP);
            buttonIntent.putExtra(AlarmMsg.COL_ID, alarmMsgId);
            buttonIntent.putExtra(AlarmMsg.COL_ALARMID, alarmId);
            PendingIntent btPendingIntentSkip = PendingIntent.getBroadcast(context, 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.circle, "Skip", btPendingIntentSkip);

            buttonIntent = new Intent(context, AlarmReceiver.class);
            buttonIntent.setAction(TAKE);
            buttonIntent.putExtra(AlarmMsg.COL_ID, alarmMsgId);
            buttonIntent.putExtra(AlarmMsg.COL_ALARMID, alarmId);
            PendingIntent btPendingIntentTake = PendingIntent.getBroadcast(context, 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_done_all, "Take", btPendingIntentTake);

            builder.setWhen(System.currentTimeMillis());
            builder.build();

//        n.setLatestEventInfo(context, "Remind Me", alarm.getName(), pi);
            myNotication = builder.build();
            if (RemindMe.isVibrate()) {
                myNotication.defaults |= Notification.DEFAULT_VIBRATE;
            }
            if (alarm.getSound()) {
                myNotication.sound = Uri.parse(RemindMe.getRingtone());
//			n.defaults |= Notification.DEFAULT_SOUND;
            }
            myNotication.flags |= Notification.FLAG_NO_CLEAR;

            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify((int) alarmMsgId, myNotication);
        }

    }

}

