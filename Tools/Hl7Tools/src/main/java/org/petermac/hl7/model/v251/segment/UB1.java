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
 *<p>Represents an HL7 UB1 message segment (UB82). 
 * This segment has the following fields:</p>
 * <ul>
     * <li>UB1-1: Set ID - UB1 (SI) <b>optional </b>
     * <li>UB1-2: Blood Deductible  (43) (NM) <b>optional </b>
     * <li>UB1-3: Blood Furnished-Pints Of (40) (NM) <b>optional </b>
     * <li>UB1-4: Blood Replaced-Pints (41) (NM) <b>optional </b>
     * <li>UB1-5: Blood Not Replaced-Pints(42) (NM) <b>optional </b>
     * <li>UB1-6: Co-Insurance Days (25) (NM) <b>optional </b>
     * <li>UB1-7: Condition Code (35-39) (IS) <b>optional repeating</b>
     * <li>UB1-8: Covered Days - (23) (NM) <b>optional </b>
     * <li>UB1-9: Non Covered Days - (24) (NM) <b>optional </b>
     * <li>UB1-10: Value Amount & Code (46-49) (UVC) <b>optional repeating</b>
     * <li>UB1-11: Number Of Grace Days (90) (NM) <b>optional </b>
     * <li>UB1-12: Special Program Indicator (44) (CE) <b>optional </b>
     * <li>UB1-13: PSRO/UR Approval Indicator (87) (CE) <b>optional </b>
     * <li>UB1-14: PSRO/UR Approved Stay-Fm (88) (DT) <b>optional </b>
     * <li>UB1-15: PSRO/UR Approved Stay-To (89) (DT) <b>optional </b>
     * <li>UB1-16: Occurrence (28-32) (OCD) <b>optional repeating</b>
     * <li>UB1-17: Occurrence Span (33) (CE) <b>optional </b>
     * <li>UB1-18: Occur Span Start Date(33) (DT) <b>optional </b>
     * <li>UB1-19: Occur Span End Date (33) (DT) <b>optional </b>
     * <li>UB1-20: UB-82 Locator 2 (ST) <b>optional </b>
     * <li>UB1-21: UB-82 Locator 9 (ST) <b>optional </b>
     * <li>UB1-22: UB-82 Locator 27 (ST) <b>optional </b>
     * <li>UB1-23: UB-82 Locator 45 (ST) <b>optional </b>
 * </ul>
 */
@SuppressWarnings("unused")
public class UB1 extends AbstractSegment {

