package mubin.khalife.dailyreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;


public class TimePreferenceActivity extends ActionBarActivity {

    SharedPreferences prefTime;
    SharedPreferences.Editor ed;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_preference);

        //Read value from stored preference and set it to the time selector
        prefTime = getSharedPreferences(ReminderPreference.PreferedTimeKey, Context.MODE_PRIVATE);
        String storedPrefTime = prefTime.getString(ReminderPreference.PreferedTimeKey,"22:00");

        String [] splitSavedTime = storedPrefTime.split(":");

        TimePicker tp = (TimePicker) findViewById(R.id.prefTimePicker);
        tp.setCurrentHour(Integer.parseInt(splitSavedTime[0]));
        tp.setCurrentMinute(Integer.parseInt(splitSavedTime[1]));

        //Toast.makeText(this,"Stored time preference is "+storedPrefTime,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_preference, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_pref) {
            TimePicker saveTime = (TimePicker) findViewById(R.id.prefTimePicker);

            String strHour = saveTime.getCurrentHour().toString();
            String strMinutes = saveTime.getCurrentMinute().toString();

            /*strHour = (strHour.length()<2)? "0"+strHour : strHour;
            strMinutes = (strMinutes.length()<2)? "0"+strMinutes : strMinutes;*/

            String timeToSave = strHour+":"+strMinutes;
            Toast.makeText(this,"Your time preference has been saved sucessfully",Toast.LENGTH_SHORT).show();
            ed = prefTime.edit();
            ed.putString(ReminderPreference.PreferedTimeKey, timeToSave);
            ed.commit();

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, saveTime.getCurrentHour());
            calendar.set(Calendar.MINUTE, saveTime.getCurrentMinute());
            calendar.set(Calendar.SECOND, 0);

            Intent myIntent = new Intent(this, ReminderReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent,0);

            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

            startActivity(new Intent(this, MainActivity.class));
            this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        else if (id == R.id.cancel_action)
        {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
