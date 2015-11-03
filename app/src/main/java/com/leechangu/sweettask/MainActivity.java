package com.leechangu.sweettask;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.leechangu.sweettask.db.TaskDb;
import com.leechangu.sweettask.settask.TaskPreferenceActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActionBarActivity implements CheckBox.OnClickListener{
    private ListView taskListView;
    private TaskArrayAdapter taskArrayAdapter;
    private final static String EDIT_STRING = "Edit";
    private final static String DELETE_STRING = "Delete";
    public final static int REQUESTCODE_LOCATION = 2;
    List<CheckBox> checkBoxeList;

    ImageView uploadedPhoto;
    private String imageDecodableString;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static int RESULT_LOAD_IMG = 1;
    private static int RESULT_TAKE_PIC_FROM_CAMERA = 2;

    CheckBox mapCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TaskDb.init(MainActivity.this);
        taskListView = (ListView)findViewById(R.id.taskListView);
        final List<TaskItem> taskItems = TaskDb.getAll();
        taskArrayAdapter = new TaskArrayAdapter(this,R.layout.custom_task_row, taskItems);
        taskListView.setAdapter(taskArrayAdapter);
        taskListView.setLongClickable(true);
        taskListView.setClickable(true);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                final TaskItem taskItem = (TaskItem) taskListView.getItemAtPosition(position);

                final AlertDialog.Builder alert;
                alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Task requirement:");

                LayoutInflater inflater = getLayoutInflater();
                final View modifyView = inflater.inflate(R.layout.finish_task_dialog, null);
                alert.setView(modifyView);

                uploadedPhoto = (ImageView)modifyView.findViewById(R.id.iv_photo_upload);

                checkBoxeList = new ArrayList<CheckBox>();
                CheckBox contentCheckBox = (CheckBox)modifyView.findViewById(R.id.contentCheckBox);
                contentCheckBox.setText(taskItem.getContent());
                checkBoxeList.add(contentCheckBox);

                //Add checkBox for Map
                mapCheckBox = (CheckBox) modifyView.findViewById(R.id.mapCheckBox);
                if (taskItem.getMapInfo() == null) {
                    mapCheckBox.setVisibility(View.INVISIBLE);
                } else {
                    mapCheckBox.setVisibility(View.VISIBLE);
                    checkBoxeList.add(mapCheckBox);
                    mapCheckBox.setText("Map task (Click for destination)");
                    mapCheckBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mapCheckBox.isChecked()) {
                                mapCheckBox.setChecked(false);
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, MyLocationActivity.class);
                                intent.putExtra("map_info", taskItem.getMapInfo());
                                startActivityForResult(intent, REQUESTCODE_LOCATION);
                            }
                        }

                    });
                }
                //Add checkBox for Photo
                final CheckBox photoCheckBox = (CheckBox) modifyView.findViewById(R.id.photoCheckBox);
                photoCheckBox.setVisibility(View.INVISIBLE);
                if(taskItem.isPhotoTask()){
                    photoCheckBox.setVisibility(View.VISIBLE);
                    checkBoxeList.add(photoCheckBox);
                    photoCheckBox.setText("Photo task (Click for uploading photo)");
                    photoCheckBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if(photoCheckBox.isChecked()){
                                photoCheckBox.setChecked(false);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                                alertDialog.setTitle("Please select your photo").setPositiveButton("From camera", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        takePictureFromCamera();
                                    }
                                }).setNegativeButton("From gallery", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        loadImagefromGallery(uploadedPhoto);
                                    }
                                }).show();

                            }
                        }
                    });
                }

                alert.setPositiveButton("Finished", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        boolean allChecked = true;
                        for (CheckBox checkBox: checkBoxeList)
                        {
                            if (!checkBox.isChecked())
                            {
                                allChecked = false;
                                break;
                            }
                        }
                        if (allChecked) {
                            Toast.makeText(getApplicationContext(), "Congratulation!", Toast.LENGTH_SHORT).show();
                            taskItem.setFinished(true);
                            TaskDb.update(taskItem);
                            updateAlarmList();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Some tasks are yet to be finished.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNegativeButton("Not yet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        taskItem.setFinished(false);
                        TaskDb.update(taskItem);
                        taskArrayAdapter.notifyDataSetChanged();
                    }
                });
                alert.show();
            }

        });

        // display tasks in listView
        updateAlarmList();
        registerForContextMenu(taskListView);
    }

    // The following two methods are for upload photo for photoTask---------------------------------
    public void loadImagefromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void takePictureFromCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, RESULT_TAKE_PIC_FROM_CAMERA);
    }


    //----------------------------------------------------------------------------------------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_new).setVisible(true);
        menu.findItem(R.id.menu_item_delete).setVisible(false);
        menu.findItem(R.id.menu_item_save).setVisible(false);
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
        TaskItem taskItem = (TaskItem)taskListView.getItemAtPosition(itemInfo.position);
        switch (item.toString())
        {
            case EDIT_STRING:
                Intent intent = new Intent();
                intent.setClass(this, TaskPreferenceActivity.class);
                intent.putExtra("taskItem",taskItem);
                startActivity(intent);
                break;
            case DELETE_STRING:
                TaskDb.deleteEntry(taskItem);
                break;
        }
        updateAlarmList();
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateAlarmList();
    }

    @Override
    protected void onPause() {
        // setListAdapter(null);
        TaskDb.deactivate();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAlarmList();
    }

    public void updateAlarmList(){
        TaskDb.init(MainActivity.this);
        final List<TaskItem> taskItems = TaskDb.getAll();
        taskArrayAdapter.setTaskItems(taskItems);
        runOnUiThread(new Runnable() {
            public void run() {
                MainActivity.this.taskArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activeCheckBox) {
            CheckBox checkBox = (CheckBox) v;
            TaskItem taskItem = (TaskItem) taskArrayAdapter.getItem((Integer) checkBox.getTag());
            taskItem.setActive(checkBox.isChecked());
            TaskDb.update(taskItem);
            // AlarmActivity.this.callMathAlarmScheduleService();
            callScheduleService();
            if (checkBox.isChecked()) {
                Toast.makeText(MainActivity.this, taskItem.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void callScheduleService()
    {
        Intent scheduleServiceIntent = new Intent(this, TaskNotificationUpdateBroadcastReceiver.class);
        sendBroadcast(scheduleServiceIntent, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getApplicationContext(), "onActivityResult,"+requestCode+","+resultCode, Toast.LENGTH_SHORT).show();

        // For permission (Only when API 23+)
        int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        if (resultCode == REQUESTCODE_LOCATION) {
            Bundle bundle = data.getExtras();
            boolean finishMapOrNot = bundle.getBoolean(MyLocationActivity.LOCATION_RESULT);
            if (finishMapOrNot)
                mapCheckBox.setChecked(finishMapOrNot);
            else
                Toast.makeText(getApplicationContext(), "Map task not finished", Toast.LENGTH_SHORT).show();
        }

        try {
            // Selecting a photo by picking from gallery
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                uploadedPhoto.setImageBitmap(BitmapFactory
                        .decodeFile(imageDecodableString));

                // Decode the image to byte
                File imageFile = new File(imageDecodableString);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bm = BitmapFactory.decodeStream(fis);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50 , baos);
                byte[] b = baos.toByteArray();

                //decode a image-------
                Bitmap bmp=BitmapFactory.decodeByteArray(b,0,b.length);
                uploadedPhoto.setImageBitmap(bmp);
                //---------------------



                // Decode this image to ParseFile
//                file = new ParseFile("propic.jpg", b);
//
//                Toast.makeText(this, "You set your pro pic successfully!",
//                        Toast.LENGTH_SHORT).show();
//
//                // Only saved file can be push to Parse
//                file.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            Log.d("TAG", "save to Parse");
//                        }
//                    }
//                });

                // Setting a propic by camera
            } else if (requestCode == RESULT_TAKE_PIC_FROM_CAMERA && resultCode == RESULT_OK){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                uploadedPhoto
                        .setImageBitmap(photo);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] b = stream.toByteArray();

//                // Decode this image to ParseFile
//                file = new ParseFile("propic.jpg", b);
//
//                Toast.makeText(this, "You set your pro pic successfully!",
//                        Toast.LENGTH_SHORT).show();
//
//                // Only saved file can be push to Parse
//                file.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if(e == null){
//                            Log.d("TAG", "save to Parse");
//                        }
//                    }
//                });


            } else {
                Toast.makeText(this, "You haven't picked photo",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT)
                    .show();
            Log.d("TAG", e.toString());
        }

    }
}
