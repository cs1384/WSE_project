/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject;

import java.net.*;
import java.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

public class WikiJsonReader
{

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException
    {
        InputStream is = new URL(url).openStream();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String input;
            StringBuilder sb = new StringBuilder();
            while((input = br.readLine()) != null){
                System.out.println(input);
                sb.append(input);
            }
            
            JSONObject json = new JSONObject(sb.toString());
            return json;
        } 
        catch(Exception e)
        {
            System.out.println("Failed to download json for: " + url);
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
        finally
        {
            is.close();
        }
    }

    //List_of_tallest_buildings_and_structures_in_London
    public static String getWikiText(String article) 
    {
        //System.out.println("Read and loaded JSON for: " + article);
        article = article.replace(" ", "_");
        String srcURL = "http://en.wikipedia.org/w/api.php?action=query&prop=revisions&"
                + "rvprop=content&format=json&titles=" + article;
        try
        {
            JSONObject json = readJsonFromUrl(srcURL);
            JSONObject pages = json.getJSONObject("query").getJSONObject("pages");
            Iterator i = pages.keys();
            while (i.hasNext())
            {
                String key = (String) i.next();
                JSONObject page = pages.getJSONObject(key);
                JSONObject rev0 = page.getJSONArray("revisions").getJSONObject(0);
                String text = rev0.getString("*");
                //System.out.println("Read and loaded JSON for: " + article);
                String t = text.toString();
                //System.out.println(t);
                return t;
            }
            
        }
        catch(Exception e)
        {
            System.out.println("Failed to load JSON for: " + article);
        }
        return null;
    }
    
    public static void main(String[] args){
        WikiJsonReader test = new WikiJsonReader();
        test.getWikiText("Alabama");
        //System.out.println(test.getWikiText("Alabama"));
    }
    
    
}
