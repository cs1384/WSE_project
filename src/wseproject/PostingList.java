package wseproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;


/**
 * <id> <NOfRelation> [<RelationName> <startPointer> <NOfEntity>]* [<EntityId>]*
 * @author Tin
 *
 */
public class PostingList {
    public static class Relation{
        String name = null;
        int startPoint = -1;
        int NOfEntity = -1;
        List<Integer> entity = new ArrayList<Integer>();
    }
    //for indexing
    int _id = -1;
    public PostingList(int id){
        this._id = id;
    }
    public Map<String, Relation> _relation = new HashMap<String, Relation>();
    //for writing to disk
    private int _NOfRelation = 0;
    private int _lastOffset = 1;
    //for reading from disk
    public PostingList(){}
    
    @Override
    public String toString(){
        //list info
        this._NOfRelation = _relation.size();
        StringBuilder sb = new StringBuilder();
        sb.append(this._id);
        sb.append(" ").append(this._NOfRelation);
        //relation info
        this._lastOffset = 1; //entity offset starts from 1
        Queue<Integer> Q = new LinkedList<Integer>();
        for(Map.Entry<String, Relation> p : this._relation.entrySet()){
            processRelation(p,sb,Q);
        }
        //entities
        while(!Q.isEmpty()){
            sb.append(" ").append(Q.poll());
        }
        //return string
        return sb.toString();
    }
    private void processRelation(Map.Entry<String, Relation> e, 
            StringBuilder sb, Queue<Integer> Q){
        //update both this relation and the PostingList
        Relation r = e.getValue();
        r.name = e.getKey();
        r.startPoint = this._lastOffset;
        r.NOfEntity = r.entity.size();
        this._lastOffset += r.NOfEntity;
        this._NOfRelation++;
        //update StringBuilder
        sb.append(" ").append(r.name);
        sb.append(" ").append(r.startPoint);
        sb.append(" ").append(r.NOfEntity);
        //add entities to Queue
        for(Integer i : r.entity){
            Q.add(i);
        }
    }
    public void addEntity(String relation, int entityN){
        if(this._relation.containsKey(relation)){
            this._relation.get(relation).entity.add(entityN);
            Collections.sort(this._relation.get(relation).entity);
        }else{
            Relation temp = new Relation();
            temp.entity.add(entityN);
            this._relation.put(relation, temp);
        }
    }
    public void load(String line){
        try{
            Scanner sc = new Scanner(line);
            this._id = sc.nextInt();
            this._NOfRelation = sc.nextInt();
            List<String> relation = new ArrayList<String>();
            for(int i=0;i<this._NOfRelation;i++){
                Relation r = new Relation();
                relation.add(sc.next());
                r.startPoint = sc.nextInt();
                r.NOfEntity = sc.nextInt();
                this._relation.put(relation.get(i), r);
            }
            Relation temp;
            for(String s : relation){
                temp = this._relation.get(s);
                int top = temp.NOfEntity;
                for(int i=0;i<top;i++){
                    temp.entity.add(sc.nextInt());
                    this._lastOffset++;
                }
            }
        }catch(Exception e){
            System.out.println("Input Mismatch!");
        }
    }
    public static void main(String[] args){
        PostingList p = new PostingList();
    }
    
}
