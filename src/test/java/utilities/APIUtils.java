package utilities;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.json.XML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIUtils {
    public static boolean
    isValidGUID(String str)
    {
        // Regex to check valid
        // GUID (Globally Unique Identifier)
        String regex
                = "^[{]?[0-9a-fA-F]{8}"
                + "-([0-9a-fA-F]{4}-)"
                + "{3}[0-9a-fA-F]{12}[}]?$";
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }
        // Find match between given string
        // and regular expression
        // uSing Pattern.matcher()
        Matcher m = p.matcher(str);
        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    public static String decoderToXml(String base64){
        String kripto=base64;
        byte[] byteArray = Base64.decodeBase64(kripto.getBytes());
        // Print the decoded string
        return new String(byteArray);

    }

    public static String decoderToJson(String base64){
        String kripto=base64;
        byte[] byteArray = Base64.decodeBase64(kripto.getBytes());
        // Print the decoded string
        String xml = new String(byteArray);
        JSONObject json = XML.toJSONObject(xml);
        String jsonString = json.toString(4);
        return jsonString;
    }

    public static Boolean isBase64(String string){
       return Base64.isBase64(string.getBytes());
    }

}
