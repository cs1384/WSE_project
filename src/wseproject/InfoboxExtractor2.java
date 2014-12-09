package wseproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import edu.jhu.nlp.wikipedia.*;

public class InfoboxExtractor2 {
    BufferedReader br;
    BufferedWriter bw;
    
    class Info{
        int id = -1;
        int index = -1;
        public Info(int id){
            this.id = id;
        }
    }
    
    
    //Map<String, Integer> property = new HashMap<String, Integer>();
    Map<String, Info> entity = new HashMap<String, Info>();
    Set<String> boxType = new HashSet<String>();
    String corpusPath = "/Volumes/Tin's Drive/Wikipedia/enwiki-20141106-pages-articles.xml";
    //String dataPath = "/Volumes/Tin's Drive/project_data/";
    String dataPath = "data/";
    int postingIndex = 0;
    int propertyIndex = 0;
    
    public void output() throws IOException{
        File file;
        BufferedWriter bw;
        
        file = new File(this.dataPath + "entity_map");
        bw = new BufferedWriter(new FileWriter(file));
        for(String s : entity.keySet()){
            bw.write(s + ":" + entity.get(s) + "\n");
        }
        bw.close();
        file = new File(this.dataPath + "box_type");
        bw = new BufferedWriter(new FileWriter(file));
        for(String s : boxType){
            bw.write(s + "\n");
        }
        bw.close();
    }
    public void start(String outputPath, String articleListPath) throws IOException{
        File outputFile = new File(outputPath);
        this.bw = new BufferedWriter(new FileWriter(outputFile));
        File articleListFile = new File(articleListPath);
        this.br = new BufferedReader(new FileReader(articleListFile));
        String article;
        while((article = br.readLine()) != null){
            
            String infobox = WikiInfoboxReader.getByArticleName(article);
            if(infobox.length()==0)
                continue;
            System.out.println(article);
            System.out.println(infobox);
            processInfobox(article, infobox);
        }
        this.bw.close();
        this.br.close();
        System.out.println("DONE!");
    }    
    
    public void processInfobox(String source, String box) throws IOException{
        Scanner sc = new Scanner(box);
        String type = null;
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            //System.out.println(line);
            if(line.matches("(.*)Infobox(.*)")){
                //System.out.println("line: "+line);
                type = line.substring(line.lastIndexOf("{")+9).trim().toLowerCase();
                System.out.println("type: "+type);
                boxType.add(type);
            }else{
                processLine(source, type, line);
            }
        }
        return;
    }
    public void write(String entity, String type, String property, 
            String content, String source) throws IOException{
        StringBuilder sb = new StringBuilder();
        sb.append(entity).append(Indexer.DELIM);
        sb.append(type).append(Indexer.DELIM);
        sb.append(property).append(Indexer.DELIM);
        sb.append(content).append(Indexer.DELIM);
        sb.append(source).append("\n");
        this.bw.write(sb.toString());
        System.out.print(": " + sb.toString());
    }

    public void processLine(String source, String type, String line) throws IOException{
        String[] token = line.split("=");
        //no value for this property
        if(token.length<=1 || token[1].equals(" "))
            return;
        String prop = token[0].substring(token[0].indexOf("|")+1).trim().toLowerCase();
        //System.out.println(prop);
        //unimportant property
        if(prop.matches("(.*)image(.*)") || prop.matches("(.*)logo(.*)") 
                || prop.matches("(.*)display(.*)") || prop.matches("(.*)caption(.*)")
                || prop.matches("(.*)name(.*)") || prop.matches("(.*)alt(.*)")
                || prop.matches("(.*)alt(.*)") || prop.matches("(.*)coordinates(.*)")
                || prop.matches("(.*)seat(.*)")|| prop.matches("(.*)footnotes(.*)"))
            return;
        
        String[] props = token[1].split(" *<br(.){0,2}> *|( ){2,}");
        Scanner sc;
        String temp;
        StringBuilder phrase = new StringBuilder(); //dede and {{dede ded}}
        StringBuilder curly = new StringBuilder(); //{{efede ded e}}
        StringBuilder square = new StringBuilder(); //[[efede ded e]]
        boolean lookingCurly = false;
        boolean lookingSquare = false;
        for(String p : props){
            //System.out.println(p);
            sc = new Scanner(p);
            while(sc.hasNext()){
                String piece = sc.next();
                //System.out.println(piece);
                if(piece.matches("(.*)}}(,)?")){
                    curly.append(piece);
                    temp = this.processCurlyBraces(
                            curly.substring(curly.indexOf("{")+2,curly.indexOf("}")));
                    if(temp!=null)
                        phrase.append(temp).append(" ");
                    curly.setLength(0);
                    curly.trimToSize();
                    lookingCurly = false;
                }else if(piece.startsWith("{{")){
                    curly.append(piece).append(" ");
                    lookingCurly = true;
                }else if(lookingCurly){
                    curly.append(piece).append(" ");
                }else if(piece.matches("(.*)]](,)?")){
                    square.append(piece);
                    this.processSquareBrackets(
                            square.substring(square.indexOf("[")+2,square.indexOf("]"))
                            ,prop, type, source); //reverseRelation
                    phrase.append(square).append(" ");
                    square.setLength(0);
                    square.trimToSize();
                    lookingSquare = false;
                }else if(piece.contains("[[")){
                    square.append(piece).append(" ");
                    lookingSquare = true;
                }else if(lookingSquare){
                    square.append(piece).append(" ");
                }else{
                    phrase.append(piece).append(" ");
                }
            }
            this.write(source, type, prop, phrase.toString().trim(), source);
        }
    }
    public void processSquareBrackets(String entityName, String prop, 
            String type, String article) throws IOException{
        if(entityName.contains("|"))
            entityName = entityName.substring(0,entityName.indexOf("|"));
        
        this.write(entityName, prop, type, article, article);
    }
    public String processCurlyBraces(String word){
        int start = -1;
        int count = 0;
        StringBuilder sb = new StringBuilder();
        //System.out.println(word.substring(word.indexOf('{')+2,word.lastIndexOf("}")-1));
        String[] token = word.split("\\|");
        if(token.length<=1)
            return null;
        //System.out.println("token: "+token[0]);
        if(token[0].contains("death date")){
            start=1;count=3;
        }else if(token[0].contains("date")){
            start=1;count=3;
        }else if(token[0].contains("Sfn")){
            return null;
        }else if(token[0].contains("convert")){
            start=1;count=2;
        }else if(token[0].contains("coord")){
            start=1;count=2;
        }else if(token[0].contains("bar")){
            return null;
        }else{
            start=1;count=token.length-1;
        }
        for(int i=start;count>0;i++){
            if(token[i].contains("="))
                continue;
            if(token[i].contains("{{"))
                sb.append(processCurlyBraces(token[i])).append(" ");
            else 
                sb.append(token[i]).append(" ");
            count--;
        }
        //System.out.println(sb.toString());
        return sb.toString();
    }
    
    public static void main(String[] args) throws Exception{
        String[] props = "[[Richard Shelby]] (R)<br />[[Jeff Sessions]] (R)".split(" *<br(.)*> *|( ){2,}");
        for(String s : props)
            System.out.println(":"+s);
        String line = "}}{{Infobox aircraft type";
        line = line.substring(line.lastIndexOf("{")+9);
        System.out.println(line);
        InfoboxExtractor2 extractor = new InfoboxExtractor2();
        extractor.start("data/relations/tin_entity_prop_out.txt", "data/tinTestList");
    }
}
