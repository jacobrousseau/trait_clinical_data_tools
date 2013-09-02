/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper object to record the subject ID and the maximal number of group- and
 * event-repeats.
 *
 * @author j.rousseau
 */
public class SubjectRecord {

    private String id;
    private String siteId;
    private int maxEvent;
    private int maxGroup;
    private int maxCRF;
    /**
     * Contains the line of an input file which belong to this subject.
     */
    private ArrayList<ArrayList<String>> inputLineNumbers = new ArrayList<ArrayList<String>>();

    public SubjectRecord(String anID, String aSiteID) {
        id = anID;
        siteId = aSiteID;
    }

    public void addLine(ArrayList<String> aLine) {
        inputLineNumbers.add(aLine);
    }

    public ArrayList<String> getLineForEventGroup(int anEventColumnIndex, int anEvent,
            int aGroupColumnIndex, int aGroup) {
        for (ArrayList<String> line : inputLineNumbers) {
            String event = line.get(anEventColumnIndex);
            String group = line.get(aGroupColumnIndex);
            if ((Integer.valueOf(event) == anEvent)
                    && (Integer.valueOf(group) == aGroup)) {
                return line;
            }
        }
        return null;
    }

    public void incrementMaxRepeat(int aValue, RepeatType aRepeatType) {
        if (RepeatType.EVENT == aRepeatType) {
            maxEvent = Math.max(aValue, maxEvent);
        }
        if (RepeatType.GROUP == aRepeatType) {
            maxGroup = Math.max(aValue, maxGroup);
        }
        if (RepeatType.CRF == aRepeatType) {
            maxCRF = Math.max(aValue, maxCRF);
        }
    }

    public int getMaxRepeat(RepeatType aRepeatType) {
        if (RepeatType.EVENT == aRepeatType) {
            return maxEvent;
        }
        if (RepeatType.GROUP == aRepeatType) {
            return maxGroup;
        }
        if (RepeatType.CRF == aRepeatType) {
            return maxCRF;
        }
        throw new IllegalArgumentException("No max defined for repeat type " + aRepeatType);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object aThat) {
        if (this == aThat) {
            return true;
        }
        if (!(aThat instanceof SubjectRecord)) {
            return false;
        }
        SubjectRecord that = (SubjectRecord) aThat;
        return this.getId() == null ? that.getId() == null : getId().equals(that.getId());
    }

    /**
     * @return the siteId
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
}
