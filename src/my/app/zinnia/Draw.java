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
	public void add(float f,float g){
		this.add(new Point((int)f,(int)g));
	}
}
public class Draw extends ArrayList<Line>{
	public Draw(int height,int width){
	}
}