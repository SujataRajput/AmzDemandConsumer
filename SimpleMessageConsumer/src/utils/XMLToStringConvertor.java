package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class XMLToStringConvertor {

	public static String convertXMLToString(File inputFile){ 
		
		String xmlToString = "";
		try{ 
			InputStream inputStream = new FileInputStream(inputFile);
			StringBuilder builder =  new StringBuilder();
			int ptr = 0;
			while ((ptr = inputStream.read()) != -1 )
			{
				builder.append((char) ptr);
			}

			xmlToString  = builder.toString();
			inputStream.close();	     

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return xmlToString;
	}

}
