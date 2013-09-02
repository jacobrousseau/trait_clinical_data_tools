/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author j.rousseau
 */
public class FileUtils {
    
    /**
     * Reads lines from a file
     * @param aFileName the file to read
     * @return a list of the file lines
     * @throws IOException if something goes wrong
     */
     public static List<String> readLinesFromFile(String aFileName) throws IOException {        
        BufferedReader reader = null;
        List<String> ret = new ArrayList<String>();        
        try {
            FileReader fileReader = new FileReader(aFileName);
            reader = new BufferedReader(fileReader);            
            String line = reader.readLine();
            while (line != null) {
                ret.add(line);
                line = reader.readLine();            
            }
        }
        
        finally {
            if (reader != null) {
                reader.close();
            }
        }        
        return ret;
    }
     
    /**
     * Wrotes a list of strings to file
     * @param aFileName the file to write to
     * @param aList the list of strings
     * @throws IOException if something goes amiss
     */ 
    public static void writeLinesToFile(String aFileName, List<String> aList) throws IOException {        
        FileWriter writer = null;
        String endOfLine = System.getProperty("line.separator");
        try {
            writer = new FileWriter(aFileName);               
            for (String line : aList) {
                writer.write(line + endOfLine);   
            }
        }
        catch (Exception e) {
            System.err.println("Problem writing to output " + aFileName + ". " + e.getMessage());
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    } 
    
    /**
     * removes the file name extension from a file name
     * @param aFileName the filename
     * @return the file name without extension
     */
    public static String stripFileNameExtension(String aFileName) {
        return aFileName.substring(0, aFileName.lastIndexOf("."));
    }
}
