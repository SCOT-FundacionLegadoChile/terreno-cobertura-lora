package cl.matiml.fundacionlegado.scot.loreapp;


import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class BluetoothThread extends HandlerThread {
    private String TAG = BluetoothThread.class.getSimpleName();
    private Handler mWorkerHandler;

    public BluetoothThread(String name) {
        super(name);
    }

    public void postTask(Runnable task) {
        Log.i(TAG, "postTask - in");
        mWorkerHandler.post(task);
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper());
    }
}
