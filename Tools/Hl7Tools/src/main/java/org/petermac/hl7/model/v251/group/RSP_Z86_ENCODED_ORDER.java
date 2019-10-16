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
 * <p>Represents a RSP_Z86_ENCODED_ORDER group structure (a Group object).
 * A Group is an ordered collection of message segments that can repeat together or be optionally in/excluded together.
 * This Group contains the following elements:  
 * </p>
 * <ul>
                         * <li>1: RXE (Pharmacy/Treatment Encoded Order) <b>  </b></li>
                         * <li>2: RSP_Z86_TIMING_ENCODED (a Group object) <b>optional repeating </b></li>
                         * <li>3: RXR (Pharmacy/Treatment Route) <b> repeating </b></li>
                         * <li>4: RXC (Pharmacy/Treatment Component Order) <b>optional repeating </b></li>
 * </ul>
 */
//@SuppressWarnings("unused")
public class RSP_Z86_ENCODED_ORDER extends AbstractGroup {

    /** 
     * Creates a new RSP_Z86_ENCODED_ORDER group
     */
    public RSP_Z86_ENCODED_ORDER(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(RXE.class, true, false, false);
                                  this.add(RSP_Z86_TIMING_ENCODED.class, false, true, false);
                                  this.add(RXR.class, true, true, false);
                                  this.add(RXC.class, false, true, false);
       } catch(HL7Exception e) {
          log.error("Unexpected error creating RSP_Z86_ENCODED_ORDER - this is probably a bug in the source code generator.", e);
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
     * RXE (Pharmacy/Treatment Encoded Order) - creates it if necessary
     */
    public RXE getRXE() { 
       RXE retVal = getTyped("RXE", RXE.class);
       return retVal;
    }




    /**
     * Returns
     * the first repetition of 
     * TIMING_ENCODED (a Group object) - creates it if necessary
     */
    public RSP_Z86_TIMING_ENCODED getTIMING_ENCODED() { 
       RSP_Z86_TIMING_ENCODED retVal = getTyped("TIMING_ENCODED", RSP_Z86_TIMING_ENCODED.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * TIMING_ENCODED (a Group object) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public RSP_Z86_TIMING_ENCODED getTIMING_ENCODED(int rep) { 
       RSP_Z86_TIMING_ENCODED retVal = getTyped("TIMING_ENCODED", rep, RSP_Z86_TIMING_ENCODED.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of TIMING_ENCODED 
     */ 
    public int getTIMING_ENCODEDReps() {  
        return getReps("TIMING_ENCODED");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of TIMING_ENCODED.
     * <p>
     * <p>
     * Note that unlike {@link #getTIMING_ENCODED()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<RSP_Z86_TIMING_ENCODED> getTIMING_ENCODEDAll() throws HL7Exception {
    	return getAllAsList("TIMING_ENCODED", RSP_Z86_TIMING_ENCODED.class);
    } 

    /**
     * Inserts a specific repetition of TIMING_ENCODED (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertTIMING_ENCODED(RSP_Z86_TIMING_ENCODED structure, int rep) throws HL7Exception { 
       super.insertRepetition("TIMING_ENCODED", structure, rep);
    }


    /**
     * Inserts a specific repetition of TIMING_ENCODED (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public RSP_Z86_TIMING_ENCODED insertTIMING_ENCODED(int rep) throws HL7Exception { 
       return (RSP_Z86_TIMING_ENCODED)super.insertRepetition("TIMING_ENCODED", rep);
    }


    /**
     * Removes a specific repetition of TIMING_ENCODED (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public RSP_Z86_TIMING_ENCODED removeTIMING_ENCODED(int rep) throws HL7Exception { 
       return (RSP_Z86_TIMING_ENCODED)super.removeRepetition("TIMING_ENCODED", rep);
    }



    /**
     * Returns
     * the first repetition of 
     * RXR (Pharmacy/Treatment Route) - creates it if necessary
     */
    public RXR getRXR() { 
       RXR retVal = getTyped("RXR", RXR.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * RXR (Pharmacy/Treatment Route) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public RXR getRXR(int rep) { 
       RXR retVal = getTyped("RXR", rep, RXR.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of RXR 
     */ 
    public int getRXRReps() {  
        return getReps("RXR");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of RXR.
     * <p>
     * <p>
     * Note that unlike {@link #getRXR()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<RXR> getRXRAll() throws HL7Exception {
    	return getAllAsList("RXR", RXR.class);
    } 

    /**
     * Inserts a specific repetition of RXR (Pharmacy/Treatment Route)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertRXR(RXR structure, int rep) throws HL7Exception { 
       super.insertRepetition("RXR", structure, rep);
    }


    /**
     * Inserts a specific repetition of RXR (Pharmacy/Treatment Route)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public RXR insertRXR(int rep) throws HL7Exception { 
       return (RXR)super.insertRepetition("RXR", rep);
    }


    /**
     * Removes a specific repetition of RXR (Pharmacy/Treatment Route)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public RXR removeRXR(int rep) throws HL7Exception { 
       return (RXR)super.removeRepetition("RXR", rep);
    }



    /**
     * Returns
     * the first repetition of 
     * RXC (Pharmacy/Treatment Component Order) - creates it if necessary
     */
    public RXC getRXC() { 
       RXC retVal = getTyped("RXC", RXC.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * RXC (Pharmacy/Treatment Component Order) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public RXC getRXC(int rep) { 
       RXC retVal = getTyped("RXC", rep, RXC.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of RXC 
     */ 
    public int getRXCReps() {  
        return getReps("RXC");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of RXC.
     * <p>
     * <p>
     * Note that unlike {@link #getRXC()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<RXC> getRXCAll() throws HL7Exception {
    	return getAllAsList("RXC", RXC.class);
    } 

    /**
     * Inserts a specific repetition of RXC (Pharmacy/Treatment Component Order)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertRXC(RXC structure, int rep) throws HL7Exception { 
       super.insertRepetition("RXC", structure, rep);
    }


    /**
     * Inserts a specific repetition of RXC (Pharmacy/Treatment Component Order)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public RXC insertRXC(int rep) throws HL7Exception { 
       return (RXC)super.insertRepetition("RXC", rep);
    }


    /**
     * Removes a specific repetition of RXC (Pharmacy/Treatment Component Order)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public RXC removeRXC(int rep) throws HL7Exception { 
       return (RXC)super.removeRepetition("RXC", rep);
    }



}

