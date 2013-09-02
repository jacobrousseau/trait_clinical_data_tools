/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;

/**
 * Is responsible for the creation of the variable list and various methods
 * associated with the transformation of the columns
 *
 * @author j.rousseau
 */
public class VariableList {

    /**
     * List of the variables contained in the input file
     */
    private List<Variable> variables;
    /**
     * List of all the headers which must be sent to output (subjectID,
     * repeating and non-repeating variables
     */
    private Set<String> headers;
    /**
     * List of the headers of only the repeating items
     */
    private Set<String> repeatingHeaders;
    /**
     * list of all the possible repeating combinations (e.g. E1_G4_C2 etc)
     */
    private List<String> repeatingItems;
    private int numberOfNonRepeatingVariables;
    /**
     * the input/output delimiter
     */
    private String delimiter;

    public VariableList(String columnHeaders, String delimiter) {
        this.delimiter = delimiter;
        createVariables(columnHeaders);
        createHeaderSet();
        determineNumberOfNonRepeatingVariables();
    }

    public void reset() {
        for (Variable var : variables) {
            var.setValue("");
        }
    }

    public void appendValue(String value, int atIndex) {
        Variable var = variables.get(atIndex);
        var.setValue(value);
    }

    public void addToOutput(String subjectID, List<String> transformedOutputList) {
        Variable var;
        for (String repeatingItem : repeatingItems) {
            StringBuilder output = new StringBuilder();
            boolean outputPresent = false;
            for (String outputName : repeatingHeaders) {
                String searchName = outputName + "_" + repeatingItem;
                Variable searchVariable = new Variable(searchName);
                int index = variables.indexOf(searchVariable);
                if (index >= 0) {
                    var = variables.get(index);
                    String value = var.getValue();
                    if (StringUtils.isNotBlank(value)) {
                        outputPresent = true;
                    }
                    output.append(value);
                }
                output.append(delimiter);
            }
            if (outputPresent) {
                // add the subjectID, event, group and/or CRF-version id
                // as a prefix to the output
                Variable prefixVar = new Variable("DUMMY_" + repeatingItem);
                String prefix = prefixVar.createPrefix(delimiter);
                prefix = subjectID + delimiter + prefix;
                output.insert(0, prefix);
                for (int j = 0; j < numberOfNonRepeatingVariables; j++) {
                    output.append(delimiter);
                }
                transformedOutputList.add(StringUtils.removeEnd(output.toString(), delimiter));
            }
        }
        // now the non-repeating items
        if (numberOfNonRepeatingVariables > 0) {
            String nonRepeatingOutput = createNonRepeatingOutputLine(subjectID);
            transformedOutputList.add(nonRepeatingOutput);
        }
    }

    public int getNumberOfNonRepeatingVariables() {
        return numberOfNonRepeatingVariables;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public Set<String> getHeaders() {
        return headers;
    }

    public List<String> getRepeatingItems() {
        return repeatingItems;
    }

    public String createHeaderLineOutput() {
        StringBuilder ret = new StringBuilder();
        for (String header : headers) {
            ret.append(header);
            ret.append(delimiter);
        }
        return StringUtils.removeEnd(ret.toString(), delimiter);
    }

    private String createNonRepeatingOutputLine(String subjectID) {
        StringBuilder ret = new StringBuilder();
        ret.append(subjectID);
        ret.append(delimiter);
        // first add empty values for all the repeating items.
        for (int i = 0; i < (headers.size() - numberOfNonRepeatingVariables - 1); i++) {
            ret.append(delimiter);
        }
        for (Variable var : variables) {
            if (var.getType() == VariableType.NON_REPEATING) {
                ret.append(var.getValue());
                ret.append(delimiter);
            }
        }
        return StringUtils.removeEnd(ret.toString(), delimiter);
    }

    private void determineNumberOfNonRepeatingVariables() {
        numberOfNonRepeatingVariables = 0;
        for (Variable var : variables) {
            if (var.getType() == VariableType.NON_REPEATING) {
                numberOfNonRepeatingVariables++;
            }
        }
    }

    private void createVariables(String columnHeaders) {
        variables = new ArrayList<Variable>();
        StringTokenizer tokenizer = new StringTokenizer(columnHeaders, delimiter);
        while (tokenizer.hasMoreTokens()) {
            String rawColumnName = tokenizer.nextToken();
            determineVariable(rawColumnName);
        }
        createRepeatingItemLists();
    }

    /**
     * Creates (from the {@link #variables}) a list of the combinations present
     * of the variables groupID, eventID and crfVersionID. Must be run after the
     * variables have been created
     */
    private void createRepeatingItemLists() {
        Set<String> uniqueSet = new HashSet<String>();
        for (Variable var : variables) {
            String repeatingElement = var.getRepeatingElements();
            if (!StringUtils.isBlank(repeatingElement)) {
                uniqueSet.add(repeatingElement);

            }
        }
        repeatingItems = new ArrayList(uniqueSet);

    }

    /**
     * Returns a variable name
     *
     * @param rawColumnName
     * @param varList
     * @return
     */
    private void determineVariable(String rawColumnName) {
        String name = rawColumnName.trim();
        Variable var = new Variable(name);
        if (variables.isEmpty()) {
            // this is the fixed subjectID
            var.setType(VariableType.FIXED);
        } else {
            if (!StringUtils.isBlank(var.getRepeatingElements())) {
                var.setType(VariableType.REPEATING);
            } else {
                var.setType(VariableType.NON_REPEATING);
            }
        }
        variables.add(var);
    }

    private void createHeaderSet() {
        Variable idVariable = variables.get(0);
        headers = new LinkedHashSet<String>();
        repeatingHeaders = new LinkedHashSet<String>();
        headers.add(idVariable.getName());
        headers.add("EVENT_INDEX");
        headers.add("GROUP_INDEX");
        headers.add("CRF_INDEX");
        for (Variable variable : variables) {
            if (VariableType.REPEATING == variable.getType()) {
                String variableName = variable.getName();
                headers.add(variableName);
                repeatingHeaders.add(variableName);
            }
        }
        for (Variable variable : variables) {
            if (VariableType.NON_REPEATING == variable.getType()) {
                String variableName = variable.getName();
                headers.add(variableName);
            }
        }
    }
}
