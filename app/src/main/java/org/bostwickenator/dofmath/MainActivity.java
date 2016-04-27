package org.bostwickenator.dofmath;

import android.os.Bundle;

import android.widget.SeekBar;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.github.ma1co.pmcademo.app.BaseActivity;
import com.github.ma1co.pmcademo.app.Logger;

public class MainActivity extends BaseActivity {

    SeekBar focalLength, fstop, subjectDistance;
    TextView focalLengthValue, fstopValue, subjectDistanceValue, dof;

    SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            doMath();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                StringWriter sw = new StringWriter();
                sw.append(throwable.toString());
                sw.append("\n");
                throwable.printStackTrace(new PrintWriter(sw));
                Logger.error(sw.toString());

                System.exit(0);
            }
        });

        focalLength = (SeekBar)findViewById(R.id.seekBarFocalLength);
        fstop = (SeekBar)findViewById(R.id.seekBarFStop);
        subjectDistance = (SeekBar)findViewById(R.id.seekBarSubjectDistance);

        focalLengthValue = (TextView)findViewById(R.id.textViewFocalLengthValue);
        fstopValue = (TextView)findViewById(R.id.textViewFStopValue);
        subjectDistanceValue = (TextView)findViewById(R.id.textViewSubjectDistanceValue);

        dof =  (TextView)findViewById(R.id.textViewDof);


        focalLength.setOnSeekBarChangeListener(seekListener);
        fstop.setOnSeekBarChangeListener(seekListener);
        subjectDistance.setOnSeekBarChangeListener(seekListener);

        doMath();
    }

    public void doMath(){



        //inputs
        float distance = ((float)subjectDistance.getProgress()) / 10;
        float CoC = 0.03f;
        float aperture = ((float)fstop.getProgress()) / 10;
        float focal = focalLength.getProgress();
        float units = 1;

        focalLengthValue.setText(""+focal);
        fstopValue.setText("" + aperture);
        subjectDistanceValue.setText(""+distance);


        //outputs
        float dofNear, hyperFocal, dofFar, dofTotal, dofNearPercent, dofFarPercent;

         hyperFocal = (focal * focal) / (aperture * CoC) + focal;

            distance = distance*1000*units; // convert to millimeters
            dofNear = ((hyperFocal - focal) * distance) / (hyperFocal + distance - (2*focal));

            // Prevent 'divide by zero' when calculating far distance.
            if ( (hyperFocal - distance) <= 0.00001)
                dofFar = 10000000.0f; // set infinity at 10000m
            else
                dofFar = ((hyperFocal-focal) * distance) / (hyperFocal - distance);

            // Calculate percentage of DOF in front and behind the subject.
            dofNearPercent = (distance - dofNear)/(dofFar-dofNear) * 100.0f;
            dofFarPercent = (dofFar - distance)/(dofFar-dofNear) * 100.0f;

            // Convert depth of field numbers to proper units
            dofNear = dofNear / 1000.0f / units;
            dofFar  = dofFar / 1000.0f / units;
            dofTotal = dofFar - dofNear;
            distance = distance / 1000.0f / units;



        dof.setText("Near limit:" + dofNear + "\n"
        + "Far limit: " +dofFar + "\n"
        + "Total:     " + dofTotal + "\n"
        + "Hyperfocal distance :" + (hyperFocal / 1000f)
        );
    }

}