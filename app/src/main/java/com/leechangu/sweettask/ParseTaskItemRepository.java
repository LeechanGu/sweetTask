package com.leechangu.sweettask;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CharlesGao on 15-11-17.
 *
 * Function: This is a class that have all CRUD queries with Parse DB;
 *      ParseObject <---> ParseTaskItem
 */
public class ParseTaskItemRepository {

    // Read All
    // For this method, parameter is Username, when you want to have your partner's tasks just pass
    // his/her username.
    public static List<ParseTaskItem> getAllParseTasksFromParseByUserName(String username){
        final List<ParseTaskItem> parseTaskItems = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseTaskItem");
        query.whereEqualTo("belonger", username);
        try {
            List<ParseObject> parseObjects = query.find();
            // Save all the parse object
            for (ParseObject parseObject : parseObjects) {
                // Create a new ParseTaskItem and pass all the fields from ParseObject to
                // ParseTaskItem (because could not cast ParseObject to ParseTaskItem)
                ParseTaskItem parseTaskItem = new ParseTaskItem();
                parseTaskItem.setId(parseObject.getObjectId());
                fillTaskItem(parseTaskItem, parseObject);

                // Add it to parseTaskItems
                parseTaskItems.add(parseTaskItem);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return parseTaskItems;
    }

    // Create
    public static boolean createParseTask(final ParseTaskItem parseTaskItem, String username) {

        ParseUser currentUser = ParseUser.getCurrentUser();

        final ParseObject parseObject = new ParseObject("ParseTaskItem");
        parseObject.put("belonger", username);
        fillParseObjectWithoutBelonger(parseObject, parseTaskItem);
        try {
            parseObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    // Update
    public static boolean updateParseTask(final ParseTaskItem parseTaskItem){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseTaskItem");
        try {
            ParseObject parseObject = query.get(parseTaskItem.getId());
            // Update something to Parse Cloud, the belonger name would not change so it's
            // good to not do with belonger here for security reason.
            fillParseObjectWithoutBelonger(parseObject, parseTaskItem);
            try {
                parseObject.save();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static void fillTaskItem(ParseTaskItem parseTaskItem, ParseObject parseObject) {
        parseTaskItem.setContent(parseObject.getString("taskContent"));
        parseTaskItem.setVibrate(parseObject.getBoolean("vibrate"));
        parseTaskItem.setActive(parseObject.getBoolean("active"));
        parseTaskItem.setTimeBasis(ParseTaskItem.TimeBasisEnum.valueOf(parseObject.getString("timeBasis")));
        parseTaskItem.setIsPhotoTask(parseObject.getBoolean("isPhotoTask"));
        parseTaskItem.setIsPhotoTaskFinished(parseObject.getBoolean("isPhotoTaskFinished"));
        parseTaskItem.setIsMapTaskFinished(parseObject.getBoolean("isMapTaskFinished"));
        parseTaskItem.setMapInfo(parseObject.getString("mapInfo"));
        parseTaskItem.setAlarmTonePath(parseObject.getString("alarmTonePath"));
        parseTaskItem.setIfAllTasksFinished(parseObject.getBoolean("ifAllTasksFinished"));
        parseTaskItem.setCompleteDatesFromJSONArray(parseObject.getJSONArray("completeDates"));
    }

    private static void fillParseObjectWithoutBelonger(ParseObject parseObject, ParseTaskItem parseTaskItem) {
        parseObject.put("taskContent", parseTaskItem.getContent());
        parseObject.put("vibrate", parseTaskItem.isVibrate());
        parseObject.put("active", parseTaskItem.isActive());
        parseObject.put("timeBasis", parseTaskItem.getTimeBasisEnum().toString());
        parseObject.put("isPhotoTask", parseTaskItem.isPhotoTask());
        parseObject.put("isPhotoTaskFinished", parseTaskItem.isPhotoTaskFinished());
        parseObject.put("isMapTaskFinished", parseTaskItem.isMapTaskFinished());
        parseObject.put("mapInfo", parseTaskItem.getMapInfo());
        parseObject.put("alarmTonePath", parseTaskItem.getAlarmTonePath());
        parseObject.put("ifAllTasksFinished", parseTaskItem.ifAllTasksFinished());
        parseObject.put("completeDates", list2JSONArray(parseTaskItem.getCompleteDates()));
    }

    private static JSONArray list2JSONArray(List<Long> list) {
        JSONArray array = new JSONArray();
        for (Long each : list)
            array.put(each);
        return array;
    }

    // Delete
    public static boolean deleteParseTask(final ParseTaskItem parseTaskItem){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseTaskItem");
        try {
            ParseObject parseObject = query.get(parseTaskItem.getId());
            parseObject.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static ParseTaskItem getParseTaskItemById(String taskId) {

        ParseTaskItem parseTaskItem;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseTaskItem");
        query.whereEqualTo("objectId", taskId);
        try {
            List<ParseObject> parseObjects = query.find();
            parseTaskItem = new ParseTaskItem();
            fillTaskItem(parseTaskItem, parseObjects.get(0));
            return parseTaskItem;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Set Photo to specific task
    public static boolean setPhotoToTaskById(String taskId, ParseFile file){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseTaskItem");
        query.whereEqualTo("objectId", taskId);
        try {
            List<ParseObject> parseObjects = query.find();
            ParseObject parseObject = parseObjects.get(0);
            parseObject.put("taskPic", file);
            parseObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }


    public static Bitmap fetchPhotoByTaskId(String taskId){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseTaskItem");
        query.whereEqualTo("objectId", taskId);
        Bitmap bitmap = null;

        try {
            List<ParseObject> parseObjects = query.find();
            ParseObject parseObject = parseObjects.get(0);
            ParseFile parseFile = parseObject.getParseFile("taskPic");

            bitmap = BitmapFactory.decodeByteArray(parseFile.getData(), 0,
                                        parseFile.getData().length);

//            parseFile.getDataInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] data, ParseException e) {
//                    bitmap = BitmapFactory.decodeByteArray(data, 0,
//                            data.length);
//                }
//            });

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bitmap;
    }



}
