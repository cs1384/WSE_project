/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author Ali Local
 */
public class Tester
{
    
    public static void main(String[] args) 
    {
        try
        {
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("F:\\cs2580\\Project\\entity_prop_out.txt")));
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("F:\\cs2580\\Project\\filmography_entity_prop_out.txt")));
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("data/relations/country_entity_prop_out.txt")));
            BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("data/relations/tin_entity_prop_out.txt")));
            
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\small_list.txt")));
            BufferedReader br = new BufferedReader(new FileReader(new File("data/ListOfArticlesWithTables")));
            Vector<String> listOfLists = new Vector<String>();
            
            String line;
            while((line = br.readLine()) != null)
            {
                listOfLists.add(line);
            }
            
            TableParser tp = TableParser.getInstance();
            
            for(String pgTitle: listOfLists)
            {
                //if(!pgTitle.toLowerCase().contains("filmography"))
                if(!(
                        (pgTitle.toLowerCase().contains("country") || pgTitle.toLowerCase().contains("countries")) &&
                        pgTitle.toLowerCase().contains("list of") 
                    ))
                    continue;
                
                try
                {
                    System.out.println("Title :: " + pgTitle);
                    String wikiText = WikiJsonReader.getWikiText(pgTitle);
                    Vector<String> tables = tp.findTable(wikiText);



                    for(String table: tables)
                    {
                        try
                        {
                            WikiTable wikiTable = tp.parseTable2(table);
                            if(wikiTable != null)
                            {
                                wikiTable.setPageTitle(pgTitle);
                                //Vector<Vector<String>> rows = wikiTable.getTable();
                                //System.out.println("rows = ");
                                //System.out.println(wikiTable.getTable());
                                /*
                                for(Vector<String> row: rows)
                                {
                                    for(String col: row)
                                    {
                                        System.out.print("<" + col + ">" + "\t");
                                    }

                                    System.out.println(row.size());
                                }
                                System.out.println("<<<END>>>");
                                */
                                //Vector<Vector<String>>  transpose = wikiTable.getTranspose();
                                //System.out.println("transpose = ");
                                //System.out.println(transpose);

                                //Vector<MonotonicLog> monotonicColumns = (new TableAnalyzer()).getMonoticColumns_FromCols(transpose);

                                TableAnalyzer analyzer = new TableAnalyzer(wikiTable);

                                List<TableAnalyzer.RowProps> allEntityProps = analyzer.getPropertiesFromRows();

                                for(TableAnalyzer.RowProps props: allEntityProps)
                                {
                                    //writer.write("entity = ");
                                    //writer.write("entity = ");
                                    
                                    //System.out.println("entity = " + props.getEntity() + "  ,  type = " + StanfordManager.getLemma(props.getEntityType()));
                                    for(Map.Entry e: props.getProps().entrySet())
                                    {
                                        //System.out.println(e.getKey() + " => " + e.getValue());
                                        bigWriter.write(props.getEntity().replaceAll("\n", ""));// + "  ,  type = " + StanfordManager.getLemma(props.getEntityType()));
                                        bigWriter.write(Indexer.DELIM);
                                        bigWriter.write(StanfordManager.getLemma(props.getEntityType()).replaceAll("\n", ""));
                                        bigWriter.write(Indexer.DELIM);
                                        bigWriter.write(((String)e.getKey()).replaceAll("\n", ""));
                                        bigWriter.write(Indexer.DELIM);
                                        bigWriter.write(((String)e.getValue()).replaceAll("\n", ""));
                                        bigWriter.write(Indexer.DELIM);
                                        bigWriter.write(pgTitle.replaceAll("\n", ""));
                                        bigWriter.write("\n");


                                    }
                                    
                                }

                                Vector<TableAnalyzer.MonotonicLog> monotonicColumns = analyzer.getMonoticColumns();

                                /*
                                for(TableAnalyzer.MonotonicLog l: monotonicColumns)
                                {
                                    System.out.println("mono col = " + l.columnName + "   ,  dir = " + l.dir);

                                }
                                */
                            }
                            else
                            {
                                //System.out.println("rows null");
                            }
                        }
                        catch(TableParser.ColRowSpanException crse)
                        {
                            //System.out.println(crse.getMessage());
                            //System.out.println("Ignore and move on");
                        }
                        catch(Exception e)
                        {
                            //System.err.println("other exception: " + e.getMessage());
                        }
                    }
                    
                }
                catch(Exception e)
                {
                    //System.err.println("Some error: " + e.getMessage());
                }
            }
            
            bigWriter.close();
            
            //tp.findTable(sb.toString());
            /*
            String pgTitle = "List of oldest people by year of birth";
                   pgTitle = "List of largest optical refracting telescopes";
                   pgTitle = "List of cities in New York";
                   pgTitle = "List of Byzantine emperors";
                   pgTitle = "List of presidents of FIFA";
            
            //wikiText = WikiJsonReader.getWikiText("List_of_largest_optical_refracting_telescopes");
            */


        }    
        catch(Exception e)
        {
            e.printStackTrace();
        }
            
    }
    
    
    public static void main1(String[] args) 
    {
        //System.out.println(StanfordManager.getLemma("Presidency"));
        //System.out.println(StanfordManager.getLemma("President"));
        //System.out.println(StanfordManager.getLemma("Presidents"));
                
        /*
        String thing = "| sasa\n| qqq\n| {{aaa\n|bbb\n|ccc}} \n|xvf \n| fferesasa\n| vfqqq\n| {{rraaa\n|brbb\n|ppccc}} \n|mnjxvf";
        Pattern regex = Pattern.compile("(?s)\\{\\{(.*?)\\}\\}");
        Matcher regexMatcher = regex.matcher(thing);
        
        System.out.println("orig = ");
        System.out.println(thing);
        System.out.println("");
        if (regexMatcher.find()) 
        {
            System.out.println("=> " + regexMatcher.group(1));
            String resultString = regexMatcher.replaceAll(
                "{{" + regexMatcher.group(1).replaceAll("\n", "") + "}}"
            );
            System.out.println("\nres = \n" + resultString);
        }
        else
            System.out.println("did not find");
        
        //System.out.println(thing);
        
        
        if(true)
            return;
        */
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\hfile.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\Alfred+Hitchcock+filmography")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\List+of+longest+films")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\wikidumps\\clean\\texts\\List+of+longest-running+United+States+television+series")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\wikidumps\\clean\\texts\\List+of+longest+placenames+in+Ireland")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\wikidumps\\clean\\texts\\List+of+tallest+buildings+and+structures+in+London")));
            
            String line = "";
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }
            
            TableParser tp = TableParser.getInstance();
            //tp.findTable(sb.toString());
            String wikiText = sb.toString();
            
            
            String pgTitle = "List of oldest people by year of birth";
                   pgTitle = "List of largest optical refracting telescopes";
                   pgTitle = "List of cities in New York";
                   pgTitle = "List of Byzantine emperors";
                   pgTitle = "List of presidents of FIFA";
                   //pgTitle = "List of national anthems";
                   //pgTitle = "List of fictional guidebooks";
                   pgTitle = "Laurel and Hardy filmography";
            
            //wikiText = WikiJsonReader.getWikiText("List_of_largest_optical_refracting_telescopes");
            wikiText = WikiJsonReader.getWikiText(pgTitle);
            Vector<String> tables = tp.findTable(wikiText);

            int t = 0;
            for(String table: tables)
            {
                t++;
                //if(t == 1)
                //    continue;
                //System.out.println("Table::");
                //System.out.println(table);

                try
                {
                    WikiTable wikiTable = tp.parseTable2(table);
                    if(wikiTable != null)
                    {
                        wikiTable.setPageTitle(pgTitle);
                        Vector<Vector<String>> rows = wikiTable.getTable();
                        System.out.println("rows = ");
                        //System.out.println(wikiTable.getTable());
                        for(Vector<String> row: rows)
                        {
                            for(String col: row)
                            {
                                System.out.print("<" + col + ">" + "\t");
                            }
                           
                            System.out.println(row.size());
                        }
                        System.out.println("<<<END>>>");
                        //Vector<Vector<String>>  transpose = wikiTable.getTranspose();
                        //System.out.println("transpose = ");
                        //System.out.println(transpose);
                        
                        //Vector<MonotonicLog> monotonicColumns = (new TableAnalyzer()).getMonoticColumns_FromCols(transpose);
                        
                        TableAnalyzer analyzer = new TableAnalyzer(wikiTable);
                        
                        List<TableAnalyzer.RowProps> allEntityProps = analyzer.getPropertiesFromRows();
                        if(allEntityProps == null || allEntityProps.size() == 0)
                            System.out.println("allEntitiy props is empty or null");
                        
                        for(TableAnalyzer.RowProps props: allEntityProps)
                        {
                            System.out.println("entity = " + props.getEntity() + "  ,  type = " + StanfordManager.getLemma(props.getEntityType()));
                            for(Map.Entry e: props.getProps().entrySet())
                            {
                                System.out.println(e.getKey() + " => " + e.getValue());
                            }
                        }
                        
                        Vector<TableAnalyzer.MonotonicLog> monotonicColumns = analyzer.getMonoticColumns();
                        
                        
                        for(TableAnalyzer.MonotonicLog l: monotonicColumns)
                        {
                            System.out.println("mono col = " + l.columnName + "   ,  dir = " + l.dir);
                            
                        }
                    }
                    else
                    {
                        System.out.println("rows null");
                    }
                }
                catch(TableParser.ColRowSpanException crse)
                {
                    System.out.println(crse.getMessage());
                    System.out.println("Ignore and move on");
                }
                catch(Exception e)
                {
                    System.err.println("other exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        }    
        catch(Exception e)
        {
            e.printStackTrace();
        }
            
    }
}
