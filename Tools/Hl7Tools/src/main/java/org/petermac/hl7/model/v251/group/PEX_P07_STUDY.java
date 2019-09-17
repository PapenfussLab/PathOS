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
 * <p>Represents a PEX_P07_STUDY group structure (a Group object).
 * A Group is an ordered collection of message segments that can repeat together or be optionally in/excluded together.
 * This Group contains the following elements:  
 * </p>
 * <ul>
                         * <li>1: CSR (Clinical Study Registration) <b>  </b></li>
                         * <li>2: CSP (Clinical Study Phase) <b>optional repeating </b></li>
 * </ul>
 */
//@SuppressWarnings("unused")
public class PEX_P07_STUDY extends AbstractGroup {

    /** 
     * Creates a new PEX_P07_STUDY group
     */
    public PEX_P07_STUDY(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(CSR.class, true, false, false);
                                  this.add(CSP.class, false, true, false);
       } catch(HL7Exception e) {
          log.error("Unexpected error creating PEX_P07_STUDY - this is probably a bug in the source code generator.", e);
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
     * CSR (Clinical Study Registration) - creates it if necessary
     */
    public CSR getCSR() { 
       CSR retVal = getTyped("CSR", CSR.class);
       return retVal;
    }




    /**
     * Returns
     * the first repetition of 
     * CSP (Clinical Study Phase) - creates it if necessary
     */
    public CSP getCSP() { 
       CSP retVal = getTyped("CSP", CSP.class);
       return retVal;
    }


    /**
     * Returns a specific repetition of
     * CSP (Clinical Study Phase) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed, i.e. the first repetition is at index 0)
     * @throws HL7Exception if the repetition requested is more than one 
     *     greater than the number of existing repetitions.
     */
    public CSP getCSP(int rep) { 
       CSP retVal = getTyped("CSP", rep, CSP.class);
       return retVal;
    }

    /** 
     * Returns the number of existing repetitions of CSP 
     */ 
    public int getCSPReps() {  
        return getReps("CSP");
    } 

    /** 
     * <p>
     * Returns a non-modifiable List containing all current existing repetitions of CSP.
     * <p>
     * <p>
     * Note that unlike {@link #getCSP()}, this method will not create any reps
     * if none are already present, so an empty list may be returned.
     * </p>
     */ 
    public java.util.List<CSP> getCSPAll() throws HL7Exception {
    	return getAllAsList("CSP", CSP.class);
    } 

    /**
     * Inserts a specific repetition of CSP (Clinical Study Phase)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public void insertCSP(CSP structure, int rep) throws HL7Exception { 
       super.insertRepetition("CSP", structure, rep);
    }


    /**
     * Inserts a specific repetition of CSP (Clinical Study Phase)
     * @see AbstractGroup#insertRepetition(Structure, int) 
     */
    public CSP insertCSP(int rep) throws HL7Exception { 
       return (CSP)super.insertRepetition("CSP", rep);
    }


    /**
     * Removes a specific repetition of CSP (Clinical Study Phase)
     * @see AbstractGroup#removeRepetition(String, int) 
     */
    public CSP removeCSP(int rep) throws HL7Exception { 
       return (CSP)super.removeRepetition("CSP", rep);
    }



}

