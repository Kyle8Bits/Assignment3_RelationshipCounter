package com.example.assignment3_relationshipcounter.service.firestore;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    /**
     *
     * @return the date and time for Relationship
     * @see com.example.assignment3_relationshipcounter.service.models.Relationship
     *
     */

    public String getCurrentDate(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return currentDate.format(formatter);
    }

    public String getCurrentTime(){
        LocalTime currentTime = LocalTime.now();

        // Format the time to "HH:mm"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Return the formatted time
        return currentTime.format(formatter);
    }


}
