/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

/**
 * Enumeration for the types of variables.
 *
 * @author j.rousseau
 */
public enum VariableType {

    /**
     * a fixed variable is always present; only the subjectID is fixed
     */
    FIXED,
    /**
     * an (independent) variable with a repeat
     */
    REPEATING,
    /**
     * an (independent) variable without a repeat
     */
    NON_REPEATING;
}
