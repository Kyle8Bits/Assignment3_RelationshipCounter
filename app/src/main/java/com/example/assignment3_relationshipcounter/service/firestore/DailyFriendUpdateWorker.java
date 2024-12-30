package com.example.assignment3_relationshipcounter.service.firestore;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


//Function use for update the daily counter
public class DailyFriendUpdateWorker extends Worker {

    public DailyFriendUpdateWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get an instance of DataUtils
        DataUtils dataUtils = new DataUtils();
        final boolean[] isSuccessful = {true};
        // Call the updateCounterDaily method
        dataUtils.updateCounterDaily(new DataUtils.NormalCallback<Void>() {
            @Override
            public void onSuccess() {
                System.out.println("All counters updated successfully.");
                isSuccessful[0] = true;
            }
            @Override
            public void onFailure(Exception e) {
                System.err.println("Failed to update counters: " + e.getMessage());
                isSuccessful[0] = false;
            }
        });
        // Return success or failure based on the callback result
        return isSuccessful[0] ? Result.success() : Result.failure();
    }

}
