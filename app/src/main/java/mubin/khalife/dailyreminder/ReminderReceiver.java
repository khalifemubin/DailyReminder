package mubin.khalife.dailyreminder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    private final int NOTIFICATION_ID = 1;

    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service1 = new Intent(context, ReminderService.class);
        context.startService(service1);
    }
}
