/*
 * This class is an auto-generated source file for a HAPI
 * HL7 v2.x standard structure class.
 *
 * For more information, visit: http://hl7api.sourceforge.net/
 * 
 * The contents of this file are subject to the Mozilla Public License Version 1.1 
 * (the "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/ 
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the 
 * specific language governing rights and limitations under the License. 
 * 
 * The Original Code is "[file_name]".  Description: 
 * "[one_line_description]" 
 * 
 * The Initial Developer of the Original Code is University Health Network. Copyright (C) 
 * 2012.  All Rights Reserved. 
 * 
 * Contributor(s): ______________________________________. 
 * 
 * Alternatively, the contents of this file may be used under the terms of the 
 * GNU General Public License (the  "GPL"), in which case the provisions of the GPL are 
 * applicable instead of those above.  If you wish to allow use of your version of this 
 * file only under the terms of the GPL and not to allow others to use your version 
 * of this file under the MPL, indicate your decision by deleting  the provisions above 
 * and replace  them with the notice and other provisions required by the GPL License.  
 * If you do not delete the provisions above, a recipient may use your version of 
 * this file under either the MPL or the GPL. 
 * 
 */


package org.petermac.hl7.model.v251.group;

import org.petermac.hl7.model.v251.segment.*;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.model.*;

/**
 * <p>Represents a CSU_C09_STUDY_OBSERVATION group structure (a Group object).
 * A Group is an ordered collection of message segments that can repeat together or be optionally in/excluded together.
 * This Group contains the following elements:  
 * </p>
 * <ul>
                         * <li>1: ORC (Common Order) <b>optional  </b></li>
                         * <li>2: OBR (Observation Request) <b>  </b></li>
                         * <li>3: CSU_C09_TIMING_QTY (a Group object) <b>optional repeating </b></li>
                         * <li>4: OBX (Observation/Result) <b> repeating </b></li>
 * </ul>
 */
//@SuppressWarnings("unused")
public class CSU_C09_STUDY_OBSERVATION extends AbstractGroup {

    /** 
     * Creates a new CSU_C09_STUDY_OBSERVATION group
     */
    public CSU_C09_STUDY_OBSERVATION(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(ORC.class, false, false, false);
                                  this.add(OBR.class, true, false, false);
                                  this.add(CSU_C09_TIMING_QTY.class, false, true, false);
                                  this.add(OBX.class, true, true, false);
       } catch(HL7Exception e) {
          log.error("Unexpected error creating CSU_C09_STUDY_OBSERVATION - this is probably a bug in the source code generator.", e);
       }
    }

    /** 
     * Returns "2.5.1"
     */
    public String getVersion() {
       return "2.5.1";
    }



    /**
     * Returns
     * ORC (Common Order) - creates it if necessary
     */
    public ORC getORC() { 
       ORC retVal = getTyped("ORC", ORC.class);
       return retVal;
    }




    /**
     * Returns
     * OBR (Observation Request) - creates it if necessary
     */
    public OBR getOBR() { 
       OBR retVal = getTyped("OBR", OBR.class);
       return retVal;
    }




    /**
     * Returns
     * the first repetition of 
     * TIMING_QTY (a Group object) - creates it if necessary
     */
    public CSU_C09_TIMING_QTY getTIMING_QTY() { 
       CSU_C09_TIMING_QTY retVal = getTyped("TIMING_QTY", CSU_C09_TIMING_QTY.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * TIMING_QTY (a Group object) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public CSU_C09_TIMING_QTY getTIMING_QTY(int rep) { 
       CSU_C09_TIMING_QTY retVal = getTyped("TIMING_QTY", rep, CSU_C09_TIMING_QTY.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of TIMING_QTY 
     */ 
    public int getTIMING_QTYReps() {  
        return getReps("TIMING_QTY");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of TIMING_QTY.
     * <p>
     * <p>
     * Note that unlike {@link #getTIMING_QTY()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<CSU_C09_TIMING_QTY> getTIMING_QTYAll() throws HL7Exception {
    	return getAllAsList("TIMING_QTY", CSU_C09_TIMING_QTY.class);
    } 

    /**
     * Inserts a specific repetition of TIMING_QTY (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertTIMING_QTY(CSU_C09_TIMING_QTY structure, int rep) throws HL7Exception { 
       super.insertRepetition("TIMING_QTY", structure, rep);
    }


    /**
     * Inserts a specific repetition of TIMING_QTY (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public CSU_C09_TIMING_QTY insertTIMING_QTY(int rep) throws HL7Exception { 
       return (CSU_C09_TIMING_QTY)super.insertRepetition("TIMING_QTY", rep);
    }


    /**
     * Removes a specific repetition of TIMING_QTY (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public CSU_C09_TIMING_QTY removeTIMING_QTY(int rep) throws HL7Exception { 
       return (CSU_C09_TIMING_QTY)super.removeRepetition("TIMING_QTY", rep);
    }



    /**
     * Returns
     * the first repetition of 
     * OBX (Observation/Result) - creates it if necessary
     */
    public OBX getOBX() { 
       OBX retVal = getTyped("OBX", OBX.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * OBX (Observation/Result) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public OBX getOBX(int rep) { 
       OBX retVal = getTyped("OBX", rep, OBX.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of OBX 
     */ 
    public int getOBXReps() {  
        return getReps("OBX");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of OBX.
     * <p>
     * <p>
     * Note that unlike {@link #getOBX()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<OBX> getOBXAll() throws HL7Exception {
    	return getAllAsList("OBX", OBX.class);
    } 

    /**
     * Inserts a specific repetition of OBX (Observation/Result)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertOBX(OBX structure, int rep) throws HL7Exception { 
       super.insertRepetition("OBX", structure, rep);
    }


    /**
     * Inserts a specific repetition of OBX (Observation/Result)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public OBX insertOBX(int rep) throws HL7Exception { 
       return (OBX)super.insertRepetition("OBX", rep);
    }


    /**
     * Removes a specific repetition of OBX (Observation/Result)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public OBX removeOBX(int rep) throws HL7Exception { 
       return (OBX)super.removeRepetition("OBX", rep);
    }



}

