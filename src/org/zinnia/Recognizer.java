package org.zinnia;

public class Recognizer extends ZinniaBase{
	static {
		System.loadLibrary("ZinniaJNI");
	}
	
	protected native long nativeNew();
	protected native boolean nativeDelete(long address);
	
	protected native boolean open(long address, String filename);
	protected native boolean open(long address, String ptr, long size);
	protected native boolean close(long address);
	protected native long size(long address);
	protected native String value(long address,long i);
	protected native long classify( long address,long character_address, long nbest);
	protected native String what(long address);
	 
	public boolean open(String filename){
		  return open(getAddress(),filename);
	}
	public boolean open(String ptr, long size){
		  return open(getAddress(),ptr,size);
	}
	public boolean close(){
		  return close(getAddress());
	}
	public long size(){
		  return size(getAddress());
	}
	public String value(long i){
		  return value(this.address,i);
	}
	public Result classify( Character character, long nbest){
		  long address = classify(getAddress(),character.getAddress(),nbest);
		  return new Result(address);
	}
	public String what(){
		  return what(getAddress());
	}
}
