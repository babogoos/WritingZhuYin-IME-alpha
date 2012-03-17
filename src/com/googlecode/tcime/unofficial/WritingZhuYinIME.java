package com.googlecode.tcime.unofficial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class WritingZhuYinIME extends ZhuyinIME{

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
	}
	
	@Override public void onInitializeInterface() {
		 super.onInitializeInterface();
	}
	
	LinearLayout layout;
	my.app.zinnia.InputView draw;
	my.app.zinnia.CandidateCharacter character;
	LinearLayout candidate;
	EditText edit;
	Button commit;
	Button clear;
	@Override
	public View onCreateInputView(){
		
		super.onCreateInputView();
		
		layout = (LinearLayout)getLayoutInflater().inflate(R.layout.invisible,null);
		
		character =(my.app.zinnia.CandidateCharacter)getLayoutInflater().inflate(R.layout.character, null);
		character.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,50));	
		layout.addView(character);
		
		candidate = (LinearLayout)getLayoutInflater().inflate(R.layout.candidate,null);
		candidate.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,50));	
		edit=(EditText)candidate.getChildAt(1);
		
		commit=(Button)candidate.getChildAt(2);
		
		commit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				InputConnection ic = getCurrentInputConnection();
				ic.commitText(edit.getText().toString(), 1);
				edit.setText("");
			}});
		
		clear=(Button)candidate.getChildAt(3);
		
		clear.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				edit.setText("");
				character.clear();
				draw.clear();
			}});
		
		layout.addView(candidate);
		character.setEditText(edit);
		
		draw =(my.app.zinnia.InputView)getLayoutInflater().inflate(R.layout.input, null);
		draw.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,200));
		layout.addView(draw);	
		draw.setResultView(character);
		
		return layout;
	}
	
	
}
