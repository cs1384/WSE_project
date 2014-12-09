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


public class SSJListPageXMLExtractor 
{
    public static int count = 0;
    public static int useful = 0;
    public static int cats = 0;
    public static String source = "F:\\wikidumps\\enwiki-20140903-pages-articles.xml";
    //public static String infoboxOut = "/Users/Wikero/wikicorpus/clean/infoboxes/";
    
    public static String ssjOut = "F:\\cs2580\\Project\\SSJout";
    public BufferedWriter bigWriter;
    public static boolean found = false;
    
    public SSJListPageXMLExtractor()
    {
        try
        {
            bigWriter = new BufferedWriter(new FileWriter(new File(ssjOut)));
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
                        if(SSJListPageXMLExtractor.count % 10000 == 0)
                        {
                            System.out.print("Processed " + SSJListPageXMLExtractor.count);
                            System.out.print(",  Useful " + SSJListPageXMLExtractor.useful);
                            System.out.println(",  Cats " + SSJListPageXMLExtractor.cats);
                        }
                        SSJListPageXMLExtractor.count++;


                        //String encodedTitle = null;
                        String title = page.getTitle().trim();

                        boolean isCat = title.length() > 9 && title.substring(0,9).equals("Category:");
                        if(isCat)
                            SSJListPageXMLExtractor.cats++;

                        if(page.isDisambiguationPage() || page.isRedirect())
                            return;

                        if(page.isSpecialPage() && !isCat)
                            return;

                        String ssj = null;

                        //some heuristics
                        if(!
                            ((title.contains("list of") || title.contains("List of")) && (title.contains("most") || title.contains("est")))
                          )
                            return;

                        try
                        {
                            Vector<String> posTags = StanfordManager.getPOSTags(title);

                            for(int i=0;i<posTags.size();i++)
                            {
                                String wordTag = posTags.get(i);

                                String arr[] = wordTag.split("/");
                                String tag = arr[arr.length-1];

                                if(tag.equalsIgnoreCase("JJS"))
                                {
                                    if(arr[0].equalsIgnoreCase("most") && i < posTags.size()-1)
                                    {
                                        ssj = posTags.get(i+1).split("/")[0];
                                    }
                                    else
                                    {
                                        ssj = arr[0];
                                        break;
                                    }
                                }
                            }
                        }
                        catch(Exception e)
                        {
                            System.err.println("Failed to parse title \"" + title + "\"");
                        }


                        if(ssj == null)
                        {
                            //System.out.println("ssj is null");
                            return;
                        }
                        else
                        {
                            //System.out.println("ssj not null");
                        }


                        //String wikiText = page.getWikiText();

                        String wikiText = WikiJsonReader.getWikiText(title);

                        if(wikiText.indexOf("{|") < 0)
                            return;

                        TableParser tp = TableParser.getInstance();
                        Vector<String> tablesText = tp.findTable(wikiText);

                        StringBuilder outputSB = new StringBuilder();

                        outputSB.append("Title:: " + title);
                        outputSB.append("\n");
                        System.out.println("Title:: " + title);

                        int tableCount = 0;
                        for(String tableText: tablesText)
                        {
                            try
                            {

                                WikiTable wikiTable = tp.parseTable2(tableText);
                                if(wikiTable != null)
                                {
                                    //wikiTable.getTranspose()
                                    try
                                    {
                                        Vector<TableAnalyzer.MonotonicLog> monotonicColumns = (new TableAnalyzer(wikiTable)).getMonoticColumns();

                                        if(monotonicColumns == null)
                                            throw new NullPointerException("monotonic cols");

                                        outputSB.append("##Table ");
                                        outputSB.append(tableCount++);
                                        outputSB.append(": \n");


                                        for(TableAnalyzer.MonotonicLog l: monotonicColumns)
                                        {
                                            //System.out.println("mono col = " + l.columnName + "   ,  dir = " + l.dir);
                                            outputSB.append(l.columnName);
                                            outputSB.append("\t");
                                            outputSB.append(l.dir);
                                            outputSB.append("\n");
                                        }
                                    }
                                    catch(NullPointerException npe)
                                    {
                                        System.err.println("monotonic columns null");
                                    }
                                    catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                                else
                                    System.out.println("rows null");
                            }
                            catch(TableParser.ColRowSpanException crse)
                            {
                                System.err.println("ColRowSPanException: " + crse.getMessage());
                            }
                            catch(Exception e)
                            {
                                System.err.println("error here: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        try
                        {
                            bigWriter.write(outputSB.toString());
                            bigWriter.write("-----------------------\n");
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
        SSJListPageXMLExtractor ext = new SSJListPageXMLExtractor();
        ext.extract();

    }
}
