package utils;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.XML;
import java.io.*;
/**
 *
 * @author srajput
 */
    public class XMLtoJSONConvertor{
 
        public static void main(String[] args) throws Exception 
        {
            try
            {
 
                File file = new File ("C:/Users/Sujata Rajput/AmazonMessageConsumer/SimpleMessageConsumer/src/SimpleMessageConsumerTest/HW_Demand_Test1.xml");
                System.out.println(file.getAbsolutePath());
                InputStream inputStream = new FileInputStream(file);
                StringBuilder builder =  new StringBuilder();
                int ptr = 0;
                while ((ptr = inputStream.read()) != -1 )
                {
                    builder.append((char) ptr);
                }
 
                String xml  = builder.toString();
                JSONObject jsonObj = XML.toJSONObject(xml); 
                System.out.println(jsonObj);
                inputStream.close();
 
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
}
