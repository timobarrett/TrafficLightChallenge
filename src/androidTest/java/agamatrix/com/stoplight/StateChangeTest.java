package agamatrix.com.stoplight;

import android.util.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StateChangeTest {
    private boolean mResetPressed = false;
    private final String[] mColorStr = {"red","green","yellow"};
    private int colorIndex;

    @Test
    public void plainLoopTest(){
        final long RED_ON_TIME = 3000L;
        final long YELLOW_ON_TIME = 2000L;
        final long GREEN_ON_TIME = 1000L;
        long mSystemTime;
        long currentTimer;
        mSystemTime = System.currentTimeMillis();
        looping();
        while (!mResetPressed){
            currentTimer = System.currentTimeMillis();
            if ((currentTimer - mSystemTime) > RED_ON_TIME && mColorStr[colorIndex].equals("red")){
                Log.d("TEST","RED TO GREEN INDEX = "+colorIndex);
                colorIndex++;
                mSystemTime = System.currentTimeMillis();
            }
            if ((currentTimer - mSystemTime) > GREEN_ON_TIME && mColorStr[colorIndex].equals("green")){
                Log.d("TEST","GREEN TO YELLOWINDEX = "+colorIndex);
                colorIndex++;
                mSystemTime = System.currentTimeMillis();
            }
            if ((currentTimer - mSystemTime) > YELLOW_ON_TIME && mColorStr[colorIndex].equals("yellow")){
                Log.d("TEST","YELLOW TO REDINDEX = "+colorIndex);
                colorIndex=0;
                mSystemTime = System.currentTimeMillis();
            }

        }
    }

    private void looping(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(12000);
                } catch (InterruptedException e) {
                    Log.d("TEST", "Interrupt exception");
                }
                mResetPressed = true;
            }
        }).start();
    }

}
