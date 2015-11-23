package com.leechangu.sweettask;


import com.leechangu.sweettask.settask.TaskPreferenceActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
                parseTaskItem.setContent(parseObject.getString("taskContent"));
                parseTaskItem.setVibrate(parseObject.getBoolean("vibrate"));
                parseTaskItem.setActive(parseObject.getBoolean("active"));
                parseTaskItem.setTimeBasis(ParseTaskItem.TimeBasisEnum.valueOf(parseObject.getString("timeBasis")));
                parseTaskItem.setIsPhotoTask(parseObject.getBoolean("isPhotoTask"));
                parseTaskItem.setIsPhotoTaskFinished(parseObject.getBoolean("isPhotoTaskFinished"));
                parseTaskItem.setIsMapTask(parseObject.getBoolean("isMapTask"));
                parseTaskItem.setIsMapTaskFinished(parseObject.getBoolean("isMapTaskFinished"));
                parseTaskItem.setMapInfo(parseObject.getString("mapInfo"));
                parseTaskItem.setAlarmTonePath(parseObject.getString("alarmTonePath"));
                parseTaskItem.setIfAllTasksFinished(parseObject.getBoolean("ifAllTasksFinished"));

                // Add it to parseTaskItems
                parseTaskItems.add(parseTaskItem);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> parseObjectList, ParseException e) {
//                for (ParseObject parseObject : parseObjectList) {
//                    // Create a new ParseTaskItem and pass all the fields from ParseObject to
//                    // ParseTaskItem (because could not cast ParseObject to ParseTaskItem)
//                    ParseTaskItem parseTaskItem = new ParseTaskItem();
//                    Log.d("TAG READALL", parseObject.getClassName() + "" + parseObject.getObjectId());
//                    parseTaskItem.setContent(parseObject.getString("taskContent"));
//                    parseTaskItem.setVibrate(parseObject.getBoolean("vibrate"));
//                    parseTaskItem.setActive(parseObject.getBoolean("active"));
//                    parseTaskItem.setTimeBasis(ParseTaskItem.TimeBasisEnum.valueOf(parseObject.getString("timeBasis")));
//                    parseTaskItem.setIsPhotoTask(parseObject.getBoolean("isPhotoTask"));
//                    parseTaskItem.setIsPhotoTaskFinished(parseObject.getBoolean("isPhotoTaskFinished"));
//                    parseTaskItem.setIsMapTask(parseObject.getBoolean("isMapTask"));
//                    parseTaskItem.setIsMapTaskFinished(parseObject.getBoolean("isMapTaskFinished"));
//                    parseTaskItem.setMapInfo(parseObject.getString("mapInfo"));
//                    parseTaskItem.setAlarmTonePath(parseObject.getString("alarmTonePath"));
//                    parseTaskItem.setIfAllTasksFinished(parseObject.getBoolean("ifAllTasksFinished"));
//
//                    // Add it to parseTaskItems
//                    parseTaskItems.add(parseTaskItem);
//                }
//            }
//        });

        return parseTaskItems;
    }


    // Read Single Task
    public static boolean readParseTask(final ParseTaskItem parseTaskItem){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseTaskItem");
        query.getInBackground(parseTaskItem.getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    parseTaskItem.setContent(parseObject.getString("taskContent"));
                    parseTaskItem.setVibrate(parseObject.getBoolean("vibrate"));
                    parseTaskItem.setActive(parseObject.getBoolean("active"));
                    parseTaskItem.setTimeBasis(ParseTaskItem.TimeBasisEnum.valueOf(parseObject.getString("timeBasis")));
                    parseTaskItem.setIsPhotoTask(parseObject.getBoolean("isPhotoTask"));
                    parseTaskItem.setIsPhotoTaskFinished(parseObject.getBoolean("isPhotoTaskFinished"));
                    parseTaskItem.setIsMapTask(parseObject.getBoolean("isMapTask"));
                    parseTaskItem.setIsMapTaskFinished(parseObject.getBoolean("isMapTaskFinished"));
                    parseTaskItem.setMapInfo(parseObject.getString("mapInfo"));
                    parseTaskItem.setAlarmTonePath(parseObject.getString("alarmTonePath"));
                    parseTaskItem.setIfAllTasksFinished(parseObject.getBoolean("ifAllTasksFinished"));

                } else {
                    // something went wrong
                    // Else show the error message on TaskPreferenceActivity
                    new TaskPreferenceActivity().toastSomething(e.toString());
                }
            }
        });

        return true;
    }

    // Create
    public static boolean createParseTask(final ParseTaskItem parseTaskItem){

        ParseUser currentUser = ParseUser.getCurrentUser();

        final ParseObject ParseTaskItemObject = new ParseObject("ParseTaskItem");
        ParseTaskItemObject.put("belonger", currentUser.getUsername());
        ParseTaskItemObject.put("taskContent", parseTaskItem.getContent());
        ParseTaskItemObject.put("vibrate", parseTaskItem.isVibrate());
        ParseTaskItemObject.put("active", parseTaskItem.isActive());
        ParseTaskItemObject.put("timeBasis", parseTaskItem.getTimeBasisEnum().toString());
        ParseTaskItemObject.put("isPhotoTask", parseTaskItem.isPhotoTask());
        ParseTaskItemObject.put("isPhotoTaskFinished", parseTaskItem.isPhotoTaskFinished());
        ParseTaskItemObject.put("isMapTask", parseTaskItem.isMapTask());
        ParseTaskItemObject.put("isMapTaskFinished", parseTaskItem.isMapTaskFinished());
        ParseTaskItemObject.put("mapInfo", parseTaskItem.getMapInfo());
        ParseTaskItemObject.put("alarmTonePath", parseTaskItem.getAlarmTonePath());
        ParseTaskItemObject.put("ifAllTasksFinished", parseTaskItem.ifAllTasksFinished());
        try {
            ParseTaskItemObject.save();
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
            parseObject.put("taskContent", parseTaskItem.getContent());
            parseObject.put("vibrate", parseTaskItem.isVibrate());
            parseObject.put("active", parseTaskItem.isActive());
            parseObject.put("timeBasis", parseTaskItem.getTimeBasisEnum().toString());
            parseObject.put("isPhotoTask", parseTaskItem.isPhotoTask());
            parseObject.put("isPhotoTaskFinished", parseTaskItem.isPhotoTaskFinished());
            parseObject.put("isMapTask", parseTaskItem.isMapTask());
            parseObject.put("isMapTaskFinished", parseTaskItem.isMapTaskFinished());
            parseObject.put("mapInfo", parseTaskItem.getMapInfo());
            parseObject.put("alarmTonePath", parseTaskItem.getAlarmTonePath());
            parseObject.put("ifAllTasksFinished", parseTaskItem.ifAllTasksFinished());
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

}
