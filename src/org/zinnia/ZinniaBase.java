package org.zinnia;

abstract class ZinniaBase {
	static {
		System.loadLibrary("ZinniaJNI");
	}
	protected long address;
	protected long getAddress(){
		if(this.address == 0){
			throw new NullPointerException();
		}
		return this.address;
	}
	
	protected abstract long nativeNew();
	protected abstract boolean nativeDelete(long address);
	
	ZinniaBase(){
		this.address = nativeNew();
	};
	public void dispose(){
		this.nativeDelete(this.address);
		this.address = 0;
	}
	protected void finalize() {
		if(this.address != 0){
			nativeDelete(this.address);
		}
	}
}