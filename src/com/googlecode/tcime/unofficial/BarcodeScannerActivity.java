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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


public class BarcodeScannerActivity extends Activity {
	private static final String TAG = "TCIME";
	private static final String PACKAGE = "com.google.zxing.client.android";
	private static final Method SET_PACKAGE;
	static {
		Method temp;
		try {
			temp = Intent.class.getMethod("setPackage", new Class[] {String.class});
		} catch(NoSuchMethodException ne) {
			temp = null;
		}
		SET_PACKAGE = temp;
	}

	/**
	 * Create an Intent to scan a QR Code as text
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent bcScanner = new Intent(PACKAGE + ".SCAN");
		if(getPackageManager().queryIntentActivities(bcScanner, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
			setPackage(bcScanner, PACKAGE); // Try setPackage (API 4)
			bcScanner.putExtra("SCAN_MODE", "QR_CODE_MODE");
			Log.d(TAG, "Got Barcode intent, sending...");
			startActivityForResult(bcScanner, 1);
		} else {
			// Prompt the user to install ZXing Barcode Scanner
			new AlertDialog.Builder(this)
				.setTitle(R.string.str_notavailable)
				.setMessage(R.string.barcode_missing)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int i) {
						Uri uri = Uri.parse("market://search?q=pname:" + PACKAGE);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
						finish();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int i) {
						di.dismiss();
						finish();
					}
			})
			.show();
		}
	}

	/**
	 * Called when Barcode Scanner got the data.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1 && resultCode == RESULT_OK) {  // Handle successful scan
			Log.d(TAG, "SCAN = " + data.getStringExtra("SCAN_RESULT"));
			Intent iResult = new Intent();
			iResult.setAction(AbstractIME.TEXT_GOT);
			iResult.putExtra("TEXT_RESULT", data.getStringExtra("SCAN_RESULT"));
			sendBroadcast(iResult); // Make an Intent to broadcast back to IME
		}
		super.onActivityResult(requestCode, resultCode, data);
		finish(); // End this Activity
	}

	/**
	 * Intent.setPackage alternative. Before API 4 the method doesn't exist.
	 *
	 * @param intent Intent object
	 * @param pname Package name
	 * @see android.content.Intent#setPackage(String)
	 */
	private static void setPackage(Intent intent, String pname) {
		if(SET_PACKAGE != null) {
			try {
				SET_PACKAGE.invoke(intent, pname);
			} catch(InvocationTargetException ite) {
				Log.w(TAG, ite.getTargetException());
			} catch(IllegalAccessException iae) {
				Log.w(TAG, iae);
			}
		}
	}
}
