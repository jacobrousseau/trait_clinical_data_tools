/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

/**
 * Main class which converts a PHAROS file containing the group and events
 * repeats in separate columns to a file with 2 extra columns called
 * <code>eventID</code> and
 * <code>groupID</code>.
 *
 * Usage:
 * <code>java -jar ColumnTransformer [FILE_NAME] [DELIMETER]</code>
 *
 * Note: to denote a tab use as [DELIMITER] the text 'TAB'
 *
 * @author j.rousseau
 */
public class ColumnTransformer {

    /**
     * The extention which is added to the newly created file which contains the
     * remapped columns.
     */
    private static final String OUTPUT_FILE_NAME_POST_FIX = "_transformed";
    private static String delimiter;

    /**
     *
     * @param inputString the current line of the input file
     * @param outputList the list for the output
     * @param variableList contains the list of variables
     * @param repeatingValuesList contains a list of string with all the availbe
     * repeats in the variable list e.g. E1G1, E2G1, E1G2
     */
    private static void transformLine(String inputString,
            int lineNumber,
            List<String> outputList,
            VariableList variableList) {
        String subjectID = "";
        String[] tokenAr = StringUtils.splitByWholeSeparatorPreserveAllTokens(inputString, delimiter);
        if (tokenAr.length != variableList.getVariables().size()) {
            throw new IllegalStateException("Number of tokens (" + tokenAr.length + ") in line number " + lineNumber + " does not match the number of headers (" + variableList.getVariables().size() + ").");
        }
        variableList.reset();
        for (int index = 0; index < tokenAr.length; index++) {
            String value = tokenAr[index];
            if (index == 0) {
                subjectID = value;
            } else {
                variableList.appendValue(value, index);
            }
        }
        variableList.addToOutput(subjectID, outputList);
    }

    private static void transform(List<String> lineList, List<String> outputList, VariableList variableList) {
        int count = 0;
        for (String line : lineList) {
            // skip the header line
            if (count > 0) {
                transformLine(line, count, outputList, variableList);
                System.out.println("Transformed line " + count
                        + " of " + lineList.size());
            }
            count++;
        }
    }

    private static void usage() {
        System.out.println("Converts the columns of OpenClinica TAB_delimited files to files which can be uploaded with OCWS by");
        System.out.println("creating columns containing the ItemGroupRepeatKey, StudyEventRepeatKey and CRF-version");
        System.out.println("Usage: java -jar ColumnTransformer [FILE_NAME] [DELIMITER]");
        System.out.println("");
        System.out.println("FILE_NAME the file name to convert. The converted file is called FILE_NAME" + OUTPUT_FILE_NAME_POST_FIX);
        System.out.println("DELIMTER the delimter used in the input file e.g. \t");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Wrong number of command-line parameters.");
            usage();
            System.exit(1);
        }
        String fileName = args[0];
        delimiter = args[1];
        if ("tab".equalsIgnoreCase(delimiter)) {
            delimiter = "\t";
        }
        System.out.println("Running file name " + fileName + ", delimiter '" + delimiter + "'");
        try {
            List<String> lineList = FileUtils.readLinesFromFile(fileName);
            VariableList variableList = new VariableList(lineList.get(0), delimiter);
            System.out.println("Found " + variableList.getVariables().size() + " variables:");
            List<String> outputList = new ArrayList<String>();
            outputList.add(variableList.createHeaderLineOutput());
            transform(lineList, outputList, variableList);
            String outputFileName = FileUtils.stripFileNameExtension(fileName);
            String outputFileNameExtension = StringUtils.substringAfterLast(fileName, ".");
            outputFileName = outputFileName + OUTPUT_FILE_NAME_POST_FIX + "." + outputFileNameExtension;
            System.out.println("Saving output to file: " + outputFileName);
            FileUtils.writeLinesToFile(outputFileName, outputList);
        } catch (IOException ioe) {
            System.err.println("Problem occured: " + ioe.getMessage());
            System.exit(2);
        }
    }
}
