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
public class Hearst
{
    public static void main(String[] args)
    {
        //Pattern pattern = Pattern.compile("<np>(\\w+)<\\/np> such as (?:(?:, | or | and )?<np>(\\w+)<\\/np>)*");
        Pattern pattern = Pattern.compile("<np>(\\w+)</np> such as|\\G(?:,| or| and)? <np>(\\w+)</np>");
        Matcher matcher = pattern.matcher("I have a <np>car</np> such as <np>BMW</np>, <np>Audi</np> or <np>Mercedes</np> and this can drive fast.");

        while (matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
    }
}
