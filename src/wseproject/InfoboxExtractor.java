package wseproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
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

public class InfoboxExtractor {
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
    String dataPath = "/Users/Tin/Desktop/project_data/";
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
    
    public void parseXmlDump() throws Exception{
        WikiXMLParser parser = WikiXMLParserFactory.getSAXParser(this.corpusPath);
        parser.setPageCallback(new PageCallbackHandler() {
            int count = 1;
            public void process(WikiPage page) {
                if(count<=500000){
                    try {
                        InfoBox infobox = page.getInfoBox();
                        int index;
                        String entityName = page.getTitle().trim();
                        PostingList postingL = getPostingList(entityName);
                        PropertyList propertyL = getPropertyList(entityName);
                        System.out.println("Start " + count + "th files: " + entityName);
                        if(infobox==null)
                            System.out.println("no infobox");
                        processInfobox(infobox, postingL, propertyL);
                        //System.out.println("postingList: " +postingL.toString());
                        System.out.println("propertyList: " +propertyL.toString());
                        System.out.println("postingList: " +postingL.toString());
                        //will create index for info, so need to go before setPropertyList
                        setPostingList(entityName, postingL);  
                        setPropertyList(entityName, propertyL);
                    }catch(Exception e) {
                        //System.out.println("no infobox");
                    }
                }else if(count==500001){
                    try {
                        output();
                        System.out.println("finished output!");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                count++;
            }
        });
        parser.parse();
    }
    
    private PostingList getPostingList(String entityName){
        Info info;
      //make sure the current entity is in the graph
        if(entity.containsKey(entityName)){
            info = this.entity.get(entityName);
        }else{
            info = new Info(this.entity.size());
            this.entity.put(entityName, info);
            return new PostingList(info.id);
        }
        PostingList result = new PostingList(info.id);
        if(info.index != -1){
            int fileN = info.index / 4999;
            int lineN = info.index % 5000;
            String filePath = this.dataPath + "/relation/index_" + fileN;
            try {
                File file = new File(filePath);
                String line = FileUtils.readLines(file).get(lineN).toString();
                result.load(line);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
    private PropertyList getPropertyList(String entityName){
        Info info;
        if(entity.containsKey(entityName)){
            info = this.entity.get(entityName);
        }else{
            info = new Info(this.entity.size());
            this.entity.put(entityName, info);
            return new PropertyList(info.id);
        }
        PropertyList result = new PropertyList(info.id);
        if(info.index != -1){
            int fileN = info.index / 4999;
            int lineN = info.index % 5000;
            String filePath = this.dataPath + "/property/index_" + fileN;
            try {
                File file = new File(filePath);
                String line = FileUtils.readLines(file).get(lineN).toString();
                result.load(line);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
    private boolean setPostingList(String entityName, PostingList pl){
        Info info = this.entity.get(entityName);
        if(info.index==-1){
            info.index = this.postingIndex++;
            int fileN = info.index / 4999;
            //System.out.println(info.index);
            String filePath = this.dataPath + "/relation/index_" + fileN;
            try {
                File file = new File(filePath);
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                //System.out.println(pl.toString());
                bw.write(pl.toString()+"\n");
                bw.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //return false;
            }
            return true;
        }else{
            //should handle the modification for the list
            return false;
        }
    }
    private boolean setPropertyList(String entityName, PropertyList pl){
        Info info = this.entity.get(entityName);
        int fileN = info.index / 4999;
        //System.out.println(index);
        //System.out.println(lineN);
        String filePath = this.dataPath + "/property/index_" + fileN;
        //System.out.println("======================" + filePath);
        try {
            File file = new File(filePath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(pl.toString()+"\n");
            bw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //return false;
        }
        return true;
    } 
    
    public void processInfobox(InfoBox box, PostingList postingL, PropertyList propertyL){
        //System.out.println(box.dumpRaw());
        Scanner sc = new Scanner(box.dumpRaw());
        String type = null;
        
        while(sc.hasNextLine()){
            String s = sc.nextLine();
            System.out.println(s);
            if(s.matches("(.*)Infobox(.*)")){
                //System.out.println("line: "+s);
                if(s.contains("|"))
                    type = s.substring(10,s.indexOf("|")).trim().toLowerCase();
                else
                    type = s.substring(10).trim().toLowerCase();
                //System.out.println("type: "+type);
                boxType.add(type);
            }else{
                processLine(type, s, postingL, propertyL);
            }
        }
        return;
    }
    public void processLine(String type, String s, PostingList postingL, PropertyList propertyL){
        String[] token = s.split("=");
        //no value for this property
        if(token.length<=1 || token[1].equals(" "))
            return;
        String prop = token[0].substring(1).trim().toLowerCase();
        //unimportant property
        if(prop.matches("(.*)image(.*)") || prop.matches("(.*)logo(.*)") 
                || prop.matches("(.*)display(.*)") || prop.matches("(.*)caption(.*)")
                || prop.matches("(.*)name(.*)"))
            return;
        
        //stem the property
        //System.out.println("original: " + prop);
        
        Stemmer stemmer = new Stemmer(); 
        stemmer.add(prop.toCharArray(), prop.length());
        stemmer.stem();
        prop = stemmer.toString().toLowerCase();
        //decide which container in the entry to use
        
        boolean isRelation = token[1].contains("[[");
        Scanner sc2;
        
        if(!isRelation){
            
            if(!propertyL._property.containsKey(prop)){
                PropertyList.Property pt = new PropertyList.Property();
                propertyL._property.put(prop, pt);
            }
            List<String> op = propertyL._property.get(prop).attribute;
            //List<String> op = new ArrayList<String>();
            
            String temp;
            StringBuilder content = new StringBuilder(); //dede and {{dede ded}}
            StringBuilder word = new StringBuilder(); //{{efede ded e}}
            boolean looking = false;
            //if contains formatting sign

            
            if(token[1].contains("{{")){
                
                sc2 = new Scanner(token[1]);
                while(sc2.hasNext()){
                    String piece = sc2.next();
                    //if we find }}, we will have to process {{xxx}}
                    if(piece.matches("(.*)}}")){
                        word.append(piece).append(" ");
                        temp = processCurlyBraces(word.toString().trim());
                        if(temp!=null)
                            content.append(temp);
                        op.add(content.toString().trim());
                        word.setLength(0);
                        word.trimToSize();
                        content.setLength(0);
                        content.trimToSize();
                        looking = false;
                    //if we find {{, we should start looking for }}
                    }else if(piece.startsWith("{{")){
                        word.append(piece).append(" ");
                        looking = true;
                    //while looking, all text should be contained in {{xxx}}
                    }else if(looking){
                        word.append(piece).append(" ");
                    //general text should be contained as well
                    }else{
                        content.append(piece).append("_");
                    }
                }
                
            }else{
                
                String[] arr = token[1].split(",");
                for(String str : arr){
                    str = str.replaceAll(" ", "_");
                    op.add(str.trim());
                }
            }
            /*
            if(!propertyL._property.containsKey(prop)){
                PropertyList.Property pt = new PropertyList.Property();
                pt.attribute = op;
                //System.out.println(pt.NOfAttribute);
                //System.out.println(pt.attribute.size());
                propertyL._property.put(prop, pt);
            }else{
                propertyL._property.get(prop).attribute.addAll(op);
            }
            */
        }else{
            
            if(!postingL._relation.containsKey(prop)){
                postingL._relation.put(prop, new PostingList.Relation());    
            }
            List<Integer> op = postingL._relation.get(prop).entity;
            
            int left = token[1].indexOf("[")+2;
            int right = token[1].indexOf("]");
            int target;
            String entityName;
            while(left!=-1 && right!=-1){
                entityName = token[1].substring(left,right);
                target = processSquareBrackets(entityName, type);
                op.add(target);
                left = token[1].indexOf("[",left+2)+2;
                right = token[1].indexOf("]",right+2);
            }
            
        }
        return;
    }
    public int processSquareBrackets(String entityName, String type){
        //System.out.println("original: " + type);
        Stemmer stemmer = new Stemmer(); 
        stemmer.add(type.toCharArray(), type.length());
        stemmer.stem();
        type = stemmer.toString().toLowerCase();
        
        if(entityName.contains("|"))
            entityName = entityName.substring(0,entityName.indexOf("|"));
        
        Info info;
        if(entity.containsKey(entityName)){
            info = this.entity.get(entityName);
        }else{
            info = new Info(this.entity.size());
            this.entity.put(entityName, info);
        }
        //postingL.addEntity(type, postingL._id);
        //setPostingList(postingL);
        return info.id;
    }
    public String processCurlyBraces(String word){
        int start = -1;
        int count = 0;
        StringBuilder sb = new StringBuilder();
        //System.out.println(word.substring(word.indexOf('{')+2,word.lastIndexOf("}")-1));
        String[] token = word.substring(word.indexOf('{')+2,word.lastIndexOf("}")-1).split("\\|");
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
                sb.append(processCurlyBraces(token[i])).append("_");
            else 
                sb.append(token[i]).append("_");
            count--;
        }
        //System.out.println(sb.toString());
        return sb.toString();
    }
    
    public static void main(String[] args) throws Exception{
        InfoboxExtractor extractor = new InfoboxExtractor();
        extractor.parseXmlDump();
    }
}
