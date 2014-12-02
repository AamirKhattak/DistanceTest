package sensortest.example.com;

import java.util.List;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.content.pm.ActivityInfo;

public class SensorActivity extends Activity implements SensorEventListener
{
    TextView textListSensors;
    TextView textGravity;
    SensorManager sensorManager;
    Sensor mOrientation;
    Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
// Find views
        View relativeLayout = findViewById(R.id.my_id);
        textListSensors = (TextView) findViewById(R.id.list_sensors);
        textListSensors.setText("");
// Get sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

// Test if orientation sensor exist on the device
        boolean orientok = sensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_UI);
        if (!orientok){
            sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
            textListSensors.setText("No orientation sensor\n");
            Button btn = (Button) findViewById(R.id.button_distance );
            btn.setEnabled(false);
        }

// Test if accelerometer sensor exist on the device
        boolean accelerometerok = sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (!accelerometerok){
            sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            textListSensors.append("No accelerometer sensor\n");
            Button btn = (Button) findViewById(R.id.button_distance );
            btn.setEnabled(false);
        }

// List sensors
        List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.d("SensorActivity", "Found sensors: " + list.size());
        if ( list.size() > 0)
        {
            textListSensors.append("\n" + "Sensor List" );
            textListSensors.append("\n" + "-----------" );
            for (Sensor currentSensor : list) {
                textListSensors.append("\n" + currentSensor.getName());
            }
        }
        relativeLayout.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }
     @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

   public void onAccuracyChanged(Sensor sensor, int accuracy) {
   }

    // Called when sensor changes
    public void onSensorChanged(SensorEvent event) {
        if ( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER )
        {
            int i = 0;
            for( float value_fl : event.values ) {
                String outfloat;
                outfloat = String.format("Acceleromter[%d]: %.2f", i, value_fl);
           //     outfloat = outfloat + "\n";
            //    textListSensors.append(outfloat);
                i = i + 1;
            }
        }
    }

    /** Called when the user clicks the Send button */
    public void getDistance(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, AndroidCamera.class);
        startActivity(intent);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private boolean checkCameraAutoFocusHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)){
            // this device has a camera auto focus
            return true;
        } else {
            // no camera on this device with auto focus
            return false;
        }
    }
    /** A safe way to get an instance of the Camera object. */

    public static Camera getCameraInstance(){

        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

}