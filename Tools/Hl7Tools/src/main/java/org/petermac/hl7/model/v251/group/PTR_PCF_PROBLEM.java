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
 * <p>Represents a PTR_PCF_PROBLEM group structure (a Group object).
 * A Group is an ordered collection of message segments that can repeat together or be optionally in/excluded together.
 * This Group contains the following elements:  
 * </p>
 * <ul>
                         * <li>1: PRB (Problem Details) <b>  </b></li>
                         * <li>2: NTE (Notes and Comments) <b>optional repeating </b></li>
                         * <li>3: VAR (Variance) <b>optional repeating </b></li>
                         * <li>4: PTR_PCF_PROBLEM_ROLE (a Group object) <b>optional repeating </b></li>
                         * <li>5: PTR_PCF_PROBLEM_OBSERVATION (a Group object) <b>optional repeating </b></li>
                         * <li>6: PTR_PCF_GOAL (a Group object) <b>optional repeating </b></li>
                         * <li>7: PTR_PCF_ORDER (a Group object) <b>optional repeating </b></li>
 * </ul>
 */
//@SuppressWarnings("unused")
public class PTR_PCF_PROBLEM extends AbstractGroup {

    /** 
     * Creates a new PTR_PCF_PROBLEM group
     */
    public PTR_PCF_PROBLEM(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(PRB.class, true, false, false);
                                  this.add(NTE.class, false, true, false);
                                  this.add(VAR.class, false, true, false);
                                  this.add(PTR_PCF_PROBLEM_ROLE.class, false, true, false);
                                  this.add(PTR_PCF_PROBLEM_OBSERVATION.class, false, true, false);
                                  this.add(PTR_PCF_GOAL.class, false, true, false);
                                  this.add(PTR_PCF_ORDER.class, false, true, false);
       } catch(HL7Exception e) {
          log.error("Unexpected error creating PTR_PCF_PROBLEM - this is probably a bug in the source code generator.", e);
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
     * PRB (Problem Details) - creates it if necessary
     */
    public PRB getPRB() { 
       PRB retVal = getTyped("PRB", PRB.class);
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
     * VAR (Variance) - creates it if necessary
     */
    public VAR getVAR() { 
       VAR retVal = getTyped("VAR", VAR.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * VAR (Variance) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public VAR getVAR(int rep) { 
       VAR retVal = getTyped("VAR", rep, VAR.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of VAR 
     */ 
    public int getVARReps() {  
        return getReps("VAR");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of VAR.
     * <p>
     * <p>
     * Note that unlike {@link #getVAR()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<VAR> getVARAll() throws HL7Exception {
    	return getAllAsList("VAR", VAR.class);
    } 

    /**
     * Inserts a specific repetition of VAR (Variance)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertVAR(VAR structure, int rep) throws HL7Exception { 
       super.insertRepetition("VAR", structure, rep);
    }


    /**
     * Inserts a specific repetition of VAR (Variance)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public VAR insertVAR(int rep) throws HL7Exception { 
       return (VAR)super.insertRepetition("VAR", rep);
    }


    /**
     * Removes a specific repetition of VAR (Variance)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public VAR removeVAR(int rep) throws HL7Exception { 
       return (VAR)super.removeRepetition("VAR", rep);
    }



    /**
     * Returns
     * the first repetition of 
     * PROBLEM_ROLE (a Group object) - creates it if necessary
     */
    public PTR_PCF_PROBLEM_ROLE getPROBLEM_ROLE() { 
       PTR_PCF_PROBLEM_ROLE retVal = getTyped("PROBLEM_ROLE", PTR_PCF_PROBLEM_ROLE.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * PROBLEM_ROLE (a Group object) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public PTR_PCF_PROBLEM_ROLE getPROBLEM_ROLE(int rep) { 
       PTR_PCF_PROBLEM_ROLE retVal = getTyped("PROBLEM_ROLE", rep, PTR_PCF_PROBLEM_ROLE.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of PROBLEM_ROLE 
     */ 
    public int getPROBLEM_ROLEReps() {  
        return getReps("PROBLEM_ROLE");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of PROBLEM_ROLE.
     * <p>
     * <p>
     * Note that unlike {@link #getPROBLEM_ROLE()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<PTR_PCF_PROBLEM_ROLE> getPROBLEM_ROLEAll() throws HL7Exception {
    	return getAllAsList("PROBLEM_ROLE", PTR_PCF_PROBLEM_ROLE.class);
    } 

    /**
     * Inserts a specific repetition of PROBLEM_ROLE (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertPROBLEM_ROLE(PTR_PCF_PROBLEM_ROLE structure, int rep) throws HL7Exception { 
       super.insertRepetition("PROBLEM_ROLE", structure, rep);
    }


    /**
     * Inserts a specific repetition of PROBLEM_ROLE (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public PTR_PCF_PROBLEM_ROLE insertPROBLEM_ROLE(int rep) throws HL7Exception { 
       return (PTR_PCF_PROBLEM_ROLE)super.insertRepetition("PROBLEM_ROLE", rep);
    }


    /**
     * Removes a specific repetition of PROBLEM_ROLE (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public PTR_PCF_PROBLEM_ROLE removePROBLEM_ROLE(int rep) throws HL7Exception { 
       return (PTR_PCF_PROBLEM_ROLE)super.removeRepetition("PROBLEM_ROLE", rep);
    }



    /**
     * Returns
     * the first repetition of 
     * PROBLEM_OBSERVATION (a Group object) - creates it if necessary
     */
    public PTR_PCF_PROBLEM_OBSERVATION getPROBLEM_OBSERVATION() { 
       PTR_PCF_PROBLEM_OBSERVATION retVal = getTyped("PROBLEM_OBSERVATION", PTR_PCF_PROBLEM_OBSERVATION.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * PROBLEM_OBSERVATION (a Group object) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public PTR_PCF_PROBLEM_OBSERVATION getPROBLEM_OBSERVATION(int rep) { 
       PTR_PCF_PROBLEM_OBSERVATION retVal = getTyped("PROBLEM_OBSERVATION", rep, PTR_PCF_PROBLEM_OBSERVATION.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of PROBLEM_OBSERVATION 
     */ 
    public int getPROBLEM_OBSERVATIONReps() {  
        return getReps("PROBLEM_OBSERVATION");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of PROBLEM_OBSERVATION.
     * <p>
     * <p>
     * Note that unlike {@link #getPROBLEM_OBSERVATION()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<PTR_PCF_PROBLEM_OBSERVATION> getPROBLEM_OBSERVATIONAll() throws HL7Exception {
    	return getAllAsList("PROBLEM_OBSERVATION", PTR_PCF_PROBLEM_OBSERVATION.class);
    } 

    /**
     * Inserts a specific repetition of PROBLEM_OBSERVATION (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertPROBLEM_OBSERVATION(PTR_PCF_PROBLEM_OBSERVATION structure, int rep) throws HL7Exception { 
       super.insertRepetition("PROBLEM_OBSERVATION", structure, rep);
    }


    /**
     * Inserts a specific repetition of PROBLEM_OBSERVATION (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public PTR_PCF_PROBLEM_OBSERVATION insertPROBLEM_OBSERVATION(int rep) throws HL7Exception { 
       return (PTR_PCF_PROBLEM_OBSERVATION)super.insertRepetition("PROBLEM_OBSERVATION", rep);
    }


    /**
     * Removes a specific repetition of PROBLEM_OBSERVATION (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public PTR_PCF_PROBLEM_OBSERVATION removePROBLEM_OBSERVATION(int rep) throws HL7Exception { 
       return (PTR_PCF_PROBLEM_OBSERVATION)super.removeRepetition("PROBLEM_OBSERVATION", rep);
    }



    /**
     * Returns
     * the first repetition of 
     * GOAL (a Group object) - creates it if necessary
     */
    public PTR_PCF_GOAL getGOAL() { 
       PTR_PCF_GOAL retVal = getTyped("GOAL", PTR_PCF_GOAL.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * GOAL (a Group object) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public PTR_PCF_GOAL getGOAL(int rep) { 
       PTR_PCF_GOAL retVal = getTyped("GOAL", rep, PTR_PCF_GOAL.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of GOAL 
     */ 
    public int getGOALReps() {  
        return getReps("GOAL");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of GOAL.
     * <p>
     * <p>
     * Note that unlike {@link #getGOAL()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<PTR_PCF_GOAL> getGOALAll() throws HL7Exception {
    	return getAllAsList("GOAL", PTR_PCF_GOAL.class);
    } 

    /**
     * Inserts a specific repetition of GOAL (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertGOAL(PTR_PCF_GOAL structure, int rep) throws HL7Exception { 
       super.insertRepetition("GOAL", structure, rep);
    }


    /**
     * Inserts a specific repetition of GOAL (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public PTR_PCF_GOAL insertGOAL(int rep) throws HL7Exception { 
       return (PTR_PCF_GOAL)super.insertRepetition("GOAL", rep);
    }


    /**
     * Removes a specific repetition of GOAL (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public PTR_PCF_GOAL removeGOAL(int rep) throws HL7Exception { 
       return (PTR_PCF_GOAL)super.removeRepetition("GOAL", rep);
    }



    /**
     * Returns
     * the first repetition of 
     * ORDER (a Group object) - creates it if necessary
     */
    public PTR_PCF_ORDER getORDER() { 
       PTR_PCF_ORDER retVal = getTyped("ORDER", PTR_PCF_ORDER.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * ORDER (a Group object) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public PTR_PCF_ORDER getORDER(int rep) { 
       PTR_PCF_ORDER retVal = getTyped("ORDER", rep, PTR_PCF_ORDER.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of ORDER 
     */ 
    public int getORDERReps() {  
        return getReps("ORDER");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of ORDER.
     * <p>
     * <p>
     * Note that unlike {@link #getORDER()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<PTR_PCF_ORDER> getORDERAll() throws HL7Exception {
    	return getAllAsList("ORDER", PTR_PCF_ORDER.class);
    } 

    /**
     * Inserts a specific repetition of ORDER (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertORDER(PTR_PCF_ORDER structure, int rep) throws HL7Exception { 
       super.insertRepetition("ORDER", structure, rep);
    }


    /**
     * Inserts a specific repetition of ORDER (a Group object)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public PTR_PCF_ORDER insertORDER(int rep) throws HL7Exception { 
       return (PTR_PCF_ORDER)super.insertRepetition("ORDER", rep);
    }


    /**
     * Removes a specific repetition of ORDER (a Group object)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public PTR_PCF_ORDER removeORDER(int rep) throws HL7Exception { 
       return (PTR_PCF_ORDER)super.removeRepetition("ORDER", rep);
    }



}

