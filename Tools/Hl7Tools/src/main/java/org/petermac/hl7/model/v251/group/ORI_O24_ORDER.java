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
 * <p>Represents a ORI_O24_ORDER group structure (a Group object).
 * A Group is an ordered collection of message segments that can repeat together or be optionally in/excluded together.
 * This Group contains the following elements:  
 * </p>
 * <ul>
                         * <li>1: ORC (Common Order) <b>  </b></li>
                         * <li>2: ORI_O24_TIMING (a Group object) <b>optional repeating </b></li>
                         * <li>3: OBR (Observation Request) <b>  </b></li>
                         * <li>4: NTE (Notes and Comments) <b>optional repeating </b></li>
                         * <li>5: IPC (Imaging Procedure Control Segment) <b> repeating </b></li>
 * </ul>
 */
//@SuppressWarnings("unused")
public class ORI_O24_ORDER extends AbstractGroup {

    /** 
     * Creates a new ORI_O24_ORDER group
     */
    public ORI_O24_ORDER(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(ORC.class, true, false, false);
                                  this.add(ORI_O24_TIMING.class, false, true, false);
                                  this.add(OBR.class, true, false, false);
                                  this.add(NTE.class, false, true, false);
                                  this.add(IPC.class, true, true, false);
       } catch(HL7Exception e) {
          log.error("Unexpected error creating ORI_O24_ORDER - this is probably a bug in the source code generator.", e);
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
    public ORI_O24_TIMING getTIMING() { 
       ORI_O24_TIMING retVal = getTyped("TIMING", ORI_O24_TIMING.class);
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
    public ORI_O24_TIMING getTIMING(int rep) { 
       ORI_O24_TIMING retVal = getTyped("TIMING", rep, ORI_O24_TIMING.class);
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
    public java.util.List<ORI_O24_TIMING> getTIMINGAll() throws HL7Exception {
    	return getAllAsList("TIMING", ORI_O24_TIMING.class);
    } 

    /**
     * Inserts a specific repetition of TIMING (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertTIMING(ORI_O24_TIMING structure, int rep) throws HL7Exception { 
       super.insertRepetition("TIMING", structure, rep);
    }


    /**
     * Inserts a specific repetition of TIMING (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public ORI_O24_TIMING insertTIMING(int rep) throws HL7Exception { 
       return (ORI_O24_TIMING)super.insertRepetition("TIMING", rep);
    }


    /**
     * Removes a specific repetition of TIMING (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public ORI_O24_TIMING removeTIMING(int rep) throws HL7Exception { 
       return (ORI_O24_TIMING)super.removeRepetition("TIMING", rep);
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



    /**
     * Returns
     * the first repetition of 
     * IPC (Imaging Procedure Control Segment) - creates it if necessary
     */
    public IPC getIPC() { 
       IPC retVal = getTyped("IPC", IPC.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * IPC (Imaging Procedure Control Segment) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public IPC getIPC(int rep) { 
       IPC retVal = getTyped("IPC", rep, IPC.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of IPC 
     */ 
    public int getIPCReps() {  
        return getReps("IPC");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of IPC.
     * <p>
     * <p>
     * Note that unlike {@link #getIPC()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<IPC> getIPCAll() throws HL7Exception {
    	return getAllAsList("IPC", IPC.class);
    } 

    /**
     * Inserts a specific repetition of IPC (Imaging Procedure Control Segment)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertIPC(IPC structure, int rep) throws HL7Exception { 
       super.insertRepetition("IPC", structure, rep);
    }


    /**
     * Inserts a specific repetition of IPC (Imaging Procedure Control Segment)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public IPC insertIPC(int rep) throws HL7Exception { 
       return (IPC)super.insertRepetition("IPC", rep);
    }


    /**
     * Removes a specific repetition of IPC (Imaging Procedure Control Segment)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public IPC removeIPC(int rep) throws HL7Exception { 
       return (IPC)super.removeRepetition("IPC", rep);
    }



}

