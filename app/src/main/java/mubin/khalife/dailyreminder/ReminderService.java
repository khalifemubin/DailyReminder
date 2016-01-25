package mubin.khalife.dailyreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

public class ReminderService extends Service {

    SharedPreferences prefTime;

    private final int NOTIFICATION_ID = 1;

    public ReminderService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prefTime = getSharedPreferences(ReminderPreference.PreferedTimeKey, Context.MODE_PRIVATE);

        //String notifiedForTheDay = prefTime.getString(ReminderPreference.NotifiedForTheDay, "NO");

        String storedPrefTime = prefTime.getString(ReminderPreference.PreferedTimeKey, "22:00");

        String[] splitSavedTime = storedPrefTime.split(":");
        int savedHour = Integer.parseInt(splitSavedTime[0]);
        int savedMinute = Integer.parseInt(splitSavedTime[1]);

        Calendar cal = Calendar.getInstance();

        if (cal.get(Calendar.HOUR_OF_DAY) == savedHour && cal.get(Calendar.MINUTE) == savedMinute && cal.get(Calendar.SECOND) == 0) {
            Intent notificationintent = new Intent();
            notificationintent.setClass(this, AddReminder.class);
            PendingIntent notificationPage = PendingIntent.getActivity(this, 0, notificationintent, 0);

            NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this);
            mbuilder.setSmallIcon(android.R.drawable.ic_notification_overlay);
            mbuilder.setContentTitle(getString(R.string.app_name));
            mbuilder.setContentText("Daily Diary Reminder!");
            mbuilder.setOngoing(false);//when set to false, onclick of button again, ticker is flashed again once the notification from system is cleared

            mbuilder.setContentIntent(notificationPage);

            mbuilder.setTicker("Do you want to add any new entry to your diary?\nAdd the moment of your day to the diary.");

            mbuilder.setAutoCancel(true);

            mbuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            mbuilder.setVibrate(new long[]{1, 0, 1, 0, 1});


            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(NOTIFICATION_ID, mbuilder.build());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
