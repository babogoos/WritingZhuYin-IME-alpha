package my.app.zinnia;

import java.util.ArrayList;

import my.app.delegate.Clearable;
import my.app.delegate.Showable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class InputView extends View implements Clearable{
	Handler handler = new Handler();
	private ClearDelegate clear = new ClearDelegate();
	
	private float posx = 0f;  
	private float posy = 0f;  
	private Path path = null;  
	private Draw draw = null;
	private Line line = null;
	Showable resultView;
	ArrayList<Path> draw_list = new ArrayList<Path>();
	
	
	public void setResultView(Showable result){
		this.resultView = result;
	}
	 
	public InputView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public InputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
 
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();  
		paint.setColor(Color.BLACK);  
		paint.setAntiAlias(true);  
		paint.setStyle(Paint.Style.STROKE);  
		paint.setStrokeWidth(2);  
		paint.setStrokeCap(Paint.Cap.ROUND);  
		paint.setStrokeJoin(Paint.Join.ROUND);
		 
		for (Path pt : draw_list) {  
			canvas.drawPath(pt, paint);  
		}  
		//current  
		if(path != null){  
			canvas.drawPath(path, paint);  
		}
		if(this.draw == null){
			this.draw = new Draw(canvas.getHeight(),canvas.getWidth());
		}
	}  
	 
 @Override
 public boolean onTouchEvent(MotionEvent e){  
	 super.onTouchEvent(e);
	 switch(e.getAction()){  
		case MotionEvent.ACTION_DOWN:
			//when down event is occured ,refresh line object  
			path = new Path();
			line = new Line();
			posx = e.getX();  
			posy = e.getY();
			path.moveTo(e.getX(), e.getY());
			 
			//To Searching
			line.add(e.getX(), e.getY());
			break;  
		case MotionEvent.ACTION_MOVE:
			//until up event is occured ,add line object
			posx += (e.getX()-posx)/1.4;  
			posy += (e.getY()-posy)/1.4;  
			path.lineTo(posx, posy);  
			invalidate();  
			 
			//To Searching
			line.add(e.getX(), e.getY());
			break;  
		case MotionEvent.ACTION_UP:
			//when up event is occured ,complete line object and show candidate character.
			path.lineTo(e.getX(), e.getY());  
			draw_list.add(path); 
			invalidate();  
			
			//To Searching
			line.add(e.getX(), e.getY());
			draw.add(line);
			if(resultView != null){
				resultView.show(draw);
			}
			break;  
		default:  
			break;  
	 }  
	 return true;  
 }
	 class ClearDelegate implements Clearable{
		 @Override
		 public void clear() {
			 handler.post(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					draw.clear();
					draw_list.clear();
					path = null;
					invalidate();
				}});
		 }
	 }
	@Override
	public void clear() {
		this.clear.clear();
	}
}  
