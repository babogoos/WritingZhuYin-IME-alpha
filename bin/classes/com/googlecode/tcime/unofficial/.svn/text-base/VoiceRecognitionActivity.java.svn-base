/*
 * Copyright 2011 Scribe Hwang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.tcime.unofficial;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;


public class VoiceRecognitionActivity extends Activity {
	private static final String TAG = "TCIME";

	/**
	 * Create an Intent to do Voice Recognition
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent iVR = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		if(getPackageManager().queryIntentActivities(iVR, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
			iVR.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			iVR.putExtra("calling_package", "com.googlecode.tcime.unofficial");
			iVR.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5); // Limit 5 possible results
			iVR.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_speaknow));
			Log.d(TAG, "Got Voice intent, sending...");
			startActivityForResult(iVR, 1);
		} else {
			// Prompt the user to install Voice Recognition
			new AlertDialog.Builder(this)
				.setTitle(R.string.str_notavailable)
				.setMessage(R.string.voice_missing)
				.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int i) {
						di.dismiss();
						finish();
					}
			})
			.show();
		}
	}

	/**
	 * Called when Voice Recognition got the data.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1 && resultCode == RESULT_OK) {  // Handle successful
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			Log.d(TAG, "Voice length = " + matches.size());
			final CharSequence[] results = matches.toArray(new CharSequence[matches.size()]);
			for(CharSequence c: results) Log.i(TAG, "Voice = " + c);
			new AlertDialog.Builder(this)
				.setTitle(R.string.voice_chooseone)
				.setItems(results, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int item) {
						Intent iResult = new Intent();
						iResult.setAction(AbstractIME.TEXT_GOT);
						iResult.putExtra("TEXT_RESULT", results[item]);
						Log.d(TAG, "Voice = " + results[item]);
						sendBroadcast(iResult); // Make an Intent to broadcast back to IME
						finish();
					}
			})
			.setOnCancelListener(new DialogInterface.OnCancelListener() {
				// Handles user cancellation
				public void onCancel(DialogInterface dialog) {
					Log.d(TAG, "Voice cancellation");
					dialog.dismiss();
					finish();
				}
			})
			.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
