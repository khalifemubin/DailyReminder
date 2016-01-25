package mubin.khalife.dailyreminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.widget.AbsListView.MultiChoiceModeListener;

public class MainActivity extends ActionBarActivity {
    private PendingIntent pendingIntent;
    SharedPreferences prefTime;
    EntryDisplayAdapter da;
    ListView lst;

    int iListEntriesSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prefTime = getPreferences(MODE_PRIVATE);

        prefTime = getSharedPreferences(ReminderPreference.PreferedTimeKey, Context.MODE_PRIVATE);
        String storedPrefTime = prefTime.getString(ReminderPreference.PreferedTimeKey, "22:00");

        String[] splitSavedTime = storedPrefTime.split(":");
        int savedHour = Integer.parseInt(splitSavedTime[0]);
        int savedMinute = Integer.parseInt(splitSavedTime[1]);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, savedHour);
        calendar.set(Calendar.MINUTE, savedMinute);
        calendar.set(Calendar.SECOND, 0);

        //calendar.set(Calendar.AM_PM,Calendar.PM);

        Intent myIntent = new Intent(MainActivity.this, ReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);



        /*
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, savedHour);
        calendar.set(Calendar.MINUTE, savedMinute);
        calendar.set(Calendar.SECOND, 00);

        Intent intent = new Intent(this, ReminderService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pintent);
        */

        DBHelper db = new DBHelper(this);

        if (db.reminders_count() <= 0) {

            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Oops!");
            ad.setMessage("There are no entries to show!. Do you want to add one?");
            //ad.setIcon(android.R.drawable.ic_dialog_alert);
            ad.setIcon(R.mipmap.ic_dialog_alert);
            ad.setPositiveButton(R.string.text_add, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(MainActivity.this, AddReminder.class));
                }
            });

            ad.setNegativeButton(android.R.string.no, null);

            ad.show();
        } else {
            repopulate_entries();
        }

        //Code to background service starts
        /*
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, savedHour);
        cal.set(Calendar.MINUTE, savedMinute);
        cal.set(Calendar.SECOND, 0);

        PendingIntent pi = PendingIntent.getService(this,0,new Intent(this, ReminderReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
        */
        //Code to background service ends
    }

    @Override
    protected void onResume() {
        super.onResume();
        repopulate_entries();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void repopulate_entries() {
        final DBHelper db = new DBHelper(this);
        //Populate the listview
        final List<Entries> entries_data = new ArrayList<Entries>();
        Cursor cr = db.getAllEntries();

        //System.out.println(cr);

        //ArrayList<String> lstEntries = new ArrayList<String>();

        String tempTitle, tempDesc, tempDate, tempPriority = "";
        int tempId = 0;
        int iCharacetersToDisplayTitle = 30;
        int iCharacetersToDisplayDescription = 140;

        if (cr != null) {
            while (cr.moveToNext()) {
                int cursorIndex = cr.getColumnIndex(db.PRIORITY_REMINDER_TITLE);
                tempTitle = cr.getString(cursorIndex);

                //Take only first few characeters from title
                if(tempTitle.length()>iCharacetersToDisplayTitle) {
                    tempTitle = tempTitle.substring(0, Math.min(tempTitle.length(), iCharacetersToDisplayTitle));
                    tempTitle += "..";
                }

                cursorIndex = cr.getColumnIndex(db.PRIORITY_REMINDER_DESC);
                tempDesc = cr.getString(cursorIndex);

                if(tempDesc.length()>iCharacetersToDisplayDescription) {
                    tempDesc = tempDesc.substring(0, Math.min(tempDesc.length(), iCharacetersToDisplayDescription));
                    tempDesc+="..";
                }

                cursorIndex = cr.getColumnIndex(db.PRIORITY_REMINDER_DATE);
                //tempDate = "added on " + cr.getString(cursorIndex);
                tempDate = cr.getString(cursorIndex);

                cursorIndex = cr.getColumnIndex(db.PRIORITY_REMINDER_ID);
                tempId = cr.getInt(cursorIndex);

                cursorIndex = cr.getColumnIndex(db.PRIORITY_COLUMN_PRIORITY);
                tempPriority = cr.getString(cursorIndex).toLowerCase();

                entries_data.add(new Entries(tempId, tempTitle, tempDesc, tempDate, tempPriority));
                //  entries_data  = new Entries[]{new Entries(tempTitle,tempDesc,tempDate)};


            }
            cr.close();

            da = new EntryDisplayAdapter(this, entries_data);

            lst = (ListView) findViewById(R.id.lvEntries);

            lst.setAdapter(da);


            //Contextual action bar code starts


            lst.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

            // Capture ListView item click
            lst.setMultiChoiceModeListener(new MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                    View c = lst.getChildAt(position);

                    if (checked) {
                        iListEntriesSelected++;
                        da.setNewSelection(position, checked);
                        c.setBackgroundColor(Color.parseColor("#FF0000"));
                    } else {
                        iListEntriesSelected--;
                        //da.removeSelection(position);
                        da.setNewSelection(position, checked);
                        c.setBackgroundResource(R.color.app_bacckground);
                    }

                    mode.setTitle(iListEntriesSelected + " selected");
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    iListEntriesSelected = 0;
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.delete_main, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete_entry:
                            // Calls getSelectedIds method from ListViewAdapter Class
                            Map<Integer, Boolean> selected = da.getSelectedIds();

                            List<Integer> dbDeleteIds = new ArrayList<Integer>();

                            for (Map.Entry<Integer, Boolean> entry : selected.entrySet()) {
                                //System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());

                                try {
                                    Entries deleteEntry = da.getItem(entry.getKey());
                                    dbDeleteIds.add(deleteEntry.entryId);

                                    //lst.removeViewAt(entry.getKey());
                                    da.remove(deleteEntry);
                                    //da.notifyDataSetChanged();


                                } catch (Exception e) {
                                    System.out.println("Delete exception - " + e.toString());
                                }

                            }

                            if (dbDeleteIds.size() > 0) {
                                List<String> newList = new ArrayList<String>(dbDeleteIds.size());

                                for (Integer myInt : dbDeleteIds) {
                                    newList.add(String.valueOf(myInt));
                                }

                                String[] strarray = newList.toArray(new String[0]);

                                for (String temp : strarray)
                                    System.out.println(temp);

                                db.delete_reminder(strarray);

                                Toast.makeText(MainActivity.this, "Entry deleted sucessfully", Toast.LENGTH_SHORT).show();

                                //refresh the adapter
                                repopulate_entries();
                            }

                            mode.finish();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    da.clearSelection();
                }
            });

            /*
            lst.setMultiChoiceModeListener(new MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode,
                                                      int position, long id, boolean checked) {
                    // Capture total checked items
                    final int checkedCount = lst.getCheckedItemCount();
                    // Set the CAB title according to total checked items
                    mode.setTitle(checkedCount + " Selected");
                    // Calls toggleSelection method from ListViewAdapter Class
                    da.toggleSelection(position);
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            // Calls getSelectedIds method from ListViewAdapter Class
                            SparseBooleanArray selected = da
                                    .getSelectedIds();
                            // Captures all selected ids with a loop
                            for (int i = (selected.size() - 1); i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    Entries selecteditem = da
                                            .getItem(selected.keyAt(i));
                                    // Remove selected items following the ids
                                    da.remove(selecteditem);
                                }
                            }
                            // Close CAB
                            mode.finish();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.delete_main, menu);
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    // TODO Auto-generated method stub
                    da.removeSelection();
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    // TODO Auto-generated method stub
                    return false;
                }
            });
            */

            //Contextual action bar code ends

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings)
            Toast.makeText(this, "Settings option selected", Toast.LENGTH_SHORT).show();
        else*/
        if (id == R.id.add_reminder) {
            startActivity(new Intent(this, AddReminder.class));
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.set_time) {
            //Toast.makeText(this, "Let's store the time preference", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TimePreferenceActivity.class));
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.app_exit)
            finish();

        return super.onOptionsItemSelected(item);
    }

}
