/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author j.rousseau
 */
public class VariableTest {

    @Test
    public void testGetOriginalName() {
        Variable instance = new Variable("expectedOriginalName");
        assertEquals("expectedOriginalName", instance.getOriginalName());
    }

    /**
     * Test of getName method, of class Variable.
     */
    @Test
    public void testGetName() {
        Variable instance = new Variable("expectedName_E1_G2_C3");
        assertEquals("expectedName", instance.getName());
    }

    /**
     * Test of getGroupID method, of class Variable.
     */
    @Test
    public void testGetGroupID() {
        Variable instance = new Variable("var_G43");
        assertEquals("G43", instance.getGroupID());
    }

    /**
     * Test of getEventID method, of class Variable.
     */
    @Test
    public void testGetEventID() {
        Variable instance = new Variable("bla_E1854");
        assertEquals("E1854", instance.getEventID());
    }

    /**
     * Test of getCrfVersionID method, of class Variable.
     */
    @Test
    public void testGetCrfVersionID() {
        Variable instance = new Variable("appelflap_C9121");
        assertEquals("C9121", instance.getCrfVersionID());
    }

    /**
     * Test of hasRepeats method, of class Variable.
     */
    @Test
    public void testHasRepeatingIndices() {
        Variable instance = new Variable("var_E1_G2_C3");
        assertEquals(true, instance.getType() == VariableType.REPEATING);
        instance = new Variable("var");
        assertEquals(true, instance.getType() == VariableType.NON_REPEATING);
    }

    @Test
    public void testTrailingNumberInName() {
        Variable instance = new Variable("BEH_Gkbehan_3_E2");
        assertEquals(instance.getRepeatingElements(), "E2");
    }

    @Test
    public void testGetRepeatingElements() {
        Variable instance = new Variable("bla_E2_G1_C1");
        assertEquals(instance.getRepeatingElements(), "E2_G1_C1");

        instance = new Variable("bla_G31_C5");
        assertEquals(instance.getRepeatingElements(), "G31_C5");

        instance = new Variable("bla_E4_C5");
        assertEquals(instance.getRepeatingElements(), "E4_C5");
    }

    @Test
    public void testBuildVariableNoItems() {
        String rawColumnName = "hba1";
        Variable result = new Variable(rawColumnName);
        assertEquals("", result.getOriginalName(), "hba1");
        assertEquals("", result.getName(), "hba1");
        assertEquals("", result.getEventID(), "");
        assertEquals("", result.getGroupID(), "");
        assertEquals("", result.getCrfVersionID(), "");
        assertEquals(true, VariableType.NON_REPEATING == result.getType());
    }

    @Test
    public void testBuildVariableOnlyEvent() {
        String rawColumnName = "hba1_E54";
        Variable result = new Variable(rawColumnName);
        assertEquals("", result.getOriginalName(), "hba1_E54");
        assertEquals("", result.getName(), "hba1");
        assertEquals("", result.getEventID(), "E54");
        assertEquals("", result.getGroupID(), "");
        assertEquals("", result.getCrfVersionID(), "");
        assertEquals(true, VariableType.REPEATING == result.getType());
    }

    @Test
    public void testBuildVariableEventAndGroup() {
        String rawColumnName = "hba1_E54_G4";
        Variable result = new Variable(rawColumnName);
        assertEquals("", result.getOriginalName(), "hba1_E54_G4");
        assertEquals("", result.getName(), "hba1");
        assertEquals("", result.getEventID(), "E54");
        assertEquals("", result.getGroupID(), "G4");
        assertEquals("", result.getCrfVersionID(), "");
        assertEquals(true, VariableType.REPEATING == result.getType());
    }

    @Test
    public void testBuildVariableEventAndCRFVersionID() {
        String rawColumnName = "hba1_E54_C497";
        Variable result = new Variable(rawColumnName);
        assertEquals("", result.getOriginalName(), "hba1_E54_C497");
        assertEquals("", result.getName(), "hba1");
        assertEquals("", result.getEventID(), "E54");
        assertEquals("", result.getGroupID(), "");
        assertEquals("", result.getCrfVersionID(), "C497");
        assertEquals(true, VariableType.REPEATING == result.getType());
    }

    @Test
    public void testEventGroupAndCRFVersion() {
        String rawColumnName = "E1VARNAME_E66_G12_C497";
        Variable result = new Variable(rawColumnName);
        assertEquals("E1VARNAME", result.getName());
        assertEquals("E66", result.getEventID());
        assertEquals("G12", result.getGroupID());
        assertEquals("C497", result.getCrfVersionID());
        assertEquals(true, VariableType.REPEATING == result.getType());
    }

    @Test
    public void testInvalidName() {
        String rawColumnName = "_E1AAAAAAA";
        Variable result = new Variable(rawColumnName);
        assertEquals("_E1AAAAAAA", result.getName());
    }
}
