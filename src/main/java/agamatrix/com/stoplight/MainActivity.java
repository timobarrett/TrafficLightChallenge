package agamatrix.com.stoplight;


/*
    Developed as a code challange for company named in package
    Posting because all you get is 30 minutes to develop

    Run, Don't walk if asked to interview here
 */

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = "stoplight " + MainActivity.class.getSimpleName();
    private  ImageButton mRedButton;
    private ImageButton mYellowButton;
    private ImageButton mGreenButton;
    private Button mResetButton;
    private boolean mResetPressed;
    private Handler mColorChangeHandler;


    //messages for handler
    private final int RED_TO_GREEN = 100;
    private final int GREEN_TO_YELLOW = 110;
    private final int YELLOW_TO_RED = 115;
    private LightProcessing mLightProcessing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG,"IN onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        mRedButton = findViewById(R.id.redButton);
        mYellowButton = findViewById(R.id.yellowButton);
        mGreenButton = findViewById(R.id.greenButton);
        mResetButton =  findViewById(R.id.resetButton);
        mResetPressed = false;
        mLightProcessing = new LightProcessing();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "IN onResume");
        super.onResume();

        new Thread(mLightProcessing).start();
        mRedButton.getBackground().setColorFilter(getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
        //added handler because prior to oreo threads could not update the UI
        mColorChangeHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Log.d(LOG_TAG, "Message received = " + message.what);
                switch (message.what) {
                    case RED_TO_GREEN: {
                        mRedButton.getBackground().setColorFilter(getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                        mGreenButton.getBackground().setColorFilter(getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
                        break;
                    }
                    case GREEN_TO_YELLOW: {
                        mGreenButton.getBackground().setColorFilter(getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                        mYellowButton.getBackground().setColorFilter(getColor(R.color.yellow), PorterDuff.Mode.MULTIPLY);
                        break;
                    }
                    case YELLOW_TO_RED: {
                        mYellowButton.getBackground().setColorFilter(getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                        mRedButton.getBackground().setColorFilter(getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
                        break;
                    }
                }
                return true;
            }
        });
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResetPressed = true;
               if ( mLightProcessing.isAlive()){
                   mLightProcessing.interrupt();
               }
                mColorChangeHandler.removeMessages(RED_TO_GREEN);
                mColorChangeHandler.removeMessages(GREEN_TO_YELLOW);
                mColorChangeHandler.removeMessages(YELLOW_TO_RED);
                resetLights();
                mResetPressed = false;
                new Thread(mLightProcessing).start();
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void resetLights(){
        Log.d(LOG_TAG,"IN resetLights");
        mYellowButton.getBackground().setColorFilter(getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        mGreenButton.getBackground().setColorFilter(getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        mRedButton.getBackground().setColorFilter(getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
    }
    private class LightProcessing extends Thread{
        private final String LOG_TAG = "stoplight - " + LightProcessing.class.getSimpleName();
        private long mSystemTime;
        private final String[] mColorStr = {"red","green","yellow"};

        private LightProcessing(){
            Log.d(LOG_TAG,"IN LightProcessing constructor");
        }
        public void run(){
            final long RED_ON_TIME = 3000L;
            final long YELLOW_ON_TIME = 2000L;
            final long GREEN_ON_TIME = 1000L;
            long currentTimer;
            int colorIndex = 0;
            mSystemTime = System.currentTimeMillis();
            while (!mResetPressed){
                currentTimer = System.currentTimeMillis();
                if (currentTimer - mSystemTime > RED_ON_TIME && mColorStr[colorIndex].equals("red")){
                    mColorChangeHandler.sendEmptyMessage(RED_TO_GREEN);
                    colorIndex++;
                    mSystemTime = System.currentTimeMillis();
                }
                if (currentTimer - mSystemTime > GREEN_ON_TIME && mColorStr[colorIndex].equals("green")){
                    mColorChangeHandler.sendEmptyMessage(GREEN_TO_YELLOW);
                    colorIndex++;
                    mSystemTime = System.currentTimeMillis();
                }
                if (currentTimer - mSystemTime > YELLOW_ON_TIME && mColorStr[colorIndex].equals("yellow")){
                    mColorChangeHandler.sendEmptyMessage(YELLOW_TO_RED);
                    colorIndex=0;
                    mSystemTime = System.currentTimeMillis();
                }

            }
        }
    }




}
