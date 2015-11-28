package com.leechangu.sweettask;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by CharlesGao on 15-11-25.
 *
 * Function: This is a class that manage the user from parse cloud
 */
public class UserMngRepository {


    public static String getPartnerUsername(){
        ParseUser parseUser = ParseUser.getCurrentUser();
        String partnerId = parseUser.get("partnerId").toString();
        return convertIdToUsername(partnerId);
    }

    public static boolean isPartnerUsernameValid(String partnerUsername){

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo("username", partnerUsername);
        try {
            List<ParseUser> parseUsers = parseQuery.find();
            if (parseUsers.size()==0) return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean isThisUserHavePartnerAlready(String partnerUsername){

        String partnerId = UserMngRepository.convertUsernameToId(partnerUsername);
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo("objectId", partnerId);

        try {
            List<ParseUser> parseUsers = parseQuery.find();
            ParseUser thisUser = parseUsers.get(0);
            if(thisUser.get("partnerId")==null){
                // ==null, which means does not have a partner
                return false;
            }else{
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean bindPartner(String partnerUsername){

        // TODO HARD CODE FOR TESTING, should use partnerUsername
        String partnerId = UserMngRepository.convertUsernameToId("111");
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put("partnerId", partnerId);
        try {
            parseUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Send binding notification
        ParseNotificationRepository.sendBindInvitationById(partnerId);

        return true;
    }

    public static boolean unbindPartner(){

        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put("partnerId", "");
        try {
            parseUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String convertUsernameToId(String username){
        String thisUserId = null;
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo("username", username);
        try {
            List<ParseUser> parseUsers = parseQuery.find();
            ParseUser thisUser = parseUsers.get(0);
            thisUserId = thisUser.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return thisUserId;
    }

    public static String convertIdToUsername(String Id){
        String thisUserUsername = null;
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo("objectId", Id);
        try {
            List<ParseUser> parseUsers = parseQuery.find();
            ParseUser thisUser = parseUsers.get(0);
            thisUserUsername = thisUser.getUsername();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return thisUserUsername;
    }

}
