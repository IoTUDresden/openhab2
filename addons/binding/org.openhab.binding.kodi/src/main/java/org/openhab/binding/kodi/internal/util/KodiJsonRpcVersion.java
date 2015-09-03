package org.openhab.binding.kodi.internal.util;

public class KodiJsonRpcVersion {
	private final int minor;
	private final int patch;
	private final int major;	
	
	public KodiJsonRpcVersion(int minor, int patch, int major) {
		this.minor = minor;
		this.patch = patch;
		this.major = major;		
	}
	
	public int getMinor(){
		return minor;
	}
	
	public int getPatch(){
		return patch;
	}
	
	public int getMajor(){
		return major;
	}

}
