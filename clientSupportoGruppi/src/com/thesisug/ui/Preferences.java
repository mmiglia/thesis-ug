package com.thesisug.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.thesisug.R;

public class Preferences extends PreferenceActivity {
	private static final String TAG = "PreferenceActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preference);
	}
}
