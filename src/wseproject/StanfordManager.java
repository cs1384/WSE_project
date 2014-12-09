/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Ali Local
 */
public class StanfordManager
{
    private static StanfordCoreNLP pipeline = new StanfordCoreNLP();
    
    private StanfordManager()
    {
        
    }
    
    public static StanfordCoreNLP getPipeline()
    {
        /*
        if(pipeline == null)
            pipeline = new StanfordCoreNLP();
        
        return pipeline;
                */
        return null;
    }
    
    public static Vector<String> getPOSTags(String input)
    {
        Annotation annotation;
        annotation = new Annotation(input);
        pipeline.annotate(annotation);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        if(sentences != null && sentences.size() > 0)
        {
            Vector<String> tags = new Vector<String>();
            CoreMap sentence = sentences.get(0);
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class))
            {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                
                String result = word;
                result += "/";
                result += pos;
                tags.add(result);
            }
            return tags;

        }
        return null;
    }
    
    public static String getLemma(String plural)
    {
        Annotation annotation;
        annotation = new Annotation(plural);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        String lemma = "";
        if(sentences != null && sentences.size() > 0)
        {
            CoreMap sentence = sentences.get(0);
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class))
            {
                String singular = token.get(CoreAnnotations.LemmaAnnotation.class);
                lemma += singular + " ";
                
            }
            return lemma.trim();
        }
        return null;
    }
    
    //public static annotate()
}
