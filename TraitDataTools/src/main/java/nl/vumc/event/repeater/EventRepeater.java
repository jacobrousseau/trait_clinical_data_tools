/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;

/**
 * Reads a CSV-file from disk and counts the number of duplicate ID in first 
 * column. Reports the number of 
 * duplicates and the maximal frequency of the duplicates and writes them to
 * a newly created file with the name: &lt;INPUT_FILE_NAMEgt;_repeating.csv 
 * <p/>
 * Usage:
 * java -jar EventRepeater [FILE_NAME]
 * 
 * 
 * 
 * @author j.rousseau
 */
public class EventRepeater {
    
    /**
     * The field delimiter of the input; a ',' for CSV files.
     */
    private static final String DELIMETER = "\t";
        
    /**
     * The header of the column which is added with the counter of the repeating
     * event
     */
    private static final String REPEATING_EVENT_HEADER = "repeat_number";
    
    /**
     * The extention which is added to the newly created file which contains
     * the repeating events.
     */
    private static final String REPEATING_FILE_NAME_POST_FIX = "_repeating";
    
    /**
     * Creates an ID taking any quotes into account
     * @param tokenizer
     * @return 
     */
    private static String createID(StringTokenizer tokenizer, int lineNumber) {
        String ID = tokenizer.nextToken();
        ID = ID.trim();
        // check if the ID contains a quotation mark e.g. "SS2306,1"
        if (ID.startsWith("\"")) {
            ID += DELIMETER + tokenizer.nextToken();
            System.out.println("Quoted ID + '" + ID + "' found in row " + lineNumber);
            // add a literral \" to the ID; required for String.equals to work
            ID = StringUtils.replace(ID, "\"", "\\\"");
            System.out.println("Escaped ID + '" + ID + "'");
        }
        return ID;
    }
    
    private static List<DuplicateRecord> readDuplicates(List<String> lineList) {
        List<DuplicateRecord> ret = new ArrayList<DuplicateRecord>();        
        int i = 0;
        for (String line : lineList) {
            StringTokenizer tokenizer = new StringTokenizer(line, DELIMETER);
            String id = createID(tokenizer, i);
            DuplicateRecord searchRecord = new DuplicateRecord(id);
            if (ret.contains(searchRecord)) {
                int index = ret.indexOf(searchRecord);
                DuplicateRecord duplicate = ret.get(index);
                duplicate.incrementFrequency();
            }
            else {
                DuplicateRecord duplicate = new DuplicateRecord(id);
                ret.add(duplicate);
            }
            i++;
        }
        return ret;
    }
    
    private static DuplicateRecord retrieveRecordWithMaxFrequency(List<DuplicateRecord> aDuplcateList) {
        DuplicateRecord ret = null;
        int maximum = 0;
        for (DuplicateRecord duplicateRecord : aDuplcateList) {
            int frequency = duplicateRecord.getFrequency();
            if (frequency > maximum)  {
                maximum = frequency;
                ret = duplicateRecord;
            }
        }
        return ret;
    }

    /**
     * Adds a index to indicate a repeating event as first column in the output
     * @param hasHeaderLine indicator if the original CSV had a header line
     * @param aLineList the list of lines
     * @param aDuplicateList the list of duplicates
     */
    private static List<String> addRepeatingEvents(boolean hasHeaderLine,
                                           List<String> aLineList, 
                                           List<DuplicateRecord> aDuplicateList) {
        
        List<String> ret = new ArrayList<String>();
        String line = aLineList.get(0);
        int startIndex = 0;
        if (hasHeaderLine) {
            line = REPEATING_EVENT_HEADER + DELIMETER + line;
            ret.add(line);
            startIndex = 1;            
        }
        for (int i = startIndex; i < aLineList.size(); i++) {
            line = aLineList.get(i);
            StringTokenizer tokenizer = new StringTokenizer(line, DELIMETER);
            String ID = createID(tokenizer, i);            
            DuplicateRecord searchRecord = new DuplicateRecord(ID);            
            if (aDuplicateList.contains(searchRecord)) {
                DuplicateRecord dupRecord = 
                    aDuplicateList.get(aDuplicateList.indexOf(searchRecord));
                int repeatEventIndex = dupRecord.getNumberWritten();
                dupRecord.incrementNWritten();
                dupRecord.decrementFrequency();  
                if (dupRecord.getFrequency() < 0) {
                    // consistency/sanity check to see if everything is OK
                    throw new IllegalStateException("Frequency smaller than 0 for ID " + ID);
                }
                line = repeatEventIndex + DELIMETER + line;  
            }
            ret.add(line);
        }
        return ret;        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        if (args.length < 1) {
            System.err.println("Filename not given. Aborting");
            System.exit(0);
        }                       
        String fileName = args[0];
        try {
            List<String> lineList = FileUtils.readLinesFromFile(fileName);
            List<DuplicateRecord> duplicateList = readDuplicates(lineList);
            DuplicateRecord recordWithMaxFrequency = 
                retrieveRecordWithMaxFrequency(duplicateList);
            int maximalFrequency = recordWithMaxFrequency.getFrequency();
            System.out.println("Unique ID count : " + duplicateList.size() +
                    ",  maximal frequency " + maximalFrequency +
                    " for ID " + recordWithMaxFrequency.getDuplicateID());
            if (maximalFrequency > 1) {
                System.out.println("Duplicate events found, adding extra column for repeating events to new CSV-file");
                List<String> listWithRepeats = addRepeatingEvents(false, lineList, duplicateList);
                FileUtils.writeLinesToFile(FileUtils.stripFileNameExtension(fileName) + REPEATING_FILE_NAME_POST_FIX + ".csv", listWithRepeats);
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.err);
            System.err.println("Problem occured: " + ioe.getMessage());
            System.exit(2);
        }
        System.exit(0);
    }
}