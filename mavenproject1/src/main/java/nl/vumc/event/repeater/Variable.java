/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

import org.apache.commons.lang.StringUtils;

/**
 * Variable for the PHAROS study which contains the name, the groupID and
 * eventID.
 *
 * @author j.rousseau
 */
public class Variable {

    private String name;
    private String groupID;
    private String eventID;
    private String crfVersionID;
    private String originalName;
    private String value;
    private VariableType type;

    public Variable(String anOriginalName) {
        originalName = anOriginalName.trim();
        eventID = createID(originalName, "E");
        groupID = createID(originalName, "G");
        crfVersionID = createID(originalName, "C");
        name = anOriginalName.trim();
        if (!StringUtils.isBlank(eventID)) {
            name = StringUtils.remove(name, "_" + eventID);
        }
        if (!StringUtils.isBlank(groupID)) {
            name = StringUtils.remove(name, "_" + groupID);
        }
        if (!StringUtils.isBlank(crfVersionID)) {
            name = StringUtils.remove(name, "_" + crfVersionID);
        }
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name can not be parsed from columnname '" + originalName + "'");
        }
        if (hasRepeats()) {
            type = VariableType.REPEATING;
        } else {
            type = VariableType.NON_REPEATING;
        }
    }

    private String createID(String rawColumnName, String anItem) {
        String index = parseIndexedItem(rawColumnName, anItem);
        if (!"".equals(index)) {
            return anItem + index;
        }
        return "";
    }

    public String createPrefix(String delimiter) {
        StringBuilder ret = new StringBuilder();
        if (!StringUtils.isBlank(eventID)) {
            ret.append(eventID);
        }
        ret.append(delimiter);
        if (!StringUtils.isBlank(groupID)) {
            ret.append(groupID);
        }
        ret.append(delimiter);
        if (!StringUtils.isBlank(crfVersionID)) {
            ret.append(crfVersionID);
        }
        ret.append(delimiter);
        return ret.toString();
    }

    private String parseIndexedItem(String aRawColumnName, String anItem) {
        int index = StringUtils.lastIndexOf(aRawColumnName, "_" + anItem);
        // not 0 to avoid hitting strings like '_E2'
        if (index >= 1) {
            String rest = aRawColumnName.substring(index + 2);
            index = StringUtils.indexOf(rest, "_", 0);
            if (index >= 0) {
                rest = rest.substring(0, index);
            }
            if (StringUtils.containsOnly(rest, "0123456789")) {
                return rest;
            }
        }
        return "";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String aValue) {
        this.value = aValue;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getName() {
        return name;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getEventID() {
        return eventID;
    }

    public VariableType getType() {
        return type;
    }

    public void setType(VariableType type) {
        this.type = type;
    }

    public String getCrfVersionID() {
        return crfVersionID;
    }

    /**
     * Returns the repeating elements of the variable; a combination of the
     * groupID, eventID and crfVersionID. If no repeating elements are present
     * then an blank string is returned.
     *
     * @return
     */
    public String getRepeatingElements() {
        String ret = "";
        if (!StringUtils.isBlank(eventID)) {
            ret += eventID + "_";
        }
        if (!StringUtils.isBlank(groupID)) {
            ret += groupID + "_";
        }
        if (!StringUtils.isBlank(crfVersionID)) {
            ret += crfVersionID + "_";
        }
        return StringUtils.removeEnd(ret, "_");
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.originalName != null ? this.originalName.hashCode() : 0);
        return hash;
    }

    /**
     * returns true if this variable has 1 or more repeating indices (event,
     * group or CRF-version).
     *
     * @return
     */
    private boolean hasRepeats() {
        return (!StringUtils.isBlank(eventID))
                || (!StringUtils.isBlank(groupID))
                || (!StringUtils.isBlank(crfVersionID));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Variable other = (Variable) obj;
        if ((this.originalName == null) ? (other.originalName != null) : !this.originalName.equals(other.originalName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return originalName + "|" + name + "|" + eventID + "|" + groupID + "|" + crfVersionID + "|";
    }
}
