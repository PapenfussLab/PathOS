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
 * <p>Represents a OMD_O03_ORDER_TRAY group structure (a Group object).
 * A Group is an ordered collection of message segments that can repeat together or be optionally in/excluded together.
 * This Group contains the following elements:  
 * </p>
 * <ul>
                         * <li>1: ORC (Common Order) <b>  </b></li>
                         * <li>2: OMD_O03_TIMING_TRAY (a Group object) <b>optional repeating </b></li>
                         * <li>3: ODT (Diet Tray Instructions) <b> repeating </b></li>
                         * <li>4: NTE (Notes and Comments) <b>optional repeating </b></li>
 * </ul>
 */
//@SuppressWarnings("unused")
public class OMD_O03_ORDER_TRAY extends AbstractGroup {

    /** 
     * Creates a new OMD_O03_ORDER_TRAY group
     */
    public OMD_O03_ORDER_TRAY(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(ORC.class, true, false, false);
                                  this.add(OMD_O03_TIMING_TRAY.class, false, true, false);
                                  this.add(ODT.class, true, true, false);
                                  this.add(NTE.class, false, true, false);
       } catch(HL7Exception e) {
          log.error("Unexpected error creating OMD_O03_ORDER_TRAY - this is probably a bug in the source code generator.", e);
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
     * TIMING_TRAY (a Group object) - creates it if necessary
     */
    public OMD_O03_TIMING_TRAY getTIMING_TRAY() { 
       OMD_O03_TIMING_TRAY retVal = getTyped("TIMING_TRAY", OMD_O03_TIMING_TRAY.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * TIMING_TRAY (a Group object) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public OMD_O03_TIMING_TRAY getTIMING_TRAY(int rep) { 
       OMD_O03_TIMING_TRAY retVal = getTyped("TIMING_TRAY", rep, OMD_O03_TIMING_TRAY.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of TIMING_TRAY 
     */ 
    public int getTIMING_TRAYReps() {  
        return getReps("TIMING_TRAY");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of TIMING_TRAY.
     * <p>
     * <p>
     * Note that unlike {@link #getTIMING_TRAY()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<OMD_O03_TIMING_TRAY> getTIMING_TRAYAll() throws HL7Exception {
    	return getAllAsList("TIMING_TRAY", OMD_O03_TIMING_TRAY.class);
    } 

    /**
     * Inserts a specific repetition of TIMING_TRAY (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertTIMING_TRAY(OMD_O03_TIMING_TRAY structure, int rep) throws HL7Exception { 
       super.insertRepetition("TIMING_TRAY", structure, rep);
    }


    /**
     * Inserts a specific repetition of TIMING_TRAY (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public OMD_O03_TIMING_TRAY insertTIMING_TRAY(int rep) throws HL7Exception { 
       return (OMD_O03_TIMING_TRAY)super.insertRepetition("TIMING_TRAY", rep);
    }


    /**
     * Removes a specific repetition of TIMING_TRAY (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public OMD_O03_TIMING_TRAY removeTIMING_TRAY(int rep) throws HL7Exception { 
       return (OMD_O03_TIMING_TRAY)super.removeRepetition("TIMING_TRAY", rep);
    }



    /**
     * Returns
     * the first repetition of 
     * ODT (Diet Tray Instructions) - creates it if necessary
     */
    public ODT getODT() { 
       ODT retVal = getTyped("ODT", ODT.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * ODT (Diet Tray Instructions) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public ODT getODT(int rep) { 
       ODT retVal = getTyped("ODT", rep, ODT.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of ODT 
     */ 
    public int getODTReps() {  
        return getReps("ODT");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of ODT.
     * <p>
     * <p>
     * Note that unlike {@link #getODT()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<ODT> getODTAll() throws HL7Exception {
    	return getAllAsList("ODT", ODT.class);
    } 

    /**
     * Inserts a specific repetition of ODT (Diet Tray Instructions)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertODT(ODT structure, int rep) throws HL7Exception { 
       super.insertRepetition("ODT", structure, rep);
    }


    /**
     * Inserts a specific repetition of ODT (Diet Tray Instructions)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public ODT insertODT(int rep) throws HL7Exception { 
       return (ODT)super.insertRepetition("ODT", rep);
    }


    /**
     * Removes a specific repetition of ODT (Diet Tray Instructions)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public ODT removeODT(int rep) throws HL7Exception { 
       return (ODT)super.removeRepetition("ODT", rep);
    }



    /**
     * Returns
     * the first repetition of 
     * NTE (Notes and Comments) - creates it if necessary
     */
    public NTE getNTE() { 
       NTE retVal = getTyped("NTE", NTE.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * NTE (Notes and Comments) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public NTE getNTE(int rep) { 
       NTE retVal = getTyped("NTE", rep, NTE.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of NTE 
     */ 
    public int getNTEReps() {  
        return getReps("NTE");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of NTE.
     * <p>
     * <p>
     * Note that unlike {@link #getNTE()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<NTE> getNTEAll() throws HL7Exception {
    	return getAllAsList("NTE", NTE.class);
    } 

    /**
     * Inserts a specific repetition of NTE (Notes and Comments)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertNTE(NTE structure, int rep) throws HL7Exception { 
       super.insertRepetition("NTE", structure, rep);
    }


    /**
     * Inserts a specific repetition of NTE (Notes and Comments)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public NTE insertNTE(int rep) throws HL7Exception { 
       return (NTE)super.insertRepetition("NTE", rep);
    }


    /**
     * Removes a specific repetition of NTE (Notes and Comments)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public NTE removeNTE(int rep) throws HL7Exception { 
       return (NTE)super.removeRepetition("NTE", rep);
    }



}

