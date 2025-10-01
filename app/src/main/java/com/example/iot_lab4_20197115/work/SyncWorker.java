package com.example.iot_lab4_20197115.work;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.iot_lab4_20197115.data.PostRepository;

public class SyncWorker extends Worker {
    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params){ super(context, params); }

    @NonNull @Override public Result doWork() {
        boolean ok = new PostRepository(getApplicationContext()).refreshBlocking();
        return ok? Result.success() : Result.retry();
    }
}