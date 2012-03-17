package org.zinnia;

public class Result extends ZinniaBase{
	static {
		System.loadLibrary("ZinniaJNI");
	}

	protected long nativeNew(){
		//None
		return 0;
	};
	
	protected native boolean nativeDelete(long address);

	protected native String value(long address,long i);
	protected native float score(long address,long i);
	protected native long size(long address);
	
	Result(long address){
		this.address = address;
	}
	
	public String value(long i){
		return value(getAddress(),i);
	}
	public float score(long i){
		return score(getAddress(),i);
	}
	public long size(){
		return size(this.address);
	}
}
