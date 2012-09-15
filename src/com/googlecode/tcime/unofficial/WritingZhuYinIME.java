package com.googlecode.tcime.unofficial;


import com.googlecode.tcime.unofficial.R;

import android.content.Context;

public class WritingZhuYinIME extends AbstractIME{
	
	
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
	
}
