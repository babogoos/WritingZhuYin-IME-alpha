package org.zinnia;

public class Character extends ZinniaBase{
	static {
		System.loadLibrary("ZinniaJNI");
	}
	
	protected native long nativeNew();
	protected native boolean nativeDelete(long address);
	protected native void set_value(long address,String str);
	protected native String value(long address);
	protected native void set_width(long address,long width);
	protected native void set_height(long address,long height);
	protected native long width(long address);
	protected native long height(long address);	
	protected native void clear(long address);
	protected native void add(long address,long id,int x,int y);
	protected native long strokes_size(long address);
	protected native long stroke_size(long address,long id);
	protected native int x(long address,long id, long i);
	protected native int y(long address,long id, long i);
	protected native boolean parse(long address,String str);
	protected native String what(long address);

	public void set_value(String str){
		set_value(getAddress(),str);
	}
	public String value(){
		return value(getAddress());
	}
	public void set_width(long width){
		set_width(getAddress(),width);
	}
	public void set_height(long height){
		set_height(getAddress(),height);
	}
	public long width(){
		return width(getAddress());
	}
	public long height(){
		return height(getAddress());
	}
	public void clear(){
		clear(getAddress());
	}
	public void add(long id,int x,int y){
		add(getAddress(),id,x,y);
	}
	public long strokes_size(){
		return strokes_size(getAddress());
	}
	public long stroke_size(long id){
		return stroke_size(getAddress(),id);
	}
	public int x(long id, long i){
		return x(getAddress(),id,i);
	}
	public int y(long id, long i){
		return y(getAddress(),id,i);
	}
	public boolean parse(String str){
		return parse(getAddress(),str);
	}
	public String what(){
		return what(getAddress());
	}
}
