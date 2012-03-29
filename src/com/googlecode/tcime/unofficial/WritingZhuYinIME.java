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
	public void setChosenZhuYin(String str) {
		this.chosenzhuyin = str;
	}
}
