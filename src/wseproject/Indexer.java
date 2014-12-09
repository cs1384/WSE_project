/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Ali Local
 */
public class Indexer
{
    //private HashMap<String, Integer> wordToIndex = new HashMap<String, Integer>();
    //private HashMap<Integer, String> indexToWord = new HashMap<Integer, String>();
    
    private HashMap<String, ArrayList<String>> tokenToEntity = new HashMap<String, ArrayList<String>>();
    private HashMap<String, Integer> entityFrequencies;
    private HashMap<String, String> entityCats = new HashMap<String, String> ();
    
    HashMap<String, ArrayList<ER>> index = new HashMap<String, ArrayList<ER>>();
            
    final static String relationsSrcFolder = "data/relations/";
    final static String relationsNamesFile = "data/relations.txt";
    
    private HashMap<String, Set<String>> entityToType = new HashMap<String, Set<String>>();
    private HashMap<String, Set<String>> typeToentity = new HashMap<String, Set<String>>();
    
    private Vector<String> relationsFiles = new Vector<String>();
    static final String DELIM = " -$|$- ";
    static final String ESCAPED_DELIM = " -\\$\\|\\$- ";
    
    static long relCount = 0;
    
    public Indexer()
    {
        System.out.println("Reading file of relations files");
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File(relationsNamesFile)));
            String line;
            while((line = br.readLine()) != null)
            {
                relationsFiles.add(line);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        
        for(String fileName: relationsFiles)
        {
            System.out.println("Reading file: " + fileName);
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(new File(relationsSrcFolder + fileName)));
                String line;
                while((line = br.readLine()) != null)
                {
                    handleRelation(line);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public void handleRelation(String line)
    {
        //System.out.println("Working with: " + line);
        try
        {
            String tokens[] = line.split(ESCAPED_DELIM);

            //System.out.println("tokens len: " + tokens.length);
            if(tokens.length != 5)
                return;

            String entity = tokens[0].toLowerCase();
            String type = tokens[1].toLowerCase();
            String property = tokens[2].toLowerCase();
            String value = tokens[3].toLowerCase();
            String article = tokens[4].toLowerCase();

            if(entity == null || entity.trim().equals(""))
                return;

            //Add poperty/value pair to the hash table against entity
            //if(article.toLowerCase().contains("filmography"))
            //if(false)
            //{

            //}
            //else
            {
                if(!entityToType.containsKey(entity))
                    entityToType.put(entity, new HashSet<String>());

                entityToType.get(entity).add(type);


                if(!typeToentity.containsKey(type))
                    typeToentity.put(type, new HashSet<String>());

                typeToentity.get(type).add(entity);

                if(!index.containsKey(entity))
                    index.put(entity, new ArrayList<ER>());
                
                index.get(entity).add(new ER(property, value, type));

                //System.out.println("added: " + entity + ",  " + property + ",  " + value);
                
                relCount++;
                if(relCount % 10000 == 0)
                    System.err.println("Added " + relCount + " relations");
                
                if(article.contains("filmography"))
                {
                    String otherEntity = article.substring(0, article.indexOf("filmography")).trim();
                    if(!index.containsKey(otherEntity))
                        index.put(otherEntity, new ArrayList<ER>());
                
                    index.get(otherEntity).add(new ER("*invRel*", entity, "movie"));

                }
                //Now we need to add reverse relation: so get article and look up constraint
                
            }



        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /*
    public void createIndex()
    {
        //final String srcDir = "F:\\wikidumps\\clean\\infoboxes_cities\\";
        final String srcDir = "F:\\wikidumps\\clean\\infoboxes_movies\\";
        File src = new File(srcDir);
        int i=0;
        for(File file : src.listFiles())
        {
            //System.out.println("File " + i + " : " + file.getName());
            try
            {
                String entity = URLDecoder.decode(file.getName(), "utf8");
                entity = entity.replaceAll("\\,|\\.", "");
                entityToTokens(entity);

            
            
            }
            catch(UnsupportedEncodingException uee)
            {
                System.err.println("Failed to decode: " + uee.getMessage());
            }
            
            
            i++;
        }
        
    }
    */
    public void preprocess(String srcDir, String category)
    {
        //final String srcDir = "F:\\wikidumps\\clean\\infoboxes_cities\\";
        //final String srcDir = "F:\\wikidumps\\clean\\infoboxes_movies\\";
        
        System.out.println("Loading Index from " + srcDir);
        
        File src = new File(srcDir);
        int i=0;
        for(File file : src.listFiles())
        {
            //System.out.println("File " + i + " : " + file.getName());
            try
            {
                String entity = URLDecoder.decode(file.getName(), "utf8");
                entity = entity.replaceAll("\\,|\\.", "");
                entityToTokens(entity);

                entityCats.put(entity, category);
            
            }
            catch(UnsupportedEncodingException uee)
            {
                System.err.println("Failed to decode: " + uee.getMessage());
            }
            
            
            i++;
        }
        
    }
    
    private void entityToTokens(String entity)
    {
        String strs[] = entity.split("\\s");
        for(String token: strs)
        {
            pairUpTokenEntity(token.toLowerCase(), entity);
        }
        
    }
    
    private void pairUpTokenEntity(String token, String entity)
    {
        if(tokenToEntity.containsKey(token))
        {
            ArrayList<String> list = tokenToEntity.get(token);
            list.add(entity);
        }
        else
        {
            ArrayList<String> list = new ArrayList<String>();
            list.add(entity);
            tokenToEntity.put(token, list);
        }
    }
    
    public void processQuery(String query)
    {
        String tokens[] = query.split("\\s");
        Set<String> results = new HashSet<String>(tokenToEntity.get(tokens[0]));
        for(int i=1;i<tokens.length;i++)
        {
            //get this token's entities:
            ArrayList<String> entities = tokenToEntity.get(tokens[i]);
            Set<String> resultsTemp = new HashSet<String>(entities);
            results.retainAll(resultsTemp);
            
        }
        System.out.println("Entities for token \"" + query + "\": ");
        
        //sort results by frequency
        ArrayList<String> resultsSorted = new ArrayList<String>(results);
        Collections.sort(resultsSorted, new Comparator()
        {

            @Override
            public int compare(Object o1, Object o2)
            {
                String e1 = (String) o1;
                String e2 = (String) o2;
                int e1Count = 0;
                int e2Count = 0;
                if(entityFrequencies.containsKey(e1))
                    e1Count = entityFrequencies.get(e1);
                if(entityFrequencies.containsKey(e2))
                    e2Count = entityFrequencies.get(e2);
                return e1Count - e2Count;
            }
        });

        for(String entity : resultsSorted)
        {
            System.out.println("\t" + entity);
        }
        System.out.println("");

    }
    
    
    public void process(String srcDir)
    {
        System.out.println("Loading Index from " + srcDir);
        
        File src = new File(srcDir);
        int i=0;
        for(File file : src.listFiles())
        {
            //System.out.println("File " + i + " : " + file.getName());
            try
            {
                String entity = URLDecoder.decode(file.getName(), "utf8");
                entity = entity.replaceAll("\\,|\\.", "");
                
                
            }
            catch(UnsupportedEncodingException uee)
            {
                System.err.println("Failed to decode: " + uee.getMessage());
            }
            
            
            i++;
        }
        
    }
    
    public static void main(String[] args)
    {
        Indexer index = new Indexer();
        
        //index.preprocess("F:\\wikidumps\\clean\\infoboxes_cities\\", "cities");
        //index.preprocess("F:\\wikidumps\\clean\\infoboxes_countries\\", "countries");
        
        //String key = "pakistan".toLowerCase();
        String key = "Brad pitt".toLowerCase();
        System.out.println("key = " + key);
        if(index.index.containsKey(key))
        {
            ArrayList<ER> ll = index.index.get(key);
            for(ER er: ll)
            {
                System.out.println(er.relationship + " => " + er.entity);
            }
        }
        else
        {
            System.out.println(key + " not in map");
        }
        
        
        key = "barack obama".toLowerCase();
        System.out.println("\nkey = " + key);
        if(index.index.containsKey(key))
        {
            ArrayList<ER> ll = index.index.get(key);
            for(ER er: ll)
            {
                System.out.println(er.relationship + " => " + er.entity);
            }
        }
        else
        {
            System.out.println(key + " not in map");
        }
        /*
        System.out.println("");
        System.out.println("");
        
        String key2 = "city".toLowerCase();
        if(index.typeToentity.containsKey(key2))
        {
            Set<String> ll = index.typeToentity.get(key2);
            for(String ss: ll)
            {
                System.out.println("entity = " + ss);
            }
        }
        else
        {
            System.out.println(key2 + " not in map");
        }
        */
        
        /*
        index.processQuery("karachi");
        index.processQuery("lahore");
        index.processQuery("java");
        index.processQuery("hyderabad");
        index.processQuery("london");
        index.processQuery("new york");
        */
    }
    
    class ER
    {
        String entity;
        String relationship;
        String nextEntityType;

        public ER(String entity, String relationship, String nextEntityType)
        {
            this.entity = entity;
            this.relationship = relationship;
            this.nextEntityType = nextEntityType;
        }
    }
}
