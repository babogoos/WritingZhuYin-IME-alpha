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
import android.inputmethodservice.Keyboard;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

/**
 * Cangjie input method.
 */
public class CangjieIME extends AbstractIME {
  private HashMap<Integer, Integer> keyMapping;
  private CangjieEditor cangjieEditor;
  private CangjieDictionary cangjieDictionary;

  @Override
  protected KeyboardSwitch createKeyboardSwitch(Context context) {
    return new KeyboardSwitch(context, R.xml.cangjie);
  }

  @Override
  protected Editor createEditor() {
    cangjieEditor =  new CangjieEditor();
    return cangjieEditor;
  }

  @Override
  protected WordDictionary createWordDictionary(Context context) {
    cangjieDictionary = new CangjieDictionary(context);
    return cangjieDictionary;
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    keyMapping = new HashMap<Integer, Integer>();
    keyMapping.put(KeyEvent.KEYCODE_Q, 25163);
    keyMapping.put(KeyEvent.KEYCODE_W, 30000);
    keyMapping.put(KeyEvent.KEYCODE_E, 27700);
    keyMapping.put(KeyEvent.KEYCODE_R, 21475);
    keyMapping.put(KeyEvent.KEYCODE_T, 24319);
    keyMapping.put(KeyEvent.KEYCODE_Y, 21340);
    keyMapping.put(KeyEvent.KEYCODE_U, 23665);
    keyMapping.put(KeyEvent.KEYCODE_I, 25096);
    keyMapping.put(KeyEvent.KEYCODE_O, 20154);
    keyMapping.put(KeyEvent.KEYCODE_P, 24515);
    
    keyMapping.put(KeyEvent.KEYCODE_A, 26085);
    keyMapping.put(KeyEvent.KEYCODE_S, 23608);
    keyMapping.put(KeyEvent.KEYCODE_D, 26408);
    keyMapping.put(KeyEvent.KEYCODE_F, 28779);
    keyMapping.put(KeyEvent.KEYCODE_G, 22303);
    keyMapping.put(KeyEvent.KEYCODE_H, 31481);
    keyMapping.put(KeyEvent.KEYCODE_J, 21313);
    keyMapping.put(KeyEvent.KEYCODE_K, 22823);
    keyMapping.put(KeyEvent.KEYCODE_L, 20013);
    
    //keyMapping.put(KeyEvent.KEYCODE_Z, 0);
    keyMapping.put(KeyEvent.KEYCODE_X, 38627);
    keyMapping.put(KeyEvent.KEYCODE_C, 37329);
    keyMapping.put(KeyEvent.KEYCODE_V, 22899);
    keyMapping.put(KeyEvent.KEYCODE_B, 26376);
    keyMapping.put(KeyEvent.KEYCODE_N, 24339);
    keyMapping.put(KeyEvent.KEYCODE_M, 19968);
  }
  
  @Override
  public void onStartInput(EditorInfo attribute, boolean restarting){
    super.onStartInput(attribute, restarting);
    int ico = keyboardSwitch.getLanguageIcon();
    if(ico == R.drawable.ime_ch && cangjieEditor != null && cangjieEditor.simplified){
    	ico = R.drawable.ime_chsp;
    }
    showStatusIcon(ico);
  }
	
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    int ico = keyboardSwitch.getLanguageIcon();
    if(ico == R.drawable.ime_ch && cangjieEditor != null && cangjieEditor.simplified){
    	ico = R.drawable.ime_chsp;
    }
    showStatusIcon(ico);
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
		if(handleLanguageChange(keyCode, event)){
			// Determine if it is simplified cangjie
			// Because the sKB we got is old. The Cangjie keyboard should be English after handleShiftSpacekey().
			boolean isCangjie = !sKB.isCangjie();
			if(isCangjie && cangjieEditor.simplified) showStatusIcon(R.drawable.ime_chsp);
			return true;
		}
		
		// Handle HardKB event on Chinese mode only
		if (sKB.isChinese()) {
			// Simulate soft keyboard press
			if (keyMapping.containsKey(keyCode)) {
				onKey(keyMapping.get(keyCode), null);
				return true;
			}
			// Handle Alt (As Cangjie simplified switch)
			if(event.isAltPressed()){
				clearKeyboardMetaState();
				onKey(Keyboard.KEYCODE_SHIFT, null);
				showStatusIcon(cangjieEditor.simplified ? R.drawable.ime_chsp : R.drawable.ime_ch);
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

  @Override
  public void onKey(int primaryCode, int[] keyCodes) {
    if (handleCangjieSimplified(primaryCode)) {
      return;
    }
    super.onKey(primaryCode, keyCodes);
  }

  private boolean handleCangjieSimplified(int keyCode) {
    if (keyCode == Keyboard.KEYCODE_SHIFT) {
      if ((inputView != null) && inputView.toggleCangjieSimplified()) {
        boolean simplified = inputView.isCangjieSimplified();
        cangjieEditor.setSimplified(simplified);
        cangjieDictionary.setSimplified(simplified);
        escape();
        return true;
      }
    }
    return false;
  }
}
