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
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

/**
 * Zhuyin input method.
 */
public class ZhuyinIME extends AbstractIME {
	private HashMap<Integer, Integer> keyMapping;
	private boolean isAltUsed = false;

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
		keyMapping.put(8,  0x3105);//ㄅ
		keyMapping.put(45, 0x3106);
		keyMapping.put(29, 0x3107);
		keyMapping.put(54, 0x3108);
		keyMapping.put(9,  0x3109);
		keyMapping.put(51, 0x310A);
		keyMapping.put(47, 0x310B);
		keyMapping.put(52, 0x310C);
		keyMapping.put(33, 0x310D);
		keyMapping.put(32, 0x310E);
		keyMapping.put(31, 0x310F);//ㄏ
		keyMapping.put(46, 0x3110);
		keyMapping.put(34, 0x3111);
		keyMapping.put(50, 0x3112);
		keyMapping.put(12, 0x3113);
		keyMapping.put(48, 0x3114);
		keyMapping.put(35, 0x3115);
		keyMapping.put(30, 0x3116);
		keyMapping.put(53, 0x3117);
		keyMapping.put(36, 0x3118);
		keyMapping.put(42, 0x3119);//ㄙ
		keyMapping.put(49, 0x3127);//一
		keyMapping.put(38, 0x3128);//ㄨ
		keyMapping.put(41, 0x3129);//ㄩ
		keyMapping.put(15, 0x311A);
		keyMapping.put(37, 0x311B);
		keyMapping.put(39, 0x311C);
		keyMapping.put(55, 0x311D);
		keyMapping.put(16, 0x311E);
		keyMapping.put(43, 0x311F);
		keyMapping.put(40, 0x3120);
		keyMapping.put(56, 0x3121);
		keyMapping.put(7,  0x3122);
		keyMapping.put(44, 0x3123);
		keyMapping.put(74, 0x3124);
		keyMapping.put(72, 0x3124); // MS1 fix: KEYCODE_RIGHT_BRACKET(?) as KEYCODE_SEMICOLON(;)
		keyMapping.put(76, 0x3125); //ㄥ
		keyMapping.put(69, 0x3126); //ㄦ
		keyMapping.put(77, 0x3126); // MS1/2 fix: KEYCODE_AT(@) as KEYCODE_MINUS(-)
		keyMapping.put(10, 0x2C7); //三聲ˇ
		keyMapping.put(11, 0x2CB); //四聲ˋ
		keyMapping.put(13, 0x2CA); //二聲ˊ
		keyMapping.put(14, 0x2D9); //輕聲˙
	}
	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting){
		super.onStartInput(attribute, restarting);
		showStatusIcon(keyboardSwitch.getLanguageIcon());
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
			SoftKeyboard sKB = (SoftKeyboard) keyboardSwitch.getCurrentKeyboard();
			if(!checkHardKeyboardAvailable(sKB)){
				return super.onKeyDown(keyCode, event);
			}
			
			// Shift + Space
			if(handleShiftSpacekey(keyCode, event)){
				isAltUsed = false; // Clear Alt status
				return true;
			}
			
			// Handle HardKB event on Chinese mode only
			if (sKB.isChinese()) {
				// Milestone first row key (If alt is pressed before, emulate 1 - 0 keys)
				if(isAltUsed || event.isAltPressed()){
					boolean isTriggered = false;
					switch(keyCode){
						case KeyEvent.KEYCODE_Q: //35
							keyCode = KeyEvent.KEYCODE_1; //8
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_W: //36
							keyCode = KeyEvent.KEYCODE_2; //9
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_E: //37
							keyCode = KeyEvent.KEYCODE_3; //10
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_R: //38
							keyCode = KeyEvent.KEYCODE_4; //11
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_T: //39
							keyCode = KeyEvent.KEYCODE_5; //12
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_Y: //40
							keyCode = KeyEvent.KEYCODE_6; //13
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_U: //41
							keyCode = KeyEvent.KEYCODE_7; //14
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_I: //42
							keyCode = KeyEvent.KEYCODE_8; //15
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_O: //43
							keyCode = KeyEvent.KEYCODE_9; //16
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_P: //44
							keyCode = KeyEvent.KEYCODE_0; //17
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_V: // MS1/2 fix (Alt + V = -)
							keyCode = KeyEvent.KEYCODE_MINUS;
							isTriggered = true;
							break;
						case KeyEvent.KEYCODE_COMMA: // MS2 fix (Alt + , = ;)
							keyCode = KeyEvent.KEYCODE_SEMICOLON;
							isTriggered = true;
							break;
					}
					if(isTriggered){
						clearKeyboardMetaState();
						isAltUsed = false;
					}else{
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
				if(keyCode == KeyEvent.KEYCODE_DEL){
					onKey(SoftKeyboard.KEYCODE_DELETE, null);
					return true;
				}
				// Handle Space
				if(keyCode == KeyEvent.KEYCODE_SPACE){
					onKey(SoftKeyboard.KEYCODE_SPACE, null);
					return true;
				}
				// Handle Enter
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					onKey(SoftKeyboard.KEYCODE_ENTER, null);
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
