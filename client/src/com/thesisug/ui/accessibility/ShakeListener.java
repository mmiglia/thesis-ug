package com.thesisug.ui.accessibility;


import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeListener implements SensorEventListener{
	public static String TAG = "ShakeListener";
	private OnShakeListener mOnShakeListener = null;  
	private SensorManager mSensorManager;
	private List<Sensor> mSensors;
	private Sensor mAccelerationSensor;
	
	private long before = 0;
	private long now = 0;
	private long mShakeTimeout = 400;
	private long minShakeDurationThreshold = 150;
	float[] previousValues = {0,0,0};
	float[] currentValues = {0,0,0};
	float minDeltaZforShake = 17.0F;
	
	private int mShakeCount = 0;
	
	private long currentReadTime = 0;
	private long previousReadTime = 0;
	private long samplingInstantReadCounts = 0;
	private int subsequentShakes = 0;
	
	/*
	 * for activate() and shutDown() to be 'sticky'
	 * meaning if you activate a ShakeListener
	 * object n times you need to shut it down n
	 * times before listener is actually unregistered.
	 * This achieves the goal of preventing a dispatching
	 * thread from denying the user of the possibility
	 * of quieting the phone when still notifying by
	 * unregistering the listener with a 'non-sticky'
	 * shutDown().
	 */
	private int numberOfActivations = 0;
	private int missingShutdownsOnReset = 0;
	
	
	
	public ShakeListener(SensorManager sm){
		mSensorManager = sm;

		mSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(mSensors.size() > 0) {
			mAccelerationSensor = mSensors.get(0);
			mSensorManager.registerListener(this, mAccelerationSensor, SensorManager.SENSOR_DELAY_GAME);
			++numberOfActivations;
		}

	}
	
	public synchronized void shutDown() {
		if (missingShutdownsOnReset == 0) {
			Log.i(TAG, "shutDown, missingShutdownsOnReset = " + missingShutdownsOnReset);
			if (numberOfActivations > 0) {
				Log.i(TAG, "shutDown, numberOfActivations = " + numberOfActivations + ", decreasing it");
				--numberOfActivations;
				if (numberOfActivations == 0) {
					Log.i(TAG, "shutDown, numberOfActivations = " + numberOfActivations + ", unregistering listener");
					mSensorManager.unregisterListener(this);
				}
			}
		} else {
			Log.i(TAG, "shutDown, missingShudownsOnReset = " + missingShutdownsOnReset + ", decreasing it");
			--missingShutdownsOnReset;
		}
	}
	
	public synchronized void activate() {
		++numberOfActivations;
		Log.i(TAG, "Activate, numberOfActivations = " + numberOfActivations);
		mSensorManager.registerListener(this, mAccelerationSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
	public synchronized void reset() {
		missingShutdownsOnReset += numberOfActivations;
		numberOfActivations = 0;
		Log.i(TAG, "reset, missingShutDownsOnReset = " + missingShutdownsOnReset + ", numberOfActivations = " + numberOfActivations);
		mSensorManager.unregisterListener(this);
	}

	
	public void setShakeTimeout(long timeout) {
		mShakeTimeout = timeout;
	}
	
	public long getShakeTimeout() {
		return mShakeTimeout;
	}
	
	public void setMinShakeDurationThreshold(long threshold) {
		minShakeDurationThreshold = threshold;
	}
	
	public long getMinShakeDurationThreshold() {
		return minShakeDurationThreshold;
	}

	public float getMinDeltaZforShake() {
		return minDeltaZforShake;
	}

	public void setMinDeltaZforShake(float minDeltaZforShake) {
		this.minDeltaZforShake = minDeltaZforShake;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public void onSensorChanged(SensorEvent event) {
		now = System.currentTimeMillis();
		updateSamplingInstants();
		recordCurrentValues(event.values);
		checkDoubleShakeTimeout();
		float [] deltas = computeDeltaWithPrevious();
		if (deltas[2] > minDeltaZforShake/*17.0D*/ && ((now - before) > minShakeDurationThreshold)) {
			before = now;
			Log.i(TAG, "Shake number " + ++mShakeCount);
			if (++subsequentShakes == 2) {
				subsequentShakes = 0;
				Log.e(TAG, "Double shake!");
				OnShake();
			}
		}
	}
	
	private void checkDoubleShakeTimeout() {
		if ((now - before) > 400) {
			subsequentShakes = 0;
		}
	}

	private void recordCurrentValues(float[] values) {
		for (int i = 0 ; i < 3 ; i++) {
			currentValues[i] = values[i];		
		}
		
	}

	private float[] computeDeltaWithPrevious() {
		float[] deltas = new float[3];
		for (int i = 0; i < 3 ; i++) {
			deltas[i] = currentValues[i] - previousValues[i];
		}
		return deltas;
	}
	
	public long getSamplingTime() {
		if (samplingInstantReadCounts != 0) {
			return currentReadTime - previousReadTime;
		} else {
			return -1;
		}
	}
	
	private void updateSamplingInstants() {
		previousReadTime = currentReadTime;
		currentReadTime = now;
		++samplingInstantReadCounts;
		if ((samplingInstantReadCounts % 100) == 0) {
			Log.v(TAG, "Sampling time = " + getSamplingTime());
		}
	}
	
	public void resetShakeCount() {
		mShakeCount = 0;
		Log.i(TAG, "Shake number was reset");
	}

	public void setOnShakeListener(OnShakeListener listener) {
		mOnShakeListener = listener;
	}  

	private void OnShake(){  
		if(mOnShakeListener!=null) {  
			mOnShakeListener.onShake();  
		}  
	}
	

	public interface OnShakeListener {  
		public abstract void onShake();  
	}

}