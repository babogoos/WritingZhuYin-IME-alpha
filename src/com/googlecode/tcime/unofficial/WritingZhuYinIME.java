package com.googlecode.tcime.unofficial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class WritingZhuYinIME extends AbstractIME{
	private boolean isAltUsed = false;
	private String chosenzhuyin = "";
	private HashMap<String,Integer > ZhuYinMapping;
	
	public void setChosenZhuYin(String str) {
		this.chosenzhuyin = str;
	}
	@Override
	protected KeyboardSwitch createKeyboardSwitch(Context context) {
	 return new KeyboardSwitch(context, R.xml.writingzhuyin);
	}

	@Override
	protected Editor createEditor() {
	  return new ZhuyinEditor();
	}

	@Override
	protected WordDictionary createWordDictionary(Context context) {
	  return new ZhuyinDictionary(context);
	}

	public void onCreate() {
		super.onCreate();
		
		AssetManager assetManager = getAssets();
        InputStream inputStream = null;
		
        try {
            // 指定/assets/handwriting-ja.model
       inputStream = assetManager.open("handwriting-ja.model");
       
           byte[] bytes = new byte[4096];
       
       int len = -1;
       		//開新檔案在應用程式資料夾
       File file = new File(this.getFilesDir(),"handwriting-ja.model");
       FileOutputStream outputStream = new FileOutputStream(file);
       
       while ((len = inputStream.read(bytes)) != -1){
       	outputStream.write(bytes, 0, len);
       }
       
       inputStream.close();
       outputStream.close();
       
      } catch (IOException e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
      }

		ZhuYinMapping = new HashMap<String,Integer>();
				
		ZhuYinMapping.put("ㄅ", 0x3105);//ㄅ
		ZhuYinMapping.put("ㄆ", 0x3106);
		ZhuYinMapping.put("ㄇ", 0x3107);
		ZhuYinMapping.put("ㄈ", 0x3108);
		ZhuYinMapping.put("ㄉ", 0x3109);
		ZhuYinMapping.put("ㄊ", 0x310A);
		ZhuYinMapping.put("ㄋ", 0x310B);
		ZhuYinMapping.put("ㄌ", 0x310C);
		ZhuYinMapping.put("ㄍ", 0x310D);
		ZhuYinMapping.put("ㄎ", 0x310E);
		ZhuYinMapping.put("ㄏ", 0x310F);//ㄏ
		ZhuYinMapping.put("ㄐ", 0x3110);
		ZhuYinMapping.put("ㄑ", 0x3111);
		ZhuYinMapping.put("ㄒ", 0x3112);
		ZhuYinMapping.put("ㄓ", 0x3113);
		ZhuYinMapping.put("ㄔ", 0x3114);
		ZhuYinMapping.put("ㄕ", 0x3115);
		ZhuYinMapping.put("ㄖ", 0x3116);
		ZhuYinMapping.put("ㄗ", 0x3117);
		ZhuYinMapping.put("ㄘ", 0x3118);
		ZhuYinMapping.put("ㄙ", 0x3119);//ㄙ
		ZhuYinMapping.put("一", 0x3127);//一
		ZhuYinMapping.put("ㄨ", 0x3128);//ㄨ
		ZhuYinMapping.put("ㄩ", 0x3129);//ㄩ
		ZhuYinMapping.put("ㄚ", 0x311A);
		ZhuYinMapping.put("ㄛ", 0x311B);
		ZhuYinMapping.put("ㄜ", 0x311C);
		ZhuYinMapping.put("ㄝ", 0x311D);
		ZhuYinMapping.put("ㄞ", 0x311E);
		ZhuYinMapping.put("ㄟ", 0x311F);
		ZhuYinMapping.put("ㄠ", 0x3120);
		ZhuYinMapping.put("ㄡ", 0x3121);
		ZhuYinMapping.put("ㄢ", 0x3122);
		ZhuYinMapping.put("ㄣ", 0x3123);
		ZhuYinMapping.put("ㄤ", 0x3124);
	  //ZhuYinMapping.put("", 0x3124); // MS1 fix: KEYCODE_RIGHT_BRACKET(?) as KEYCODE_SEMICOLON(;)
		ZhuYinMapping.put("ㄥ", 0x3125); //ㄥ
		ZhuYinMapping.put("ㄦ", 0x3126); //ㄦ
	  //ZhuYinMapping.put("", 0x3126); // MS1/2 fix: KEYCODE_AT(@) as KEYCODE_MINUS(-)
		ZhuYinMapping.put("", 0x2C7); //三聲ˇ
		ZhuYinMapping.put("", 0x2CB); //四聲ˋ
		ZhuYinMapping.put("", 0x2CA); //二聲ˊ
		ZhuYinMapping.put("", 0x2D9); //輕聲˙
	 
	}
	
	@Override 
	public void onInitializeInterface() {
		 super.onInitializeInterface();
	}
	public void onStartInput(EditorInfo attribute, boolean restarting){
		super.onStartInput(attribute, restarting);
		showStatusIcon(keyboardSwitch.getLanguageIcon());
	}
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		showStatusIcon(keyboardSwitch.getLanguageIcon());
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
				// Shift + Space
				if(handleShiftSpacekey(keyCode, event)){
					isAltUsed = false; // Clear Alt status
					return true;
				}
				// Simulate soft keyboard press
				if (ZhuYinMapping.containsKey(chosenzhuyin)) {
					onKey(ZhuYinMapping.get(chosenzhuyin), null);
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
		return super.onKeyDown(keyCode, event);
	}
}

