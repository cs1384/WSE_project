package wseproject;

//package src;

import java.io.File;
import edu.jhu.nlp.wikipedia.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Vector;


public class AllListPageXMLExtractor 
{
    public static int count = 0;
    public static int useful = 0;
    public static int cats = 0;
    public static String source = "F:\\wikidumps\\enwiki-20140903-pages-articles.xml";
    //public static String infoboxOut = "/Users/Wikero/wikicorpus/clean/infoboxes/";
    
    public static String listListOut = "F:\\cs2580\\Project\\ListOfListsWithTables";
    public BufferedWriter bigWriter;
    public static boolean found = false;
    
    public AllListPageXMLExtractor()
    {
        try
        {
            bigWriter = new BufferedWriter(new FileWriter(new File(listListOut)));
        }
        catch(Exception e)
        {
            System.err.println("Could not initialized writer");
        }
            
    }
    
    
    public void extract()
    {
        if(bigWriter == null)
        {
            System.err.println("Writer is null, returning");
            return;
        }
        
        try
        {

            WikiXMLParser parser = WikiXMLParserFactory.getSAXParser(source);
            parser.setPageCallback(new PageCallbackHandler() {
                public void process(WikiPage page) 
                {
                    try
                    {
                        if(AllListPageXMLExtractor.count % 10000 == 0)
                        {
                            System.out.print("Processed " + AllListPageXMLExtractor.count);
                            System.out.print(",  Useful " + AllListPageXMLExtractor.useful);
                            System.out.println(",  Cats " + AllListPageXMLExtractor.cats);
                        }
                        AllListPageXMLExtractor.count++;


                        //String encodedTitle = null;
                        String title = page.getTitle().trim();

                        boolean isCat = title.length() > 9 && title.substring(0,9).equals("Category:");
                        if(isCat)
                            AllListPageXMLExtractor.cats++;

                        if(page.isDisambiguationPage() || page.isRedirect())
                            return;

                        //if(page.isSpecialPage() && !isCat)
                        if(page.isSpecialPage() || isCat)
                            return;

                        

                        String wikiText = page.getWikiText();

                        //String wikiText = WikiJsonReader.getWikiText(title);

                        //if no tables, ignore for now
                        if(!wikiText.contains("{|"))
                            return;

                        //System.out.println("Title:: " + title);

                        try
                        {
                            bigWriter.write(title);
                            bigWriter.write("\n");
                        }
                        catch(IOException e)
                        {
                            System.err.println("Failed to write to file");
                        }

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            parser.parse();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public void closeWriter()
    {
        try
        {
            bigWriter.close();
        }
        catch(Exception e)
        {
            System.err.println("Failed to close writer");
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        AllListPageXMLExtractor ext = new AllListPageXMLExtractor();
        ext.extract();

    }
}
