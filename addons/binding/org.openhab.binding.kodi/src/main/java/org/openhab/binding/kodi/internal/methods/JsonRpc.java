package org.openhab.binding.kodi.internal.methods;


public class JsonRpc {
	
	public static class Version extends KodiJsonRpc{
		public static final String JSONRPC_VERSION = "JSONRPC.Version";
		
		public Version() {
			super(JSONRPC_VERSION);
		}
		
	}

}
