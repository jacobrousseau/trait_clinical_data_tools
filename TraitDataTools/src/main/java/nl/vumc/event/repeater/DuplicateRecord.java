/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

/**
 *
 * @author j.rousseau
 */
public class DuplicateRecord {

    private String duplicateID;
    private int frequency;
    private int numberWritten;

    public int getNumberWritten() {
        return numberWritten;
    }

    public DuplicateRecord(String aDuplicateID) {
        duplicateID = aDuplicateID;
        frequency = 1;
        numberWritten = 1;
    }

    public void incrementNWritten() {
        numberWritten += 1;
    }

    public void incrementFrequency() {
        frequency += 1;
    }

    public void decrementFrequency() {
        frequency -= 1;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getDuplicateID() {
        return duplicateID;
    }

    @Override
    public String toString() {
        return duplicateID + "." + frequency;
    }

    @Override
    public int hashCode() {
        return duplicateID.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DuplicateRecord) == false) {
            return false;
        }
        DuplicateRecord that = (DuplicateRecord) other;
        if (this.duplicateID != null) {
            return this.duplicateID.equals(that.duplicateID);
        }
        return false;
    }
}
