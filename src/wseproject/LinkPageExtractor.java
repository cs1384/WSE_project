package wseproject;

//package src;

import java.io.File;
import edu.jhu.nlp.wikipedia.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Vector;


public class LinkPageExtractor 
{
    public static int count = 0;
    public static int useful = 0;
    public static int cats = 0;
    public static String source = "F:\\wikidumps\\enwiki-20140903-pages-articles.xml";
    //public static String infoboxOut = "/Users/Wikero/wikicorpus/clean/infoboxes/";
    //public static String categoriesOut = "/Users/Wikero/wikicorpus/clean/categories/";
    //public BufferedWriter bigCatWriter;
    public static boolean found = false;
    
    public LinkPageExtractor()
    {
        
            
    }
    
    
    public void extract()
    {
        //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File(textOut + title)));
        try
        {
            //WikiXMLParser parser = WikiXMLParserFactory.getSAXParser("/path_to_wiki_dump/enwiki-20131202-pages-articles-multistream.xml.bz2.xml");
            WikiXMLParser parser = WikiXMLParserFactory.getSAXParser(source);
            parser.setPageCallback(new PageCallbackHandler() {
                public void process(WikiPage page) 
                {
                    if(found)
                        return;
                                
                    LinkPageExtractor.count++;
                    if(LinkPageExtractor.count % 10000 == 0)
                    {
                        System.out.print("Processed " + LinkPageExtractor.count);
                        System.out.print(",  Useful " + LinkPageExtractor.useful);
                        System.out.println(",  Cats " + LinkPageExtractor.cats);
                    }
                    //if(LinkPageExtractor.count < 3500000)
                    //if(true)
                    //    return;

                    String title = null;
                    try
                    {
                        title = URLEncoder.encode(page.getTitle().trim(), "utf-8") ;
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        System.err.println(uee.getMessage());
                        return;
                        
                    }
                    
                    boolean isCat = title.length() > 12 && title.substring(0,11).equals("Category%3A");
                    if(isCat)
                        LinkPageExtractor.cats++;
                    
                    if(page.isDisambiguationPage() || page.isRedirect())
                        return;
                    
                    if(page.isSpecialPage() && !isCat)
                        return;
                    
                    if(( title.contains("List+of") || title.contains("list+of") ) && title.contains("est") )
                    //if(title.contains("filmography") || title.contains("Filmography"))
                    {
                        String wikiText = page.getWikiText();
                        if(wikiText.indexOf("{|") < 0)
                            return;
                        /*
                        TableParser tp = TableParser.getInstance();
                        Vector<String> tables = tp.findTable(wikiText);
                        
                        for(String table: tables)
                        {
                            try
                            {
                                System.out.println("Table::");
                                System.out.println(table);

                                Vector<Vector<String>> rows = tp.parseTable(table);

                                Vector<Vector<String>>  transpose = tp.transposeTable(rows);

                                System.out.println(transpose);
                            }
                            catch(TableParser.ColRowSpanException crse)
                            {
                                System.out.println(crse.getMessage());
                                System.out.println("Ignore and move on");
                            }
                        }
                        
                        found = true;
                        System.out.println("Title = " + title);
                        System.out.println("");
                        System.out.println("");
                        
                        
                        System.out.println(wikiText);
                        */
                        System.out.println("Title = " + title);
                        
                        
                        
                        
                        
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
    
    public static void main(String[] args) throws Exception
    {
        LinkPageExtractor ext = new LinkPageExtractor();
        ext.extract();

    }
}
