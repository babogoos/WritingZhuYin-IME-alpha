/*
 * Copyright 2010 Google Inc.
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

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

/**
 * Zhuyin input method.
 */
public class ZhuyinIME extends AbstractIME {
	private HashMap<Integer, Integer> keyMapping;
	private SharedPreferences preferences;
	private boolean isAltUsed = false;
	private boolean isMS3 = false;

	@Override
	protected KeyboardSwitch createKeyboardSwitch(Context context) {
		return new KeyboardSwitch(context, R.xml.zhuyin);
	}

	@Override
	protected Editor createEditor() {
		return new ZhuyinEditor();
	}

	@Override
	protected WordDictionary createWordDictionary(Context context) {
		return new ZhuyinDictionary(context);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		keyMapping = new HashMap<Integer, Integer>();
		keyMapping.put(8, 0x3105);
		keyMapping.put(45, 0x3106);
		keyMapping.put(29, 0x3107);
		keyMapping.put(54, 0x3108);
		keyMapping.put(9, 0x3109);
		keyMapping.put(51, 0x310A);
		keyMapping.put(47, 0x310B);
		keyMapping.put(52, 0x310C);
		keyMapping.put(33, 0x310D);
		keyMapping.put(32, 0x310E);
		keyMapping.put(31, 0x310F);
		keyMapping.put(46, 0x3110);
		keyMapping.put(34, 0x3111);
		keyMapping.put(50, 0x3112);
		keyMapping.put(12, 0x3113);
		keyMapping.put(48, 0x3114);
		keyMapping.put(35, 0x3115);
		keyMapping.put(30, 0x3116);
		keyMapping.put(53, 0x3117);
		keyMapping.put(36, 0x3118);
		keyMapping.put(42, 0x3119);
		keyMapping.put(49, 0x3127);
		keyMapping.put(38, 0x3128);
		keyMapping.put(41, 0x3129);
		keyMapping.put(15, 0x311A);
		keyMapping.put(37, 0x311B);
		keyMapping.put(39, 0x311C);
		keyMapping.put(55, 0x311D);
		keyMapping.put(16, 0x311E);
		keyMapping.put(43, 0x311F);
		keyMapping.put(40, 0x3120);
		keyMapping.put(56, 0x3121);
		keyMapping.put(7, 0x3122);
		keyMapping.put(44, 0x3123);
		keyMapping.put(74, 0x3124);
		keyMapping.put(72, 0x3124); // MS1 fix: KEYCODE_RIGHT_BRACKET(?) as
									// KEYCODE_SEMICOLON(;)
		keyMapping.put(76, 0x3125);
		keyMapping.put(69, 0x3126);
		keyMapping.put(77, 0x3126); // MS1/2 fix: KEYCODE_AT(@) as
									// KEYCODE_MINUS(-)
		keyMapping.put(10, 0x2C7);
		keyMapping.put(11, 0x2CB);
		keyMapping.put(13, 0x2CA);
		keyMapping.put(14, 0x2D9);

		// Get the setting from SharedPreferences. See if this is Milestone 3
		preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		isMS3 = preferences.getBoolean(getString(R.string.prefs_ms3_key), false);
	}

	public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);
		showStatusIcon(keyboardSwitch.getLanguageIcon());
		isMS3 = preferences.getBoolean(getString(R.string.prefs_ms3_key), false);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		showStatusIcon(keyboardSwitch.getLanguageIcon());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Capture the hardware keyboard
		if (hasHardKeyboard) {
			// Check the status
			SoftKeyboard sKB = (SoftKeyboard) keyboardSwitch
					.getCurrentKeyboard();
			if (!checkHardKeyboardAvailable(sKB)) {
				return super.onKeyDown(keyCode, event);
			}

			// Shift + Space
			if (handleLanguageChange(keyCode, event)) {
				isAltUsed = false; // Clear Alt status
				return true;
			}

			// Handle HardKB event on Chinese mode only
			if (sKB.isChinese()) {
				// Milestone first row key
				// (If alt is pressed before, emulate 1 - 0 keys)
				if (isAltUsed || event.isAltPressed()) {
					boolean isTriggered = false;
					switch (keyCode) {
					case KeyEvent.KEYCODE_Q:
						keyCode = KeyEvent.KEYCODE_1;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_W:
						keyCode = KeyEvent.KEYCODE_2;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_E:
						keyCode = KeyEvent.KEYCODE_3;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_R:
						keyCode = KeyEvent.KEYCODE_4;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_T:
						keyCode = KeyEvent.KEYCODE_5;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_Y:
						keyCode = KeyEvent.KEYCODE_6;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_U:
						keyCode = KeyEvent.KEYCODE_7;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_I:
						keyCode = KeyEvent.KEYCODE_8;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_O:
						keyCode = KeyEvent.KEYCODE_9;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_P:
						if (isMS3) { // MS3 fix (Alt + P = ㄦ)
							keyCode = KeyEvent.KEYCODE_MINUS;
						} else {
							keyCode = KeyEvent.KEYCODE_0;
						}
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_V: // MS1/2 fix (Alt + V = - = ㄦ)
						keyCode = KeyEvent.KEYCODE_MINUS;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_COMMA: // MS2 fix (Alt + , = ; = ㄤ)
					case KeyEvent.KEYCODE_L: // MS3 fix (Alt + L = ㄤ)
						keyCode = KeyEvent.KEYCODE_SEMICOLON;
						isTriggered = true;
						break;
					case KeyEvent.KEYCODE_PERIOD: // MS3 fix (Alt + . = ㄥ)
						keyCode = KeyEvent.KEYCODE_SLASH;
						isTriggered = true;
						break;
					}
					if (isTriggered) {
						clearKeyboardMetaState();
						isAltUsed = false;
					} else {
						// Pressed Alt key only
						// Record if Alt key was pressed before
						isAltUsed = true;
						return true;
					}
				}

				// Simulate soft keyboard press
				if (keyMapping.containsKey(keyCode)) {
					onKey(keyMapping.get(keyCode), null);
					return true;
				}
				// Handle Delete
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					onKey(SoftKeyboard.KEYCODE_DELETE, null);
					return true;
				}
				// Handle Space
				if (keyCode == KeyEvent.KEYCODE_SPACE) {
					onKey(SoftKeyboard.KEYCODE_SPACE, null);
					return true;
				}
				// Handle Enter
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					onKey(SoftKeyboard.KEYCODE_ENTER, null);
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
