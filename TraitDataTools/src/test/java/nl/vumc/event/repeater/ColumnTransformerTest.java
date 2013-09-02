/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vumc.event.repeater;

import java.util.List;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
//import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static junit.framework.Assert.*;

/**
 *
 * @author jacob
 */
//@RunWith(JUnit4.class)
public class ColumnTransformerTest {

    private static final String DLIM = "\t";
//    @Rule
//    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    public ColumnTransformerTest() {
    }

    @Test
    public void testNoArgs() throws Exception {
        //      exit.expectSystemExitWithStatus(1);
        String[] args = new String[]{};
        ColumnTransformer.main(args);
    }

    @Test
    public void testInvalidFileName() throws Exception {
//        exit.expectSystemExitWithStatus(2);
        String[] args = new String[]{"./src/test/csv-files/non-existing.csv", ";"};
        ColumnTransformer.main(args);
    }

    /**
     * Test of main method, of class ColumnTransformer.
     */
    //@Test
    public void testMain() throws Exception {
        //String fileName = "./src/test/csv-files/test_repeats.csv";
        //String fileName = "C://_home//mirth//data//pharos//temp//PharosNHL_BL_MID_OC_curated.dat";
        String fileName = "C://_home//mirth//data//pharos//temp//PharosNHL_OPN_OC.dat";
        String[] args = new String[]{fileName, "TAB"};
        ColumnTransformer.main(args);
        /*
         String outputFileName = "./src/test/csv-files/test_repeats_transformed.csv";
         List<String> stringList = FileUtils.readLinesFromFile(outputFileName);

         assertEquals("SubjectID" + DLIM + "EVENT_INDEX" + DLIM + "GROUP_INDEX" + DLIM + "CRF_INDEX" + DLIM + "VAR01" + DLIM + "VAR02" + DLIM + "VAR03" + DLIM + "VAR_NON_REPEATING" + DLIM + "VAR_NON_REP_TWO", stringList.get(0));
         assertEquals("subject001" + DLIM + "E1" + DLIM + DLIM + DLIM + DLIM + DLIM + "11" + DLIM + DLIM, stringList.get(1));
         assertEquals("subject001" + DLIM + "E1" + DLIM + DLIM + "C2" + DLIM + "EE" + DLIM + "FF" + DLIM + DLIM + DLIM, stringList.get(2));
         assertEquals("subject001" + DLIM + "E1" + DLIM + DLIM + "C1" + DLIM + "AA" + DLIM + "BB" + DLIM + DLIM + DLIM, stringList.get(3));
         assertEquals("subject001" + DLIM + "E2" + DLIM + DLIM + "C1" + DLIM + "CC" + DLIM + "DD" + DLIM + DLIM + DLIM, stringList.get(4));
         assertEquals("subject001" + DLIM + "E2" + DLIM + DLIM + "C2" + DLIM + "GG" + DLIM + "HH" + DLIM + DLIM + DLIM, stringList.get(5));
         assertEquals("subject001" + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + "0A" + DLIM + "_0A", stringList.get(6));

         assertEquals("subject002" + DLIM + "E1" + DLIM + DLIM + DLIM + DLIM + DLIM + "22" + DLIM + DLIM, stringList.get(7));
         assertEquals("subject002" + DLIM + "E1" + DLIM + DLIM + "C2" + DLIM + "MM" + DLIM + "NN" + DLIM + DLIM + DLIM, stringList.get(8));
         assertEquals("subject002" + DLIM + "E1" + DLIM + DLIM + "C1" + DLIM + "II" + DLIM + "JJ" + DLIM + DLIM + DLIM, stringList.get(9));
         assertEquals("subject002" + DLIM + "E2" + DLIM + DLIM + "C1" + DLIM + "KK" + DLIM + "LL" + DLIM + DLIM + DLIM, stringList.get(10));
         assertEquals("subject002" + DLIM + "E2" + DLIM + DLIM + "C2" + DLIM + "OO" + DLIM + "PP" + DLIM + DLIM + DLIM, stringList.get(11));
         assertEquals("subject002" + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + "OB" + DLIM + "_0B", stringList.get(12));

         assertEquals("subject003" + DLIM + "E1" + DLIM + DLIM + DLIM + DLIM + DLIM + "33" + DLIM + DLIM, stringList.get(13));
         assertEquals("subject003" + DLIM + "E1" + DLIM + DLIM + "C2" + DLIM + "QQ" + DLIM + "RR" + DLIM + DLIM + DLIM, stringList.get(14));
         assertEquals("subject003" + DLIM + "E2" + DLIM + DLIM + "C2" + DLIM + "SS" + DLIM + "TT" + DLIM + DLIM + DLIM, stringList.get(15));
         assertEquals("subject003" + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + "OC" + DLIM + "_0C", stringList.get(16));

         assertEquals("subject004" + DLIM + "E1" + DLIM + DLIM + DLIM + DLIM + DLIM + "44" + DLIM + DLIM, stringList.get(17));
         assertEquals("subject004" + DLIM + "E1" + DLIM + DLIM + "C1" + DLIM + "UU" + DLIM + "VV" + DLIM + DLIM + DLIM, stringList.get(18));
         assertEquals("subject004" + DLIM + "E2" + DLIM + DLIM + "C1" + DLIM + "WW" + DLIM + "XX" + DLIM + DLIM + DLIM, stringList.get(19));
         assertEquals("subject004" + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + "OD" + DLIM + "_0D", stringList.get(20));

         assertEquals("subject005" + DLIM + "E1" + DLIM + DLIM + DLIM + DLIM + DLIM + "55" + DLIM + DLIM, stringList.get(21));
         assertEquals("subject005" + DLIM + "E1" + DLIM + DLIM + "C1" + DLIM + DLIM + DLIM + "YY" + DLIM + DLIM + DLIM, stringList.get(22));
         assertEquals("subject005" + DLIM + "E2" + DLIM + DLIM + "C1" + DLIM + DLIM + "ZZ" + DLIM + DLIM + DLIM + DLIM, stringList.get(23));
         assertEquals("subject005" + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + "OE" + DLIM + "_0E", stringList.get(24));

         assertEquals("subject006" + DLIM + "E1" + DLIM + DLIM + DLIM + DLIM + DLIM + "66" + DLIM + DLIM, stringList.get(25));
         assertEquals("subject006" + DLIM + "E1" + DLIM + DLIM + "C1" + DLIM + "AAA" + DLIM + DLIM + DLIM, stringList.get(26));
         assertEquals("subject006" + DLIM + "E2" + DLIM + DLIM + "C1" + DLIM + DLIM + "BBB" + DLIM + DLIM, stringList.get(27));
         assertEquals("subject006" + DLIM + DLIM + DLIM + DLIM + DLIM + DLIM + "OF" + DLIM + "_0F", stringList.get(28));
         */
    }
}
