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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.googlecode.tcime.unofficial.R;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;
/**
 * Abstract class extended by ZhuyinIME and CangjieIME.
 */
public abstract class AbstractIME extends InputMethodService implements 
    KeyboardView.OnKeyboardActionListener, CandidateView.CandidateViewListener {

  public static final String TEXT_GOT = "com.googlecode.tcime.unofficial.TEXT_GOT";
  private String textGot = "";
  private final int UPDATE_UI = 1; 
  public static String chosenzhuyin = "11";
  protected SoftKeyboardView inputView;
  protected CandidatesContainer candidatesContainer;
  protected KeyboardSwitch keyboardSwitch;
  private Editor editor;
  private WordDictionary wordDictionary;
  private PhraseDictionary phraseDictionary;
  private SoundMotionEffect effect;
  private int orientation;
  protected boolean hasHardKeyboard;
  protected boolean isHardKeyboardShow;
  private int toastShowedCount = 0;
  private BroadcastReceiver txtReceiver;
  private AlertDialog mOptionsDialog;
  //private static final int MENU_BARCODESCAN = 2; //0
  //private static final int MENU_VOICEINPUT = 3; //1
  private static final int MENU_SETTINGS = 0; //2
  private static final int MENU_SWITCHIME = 1; //3
  
  HandlerThread zhuthread;
  Handler zhuThreadHandler;
  Message msg;
  
  protected abstract KeyboardSwitch createKeyboardSwitch(Context context);
  protected abstract Editor createEditor();
  protected abstract WordDictionary createWordDictionary(Context context);

  @Override
  public void onCreate() {
    super.onCreate();
    keyboardSwitch = createKeyboardSwitch(this);
    editor = createEditor();
    wordDictionary = createWordDictionary(this);
    phraseDictionary = new PhraseDictionary(this);
    effect = new SoundMotionEffect(this);

    Configuration conf = getResources().getConfiguration();
    orientation = conf.orientation;
    hasHardKeyboard = (conf.keyboard != Configuration.KEYBOARD_NOKEYS);
    isHardKeyboardShow = (conf.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO);

    // Create a BroadcastReceiver to catch the TEXT_GOT result
    txtReceiver = new BroadcastReceiver(){   	
    	@Override
    	public void onReceive(Context arg0, Intent arg1) {
    		textGot = arg1.getStringExtra("TEXT_RESULT");
    		Log.d("TCIME", "Broadcast got = " + textGot);
    		// We can't commitText() on this point. Maybe because the InputConnection is invalid now.
    		// Instead, we store it temporarily. Later on onBindInput() we can commit it.
    	}
    };
    IntentFilter iFilter = new IntentFilter();
    iFilter.addAction(TEXT_GOT);
    registerReceiver(txtReceiver, iFilter);
    // Use the following line to debug IME service.
    //android.os.Debug.waitForDebugger();
    
    zhuthread= new HandlerThread("zhu");
    zhuthread.start();
    zhuThreadHandler = new Handler(){
    	
  	  public synchronized void handleMessage(Message msg) {
  		switch (msg.what){  
        case UPDATE_UI:{
        	onHandWritingKey((Integer)msg.obj);
        break;  
        }  
         default:  
         break;  
  		}  
  		 super.handleMessage(msg);
  	  }
  	  };
    
    zhuThreadHandler.post(R1);
    
		AssetManager assetManager = getAssets();
	    InputStream inputStream = null;
		
	    try {
	        // Clone the modle to /assets/handwriting-ja.model
	   inputStream = assetManager.open("handwriting-zy.model");
	   
	       byte[] bytes = new byte[4096];
	   
	   int len = -1;
	   		
	   File file = new File(this.getFilesDir(),"handwriting-zy.model");
	   //Log.i("FilesDir",this.getFilesDir().toString());
	   FileOutputStream outputStream = new FileOutputStream(file);
	   
	   while ((len = inputStream.read(bytes)) != -1){
	   	outputStream.write(bytes, 0, len);
	   }
	   
	   inputStream.close();
	   outputStream.close();
	   
	  } catch (IOException e) {
	   e.printStackTrace();
	  }
    
  }
  
  @Override
  public void onDestroy(){
	phraseDictionary.close();
	unregisterReceiver(txtReceiver);
	super.onDestroy();
	//zhuThreadHandler.getLooper().quit();
    if ( zhuThreadHandler != null) {

    	zhuThreadHandler.removeCallbacks(R1);

    }
    if (zhuthread != null) {
    	zhuthread.quit();
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    if (orientation != newConfig.orientation) {
      // Clear composing text and candidates for orientation change.
      escape();
      orientation = newConfig.orientation;
    }
    isHardKeyboardShow = (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO);
    super.onConfigurationChanged(newConfig);
  }

  @Override
  public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart,
      int newSelEnd, int candidatesStart, int candidatesEnd) {
    super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, 
        candidatesStart, candidatesEnd);
    if ((candidatesEnd != -1) &&
        ((newSelStart != candidatesEnd) || (newSelEnd != candidatesEnd))) {
      // Clear composing text and its candidates for cursor movement.
      escape();
    }
    // Update the caps-lock status for the current cursor position.
    updateCursorCapsToInputView();
  }

  @Override
  public void onComputeInsets(InputMethodService.Insets outInsets) {
    super.onComputeInsets(outInsets);
    outInsets.contentTopInsets = outInsets.visibleTopInsets;
  }
  
  
  	LinearLayout layout;
  	LinearLayout Test;
  	public static my.app.zinnia.InputView draw;
  	public static my.app.zinnia.CandidateCharacter character;


  @Override
  public View onCreateInputView() {
	Test = (LinearLayout)getLayoutInflater().inflate(R.layout.invisible,null);
	inputView = (SoftKeyboardView)Test.getChildAt(2);
	inputView.setOnKeyboardActionListener(this);
    character =(my.app.zinnia.CandidateCharacter)Test.getChildAt(0);
    draw =(my.app.zinnia.InputView)Test.getChildAt(1);
	draw.setResultView(character);
    //visibility
    character.setVisibility(View.GONE);
    draw.setVisibility(View.GONE);
    return Test;
  }

  @Override
  public View onCreateCandidatesView() {
    candidatesContainer = (CandidatesContainer) getLayoutInflater().inflate(
        R.layout.candidates, null);
    candidatesContainer.setCandidateViewListener(this);
    return candidatesContainer;
  }

  @Override
  public void onStartInputView(EditorInfo attribute, boolean restarting) {
    super.onStartInputView(attribute, restarting);

    // Reset editor and candidates when the input-view is just being started.
    editor.start(attribute.inputType);
    clearCandidates();
    effect.reset();

    keyboardSwitch.initializeKeyboard(getMaxWidth());
    // Select a keyboard based on the input type of the editing field.
    keyboardSwitch.onStartInput(attribute.inputType);
    bindKeyboardToInputView();
  }

  @Override
  public void onFinishInput() {
    // Clear composing as any active composing text will be finished, same as in
    // onFinishInputView, onFinishCandidatesView, and onUnbindInput.
    editor.clearComposingText(getCurrentInputConnection());
    super.onFinishInput();
  }

  @Override
  public void onFinishInputView(boolean finishingInput) {
    editor.clearComposingText(getCurrentInputConnection());
    super.onFinishInputView(finishingInput);
    // Dismiss any pop-ups when the input-view is being finished and hidden.
    inputView.closing();
  }

  @Override
  public void onFinishCandidatesView(boolean finishingInput) {
    editor.clearComposingText(getCurrentInputConnection());
    super.onFinishCandidatesView(finishingInput);
  }

  @Override
  public void onBindInput(){
	  // If we have textGot, commit it now.
	  // This is the workaround solution to call commitText() successfully.
	  super.onBindInput();
	  if(!textGot.equals("")){
		  Log.d("TCIME", "onBindInput commit textGot = " + textGot);
		  commitText(textGot);
		  textGot = "";
	  }
  }

  @Override
  public void onUnbindInput() {
    editor.clearComposingText(getCurrentInputConnection());
    super.onUnbindInput();
  }

  private void bindKeyboardToInputView() {
    if (inputView != null) {
      // Bind the selected keyboard to the input view.
      inputView.setKeyboard(keyboardSwitch.getCurrentKeyboard());
      updateCursorCapsToInputView();
    }
  }

  private void updateCursorCapsToInputView() {
    InputConnection ic = getCurrentInputConnection();
    if ((ic != null) && (inputView != null)) {
      int caps = 0;
      EditorInfo ei = getCurrentInputEditorInfo();
      if ((ei != null) && (ei.inputType != EditorInfo.TYPE_NULL)) {
        caps = ic.getCursorCapsMode(ei.inputType);
      }
      inputView.updateCursorCaps(caps);
    }
  }

  private void commitText(CharSequence text) {
    if (editor.commitText(getCurrentInputConnection(), text)) {
      // Clear candidates after committing any text.
      clearCandidates();
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
      // Handle the back-key to close the pop-up keyboards.
      if ((inputView != null) && inputView.handleBack()) {
        return true;
      }
    }
    // Handle DPAD
	if ((candidatesContainer != null) && candidatesContainer.isShown()) {
		if (keyCode >= KeyEvent.KEYCODE_DPAD_UP && keyCode <= KeyEvent.KEYCODE_DPAD_CENTER){
			onKey(keyCode, null);
			return true;
		}
	}
    return super.onKeyDown(keyCode, event);
  }

  public synchronized void onHandWritingKey(int i){
	  if(handleComposing(i))
		chosenzhuyin = "11";
	  	character.clear();
	  	draw.clear();
	  return;
  }
  
  
  public void onKey(int primaryCode, int[] keyCodes) {
    if (keyboardSwitch.onKey(primaryCode)) {
      escape();
      bindKeyboardToInputView();
      return;
    }
    if (primaryCode == -80){
    	chosenzhuyin = "11";
  	  	character.clear();
  	  	draw.clear();
    	return;
    }
    if (handleOption(primaryCode) || handleCapsLock(primaryCode)
       || handleEnter(primaryCode) || handleSpace(primaryCode) || handleDelete(primaryCode)
       || handleDPAD(primaryCode) || handleComposing(primaryCode)) {
    	if(handleDelete(primaryCode)){
    		
    	}
      return;
    }
    handleKey(primaryCode);
  }  
   public void onText(CharSequence text) {
    commitText(text);
  }

  public void onPress(int primaryCode) {
    effect.vibrate();
    effect.playSound();
  }

  public void onRelease(int primaryCode) {
    // no-op
  }

  public void swipeLeft() {
	sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
  }

  public void swipeRight() {
	sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT);
  }

  public void swipeUp() {
    // no-op
  }

  public void swipeDown() {
    requestHideSelf(0);
  }

  public void onPickCandidate(String candidate) {
    // Commit the picked candidate and suggest its following words.
    commitText(candidate);
    setCandidates(
        phraseDictionary.getFollowingWords(candidate.charAt(0)), false);
  }

  private void clearCandidates() {
    setCandidates("", false);
  }

  private void setCandidates(String words, boolean highlightDefault) {
    if (candidatesContainer != null) {
      setCandidatesViewShown((words.length() > 0) || editor.hasComposingText());
      candidatesContainer.setCandidates(words, highlightDefault);
      if (inputView != null) {
        inputView.setEscape(candidatesContainer.isShown());
      }
    }
  }

  private boolean handleOption(int keyCode) {
	    if (keyCode == SoftKeyboard.KEYCODE_OPTIONS) {
	    	// Create a Dialog menu
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
	    		.setTitle(R.string.ime_name)		
	    		.setIcon(android.R.drawable.ic_menu_preferences)
	        	.setCancelable(true)
	        	.setNegativeButton(android.R.string.cancel, null)
	        	.setItems(new CharSequence[] {
	        		getString(R.string.menu_settings),
	        		getString(R.string.menu_switchIME),
	        	},
	        	new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface di, int position) {
	        				di.dismiss();
	        				switch (position) {
	        					case MENU_SETTINGS: // Settings
	        						Intent iSetting = new Intent();
	        						iSetting.setClass(AbstractIME.this, ImePreferenceActivity.class);
	        						iSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
	        				        startActivity(iSetting);
	        						break;
	        					case MENU_SWITCHIME: // Switch IME
	        						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
	        							.showInputMethodPicker();
	        						break;
	        				}
	        		}
	        });
	        mOptionsDialog = builder.create();
	        Window window = mOptionsDialog.getWindow();
	        WindowManager.LayoutParams lp = window.getAttributes();
	        lp.token = inputView.getWindowToken();
	        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
	        window.setAttributes(lp);
	        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	        mOptionsDialog.show();
	    	return true;
	    }
	    return false;
	  }

  private boolean handleCapsLock(int keyCode) {
    return (keyCode == Keyboard.KEYCODE_SHIFT) && inputView.toggleCapsLock();
  }

  private boolean handleEnter(int keyCode) {
    if (keyCode == '\n') {
      if (inputView.hasEscape()) {
        escape();
      } else if (editor.treatEnterAsLinkBreak()) {
        commitText("\n");
      } else {
        sendKeyChar('\n');
      }
      return true;
    }
    return false;
  }

  private boolean handleSpace(int keyCode) {
    if (keyCode == ' ') {
      if ((candidatesContainer != null) && candidatesContainer.isShown()) {
        // The space key could either pick the highlighted candidate or escape
        // if there's no highlighted candidate and no composing-text.
        if (!candidatesContainer.pickHighlighted()
            && !editor.hasComposingText()) {
          escape();
        }
      } else {
        commitText(" ");
      }
      return true;
    }
    return false;
  }

  private boolean handleDelete(int keyCode) {
    // Handle delete-key only when no composing text. 
    if ((keyCode == Keyboard.KEYCODE_DELETE) && !editor.hasComposingText()) {
      if (inputView.hasEscape()) {
        escape();
      } else {
        sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
      }
      return true;
    }
    return false;
  }

  public boolean handleComposing(int keyCode) {
    if (editor.compose(getCurrentInputConnection(), keyCode)) {
      // Set the candidates for the updated composing-text and provide default
      // highlight for the word candidates.
      setCandidates(wordDictionary.getWords(editor.composingText()), true);
      return true;
    }
    return false;
  }

  private boolean handleDPAD(int keyCode){
	// Handle DPAD keys only
	if (keyCode < KeyEvent.KEYCODE_DPAD_UP || keyCode > KeyEvent.KEYCODE_DPAD_CENTER){
		return false;
	}
	if ((candidatesContainer != null) && candidatesContainer.isShown()) {
		switch(keyCode){
		// Center: Choose highlighted candidate word
		case KeyEvent.KEYCODE_DPAD_CENTER:
			candidatesContainer.pickHighlighted();
			break;
		// Left: Move the position to left
		case KeyEvent.KEYCODE_DPAD_LEFT:
			candidatesContainer.highlightLeft();
			break;
		// Right: Move the position to right
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			candidatesContainer.highlightRight();
			break;
		// Up: Previous candidate page
		case KeyEvent.KEYCODE_DPAD_UP:
			candidatesContainer.pagePrev();
			break;
		// Down: Next candidate page
		case KeyEvent.KEYCODE_DPAD_DOWN:
			candidatesContainer.pageNext();
			break;
		}
		return true;
	}
	return false;
  }
  
  /**
   * Handles input of SoftKeyboard key code that has not been consumed by
   * other handling-methods.
   */
  private void handleKey(int keyCode) {
    if (isInputViewShown() && inputView.isShifted()) {
      keyCode = Character.toUpperCase(keyCode);
    }
    commitText(String.valueOf((char) keyCode));
  }

  /**
   * Simulates PC Esc-key function by clearing all composing-text or candidates.
   */
  protected void escape() {
    editor.clearComposingText(getCurrentInputConnection());
    clearCandidates();
  }
  
  // Hardware Keyboard related methods
  /**
   * Clear the keyboard meta status
   */
  public void clearKeyboardMetaState(){
	int allMetaState = KeyEvent.META_ALT_ON | KeyEvent.META_ALT_LEFT_ON
		| KeyEvent.META_ALT_RIGHT_ON | KeyEvent.META_SHIFT_ON
		| KeyEvent.META_SHIFT_LEFT_ON
		| KeyEvent.META_SHIFT_RIGHT_ON | KeyEvent.META_SYM_ON;
	getCurrentInputConnection().clearMetaKeyStates(allMetaState);
  }

  /**
   * Set the status bar icon
   */
  @Override
  public void showStatusIcon(int iconResId) {
	if (hasHardKeyboard && isHardKeyboardShow) {
		super.showStatusIcon(iconResId);
	} else {
		hideStatusIcon();
	}
  }
  
  /**
   * Check if the hard keyboard can be used. To avoid force crash.
   * 
   * @param sKB The soft keyboard object. To check if it is ready.
   * @return true if hard keyboard can be used
   */
  public boolean checkHardKeyboardAvailable(SoftKeyboard sKB){
	// Hard keyboard is not showed
	if(!isHardKeyboardShow) return false;
	if(sKB == null || inputView == null){
		// Prompt user to close the keyboard and reopen it to initialize
		if(toastShowedCount < 3){
			Toast.makeText(this, R.string.str_needsreopen, Toast.LENGTH_SHORT)
				.show();
			++toastShowedCount;
		}
		return false;
	}
	return true;
  }
  
  /**
   * Handles the Shift + Space key to change the input mode.
   * 
   * @param keyCode
   * @param event
   * @return true if handled
   */
  public boolean handleShiftSpacekey(int keyCode, KeyEvent event){
	if (event.isShiftPressed() && keyCode == KeyEvent.KEYCODE_SPACE) {
		// Clear all meta state
		clearKeyboardMetaState();

		onKey(SoftKeyboard.KEYCODE_MODE_CHANGE_LETTER, null);
		showStatusIcon(keyboardSwitch.getLanguageIcon());
		return true;
	}
	return false;
  }
  
  public Runnable R1 = new Runnable()  
  {  
  	public synchronized void run()  
      {
  		if(!chosenzhuyin.equals("11")){
  			msg = new Message();
  			msg.obj = chosenzhuyin.hashCode();
  			msg.what = UPDATE_UI;
  			zhuThreadHandler.sendMessage(msg);
  		}
  		zhuThreadHandler.postDelayed(R1, 200);
      }
  };
  
}