    /** 
     * Creates a new UB1 segment
     */
    public UB1(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(SI.class, false, 1, 4, new Object[]{ getMessage() }, "Set ID - UB1");
                                  this.add(NM.class, false, 1, 1, new Object[]{ getMessage() }, "Blood Deductible  (43)");
                                  this.add(NM.class, false, 1, 2, new Object[]{ getMessage() }, "Blood Furnished-Pints Of (40)");
                                  this.add(NM.class, false, 1, 2, new Object[]{ getMessage() }, "Blood Replaced-Pints (41)");
                                  this.add(NM.class, false, 1, 2, new Object[]{ getMessage() }, "Blood Not Replaced-Pints(42)");
                                  this.add(NM.class, false, 1, 2, new Object[]{ getMessage() }, "Co-Insurance Days (25)");
                                              this.add(IS.class, false, 5, 14, new Object[]{ getMessage(), new Integer(43) }, "Condition Code (35-39)");
                                  this.add(NM.class, false, 1, 3, new Object[]{ getMessage() }, "Covered Days - (23)");
                                  this.add(NM.class, false, 1, 3, new Object[]{ getMessage() }, "Non Covered Days - (24)");
                                  this.add(UVC.class, false, 8, 41, new Object[]{ getMessage() }, "Value Amount & Code (46-49)");
                                  this.add(NM.class, false, 1, 2, new Object[]{ getMessage() }, "Number Of Grace Days (90)");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Special Program Indicator (44)");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "PSRO/UR Approval Indicator (87)");
                                  this.add(DT.class, false, 1, 8, new Object[]{ getMessage() }, "PSRO/UR Approved Stay-Fm (88)");
                                  this.add(DT.class, false, 1, 8, new Object[]{ getMessage() }, "PSRO/UR Approved Stay-To (89)");
                                  this.add(OCD.class, false, 5, 259, new Object[]{ getMessage() }, "Occurrence (28-32)");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Occurrence Span (33)");
                                  this.add(DT.class, false, 1, 8, new Object[]{ getMessage() }, "Occur Span Start Date(33)");
                                  this.add(DT.class, false, 1, 8, new Object[]{ getMessage() }, "Occur Span End Date (33)");
                                  this.add(ST.class, false, 1, 30, new Object[]{ getMessage() }, "UB-82 Locator 2");
                                  this.add(ST.class, false, 1, 7, new Object[]{ getMessage() }, "UB-82 Locator 9");
                                  this.add(ST.class, false, 1, 8, new Object[]{ getMessage() }, "UB-82 Locator 27");
                                  this.add(ST.class, false, 1, 17, new Object[]{ getMessage() }, "UB-82 Locator 45");
       } catch(HL7Exception e) {
          log.error("Unexpected error creating UB1 - this is probably a bug in the source code generator.", e);
       }
    }



    /**
     * Returns
     * UB1-1: "Set ID - UB1" - creates it if necessary
     */
    public SI getSetIDUB1() { 
		SI retVal = this.getTypedField(1, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-1: "Set ID - UB1" - creates it if necessary
     */
    public SI getUb11_SetIDUB1() { 
		SI retVal = this.getTypedField(1, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-2: "Blood Deductible  (43)" - creates it if necessary
     */
    public NM getBloodDeductible() { 
		NM retVal = this.getTypedField(2, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-2: "Blood Deductible  (43)" - creates it if necessary
     */
    public NM getUb12_BloodDeductible() { 
		NM retVal = this.getTypedField(2, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-3: "Blood Furnished-Pints Of (40)" - creates it if necessary
     */
    public NM getBloodFurnishedPintsOf() { 
		NM retVal = this.getTypedField(3, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-3: "Blood Furnished-Pints Of (40)" - creates it if necessary
     */
    public NM getUb13_BloodFurnishedPintsOf() { 
		NM retVal = this.getTypedField(3, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-4: "Blood Replaced-Pints (41)" - creates it if necessary
     */
    public NM getBloodReplacedPints() { 
		NM retVal = this.getTypedField(4, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-4: "Blood Replaced-Pints (41)" - creates it if necessary
     */
    public NM getUb14_BloodReplacedPints() { 
		NM retVal = this.getTypedField(4, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-5: "Blood Not Replaced-Pints(42)" - creates it if necessary
     */
    public NM getBloodNotReplacedPints() { 
		NM retVal = this.getTypedField(5, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-5: "Blood Not Replaced-Pints(42)" - creates it if necessary
     */
    public NM getUb15_BloodNotReplacedPints() { 
		NM retVal = this.getTypedField(5, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-6: "Co-Insurance Days (25)" - creates it if necessary
     */
    public NM getCoInsuranceDays() { 
		NM retVal = this.getTypedField(6, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-6: "Co-Insurance Days (25)" - creates it if necessary
     */
    public NM getUb16_CoInsuranceDays() { 
		NM retVal = this.getTypedField(6, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Condition Code (35-39) (UB1-7).
     */
    public IS[] getConditionCode3539() {
    	IS[] retVal = this.getTypedField(7, new IS[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Condition Code (35-39) (UB1-7).
     */
    public IS[] getUb17_ConditionCode3539() {
    	IS[] retVal = this.getTypedField(7, new IS[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Condition Code (35-39) (UB1-7).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getConditionCode3539Reps() {
    	return this.getReps(7);
    }


    /**
     * Returns a specific repetition of
     * UB1-7: "Condition Code (35-39)" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getConditionCode3539(int rep) { 
		IS retVal = this.getTypedField(7, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * UB1-7: "Condition Code (35-39)" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getUb17_ConditionCode3539(int rep) { 
		IS retVal = this.getTypedField(7, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Condition Code (35-39) (UB1-7).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getUb17_ConditionCode3539Reps() {
    	return this.getReps(7);
    }


    /**
     * Inserts a repetition of
     * UB1-7: "Condition Code (35-39)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertConditionCode3539(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(7, rep);
    }


    /**
     * Inserts a repetition of
     * UB1-7: "Condition Code (35-39)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertUb17_ConditionCode3539(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(7, rep);
    }


    /**
     * Removes a repetition of
     * UB1-7: "Condition Code (35-39)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeConditionCode3539(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(7, rep);
    }


    /**
     * Removes a repetition of
     * UB1-7: "Condition Code (35-39)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeUb17_ConditionCode3539(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(7, rep);
    }




    /**
     * Returns
     * UB1-8: "Covered Days - (23)" - creates it if necessary
     */
    public NM getCoveredDays() { 
		NM retVal = this.getTypedField(8, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-8: "Covered Days - (23)" - creates it if necessary
     */
    public NM getUb18_CoveredDays() { 
		NM retVal = this.getTypedField(8, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-9: "Non Covered Days - (24)" - creates it if necessary
     */
    public NM getNonCoveredDays() { 
		NM retVal = this.getTypedField(9, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-9: "Non Covered Days - (24)" - creates it if necessary
     */
    public NM getUb19_NonCoveredDays() { 
		NM retVal = this.getTypedField(9, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Value Amount & Code (46-49) (UB1-10).
     */
    public UVC[] getValueAmountCode4649() {
    	UVC[] retVal = this.getTypedField(10, new UVC[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Value Amount & Code (46-49) (UB1-10).
     */
    public UVC[] getUb110_ValueAmountCode4649() {
    	UVC[] retVal = this.getTypedField(10, new UVC[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Value Amount & Code (46-49) (UB1-10).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getValueAmountCode4649Reps() {
    	return this.getReps(10);
    }


    /**
     * Returns a specific repetition of
     * UB1-10: "Value Amount & Code (46-49)" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public UVC getValueAmountCode4649(int rep) { 
		UVC retVal = this.getTypedField(10, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * UB1-10: "Value Amount & Code (46-49)" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public UVC getUb110_ValueAmountCode4649(int rep) { 
		UVC retVal = this.getTypedField(10, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Value Amount & Code (46-49) (UB1-10).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getUb110_ValueAmountCode4649Reps() {
    	return this.getReps(10);
    }


    /**
     * Inserts a repetition of
     * UB1-10: "Value Amount & Code (46-49)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public UVC insertValueAmountCode4649(int rep) throws HL7Exception { 
        return (UVC) super.insertRepetition(10, rep);
    }


    /**
     * Inserts a repetition of
     * UB1-10: "Value Amount & Code (46-49)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public UVC insertUb110_ValueAmountCode4649(int rep) throws HL7Exception { 
        return (UVC) super.insertRepetition(10, rep);
    }


    /**
     * Removes a repetition of
     * UB1-10: "Value Amount & Code (46-49)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public UVC removeValueAmountCode4649(int rep) throws HL7Exception { 
        return (UVC) super.removeRepetition(10, rep);
    }


    /**
     * Removes a repetition of
     * UB1-10: "Value Amount & Code (46-49)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public UVC removeUb110_ValueAmountCode4649(int rep) throws HL7Exception { 
        return (UVC) super.removeRepetition(10, rep);
    }




    /**
     * Returns
     * UB1-11: "Number Of Grace Days (90)" - creates it if necessary
     */
    public NM getNumberOfGraceDays() { 
		NM retVal = this.getTypedField(11, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-11: "Number Of Grace Days (90)" - creates it if necessary
     */
    public NM getUb111_NumberOfGraceDays() { 
		NM retVal = this.getTypedField(11, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-12: "Special Program Indicator (44)" - creates it if necessary
     */
    public CE getSpecialProgramIndicator() { 
		CE retVal = this.getTypedField(12, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-12: "Special Program Indicator (44)" - creates it if necessary
     */
    public CE getUb112_SpecialProgramIndicator() { 
		CE retVal = this.getTypedField(12, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-13: "PSRO/UR Approval Indicator (87)" - creates it if necessary
     */
    public CE getPSROURApprovalIndicator() { 
		CE retVal = this.getTypedField(13, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-13: "PSRO/UR Approval Indicator (87)" - creates it if necessary
     */
    public CE getUb113_PSROURApprovalIndicator() { 
		CE retVal = this.getTypedField(13, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-14: "PSRO/UR Approved Stay-Fm (88)" - creates it if necessary
     */
    public DT getPSROURApprovedStayFm() { 
		DT retVal = this.getTypedField(14, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-14: "PSRO/UR Approved Stay-Fm (88)" - creates it if necessary
     */
    public DT getUb114_PSROURApprovedStayFm() { 
		DT retVal = this.getTypedField(14, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-15: "PSRO/UR Approved Stay-To (89)" - creates it if necessary
     */
    public DT getPSROURApprovedStayTo() { 
		DT retVal = this.getTypedField(15, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-15: "PSRO/UR Approved Stay-To (89)" - creates it if necessary
     */
    public DT getUb115_PSROURApprovedStayTo() { 
		DT retVal = this.getTypedField(15, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Occurrence (28-32) (UB1-16).
     */
    public OCD[] getOccurrence2832() {
    	OCD[] retVal = this.getTypedField(16, new OCD[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Occurrence (28-32) (UB1-16).
     */
    public OCD[] getUb116_Occurrence2832() {
    	OCD[] retVal = this.getTypedField(16, new OCD[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Occurrence (28-32) (UB1-16).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getOccurrence2832Reps() {
    	return this.getReps(16);
    }


    /**
     * Returns a specific repetition of
     * UB1-16: "Occurrence (28-32)" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public OCD getOccurrence2832(int rep) { 
		OCD retVal = this.getTypedField(16, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * UB1-16: "Occurrence (28-32)" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public OCD getUb116_Occurrence2832(int rep) { 
		OCD retVal = this.getTypedField(16, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Occurrence (28-32) (UB1-16).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getUb116_Occurrence2832Reps() {
    	return this.getReps(16);
    }


    /**
     * Inserts a repetition of
     * UB1-16: "Occurrence (28-32)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public OCD insertOccurrence2832(int rep) throws HL7Exception { 
        return (OCD) super.insertRepetition(16, rep);
    }


    /**
     * Inserts a repetition of
     * UB1-16: "Occurrence (28-32)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public OCD insertUb116_Occurrence2832(int rep) throws HL7Exception { 
        return (OCD) super.insertRepetition(16, rep);
    }


    /**
     * Removes a repetition of
     * UB1-16: "Occurrence (28-32)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public OCD removeOccurrence2832(int rep) throws HL7Exception { 
        return (OCD) super.removeRepetition(16, rep);
    }


    /**
     * Removes a repetition of
     * UB1-16: "Occurrence (28-32)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public OCD removeUb116_Occurrence2832(int rep) throws HL7Exception { 
        return (OCD) super.removeRepetition(16, rep);
    }




    /**
     * Returns
     * UB1-17: "Occurrence Span (33)" - creates it if necessary
     */
    public CE getOccurrenceSpan() { 
		CE retVal = this.getTypedField(17, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-17: "Occurrence Span (33)" - creates it if necessary
     */
    public CE getUb117_OccurrenceSpan() { 
		CE retVal = this.getTypedField(17, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-18: "Occur Span Start Date(33)" - creates it if necessary
     */
    public DT getOccurSpanStartDate() { 
		DT retVal = this.getTypedField(18, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-18: "Occur Span Start Date(33)" - creates it if necessary
     */
    public DT getUb118_OccurSpanStartDate() { 
		DT retVal = this.getTypedField(18, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-19: "Occur Span End Date (33)" - creates it if necessary
     */
    public DT getOccurSpanEndDate() { 
		DT retVal = this.getTypedField(19, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-19: "Occur Span End Date (33)" - creates it if necessary
     */
    public DT getUb119_OccurSpanEndDate() { 
		DT retVal = this.getTypedField(19, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-20: "UB-82 Locator 2" - creates it if necessary
     */
    public ST getUB82Locator2() { 
		ST retVal = this.getTypedField(20, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-20: "UB-82 Locator 2" - creates it if necessary
     */
    public ST getUb120_UB82Locator2() { 
		ST retVal = this.getTypedField(20, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-21: "UB-82 Locator 9" - creates it if necessary
     */
    public ST getUB82Locator9() { 
		ST retVal = this.getTypedField(21, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-21: "UB-82 Locator 9" - creates it if necessary
     */
    public ST getUb121_UB82Locator9() { 
		ST retVal = this.getTypedField(21, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-22: "UB-82 Locator 27" - creates it if necessary
     */
    public ST getUB82Locator27() { 
		ST retVal = this.getTypedField(22, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-22: "UB-82 Locator 27" - creates it if necessary
     */
    public ST getUb122_UB82Locator27() { 
		ST retVal = this.getTypedField(22, 0);
		return retVal;
    }



    /**
     * Returns
     * UB1-23: "UB-82 Locator 45" - creates it if necessary
     */
    public ST getUB82Locator45() { 
		ST retVal = this.getTypedField(23, 0);
		return retVal;
    }
    
    /**
     * Returns
     * UB1-23: "UB-82 Locator 45" - creates it if necessary
     */
    public ST getUb123_UB82Locator45() { 
		ST retVal = this.getTypedField(23, 0);
		return retVal;
    }





    /** {@inheritDoc} */   
    protected Type createNewTypeWithoutReflection(int field) {
       switch (field) {
          case 0: return new SI(getMessage());
          case 1: return new NM(getMessage());
          case 2: return new NM(getMessage());
          case 3: return new NM(getMessage());
          case 4: return new NM(getMessage());
          case 5: return new NM(getMessage());
          case 6: return new IS(getMessage(), new Integer( 43 ));
          case 7: return new NM(getMessage());
          case 8: return new NM(getMessage());
          case 9: return new UVC(getMessage());
          case 10: return new NM(getMessage());
          case 11: return new CE(getMessage());
          case 12: return new CE(getMessage());
          case 13: return new DT(getMessage());
          case 14: return new DT(getMessage());
          case 15: return new OCD(getMessage());
          case 16: return new CE(getMessage());
          case 17: return new DT(getMessage());
          case 18: return new DT(getMessage());
          case 19: return new ST(getMessage());
          case 20: return new ST(getMessage());
          case 21: return new ST(getMessage());
          case 22: return new ST(getMessage());
          default: return null;
       }
   }


}

