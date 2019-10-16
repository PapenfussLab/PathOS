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
 * <p>Represents a RSP_Z86_COMMON_ORDER group structure (a Group object).
 * A Group is an ordered collection of message segments that can repeat together or be optionally in/excluded together.
 * This Group contains the following elements:  
 * </p>
 * <ul>
                         * <li>1: ORC (Common Order) <b>  </b></li>
                         * <li>2: RSP_Z86_TIMING (a Group object) <b>optional repeating </b></li>
                         * <li>3: RSP_Z86_ORDER_DETAIL (a Group object) <b>optional  </b></li>
                         * <li>4: RSP_Z86_ENCODED_ORDER (a Group object) <b>optional  </b></li>
                         * <li>5: RSP_Z86_DISPENSE (a Group object) <b>optional  </b></li>
                         * <li>6: RSP_Z86_GIVE (a Group object) <b>optional  </b></li>
                         * <li>7: RSP_Z86_ADMINISTRATION (a Group object) <b>optional  </b></li>
                         * <li>8: RSP_Z86_OBSERVATION (a Group object) <b> repeating </b></li>
 * </ul>
 */
//@SuppressWarnings("unused")
public class RSP_Z86_COMMON_ORDER extends AbstractGroup {

    /** 
     * Creates a new RSP_Z86_COMMON_ORDER group
     */
    public RSP_Z86_COMMON_ORDER(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(ORC.class, true, false, false);
                                  this.add(RSP_Z86_TIMING.class, false, true, false);
                                  this.add(RSP_Z86_ORDER_DETAIL.class, false, false, false);
                                  this.add(RSP_Z86_ENCODED_ORDER.class, false, false, false);
                                  this.add(RSP_Z86_DISPENSE.class, false, false, false);
                                  this.add(RSP_Z86_GIVE.class, false, false, false);
                                  this.add(RSP_Z86_ADMINISTRATION.class, false, false, false);
                                  this.add(RSP_Z86_OBSERVATION.class, true, true, false);
       } catch(HL7Exception e) {
          log.error("Unexpected error creating RSP_Z86_COMMON_ORDER - this is probably a bug in the source code generator.", e);
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
     * the first repetition of 
     * TIMING (a Group object) - creates it if necessary
     */
    public RSP_Z86_TIMING getTIMING() { 
       RSP_Z86_TIMING retVal = getTyped("TIMING", RSP_Z86_TIMING.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * TIMING (a Group object) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public RSP_Z86_TIMING getTIMING(int rep) { 
       RSP_Z86_TIMING retVal = getTyped("TIMING", rep, RSP_Z86_TIMING.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of TIMING 
     */ 
    public int getTIMINGReps() {  
        return getReps("TIMING");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of TIMING.
     * <p>
     * <p>
     * Note that unlike {@link #getTIMING()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<RSP_Z86_TIMING> getTIMINGAll() throws HL7Exception {
    	return getAllAsList("TIMING", RSP_Z86_TIMING.class);
    } 

    /**
     * Inserts a specific repetition of TIMING (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertTIMING(RSP_Z86_TIMING structure, int rep) throws HL7Exception { 
       super.insertRepetition("TIMING", structure, rep);
    }


    /**
     * Inserts a specific repetition of TIMING (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public RSP_Z86_TIMING insertTIMING(int rep) throws HL7Exception { 
       return (RSP_Z86_TIMING)super.insertRepetition("TIMING", rep);
    }


    /**
     * Removes a specific repetition of TIMING (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public RSP_Z86_TIMING removeTIMING(int rep) throws HL7Exception { 
       return (RSP_Z86_TIMING)super.removeRepetition("TIMING", rep);
    }



    /**
     * Returns
     * ORDER_DETAIL (a Group object) - creates it if necessary
     */
    public RSP_Z86_ORDER_DETAIL getORDER_DETAIL() { 
       RSP_Z86_ORDER_DETAIL retVal = getTyped("ORDER_DETAIL", RSP_Z86_ORDER_DETAIL.class);
       return retVal;
    }




    /**
     * Returns
     * ENCODED_ORDER (a Group object) - creates it if necessary
     */
    public RSP_Z86_ENCODED_ORDER getENCODED_ORDER() { 
       RSP_Z86_ENCODED_ORDER retVal = getTyped("ENCODED_ORDER", RSP_Z86_ENCODED_ORDER.class);
       return retVal;
    }




    /**
     * Returns
     * DISPENSE (a Group object) - creates it if necessary
     */
    public RSP_Z86_DISPENSE getDISPENSE() { 
       RSP_Z86_DISPENSE retVal = getTyped("DISPENSE", RSP_Z86_DISPENSE.class);
       return retVal;
    }




    /**
     * Returns
     * GIVE (a Group object) - creates it if necessary
     */
    public RSP_Z86_GIVE getGIVE() { 
       RSP_Z86_GIVE retVal = getTyped("GIVE", RSP_Z86_GIVE.class);
       return retVal;
    }




    /**
     * Returns
     * ADMINISTRATION (a Group object) - creates it if necessary
     */
    public RSP_Z86_ADMINISTRATION getADMINISTRATION() { 
       RSP_Z86_ADMINISTRATION retVal = getTyped("ADMINISTRATION", RSP_Z86_ADMINISTRATION.class);
       return retVal;
    }




    /**
     * Returns
     * the first repetition of 
     * OBSERVATION (a Group object) - creates it if necessary
     */
    public RSP_Z86_OBSERVATION getOBSERVATION() { 
       RSP_Z86_OBSERVATION retVal = getTyped("OBSERVATION", RSP_Z86_OBSERVATION.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * OBSERVATION (a Group object) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public RSP_Z86_OBSERVATION getOBSERVATION(int rep) { 
       RSP_Z86_OBSERVATION retVal = getTyped("OBSERVATION", rep, RSP_Z86_OBSERVATION.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of OBSERVATION 
     */ 
    public int getOBSERVATIONReps() {  
        return getReps("OBSERVATION");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of OBSERVATION.
     * <p>
     * <p>
     * Note that unlike {@link #getOBSERVATION()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<RSP_Z86_OBSERVATION> getOBSERVATIONAll() throws HL7Exception {
    	return getAllAsList("OBSERVATION", RSP_Z86_OBSERVATION.class);
    } 

    /**
     * Inserts a specific repetition of OBSERVATION (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertOBSERVATION(RSP_Z86_OBSERVATION structure, int rep) throws HL7Exception { 
       super.insertRepetition("OBSERVATION", structure, rep);
    }


    /**
     * Inserts a specific repetition of OBSERVATION (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public RSP_Z86_OBSERVATION insertOBSERVATION(int rep) throws HL7Exception { 
       return (RSP_Z86_OBSERVATION)super.insertRepetition("OBSERVATION", rep);
    }


    /**
     * Removes a specific repetition of OBSERVATION (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public RSP_Z86_OBSERVATION removeOBSERVATION(int rep) throws HL7Exception { 
       return (RSP_Z86_OBSERVATION)super.removeRepetition("OBSERVATION", rep);
    }



}

