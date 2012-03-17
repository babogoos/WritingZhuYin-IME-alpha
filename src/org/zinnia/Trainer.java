package org.zinnia;

public class Trainer  extends ZinniaBase{
	static {
		System.loadLibrary("ZinniaJNI");
	}
	
	protected native long nativeNew();
	protected native boolean nativeDelete(long address);

	protected native boolean add(long address,long character_address);
	protected native void clear(long address);
	protected native boolean train(long address,String filename);
	
	boolean add(Character character){
		return add(getAddress(),character.getAddress());
	}
	void clear(){
		clear(this.address);
	}
	boolean train(String filename){
		return train(getAddress(),filename);
	}
}
