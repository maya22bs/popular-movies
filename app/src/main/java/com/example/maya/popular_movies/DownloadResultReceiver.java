package com.example.maya.popular_movies;


        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Parcel;
        import android.os.Parcelable;
        import android.os.ResultReceiver;

public class DownloadResultReceiver extends ResultReceiver {
    /**
     * class for transfer data from movie service to the activity that called it
     * (it will be in mReciever and wil implement the Reciever interface declered here)
     */
    private Receiver mReceiver;

    public DownloadResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}