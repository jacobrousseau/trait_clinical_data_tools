package nl.vumc.event.repeater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;

/**
 * Converts a file containing a event-, group- and/or crf-repeats to the format
 * used by Cuneyt Parlayan's DataImporter:
 *
 * [VAR_NAME]_En_Gn_Cn
 *
 * Built for the upload of the Pharos-MM (Multiple Myeloma) upload to
 * OpenClinica
 *
 * @author j.rousseau
 */
public class DataImporterTransformer {

    private static final String SUBJECT_ID = "PharosNr";
    private static final String SITE_ID = "SiteID";
    private static final String EVENT_INDEX = "Event_Index";
    private static final String GROUP_INDEX = "Group_Index";
    private static final String CRF_INDEX = "Crf_Index";
    private static final List<String> RESERVED_COLUMN_NAMES = new ArrayList<String>();

    static {
        RESERVED_COLUMN_NAMES.add(SUBJECT_ID);
        RESERVED_COLUMN_NAMES.add(SITE_ID);
        RESERVED_COLUMN_NAMES.add(EVENT_INDEX);
        RESERVED_COLUMN_NAMES.add(GROUP_INDEX);
        RESERVED_COLUMN_NAMES.add(CRF_INDEX);
    }
    /**
     * The extention which is added to the newly created file which contains the
     * remapped columns.
     */
    private static final String OUTPUT_FILE_NAME_POST_FIX = "_transformed";
    private static String delimiter;
    private static ArrayList<ArrayList<String>> inputGrid = new ArrayList<ArrayList<String>>();
    private static ArrayList<ArrayList<String>> outputGrid = new ArrayList<ArrayList<String>>();
    private static List<String> variableList = new ArrayList<String>();
    private static List<SubjectRecord> subjectList = new ArrayList<SubjectRecord>();

    /**
     * Display the usage (command-line parameters)
     */
    private static void usage() {
        System.out.println("Converts the columns of a TAB_delimited files to files which can be processed by");
        System.out.println("the DataImporter tool developed by the Dept. of Pathology of the VUmc; by Cuneyt Parlayan");
        System.out.println("Usage: java -jar DataImporterTransformer [FILE_NAME] [DELIMITER]");
        System.out.println("");
        System.out.println("FILE_NAME the file name to convert. The converted file is called FILE_NAME" + OUTPUT_FILE_NAME_POST_FIX);
        System.out.println("DELIMTER the delimter used in the input file. Note tabs are denoted by the string 'tab'");
    }

    private static void readVariableNames(String headerString) {
        StringTokenizer tokenizer = new StringTokenizer(headerString, delimiter, false);
        while (tokenizer.hasMoreTokens()) {
            String variableName = tokenizer.nextToken();
            variableList.add(variableName);
        }
    }

    private static void fillInputGrid(List<String> lineList) {
        // start from 1, skipping the header
        for (int i = 1; i < lineList.size(); i++) {
            String input = lineList.get(i);
            ArrayList<String> line = new ArrayList<String>();
            StringTokenizer tokenizer = new StringTokenizer(input, delimiter, false);
            while (tokenizer.hasMoreTokens()) {
                String variableName = tokenizer.nextToken();
                line.add(variableName);
            }
            inputGrid.add(line);
        }
    }

    private static ArrayList<String> createOutputHeader() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add(SUBJECT_ID + delimiter);
        ret.add(SITE_ID + delimiter);
        int maxGroupRepeats = determineMaxRepeat(RepeatType.GROUP);
        int maxEventRepeats = determineMaxRepeat(RepeatType.EVENT);

