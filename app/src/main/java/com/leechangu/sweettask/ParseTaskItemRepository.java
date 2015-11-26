package com.leechangu.sweettask;


import com.parse.ParseException;
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

}
