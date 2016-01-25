package mubin.khalife.dailyreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by developer on 8/21/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "reminder.db";

    public static final String REMINDER_TABLE_NAME = "reminders";
    public static final String PRIORITY_TABLE_NAME = "priority_main";

    public static final String PRIORITY_COLUMN_PRIORITY_ID = "priority_id";
    public static final String PRIORITY_COLUMN_PRIORITY = "priority";

    public static final String PRIORITY_REMINDER_ID = "reminder_id";
    public static final String PRIORITY_REMINDER_TITLE = "title";
    public static final String PRIORITY_REMINDER_DESC = "description";
    public static final String PRIORITY_REMINDER_DATE = "date";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_priority_table = "CREATE TABLE "+PRIORITY_TABLE_NAME+" ("+PRIORITY_COLUMN_PRIORITY_ID
                +" INTEGER PRIMARY KEY AUTOINCREMENT,"+PRIORITY_COLUMN_PRIORITY+" TEXT)";
        db.execSQL(create_priority_table);

        db.execSQL("INSERT INTO "+PRIORITY_TABLE_NAME+" ("+PRIORITY_COLUMN_PRIORITY+") VALUES ('LOW'), ('NORMAL'), ('HIGH')");

        String create_reminders_table = "CREATE TABLE "+REMINDER_TABLE_NAME+" ("+PRIORITY_REMINDER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                PRIORITY_REMINDER_TITLE+" TEXT,"+PRIORITY_REMINDER_DESC+" TEXT,"+PRIORITY_COLUMN_PRIORITY_ID+" INTEGER,date TEXT) ";
        db.execSQL(create_reminders_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int reminders_count()
    {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        count = (int) DatabaseUtils.queryNumEntries(db,REMINDER_TABLE_NAME);
        return count;
    }

    public void delete_reminder(String[] ids)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(REMINDER_TABLE_NAME, "CAST(reminder_id AS TEXT) IN (" + new String(new char[ids.length-1]).replace("\0", "?,") + "?)", ids);
    }

    public Cursor getImportanceLevel()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor crLevels = db.rawQuery("SELECT * FROM "+PRIORITY_TABLE_NAME,null);
        return crLevels;
    }

    public String addEntry(HashMap<String, String> queryValues)
    {
        try
        {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(PRIORITY_REMINDER_TITLE,queryValues.get("entry_title"));
            values.put(PRIORITY_REMINDER_DESC,queryValues.get("entry_desc"));
            values.put(PRIORITY_COLUMN_PRIORITY_ID, Integer.parseInt(queryValues.get("priority_id")));

            Date dNow = new Date( );
            SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy 'at' hh:mm:ss a");

            String strDate = ft.format(dNow);

            values.put(PRIORITY_REMINDER_DATE, strDate);

            int entry_id = (int) db.insert(REMINDER_TABLE_NAME, null, values);


            /*final List< Entries> entries_data = new ArrayList<Entries>() ;
            entries_data.add(new Entries(entry_id, queryValues.get("entry_title"), queryValues.get("entry_desc"), strDate));
            EntryDisplayAdapter da = null;
            da.add(entries_data);*/

            return "Entry added sucessfully!";
        }
        catch (Exception ex)
        {
            return  "Operation Failed. Error :"+ex.getMessage();
        }
    }

    public String updateEntry(HashMap<String, String> queryValues, int entry_id)
    {
        try
        {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(PRIORITY_REMINDER_TITLE,queryValues.get("entry_title"));
            values.put(PRIORITY_REMINDER_DESC,queryValues.get("entry_desc"));
            values.put(PRIORITY_COLUMN_PRIORITY_ID, Integer.parseInt(queryValues.get("priority_id")));

            Date dNow = new Date( );
            SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy 'at' hh:mm:ss a");

            String strDate = ft.format(dNow);

            values.put(PRIORITY_REMINDER_DATE, strDate);

            db.update(REMINDER_TABLE_NAME, values, PRIORITY_REMINDER_ID+"="+entry_id, null);

            return "Entry updated sucessfully!";
        }
        catch (Exception ex)
        {
            return  "Operation Failed. Error :"+ex.getMessage();
        }
    }

    public String deleteEntry(int entry_id) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(REMINDER_TABLE_NAME,PRIORITY_REMINDER_ID + "=" + entry_id,null);
            return "Entry deleted sucessfully!";
        } catch (Exception ex) {
            return "Operation Failed. Error :" + ex.getMessage();
        }
    }

    public Cursor getAllEntries()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            String strQuery = "SELECT r.*,p."+PRIORITY_COLUMN_PRIORITY+" FROM "+REMINDER_TABLE_NAME+" r JOIN "+PRIORITY_TABLE_NAME
                    + " p ON p."+PRIORITY_COLUMN_PRIORITY_ID+"=r."+PRIORITY_COLUMN_PRIORITY_ID+" ORDER BY "+PRIORITY_REMINDER_ID+" DESC";
            Cursor crLevels = db.rawQuery(strQuery,null);
            return crLevels;
        }
        catch (Exception ex)
        {
            Log.d("Operation Failed.", ex.getMessage());
            return null;
        }
    }

    public Cursor getEntry(int entryId) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String strQuery = "SELECT * FROM " + REMINDER_TABLE_NAME + " WHERE "+PRIORITY_REMINDER_ID+"="+entryId;
            Cursor crLevels = db.rawQuery(strQuery, null);
            return crLevels;
        } catch (Exception ex) {
            Log.d("Operation Failed.", ex.getMessage());
            return null;
        }
    }

}
