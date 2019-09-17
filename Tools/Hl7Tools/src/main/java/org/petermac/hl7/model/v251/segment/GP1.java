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


package org.petermac.hl7.model.v251.segment;

// import org.petermac.hl7.model.v251.group.*;
import org.petermac.hl7.model.v251.datatype.*;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Varies;


/**
 *<p>Represents an HL7 GP1 message segment (Grouping/Reimbursement - Visit). 
 * This segment has the following fields:</p>
 * <ul>
     * <li>GP1-1: Type of Bill Code (IS) <b> </b>
     * <li>GP1-2: Revenue Code (IS) <b>optional repeating</b>
     * <li>GP1-3: Overall Claim Disposition Code (IS) <b>optional </b>
     * <li>GP1-4: OCE Edits per Visit Code (IS) <b>optional repeating</b>
     * <li>GP1-5: Outlier Cost (CP) <b>optional </b>
 * </ul>
 */
@SuppressWarnings("unused")
public class GP1 extends AbstractSegment {

    /** 
     * Creates a new GP1 segment
     */
    public GP1(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                              this.add(IS.class, true, 1, 3, new Object[]{ getMessage(), new Integer(455) }, "Type of Bill Code");
                                              this.add(IS.class, false, 0, 3, new Object[]{ getMessage(), new Integer(456) }, "Revenue Code");
                                              this.add(IS.class, false, 1, 1, new Object[]{ getMessage(), new Integer(457) }, "Overall Claim Disposition Code");
                                              this.add(IS.class, false, 0, 2, new Object[]{ getMessage(), new Integer(458) }, "OCE Edits per Visit Code");
                                  this.add(CP.class, false, 1, 12, new Object[]{ getMessage() }, "Outlier Cost");
       } catch(HL7Exception e) {
          log.error("Unexpected error creating GP1 - this is probably a bug in the source code generator.", e);
       }
    }



    /**
     * Returns
     * GP1-1: "Type of Bill Code" - creates it if necessary
     */
    public IS getTypeOfBillCode() { 
		IS retVal = this.getTypedField(1, 0);
		return retVal;
    }
    
    /**
     * Returns
     * GP1-1: "Type of Bill Code" - creates it if necessary
     */
    public IS getGp11_TypeOfBillCode() { 
		IS retVal = this.getTypedField(1, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Revenue Code (GP1-2).
     */
    public IS[] getRevenueCode() {
    	IS[] retVal = this.getTypedField(2, new IS[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Revenue Code (GP1-2).
     */
    public IS[] getGp12_RevenueCode() {
    	IS[] retVal = this.getTypedField(2, new IS[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Revenue Code (GP1-2).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getRevenueCodeReps() {
    	return this.getReps(2);
    }


    /**
     * Returns a specific repetition of
     * GP1-2: "Revenue Code" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getRevenueCode(int rep) { 
		IS retVal = this.getTypedField(2, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * GP1-2: "Revenue Code" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getGp12_RevenueCode(int rep) { 
		IS retVal = this.getTypedField(2, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Revenue Code (GP1-2).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getGp12_RevenueCodeReps() {
    	return this.getReps(2);
    }


    /**
     * Inserts a repetition of
     * GP1-2: "Revenue Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertRevenueCode(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(2, rep);
    }


    /**
     * Inserts a repetition of
     * GP1-2: "Revenue Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertGp12_RevenueCode(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(2, rep);
    }


    /**
     * Removes a repetition of
     * GP1-2: "Revenue Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeRevenueCode(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(2, rep);
    }


    /**
     * Removes a repetition of
     * GP1-2: "Revenue Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeGp12_RevenueCode(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(2, rep);
    }




    /**
     * Returns
     * GP1-3: "Overall Claim Disposition Code" - creates it if necessary
     */
    public IS getOverallClaimDispositionCode() { 
		IS retVal = this.getTypedField(3, 0);
		return retVal;
    }
    
    /**
     * Returns
     * GP1-3: "Overall Claim Disposition Code" - creates it if necessary
     */
    public IS getGp13_OverallClaimDispositionCode() { 
		IS retVal = this.getTypedField(3, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of OCE Edits per Visit Code (GP1-4).
     */
    public IS[] getOCEEditsPerVisitCode() {
    	IS[] retVal = this.getTypedField(4, new IS[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of OCE Edits per Visit Code (GP1-4).
     */
    public IS[] getGp14_OCEEditsPerVisitCode() {
    	IS[] retVal = this.getTypedField(4, new IS[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of OCE Edits per Visit Code (GP1-4).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getOCEEditsPerVisitCodeReps() {
    	return this.getReps(4);
    }


    /**
     * Returns a specific repetition of
     * GP1-4: "OCE Edits per Visit Code" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getOCEEditsPerVisitCode(int rep) { 
		IS retVal = this.getTypedField(4, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * GP1-4: "OCE Edits per Visit Code" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getGp14_OCEEditsPerVisitCode(int rep) { 
		IS retVal = this.getTypedField(4, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of OCE Edits per Visit Code (GP1-4).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getGp14_OCEEditsPerVisitCodeReps() {
    	return this.getReps(4);
    }


    /**
     * Inserts a repetition of
     * GP1-4: "OCE Edits per Visit Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertOCEEditsPerVisitCode(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(4, rep);
    }


    /**
     * Inserts a repetition of
     * GP1-4: "OCE Edits per Visit Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertGp14_OCEEditsPerVisitCode(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(4, rep);
    }


    /**
     * Removes a repetition of
     * GP1-4: "OCE Edits per Visit Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeOCEEditsPerVisitCode(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(4, rep);
    }


    /**
     * Removes a repetition of
     * GP1-4: "OCE Edits per Visit Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeGp14_OCEEditsPerVisitCode(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(4, rep);
    }




    /**
     * Returns
     * GP1-5: "Outlier Cost" - creates it if necessary
     */
    public CP getOutlierCost() { 
		CP retVal = this.getTypedField(5, 0);
		return retVal;
    }
    
    /**
     * Returns
     * GP1-5: "Outlier Cost" - creates it if necessary
     */
    public CP getGp15_OutlierCost() { 
		CP retVal = this.getTypedField(5, 0);
		return retVal;
    }





    /** {@inheritDoc} */   
    protected Type createNewTypeWithoutReflection(int field) {
       switch (field) {
          case 0: return new IS(getMessage(), new Integer( 455 ));
          case 1: return new IS(getMessage(), new Integer( 456 ));
          case 2: return new IS(getMessage(), new Integer( 457 ));
          case 3: return new IS(getMessage(), new Integer( 458 ));
          case 4: return new CP(getMessage());
          default: return null;
       }
   }


}

