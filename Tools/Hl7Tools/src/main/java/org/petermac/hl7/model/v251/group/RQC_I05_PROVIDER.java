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
 * <p>Represents a RQC_I05_PROVIDER group structure (a Group object).
 * A Group is an ordered collection of message segments that can repeat together or be optionally in/excluded together.
 * This Group contains the following elements:  
 * </p>
 * <ul>
                         * <li>1: PRD (Provider Data) <b>  </b></li>
                         * <li>2: CTD (Contact Data) <b>optional repeating </b></li>
 * </ul>
 */
//@SuppressWarnings("unused")
public class RQC_I05_PROVIDER extends AbstractGroup {

    /** 
     * Creates a new RQC_I05_PROVIDER group
     */
    public RQC_I05_PROVIDER(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(PRD.class, true, false, false);
                                  this.add(CTD.class, false, true, false);
       } catch(HL7Exception e) {
          log.error("Unexpected error creating RQC_I05_PROVIDER - this is probably a bug in the source code generator.", e);
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
     * PRD (Provider Data) - creates it if necessary
     */
    public PRD getPRD() { 
       PRD retVal = getTyped("PRD", PRD.class);
       return retVal;
    }




    /**
     * Returns
     * the first repetition of 
     * CTD (Contact Data) - creates it if necessary
     */
    public CTD getCTD() { 
       CTD retVal = getTyped("CTD", CTD.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * CTD (Contact Data) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public CTD getCTD(int rep) { 
       CTD retVal = getTyped("CTD", rep, CTD.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of CTD 
     */ 
    public int getCTDReps() {  
        return getReps("CTD");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of CTD.
     * <p>
     * <p>
     * Note that unlike {@link #getCTD()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<CTD> getCTDAll() throws HL7Exception {
    	return getAllAsList("CTD", CTD.class);
    } 

    /**
     * Inserts a specific repetition of CTD (Contact Data)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertCTD(CTD structure, int rep) throws HL7Exception { 
       super.insertRepetition("CTD", structure, rep);
    }


    /**
     * Inserts a specific repetition of CTD (Contact Data)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public CTD insertCTD(int rep) throws HL7Exception { 
       return (CTD)super.insertRepetition("CTD", rep);
    }


    /**
     * Removes a specific repetition of CTD (Contact Data)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public CTD removeCTD(int rep) throws HL7Exception { 
       return (CTD)super.removeRepetition("CTD", rep);
    }



}