        for (int event = 0; event < maxEventRepeats; event++) {
            for (int group = 0; group < maxGroupRepeats; group++) {
                for (String header : variableList) {
                    if (!RESERVED_COLUMN_NAMES.contains(header)) {
                        String outputHeader = header + "_E" + (event + 1) + "_G" + (group + 1);
                        ret.add(outputHeader + delimiter);
                    }
                }
            }
        }
        return ret;
    }

    private static void createSubjectRecordList() {
        subjectList.clear();
        int eventColumnIndex = variableList.indexOf(EVENT_INDEX);
        int groupColumnIndex = variableList.indexOf(GROUP_INDEX);
        int siteIDColumnIndex = variableList.indexOf(SITE_ID);
        int subjectIDColumnIndex = variableList.indexOf(SUBJECT_ID);
        for (int i = 0; i < inputGrid.size(); i++) {
            ArrayList<String> line = inputGrid.get(i);
            String subjectId = line.get(subjectIDColumnIndex);
            String siteID = line.get(siteIDColumnIndex);
            Integer event = Integer.valueOf(line.get(eventColumnIndex));
            Integer group = Integer.valueOf(line.get(groupColumnIndex));
            SubjectRecord searchRec = new SubjectRecord(subjectId, siteID);
            if (!subjectList.contains(searchRec)) {
                subjectList.add(searchRec);
            } else {
                int index = subjectList.indexOf(searchRec);
                searchRec = subjectList.get(index);
            }
            searchRec.addLine(line);
            searchRec.incrementMaxRepeat(event, RepeatType.EVENT);
            searchRec.incrementMaxRepeat(group, RepeatType.GROUP);
        }
    }

    /**
     * returns the index of the first column containing data. The assumption is
     * that the following order allways is present:
     * <pre>
     * SUBJECT_ID (required)
     * SITE_ID (required)
     * EVENT_ID (optional)
     * GROUP_ID (optional)
     * CRF_ID (optional)
     * </pre>
     */
    private static int determineFirstDataColumnIndex() {
        for (int i = RESERVED_COLUMN_NAMES.size() - 1; i >= 0; i--) {
            int index = variableList.indexOf(RESERVED_COLUMN_NAMES.get(i));
            if (index >= 0) {
                return index + 1;
            }
        }
        throw new IllegalArgumentException("Data does not contain one of the required column names: SUBJECT_ID (required), SITE_ID (required), EVENT_ID (optional), GROUP_ID (optional), CRF_ID (optional)");
    }

    private static void addEmptyStrings(List<String> aLine, int aNumber) {
        for (int i = 0; i < aNumber; i++) {
            aLine.add(delimiter);
        }
    }

    private static void createOutput() {
        ArrayList<String> outputHeader = createOutputHeader();
        outputGrid.add(outputHeader);
        int firstDataColumn = determineFirstDataColumnIndex();

        int maxGroupRepeats = determineMaxRepeat(RepeatType.GROUP);
        int maxEventRepeats = determineMaxRepeat(RepeatType.EVENT);

        int eventColumnIndex = variableList.indexOf(EVENT_INDEX);
        int groupColumnIndex = variableList.indexOf(GROUP_INDEX);

        for (SubjectRecord subject : subjectList) {
            ArrayList<String> outputLine = new ArrayList<String>();
            outputLine.add(subject.getId() + delimiter);
            outputLine.add(subject.getSiteId() + delimiter);
            for (int event = 0; event < maxEventRepeats; event++) {
                for (int group = 0; group < maxGroupRepeats; group++) {
                    List<String> line = subject.getLineForEventGroup(eventColumnIndex, event + 1, groupColumnIndex, group + 1);
                    if (line == null) {
                        addEmptyStrings(outputLine, variableList.size() - firstDataColumn);
                    } else {
                        for (int columnNumber = firstDataColumn; columnNumber < variableList.size(); columnNumber++) {
                            outputLine.add(line.get(columnNumber) + delimiter);
                        }
                    }
                }
            }
            outputGrid.add(outputLine);
        }
    }

    private static int determineMaxRepeat(RepeatType repeatType) {
        int ret = 0;
        for (SubjectRecord subject : subjectList) {
            int value = subject.getMaxRepeat(repeatType);
            ret = Math.max(ret, value);
        }
        return ret;
    }

    private static void writeOutput(String aFileName) throws IOException {
        String outputFileName = FileUtils.stripFileNameExtension(aFileName);
        String outputFileNameExtension = StringUtils.substringAfterLast(aFileName, ".");
        outputFileName = outputFileName + OUTPUT_FILE_NAME_POST_FIX + "." + outputFileNameExtension;
        System.out.println("Saving output to file: " + outputFileName);
        List<String> outputList = new ArrayList<String>();
        for (ArrayList<String> line : outputGrid) {
            StringBuilder output = new StringBuilder(0xFFFF);
            for (String cell : line) {
                output.append(cell);
            }
            outputList.add(output.toString());
        }
        FileUtils.writeLinesToFile(outputFileName, outputList);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("DataImporter converter");
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
            readVariableNames(lineList.get(0));
            fillInputGrid(lineList);
            createSubjectRecordList();
            createOutput();
            writeOutput(fileName);
        } catch (IOException ioe) {
            System.err.println("Problem occured: " + ioe.getMessage());
            System.exit(2);
        }
    }
}
