/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ali Local
 */
public class Regexer
{
    private static final String tableRegex = "(?s)\\{\\|(.*?)\\|\\}";
    private static final String linkBoxRegex = "(?:\\[\\[)(.*?)(?:\\]\\])";
    private static final String templateRegex = "(?:\\{\\{)(.*?)(?:\\}\\})";
    private static final String multilineTemplateRegex = "(?s)\\{\\{(.*?)\\}\\}";


    private static Pattern tablePattern;
    private static Pattern linkBoxPattern;
    private static Pattern templatePattern;
    private static Pattern multilineTemplatePattern;

    private Regexer()
    {
        
    }
    
    public static Matcher matchTable(String text)
    {
        if(tablePattern == null)
            tablePattern = Pattern.compile(tableRegex);
        
        return tablePattern.matcher(text);
    }
    
    public static Matcher matchLinkBox(String text)
    {
        if(linkBoxPattern == null)
            linkBoxPattern = Pattern.compile(linkBoxRegex);
        
        return linkBoxPattern.matcher(text);
    }

    public static Matcher matchTemplate(String text)
    {
        if(templatePattern == null)
            templatePattern = Pattern.compile(templateRegex);
        
        return templatePattern.matcher(text);
    }
    
    public static Matcher matchMultilineTemplate(String text)
    {
        if(multilineTemplatePattern == null)
            multilineTemplatePattern = Pattern.compile(multilineTemplateRegex);
        
        return multilineTemplatePattern.matcher(text);
    }
    
}
