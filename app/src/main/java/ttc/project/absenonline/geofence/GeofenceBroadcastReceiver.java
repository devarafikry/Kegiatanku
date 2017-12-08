package ttc.project.absenonline.geofence;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ttc.project.absenonline.R;
import ttc.project.absenonline.activity.MainActivity;
import ttc.project.absenonline.model.ActivitySchedule;
import ttc.project.absenonline.model.Participant;
import ttc.project.absenonline.model.User;
import ttc.project.absenonline.utils.DateUtils;
import ttc.project.absenonline.utils.FirebaseUtils;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    ArrayList<String> activities = new ArrayList<>();
    int i;
    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();
    /***
     * Handles the Broadcast message sent when the Geofence Transition is triggered
     * Careful here though, this is running on the main thread so make sure you start an AsyncTask for
     * anything that takes longer than say 10 second to run
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(TAG, "onReceive called");
        // COMPLETED (4) Use GeofencingEvent.fromIntent to retrieve the GeofencingEvent that caused the transition

        final GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        // get the Geofences that triggered it
        List<Geofence> fences = event.getTriggeringGeofences();

        if (fences == null || fences.size() == 0) {
            return;
        }
        activities.clear();
        for (int i = 0;i<fences.size();i++){
            final String activityKey = fences.get(i).getRequestId();
            activities.add(activityKey);
        }
        for (final String activityKey:
             activities) {
            FirebaseUtils.getDatabaseReference().child(
                    context.getString(R.string.node_activity)
            ).child(activityKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ActivitySchedule activity = dataSnapshot.getValue(ActivitySchedule.class);
                    int transition = event.getGeofenceTransition();
                    // COMPLETED (5) Call getGeofenceTransition to get the transition type and use AudioManager to set the
                    // phone ringer mode based on the transition type. Feel free to create a helper method (setRingerMode)
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    String notification_enter, notification_dwell, notification_exit;

                    int hours =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    Long timeNowMillis = TimeUnit.HOURS.toMillis(hours);
                    boolean onTime = false;
                    if(timeNowMillis<activity.getTime_start()){
                        notification_enter = "Anda datang lebih awal di kegiatan ini ("+ DateUtils.getFriendlyTimeStartAndEnd(
                                activity.getTime_start(), activity.getTime_end()
                        )+")";
                        notification_dwell = "Kegiatan belum dimulai ("+ DateUtils.getFriendlyTimeStartAndEnd(
                                activity.getTime_start(), activity.getTime_end()
                        )+")";
                        notification_exit = "Silahkan kembali lagi nanti ("+ DateUtils.getFriendlyTimeStartAndEnd(
                                activity.getTime_start(), activity.getTime_end()
                        )+")";
                    } else if(timeNowMillis>activity.getTime_start() && timeNowMillis<activity.getTime_end()){
                        onTime = true;
                        notification_enter = "Anda telah datang di kegiatan ("+ DateUtils.getFriendlyTimeStartAndEnd(
                                activity.getTime_start(), activity.getTime_end()
                        )+")";
                        notification_dwell = "Anda sedang mengikuti kegiatan ("+ DateUtils.getFriendlyTimeStartAndEnd(
                                activity.getTime_start(), activity.getTime_end()
                        )+")";
                        notification_exit = "Anda menyelesaikan kegiatan ("+ DateUtils.getFriendlyTimeStartAndEnd(
                                activity.getTime_start(), activity.getTime_end()
                        )+")";
                    } else if(timeNowMillis>activity.getTime_end()){
                        notification_enter = "Anda datang terlambat di kegiatan ini ("+ DateUtils.getFriendlyTimeStartAndEnd(
                                activity.getTime_start(), activity.getTime_end()
                        )+")";
                        notification_dwell = "Kegiatan sudah selesai ("+ DateUtils.getFriendlyTimeStartAndEnd(
                                activity.getTime_start(), activity.getTime_end()
                        )+")";
                        notification_exit = "Anda meninggalkan tempat kegiatan ("+ DateUtils.getFriendlyTimeStartAndEnd(
                                activity.getTime_start(), activity.getTime_end()
                        )+")";
                    } else{
                        notification_enter = "Apakah anda sudah datang?";
                        notification_dwell = "Apakah anda sudah ditempat?";
                        notification_exit = "Apakah anda sudah selesai?";
                    }
                    switch (transition){
                        case Geofence.GEOFENCE_TRANSITION_ENTER:
                            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                            bigTextStyle.setBigContentTitle(activity.getName());
                            bigTextStyle.bigText(notification_enter);

                            builder.setSmallIcon(R.drawable.kegiatankulogo)
                                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                            R.drawable.kegiatankulogo))
                                    .setContentTitle(activity.getName())
                                    .setContentText(notification_enter);
                            if(onTime){
                                FirebaseUtils.getDatabaseReference()
                                        .child(context.getString(R.string.node_user))
                                        .child(FirebaseUtils.getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                Calendar c = Calendar.getInstance();
                                                int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                                                Long timemillis = TimeUnit.HOURS
                                                        .toMillis(hourOfDay);
                                                Participant participant = new Participant(
                                                        user.getNama(),
                                                        user.getEmail(),
                                                        timemillis
                                                );
                                                FirebaseUtils.getDatabaseReference()
                                                        .child(context.getString(R.string.node_attendance))
                                                        .child(activityKey)
                                                        .child(user.getUid())
                                                        .setValue(participant);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                            break;
                        case Geofence.GEOFENCE_TRANSITION_DWELL:
                            builder.setSmallIcon(R.drawable.kegiatankulogo)
                                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                            R.drawable.kegiatankulogo))
                                    .setContentTitle(activity.getName())
                                    .setContentText(notification_dwell);
                            break;
                        case Geofence.GEOFENCE_TRANSITION_EXIT:
                            builder.setSmallIcon(R.drawable.kegiatankulogo)
                                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                            R.drawable.kegiatankulogo))
                                    .setContentTitle(activity.getName())
                                    .setContentText(notification_exit);
                            break;
                        default:
                            Log.e("Geofending", "Unknown transition!");
                    }
                    NotificationManager notificationManager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        int id = activities.indexOf(activityKey);
                        Intent intent = new Intent(context, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        builder.setContentIntent(pendingIntent);
                        notificationManager.notify(id, builder.build());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        // COMPLETED (6) Show a notification to alert the user that the ringer mode has changed.
        // Feel free to create a helper method (sendNotification)

    }

    private void setRingerMode(Context context, int mode){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT < 24 ||
                (Build.VERSION.SDK_INT >= 24 && notificationManager.isNotificationPolicyAccessGranted())){
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }
}
