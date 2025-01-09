package com.example.assignment3_relationshipcounter.service.firestore;
import android.content.Intent;

import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {

    /**
     *
     * @return the date and time for Relationship
     * @see com.example.assignment3_relationshipcounter.service.models.Relationship
     *
     */

    public static void passUserModelAsIntent(Intent intent, User user){
        intent.putExtra("firstName",user.getFirstName());
        intent.putExtra("lastName",user.getLastName());
        intent.putExtra("userId",user.getId());
    }
    public static User getUserModelFromIntent(Intent intent){
        User user = new User();
        user.setId(intent.getStringExtra("userId"));
        user.setFirstName(intent.getStringExtra("firstName"));
        user.setLastName(intent.getStringExtra("lastName"));
        return user;
    }

    /**
     * Make sure two users only has one chat room
     * @param userId1
     * @param userId2
     * @return
     */
    public String getChatRoomId(String userId1, String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    /**
     * Returns the current date as a Firestore Timestamp.
     *
     * @return Current date as a Firestore Timestamp.
     */
    public static Timestamp getCurrentTimestamp() {
        return Timestamp.now();
    }

    /**
     * Returns the current date as a formatted string (yyyy-MM-dd).
     *
     * @return Current date as a string.
     */
    public static Timestamp getCurrentDate() {
        return Timestamp.now();
    }

    public static long calculateDayCount(Timestamp startDate) {
        if (startDate == null) return 0;

        long currentTimeMillis = System.currentTimeMillis();
        long startTimeMillis = startDate.toDate().getTime();

        // Calculate the difference in milliseconds and convert to days
        long diffInMillis = currentTimeMillis - startTimeMillis;
        return TimeUnit.MILLISECONDS.toDays(diffInMillis);
    }

    public static String getOtherId(String currentId, List<String> listId){
        if(currentId.equals(listId.get(0))){
            return listId.get(1);
        }else{
            return listId.get(0);
        }
    }
}
