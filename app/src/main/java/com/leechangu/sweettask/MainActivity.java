package com.leechangu.sweettask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends BaseActionBarActivity {
    private SweetTaskDbHelper sweetTaskDbHelper;
    private ListView taskListView;
    private final static String EDIT_STRING = "Edit";
    private final static String DELETE_STRING = "Delete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskListView = (ListView)findViewById(R.id.taskListView);
        sweetTaskDbHelper = new SweetTaskDbHelper(this);

        // display tasks in listView
        loadListViewFromDb();
        registerForContextMenu(taskListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_new).setVisible(true);
        menu.findItem(R.id.menu_item_delete).setVisible(false);
        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, EDIT_STRING);
        menu.add(0, v.getId(), 0, DELETE_STRING);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CommonTaskItem taskItem = (CommonTaskItem)taskListView.getItemAtPosition(itemInfo.position);

        switch (item.toString())
        {
            case EDIT_STRING:
                Intent intent = new Intent();
                intent.setClass(this, SetCommonTaskActivity.class);
                intent.putExtra(getResources().getString(R.string.intent_extra_new_or_edit), "Edit");
                intent.putExtra(getResources().getString(R.string.intent_extra_task_item_key),taskItem.getCreated());
                Toast.makeText(getApplicationContext(), "Send itemKey=" + taskItem.getCreated(), Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case DELETE_STRING:
                sweetTaskDbHelper.deleteTask(taskItem);
                break;
        }
        loadListViewFromDb();
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadListViewFromDb();
    }

    private void loadListViewFromDb()
    {
        List<CommonTaskItem> taskItems = sweetTaskDbHelper.getAllTaskItems();
        ArrayAdapter arrayAdapter=new TaskArrayAdapter(this,android.R.layout.simple_list_item_1, taskItems);
        taskListView.setAdapter(arrayAdapter);
    }
}
