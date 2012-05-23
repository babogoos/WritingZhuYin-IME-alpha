package my.app.zinnia;

import java.util.ArrayList;
class Point{
	int x;
	int y;
	Point(int x,int y){
		this.x = x;
		this.y = y;
	}
}
class Line extends ArrayList<Point>{
	
	private static final long serialVersionUID = -6437787476053552075L;


	public void add(float f,float g){
		this.add(new Point((int)f,(int)g));
	}
}
public class Draw extends ArrayList<Line>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5844648186592021530L;

	public Draw(int height,int width){
	}
}