package utils;

import org.json.JSONObject;
import org.json.XML;

public class StringToJSONConvertor {
	public static JSONObject convertStringToJSON (String xmlString){
		JSONObject jsonObj = XML.toJSONObject(xmlString); 
		return jsonObj;
	}     	
}
