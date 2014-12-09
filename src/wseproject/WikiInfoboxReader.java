package wseproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class WikiInfoboxReader {
    
    public static String getByArticleName(String article) throws MalformedURLException, IOException{
        //http://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=dump&titles=Brad%20Pitt&rvsection=0
        article = article.replaceAll(" ", "%20");
        String url = "http://en.wikipedia.org/w/api.php?action=query&prop="
                + "revisions&rvprop=content&format=dump&titles="+article
                + "&rvsection=0";
        //System.out.println(url);
        
        InputStream is = new URL(url).openStream();
        Scanner sc = new Scanner(new InputStreamReader(is, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        boolean print = false;
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            //System.out.println(line);
            if(line.contains("Infobox"))
                print = true;
            if(print && line.matches("}}"))
                break;
            if(print)
                sb.append(line).append("\n");
        }
        return sb.toString();
    }
    
    
    public static void main(String[] args){
        try {
            System.out.println(WikiInfoboxReader.getByArticleName("Brad Pitt"));
            System.out.println(WikiInfoboxReader.getByArticleName("Matt Damon"));
            System.out.println(WikiInfoboxReader.getByArticleName("Alabama"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
