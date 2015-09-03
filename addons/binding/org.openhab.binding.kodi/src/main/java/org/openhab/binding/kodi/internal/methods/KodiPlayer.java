package org.openhab.binding.kodi.internal.methods;

import com.google.gson.JsonObject;

public class KodiPlayer {
	
	public enum PlayerOpenType{
		FILE("file"),
		DIRECTORY("directory");
		
		private final String name;
		
		private PlayerOpenType(String name){
			this.name = name;			
		}
		
		public String getAsString(){
			return name;
		}
		
		public static PlayerOpenType fromString(String string){
			if("file".equals(string))
				return FILE;
			return DIRECTORY;
		}
	}
	
	
	public static class Stop extends KodiJsonRpc{
		public static final String PLAYER_CLOSE = "Player.Stop";
		
		public Stop(int playerId) {
			super(PLAYER_CLOSE);
			addParam("playerid", playerId);
		}
	}
	
	
	public static class Open extends KodiJsonRpc{
		public static final String PLAYER_OPEN = "Player.Open";
		
		public Open(String path, PlayerOpenType type) {
				super(PLAYER_OPEN);
				addProperty("id", 1);
				JsonObject item = getItem(path, type);
				addParam("item", item);				
		}
		
		private JsonObject getItem(String path, PlayerOpenType type){
			JsonObject itemObj = new JsonObject();
			itemObj.addProperty(type.getAsString(), path);
			return itemObj;
		}		
	}
}
