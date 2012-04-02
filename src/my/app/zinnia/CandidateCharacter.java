package my.app.zinnia;

import java.util.ArrayList;

import com.googlecode.tcime.unofficial.AbstractIME;


import my.app.delegate.Clearable;
import my.app.delegate.Showable;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CandidateCharacter extends LinearLayout implements Showable{
	private Handler handler = new Handler();
	private org.zinnia.Recognizer recognizer = new org.zinnia.Recognizer();
	private org.zinnia.Character character = new org.zinnia.Character();
	private ShowDelegate show = new ShowDelegate();
	private ClearDelegate clear = new ClearDelegate();
	public String chosenZhuYin;
	
	
	
	public CandidateCharacter(Context context, AttributeSet attrs) {
		super(context, attrs);
		recognizer.open("data/data/com.googlecode.tcime.unofficial/files/handwriting-ja.model");
	}
	private class SearchResultDelegate implements Runnable{
		Draw draw;  
		ArrayList<String> resultList;
		
		private SearchResultDelegate(Draw draw){
			this.draw = draw;
		}
		@Override
		public void run() {
			//Initialize
			resultList = null;
			CandidateCharacter.this.character.clear();
			if(this.draw != null){
				//for each line
				for(int i = 0;i < draw.size();i++) {  
					 Line line = draw.get(i);
					 //for each point
					 for(int j = 0;j < line.size();j++){
						 CandidateCharacter.this.character.add(i, line.get(j).x, line.get(j).y); 
					 }
				}
				org.zinnia.Result result = CandidateCharacter.this.recognizer.classify(CandidateCharacter.this.character, 10);		
				resultList = new ArrayList<String>();
				for(int k = 0; k < result.size(); k++){
					resultList.add(result.value(k));
				}
				result.dispose();
			}
			handler.post(new Runnable(){
				@Override
				public void run() {
					CandidateCharacter.this.removeAllViewsInLayout();
					if(resultList != null){
						for( final String str : resultList ){
							Button button = new Button(CandidateCharacter.this.getContext());
							button.setText((CharSequence)str);
							button.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View v) {
									//chosenZhuYin = str;
									AbstractIME.text.setText(str);
									character.clear();
									draw.clear();
									
								}});
							CandidateCharacter.this.addView(button);
							
						}
					}
					invalidate();
				}});
		}
		
	 } 
	private class ShowDelegate implements Showable{
		Object atomicTask = new Object();
		Runnable nextJob = null;
		TaskManager taskManager;
		
		class TaskManager extends Thread{
			boolean breakFlag = false;
			public boolean wasBreak(){
				return this.breakFlag;
			}
			@Override
			public void run() {
				while(true){
					Runnable currentJob;
					synchronized(atomicTask){
						if(nextJob == null){
							this.breakFlag =true;
							break;
						}else{
							currentJob=nextJob;
							nextJob = null;
						}
					}
					//Delegate run method of latest queueing job.
					currentJob.run();
				}
			}
			
		}
		
		@Override
		public void show(Draw draw){
			synchronized(atomicTask){
				nextJob = new SearchResultDelegate(draw);
			}
			// thread is not executed or break while loop
			if(taskManager == null || !taskManager.isAlive() || taskManager.wasBreak()){
					taskManager = new TaskManager();
					taskManager.start();
			}	
		}
	}
	class ClearDelegate  implements Clearable{
		@Override
		public void clear() {
			Thread thread = new Thread(new SearchResultDelegate(null));
			thread.run();
		}
	}
	public void show(Draw draw) {
		this.show.show(draw);
	}
	
	public void clear(){
		this.clear.clear();
	}
}