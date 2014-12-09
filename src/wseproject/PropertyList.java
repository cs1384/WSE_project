package wseproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PropertyList {
    public static class Property{
        int NOfAttribute;
        List<String> attribute = new ArrayList<String>();
    }
    //for indexing
    int _id = -1;
    public PropertyList(int id){
        this._id = id;
    }
    public Map<String, Property> _property = new HashMap<String, Property>();
    //for writing to disk
    private int _NOfproperty = 0;
    //for reading from disk
    public PropertyList(){}
    
    public void addProperty(String name){
        if(this._property.containsKey(name))
            return;
        else
            this._property.put(name, new Property());
    }
    
    @Override
    public String toString(){
        this._NOfproperty = this._property.size();
        StringBuilder sb = new StringBuilder();
        sb.append(this._id);
        sb.append(" ").append(this._NOfproperty);
        for(Map.Entry<String, Property> p : this._property.entrySet()){
            processProperty(p,sb);
        }
        return sb.toString();
    }
    private void processProperty(Map.Entry<String, Property> e, StringBuilder sb){
        Property p = e.getValue();
        p.NOfAttribute = p.attribute.size();
        sb.append(" ").append(e.getKey());
        sb.append(" ").append(p.NOfAttribute);
        for(String s : p.attribute){
            sb.append(" ").append(s);
        }
    }
    public void load(String line){
        Scanner sc = new Scanner(line);
        this._id = sc.nextInt();
        this._NOfproperty = sc.nextInt();
        for(int i=0;i<this._NOfproperty;i++){
            String name = sc.next();
            Property p = new Property();
            p.NOfAttribute = sc.nextInt();
            for(int j=0;j<p.NOfAttribute;j++){
                p.attribute.add(sc.next());
            }
            this._property.put(name, p);
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
