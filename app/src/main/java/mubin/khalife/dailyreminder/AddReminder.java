package mubin.khalife.dailyreminder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class AddReminder extends ActionBarActivity {

    int entry_id = 0;

    HashMap<Integer, String> spinnerMap = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        //Populate the spinner
        DBHelper db = new DBHelper(this);
        Cursor cr = null;

        int iPriority = 0;

        Intent intent = getIntent();

        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            entry_id = extras.getInt("entry_id", 0);

            if (entry_id > 0) {
                //Toast.makeText(this,"entry id="+entry_id,Toast.LENGTH_SHORT).show();
                //fetch record data from database
                cr = db.getEntry(entry_id);
                if (cr.moveToFirst()) {
                    String entryTitle = cr.getString(cr.getColumnIndex(db.PRIORITY_REMINDER_TITLE));
                    TextView txtEntryTitle = (TextView) findViewById(R.id.txtEntryTitle);
                    txtEntryTitle.setText(entryTitle);

                    String entryDesc = cr.getString(cr.getColumnIndex(db.PRIORITY_REMINDER_DESC));
                    TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
                    txtDescription.setText(entryDesc);

                    iPriority = cr.getInt(cr.getColumnIndex(db.PRIORITY_COLUMN_PRIORITY_ID));
                }
            }
        }


        //String[] spinnerArray = new String[cr.getCount()];
        ArrayList<String> spinnerArray = new ArrayList<String>();
        cr = db.getImportanceLevel();

        //Loop through the cursor
        if (cr.moveToFirst()) {
            String strPriority = "";

            do {
                int idColumn = cr.getColumnIndex(db.PRIORITY_COLUMN_PRIORITY_ID);
                int priorityColumn = cr.getColumnIndex(db.PRIORITY_COLUMN_PRIORITY);

                strPriority = cr.getString(priorityColumn);

                spinnerMap.put(cr.getInt(idColumn), strPriority);
                spinnerArray.add(cr.getString(priorityColumn));


            } while (cr.moveToNext());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner = (Spinner) findViewById(R.id.selImportance);
            spinner.setAdapter(adapter);

            if (entry_id > 0)
            {
                String selectedValue = spinnerMap.get(iPriority);
                //int spinnerPosition = getIndex(spinner, iPriority);
                int spinnerPosition = adapter.getPosition(selectedValue);
                spinner.setSelection(spinnerPosition);

            }


            //spinner.setSelection(spinnerPosition);
        }



    }

    /*
    private int getIndex(Spinner spinner, int myValue)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if ((Integer)spinner.getItemAtPosition(i) == myValue){
                index = i;
                break;
            }
        }
        return index;
    }
    */


    public void cancelAddEntries(View v) {
        //startActivity(new Intent(this,MainActivity.class));
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.back_action) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void validate_entry_fields(View v) {
        //Validate the values
        EditText txtEntryTitle = (EditText) findViewById(R.id.txtEntryTitle);
        String entryTitle = txtEntryTitle.getText().toString();
        entryTitle = entryTitle.trim();

        EditText txtDescription = (EditText) findViewById(R.id.txtDescription);
        String entryDescription = txtDescription.getText().toString();
        entryDescription = entryDescription.trim();

        if (entryTitle.equals("")) {
            txtEntryTitle.setError("Please enter the Title");
        } else if (entryDescription.equals("")) {
            txtDescription.setError("Please enter Description");
        } else {
            Spinner importance = (Spinner) findViewById(R.id.selImportance);
            //String name = importance.getSelectedItem().toString();
            //String id = spinnerMap.get(name);
            //int priority_id = ((int) importance.getSelectedItemId()) + 1;

            int priority_id = 0;

            String selectedValue = importance.getItemAtPosition(importance.getSelectedItemPosition()).toString();

            for (Map.Entry<Integer, String> entry : spinnerMap.entrySet()) {
                int  key = entry.getKey();
                String value = entry.getValue();

                if(value.equals(selectedValue))
                {
                    priority_id = key;
                    break;
                }
            }

            /*
            Iterator it = spinnerMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                //System.out.println(pair.getKey() + " = " + pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
            */



            /*String name = (String) spinnerMap.get(id);
            Toast.makeText(this, "Id is " + id + " and value is " + name, Toast.LENGTH_SHORT).show();*/

            //Save to database
            HashMap<String, String> queryValues = new HashMap<String, String>();
            queryValues.put("entry_title", entryTitle);
            queryValues.put("entry_desc", entryDescription);
            queryValues.put("priority_id", String.valueOf(priority_id));

            DBHelper db = new DBHelper(this);
            String resultAddEntry = "";

            if (entry_id == 0)
            {
                resultAddEntry = db.addEntry(queryValues);
            }
            else
            {
                resultAddEntry = db.updateEntry(queryValues,entry_id);
            }

            Toast.makeText(this, resultAddEntry, Toast.LENGTH_LONG).show();

            startActivity(new Intent(this, MainActivity.class));
            this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }
}
