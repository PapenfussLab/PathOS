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
 *<p>Represents an HL7 OBX message segment (Observation/Result). 
 * This segment has the following fields:</p>
 * <ul>
     * <li>OBX-1: Set ID - OBX (SI) <b>optional </b>
     * <li>OBX-2: Value Type (ID) <b>optional </b>
     * <li>OBX-3: Observation Identifier (CE) <b> </b>
     * <li>OBX-4: Observation Sub-ID (ST) <b>optional </b>
     * <li>OBX-5: Observation Value (Varies) <b>optional repeating</b>
     * <li>OBX-6: Units (CE) <b>optional </b>
     * <li>OBX-7: References Range (ST) <b>optional </b>
     * <li>OBX-8: Abnormal Flags (IS) <b>optional repeating</b>
     * <li>OBX-9: Probability (NM) <b>optional </b>
     * <li>OBX-10: Nature of Abnormal Test (ID) <b>optional repeating</b>
     * <li>OBX-11: Observation Result Status (ID) <b> </b>
     * <li>OBX-12: Effective Date of Reference Range Values (TS) <b>optional </b>
     * <li>OBX-13: User Defined Access Checks (ST) <b>optional </b>
     * <li>OBX-14: Date/Time of the Observation (TS) <b>optional </b>
     * <li>OBX-15: Producer's Reference (CE) <b>optional </b>
     * <li>OBX-16: Responsible Observer (XCN) <b>optional repeating</b>
     * <li>OBX-17: Observation Method (CE) <b>optional repeating</b>
     * <li>OBX-18: Equipment Instance Identifier (EI) <b>optional repeating</b>
     * <li>OBX-19: Date/Time of the Analysis (TS) <b>optional </b>
     * <li>OBX-20: Reserved for harmonization with V2.6 (Varies) <b>optional </b>
     * <li>OBX-21: Reserved for harmonization with V2.6 Number 2 (Varies) <b>optional </b>
     * <li>OBX-22: Reserved for harmonization with V2.6 Number 3 (Varies) <b>optional </b>
     * <li>OBX-23: Performing Organization Name (XON) <b>optional </b>
     * <li>OBX-24: Performing Organization Address (XAD) <b>optional </b>
     * <li>OBX-25: Performing Organization Medical Director (XCN) <b>optional </b>
 * </ul>
 */
@SuppressWarnings("unused")
public class OBX extends AbstractSegment {

    /** 
     * Creates a new OBX segment
     */
    public OBX(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(SI.class, false, 1, 4, new Object[]{ getMessage() }, "Set ID - OBX");
                                              this.add(ID.class, false, 1, 2, new Object[]{ getMessage(), new Integer(125) }, "Value Type");
                                  this.add(CE.class, true, 1, 250, new Object[]{ getMessage() }, "Observation Identifier");
                                  this.add(ST.class, false, 1, 20, new Object[]{ getMessage() }, "Observation Sub-ID");
                                  this.add(Varies.class, false, 0, 99999, new Object[]{ getMessage() }, "Observation Value");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Units");
                                  this.add(ST.class, false, 1, 60, new Object[]{ getMessage() }, "References Range");
                                              this.add(IS.class, false, 0, 5, new Object[]{ getMessage(), new Integer(78) }, "Abnormal Flags");
                                  this.add(NM.class, false, 1, 5, new Object[]{ getMessage() }, "Probability");
                                              this.add(ID.class, false, 0, 2, new Object[]{ getMessage(), new Integer(80) }, "Nature of Abnormal Test");
                                              this.add(ID.class, true, 1, 1, new Object[]{ getMessage(), new Integer(85) }, "Observation Result Status");
                                  this.add(TS.class, false, 1, 26, new Object[]{ getMessage() }, "Effective Date of Reference Range Values");
                                  this.add(ST.class, false, 1, 20, new Object[]{ getMessage() }, "User Defined Access Checks");
                                  this.add(TS.class, false, 1, 26, new Object[]{ getMessage() }, "Date/Time of the Observation");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Producer's Reference");
                                  this.add(XCN.class, false, 0, 250, new Object[]{ getMessage() }, "Responsible Observer");
                                  this.add(CE.class, false, 0, 250, new Object[]{ getMessage() }, "Observation Method");
                                  this.add(EI.class, false, 0, 22, new Object[]{ getMessage() }, "Equipment Instance Identifier");
                                  this.add(TS.class, false, 1, 26, new Object[]{ getMessage() }, "Date/Time of the Analysis");
                                  this.add(Varies.class, false, 1, 0, new Object[]{ getMessage() }, "Reserved for harmonization with V2.6");
                                  this.add(Varies.class, false, 1, 0, new Object[]{ getMessage() }, "Reserved for harmonization with V2.6 Number 2");
                                  this.add(Varies.class, false, 1, 0, new Object[]{ getMessage() }, "Reserved for harmonization with V2.6 Number 3");
                                  this.add(XON.class, false, 1, 567, new Object[]{ getMessage() }, "Performing Organization Name");
                                  this.add(XAD.class, false, 1, 631, new Object[]{ getMessage() }, "Performing Organization Address");
                                  this.add(XCN.class, false, 1, 3002, new Object[]{ getMessage() }, "Performing Organization Medical Director");
       } catch(HL7Exception e) {
          log.error("Unexpected error creating OBX - this is probably a bug in the source code generator.", e);
       }
    }



    /**
     * Returns
     * OBX-1: "Set ID - OBX" - creates it if necessary
     */
    public SI getSetIDOBX() { 
		SI retVal = this.getTypedField(1, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-1: "Set ID - OBX" - creates it if necessary
     */
    public SI getObx1_SetIDOBX() { 
		SI retVal = this.getTypedField(1, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-2: "Value Type" - creates it if necessary
     */
    public ID getValueType() { 
		ID retVal = this.getTypedField(2, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-2: "Value Type" - creates it if necessary
     */
    public ID getObx2_ValueType() { 
		ID retVal = this.getTypedField(2, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-3: "Observation Identifier" - creates it if necessary
     */
    public CE getObservationIdentifier() { 
		CE retVal = this.getTypedField(3, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-3: "Observation Identifier" - creates it if necessary
     */
    public CE getObx3_ObservationIdentifier() { 
		CE retVal = this.getTypedField(3, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-4: "Observation Sub-ID" - creates it if necessary
     */
    public ST getObservationSubID() { 
		ST retVal = this.getTypedField(4, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-4: "Observation Sub-ID" - creates it if necessary
     */
    public ST getObx4_ObservationSubID() { 
		ST retVal = this.getTypedField(4, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Observation Value (OBX-5).
     */
    public Varies[] getObservationValue() {
    	Varies[] retVal = this.getTypedField(5, new Varies[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Observation Value (OBX-5).
     */
    public Varies[] getObx5_ObservationValue() {
    	Varies[] retVal = this.getTypedField(5, new Varies[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Observation Value (OBX-5).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getObservationValueReps() {
    	return this.getReps(5);
    }


    /**
     * Returns a specific repetition of
     * OBX-5: "Observation Value" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public Varies getObservationValue(int rep) { 
		Varies retVal = this.getTypedField(5, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * OBX-5: "Observation Value" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public Varies getObx5_ObservationValue(int rep) { 
		Varies retVal = this.getTypedField(5, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Observation Value (OBX-5).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getObx5_ObservationValueReps() {
    	return this.getReps(5);
    }


    /**
     * Inserts a repetition of
     * OBX-5: "Observation Value" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public Varies insertObservationValue(int rep) throws HL7Exception { 
        return (Varies) super.insertRepetition(5, rep);
    }


    /**
     * Inserts a repetition of
     * OBX-5: "Observation Value" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public Varies insertObx5_ObservationValue(int rep) throws HL7Exception { 
        return (Varies) super.insertRepetition(5, rep);
    }


    /**
     * Removes a repetition of
     * OBX-5: "Observation Value" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public Varies removeObservationValue(int rep) throws HL7Exception { 
        return (Varies) super.removeRepetition(5, rep);
    }


    /**
     * Removes a repetition of
     * OBX-5: "Observation Value" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public Varies removeObx5_ObservationValue(int rep) throws HL7Exception { 
        return (Varies) super.removeRepetition(5, rep);
    }




    /**
     * Returns
     * OBX-6: "Units" - creates it if necessary
     */
    public CE getUnits() { 
		CE retVal = this.getTypedField(6, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-6: "Units" - creates it if necessary
     */
    public CE getObx6_Units() { 
		CE retVal = this.getTypedField(6, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-7: "References Range" - creates it if necessary
     */
    public ST getReferencesRange() { 
		ST retVal = this.getTypedField(7, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-7: "References Range" - creates it if necessary
     */
    public ST getObx7_ReferencesRange() { 
		ST retVal = this.getTypedField(7, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Abnormal Flags (OBX-8).
     */
    public IS[] getAbnormalFlags() {
    	IS[] retVal = this.getTypedField(8, new IS[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Abnormal Flags (OBX-8).
     */
    public IS[] getObx8_AbnormalFlags() {
    	IS[] retVal = this.getTypedField(8, new IS[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Abnormal Flags (OBX-8).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getAbnormalFlagsReps() {
    	return this.getReps(8);
    }


    /**
     * Returns a specific repetition of
     * OBX-8: "Abnormal Flags" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getAbnormalFlags(int rep) { 
		IS retVal = this.getTypedField(8, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * OBX-8: "Abnormal Flags" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getObx8_AbnormalFlags(int rep) { 
		IS retVal = this.getTypedField(8, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Abnormal Flags (OBX-8).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getObx8_AbnormalFlagsReps() {
    	return this.getReps(8);
    }


    /**
     * Inserts a repetition of
     * OBX-8: "Abnormal Flags" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertAbnormalFlags(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(8, rep);
    }


    /**
     * Inserts a repetition of
     * OBX-8: "Abnormal Flags" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertObx8_AbnormalFlags(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(8, rep);
    }


    /**
     * Removes a repetition of
     * OBX-8: "Abnormal Flags" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeAbnormalFlags(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(8, rep);
    }


    /**
     * Removes a repetition of
     * OBX-8: "Abnormal Flags" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeObx8_AbnormalFlags(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(8, rep);
    }




    /**
     * Returns
     * OBX-9: "Probability" - creates it if necessary
     */
    public NM getProbability() { 
		NM retVal = this.getTypedField(9, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-9: "Probability" - creates it if necessary
     */
    public NM getObx9_Probability() { 
		NM retVal = this.getTypedField(9, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Nature of Abnormal Test (OBX-10).
     */
    public ID[] getNatureOfAbnormalTest() {
    	ID[] retVal = this.getTypedField(10, new ID[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Nature of Abnormal Test (OBX-10).
     */
    public ID[] getObx10_NatureOfAbnormalTest() {
    	ID[] retVal = this.getTypedField(10, new ID[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Nature of Abnormal Test (OBX-10).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getNatureOfAbnormalTestReps() {
    	return this.getReps(10);
    }


    /**
     * Returns a specific repetition of
     * OBX-10: "Nature of Abnormal Test" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public ID getNatureOfAbnormalTest(int rep) { 
		ID retVal = this.getTypedField(10, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * OBX-10: "Nature of Abnormal Test" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public ID getObx10_NatureOfAbnormalTest(int rep) { 
		ID retVal = this.getTypedField(10, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Nature of Abnormal Test (OBX-10).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getObx10_NatureOfAbnormalTestReps() {
    	return this.getReps(10);
    }


    /**
     * Inserts a repetition of
     * OBX-10: "Nature of Abnormal Test" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ID insertNatureOfAbnormalTest(int rep) throws HL7Exception { 
        return (ID) super.insertRepetition(10, rep);
    }


    /**
     * Inserts a repetition of
     * OBX-10: "Nature of Abnormal Test" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ID insertObx10_NatureOfAbnormalTest(int rep) throws HL7Exception { 
        return (ID) super.insertRepetition(10, rep);
    }


    /**
     * Removes a repetition of
     * OBX-10: "Nature of Abnormal Test" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ID removeNatureOfAbnormalTest(int rep) throws HL7Exception { 
        return (ID) super.removeRepetition(10, rep);
    }


    /**
     * Removes a repetition of
     * OBX-10: "Nature of Abnormal Test" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ID removeObx10_NatureOfAbnormalTest(int rep) throws HL7Exception { 
        return (ID) super.removeRepetition(10, rep);
    }




    /**
     * Returns
     * OBX-11: "Observation Result Status" - creates it if necessary
     */
    public ID getObservationResultStatus() { 
		ID retVal = this.getTypedField(11, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-11: "Observation Result Status" - creates it if necessary
     */
    public ID getObx11_ObservationResultStatus() { 
		ID retVal = this.getTypedField(11, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-12: "Effective Date of Reference Range Values" - creates it if necessary
     */
    public TS getEffectiveDateOfReferenceRangeValues() { 
		TS retVal = this.getTypedField(12, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-12: "Effective Date of Reference Range Values" - creates it if necessary
     */
    public TS getObx12_EffectiveDateOfReferenceRangeValues() { 
		TS retVal = this.getTypedField(12, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-13: "User Defined Access Checks" - creates it if necessary
     */
    public ST getUserDefinedAccessChecks() { 
		ST retVal = this.getTypedField(13, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-13: "User Defined Access Checks" - creates it if necessary
     */
    public ST getObx13_UserDefinedAccessChecks() { 
		ST retVal = this.getTypedField(13, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-14: "Date/Time of the Observation" - creates it if necessary
     */
    public TS getDateTimeOfTheObservation() { 
		TS retVal = this.getTypedField(14, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-14: "Date/Time of the Observation" - creates it if necessary
     */
    public TS getObx14_DateTimeOfTheObservation() { 
		TS retVal = this.getTypedField(14, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-15: "Producer's Reference" - creates it if necessary
     */
    public CE getProducerSReference() { 
		CE retVal = this.getTypedField(15, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-15: "Producer's Reference" - creates it if necessary
     */
    public CE getObx15_ProducerSReference() { 
		CE retVal = this.getTypedField(15, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Responsible Observer (OBX-16).
     */
    public XCN[] getResponsibleObserver() {
    	XCN[] retVal = this.getTypedField(16, new XCN[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Responsible Observer (OBX-16).
     */
    public XCN[] getObx16_ResponsibleObserver() {
    	XCN[] retVal = this.getTypedField(16, new XCN[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Responsible Observer (OBX-16).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getResponsibleObserverReps() {
    	return this.getReps(16);
    }


    /**
     * Returns a specific repetition of
     * OBX-16: "Responsible Observer" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public XCN getResponsibleObserver(int rep) { 
		XCN retVal = this.getTypedField(16, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * OBX-16: "Responsible Observer" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public XCN getObx16_ResponsibleObserver(int rep) { 
		XCN retVal = this.getTypedField(16, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Responsible Observer (OBX-16).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getObx16_ResponsibleObserverReps() {
    	return this.getReps(16);
    }


    /**
     * Inserts a repetition of
     * OBX-16: "Responsible Observer" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XCN insertResponsibleObserver(int rep) throws HL7Exception { 
        return (XCN) super.insertRepetition(16, rep);
    }


    /**
     * Inserts a repetition of
     * OBX-16: "Responsible Observer" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XCN insertObx16_ResponsibleObserver(int rep) throws HL7Exception { 
        return (XCN) super.insertRepetition(16, rep);
    }


    /**
     * Removes a repetition of
     * OBX-16: "Responsible Observer" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XCN removeResponsibleObserver(int rep) throws HL7Exception { 
        return (XCN) super.removeRepetition(16, rep);
    }


    /**
     * Removes a repetition of
     * OBX-16: "Responsible Observer" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XCN removeObx16_ResponsibleObserver(int rep) throws HL7Exception { 
        return (XCN) super.removeRepetition(16, rep);
    }



    /**
     * Returns all repetitions of Observation Method (OBX-17).
     */
    public CE[] getObservationMethod() {
    	CE[] retVal = this.getTypedField(17, new CE[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Observation Method (OBX-17).
     */
    public CE[] getObx17_ObservationMethod() {
    	CE[] retVal = this.getTypedField(17, new CE[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Observation Method (OBX-17).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getObservationMethodReps() {
    	return this.getReps(17);
    }


    /**
     * Returns a specific repetition of
     * OBX-17: "Observation Method" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getObservationMethod(int rep) { 
		CE retVal = this.getTypedField(17, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * OBX-17: "Observation Method" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getObx17_ObservationMethod(int rep) { 
		CE retVal = this.getTypedField(17, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Observation Method (OBX-17).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getObx17_ObservationMethodReps() {
    	return this.getReps(17);
    }


    /**
     * Inserts a repetition of
     * OBX-17: "Observation Method" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertObservationMethod(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(17, rep);
    }


    /**
     * Inserts a repetition of
     * OBX-17: "Observation Method" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertObx17_ObservationMethod(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(17, rep);
    }


    /**
     * Removes a repetition of
     * OBX-17: "Observation Method" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeObservationMethod(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(17, rep);
    }


    /**
     * Removes a repetition of
     * OBX-17: "Observation Method" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeObx17_ObservationMethod(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(17, rep);
    }



    /**
     * Returns all repetitions of Equipment Instance Identifier (OBX-18).
     */
    public EI[] getEquipmentInstanceIdentifier() {
    	EI[] retVal = this.getTypedField(18, new EI[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Equipment Instance Identifier (OBX-18).
     */
    public EI[] getObx18_EquipmentInstanceIdentifier() {
    	EI[] retVal = this.getTypedField(18, new EI[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Equipment Instance Identifier (OBX-18).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getEquipmentInstanceIdentifierReps() {
    	return this.getReps(18);
    }


    /**
     * Returns a specific repetition of
     * OBX-18: "Equipment Instance Identifier" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public EI getEquipmentInstanceIdentifier(int rep) { 
		EI retVal = this.getTypedField(18, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * OBX-18: "Equipment Instance Identifier" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public EI getObx18_EquipmentInstanceIdentifier(int rep) { 
		EI retVal = this.getTypedField(18, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Equipment Instance Identifier (OBX-18).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getObx18_EquipmentInstanceIdentifierReps() {
    	return this.getReps(18);
    }


    /**
     * Inserts a repetition of
     * OBX-18: "Equipment Instance Identifier" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public EI insertEquipmentInstanceIdentifier(int rep) throws HL7Exception { 
        return (EI) super.insertRepetition(18, rep);
    }


    /**
     * Inserts a repetition of
     * OBX-18: "Equipment Instance Identifier" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public EI insertObx18_EquipmentInstanceIdentifier(int rep) throws HL7Exception { 
        return (EI) super.insertRepetition(18, rep);
    }


    /**
     * Removes a repetition of
     * OBX-18: "Equipment Instance Identifier" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public EI removeEquipmentInstanceIdentifier(int rep) throws HL7Exception { 
        return (EI) super.removeRepetition(18, rep);
    }


    /**
     * Removes a repetition of
     * OBX-18: "Equipment Instance Identifier" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public EI removeObx18_EquipmentInstanceIdentifier(int rep) throws HL7Exception { 
        return (EI) super.removeRepetition(18, rep);
    }




    /**
     * Returns
     * OBX-19: "Date/Time of the Analysis" - creates it if necessary
     */
    public TS getDateTimeOfTheAnalysis() { 
		TS retVal = this.getTypedField(19, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-19: "Date/Time of the Analysis" - creates it if necessary
     */
    public TS getObx19_DateTimeOfTheAnalysis() { 
		TS retVal = this.getTypedField(19, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-20: "Reserved for harmonization with V2.6" - creates it if necessary
     */
    public Varies getReservedForHarmonizationWithV26() { 
		Varies retVal = this.getTypedField(20, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-20: "Reserved for harmonization with V2.6" - creates it if necessary
     */
    public Varies getObx20_ReservedForHarmonizationWithV26() { 
		Varies retVal = this.getTypedField(20, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-21: "Reserved for harmonization with V2.6 Number 2" - creates it if necessary
     */
    public Varies getReservedForHarmonizationWithV26Number2() { 
		Varies retVal = this.getTypedField(21, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-21: "Reserved for harmonization with V2.6 Number 2" - creates it if necessary
     */
    public Varies getObx21_ReservedForHarmonizationWithV26Number2() { 
		Varies retVal = this.getTypedField(21, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-22: "Reserved for harmonization with V2.6 Number 3" - creates it if necessary
     */
    public Varies getReservedForHarmonizationWithV26Number3() { 
		Varies retVal = this.getTypedField(22, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-22: "Reserved for harmonization with V2.6 Number 3" - creates it if necessary
     */
    public Varies getObx22_ReservedForHarmonizationWithV26Number3() { 
		Varies retVal = this.getTypedField(22, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-23: "Performing Organization Name" - creates it if necessary
     */
    public XON getPerformingOrganizationName() { 
		XON retVal = this.getTypedField(23, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-23: "Performing Organization Name" - creates it if necessary
     */
    public XON getObx23_PerformingOrganizationName() { 
		XON retVal = this.getTypedField(23, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-24: "Performing Organization Address" - creates it if necessary
     */
    public XAD getPerformingOrganizationAddress() { 
		XAD retVal = this.getTypedField(24, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-24: "Performing Organization Address" - creates it if necessary
     */
    public XAD getObx24_PerformingOrganizationAddress() { 
		XAD retVal = this.getTypedField(24, 0);
		return retVal;
    }



    /**
     * Returns
     * OBX-25: "Performing Organization Medical Director" - creates it if necessary
     */
    public XCN getPerformingOrganizationMedicalDirector() { 
		XCN retVal = this.getTypedField(25, 0);
		return retVal;
    }
    
    /**
     * Returns
     * OBX-25: "Performing Organization Medical Director" - creates it if necessary
     */
    public XCN getObx25_PerformingOrganizationMedicalDirector() { 
		XCN retVal = this.getTypedField(25, 0);
		return retVal;
    }





    /** {@inheritDoc} */   
    protected Type createNewTypeWithoutReflection(int field) {
       switch (field) {
          case 0: return new SI(getMessage());
          case 1: return new ID(getMessage(), new Integer( 125 ));
          case 2: return new CE(getMessage());
          case 3: return new ST(getMessage());
          case 4: return new Varies(getMessage());
          case 5: return new CE(getMessage());
          case 6: return new ST(getMessage());
          case 7: return new IS(getMessage(), new Integer( 78 ));
          case 8: return new NM(getMessage());
          case 9: return new ID(getMessage(), new Integer( 80 ));
          case 10: return new ID(getMessage(), new Integer( 85 ));
          case 11: return new TS(getMessage());
          case 12: return new ST(getMessage());
          case 13: return new TS(getMessage());
          case 14: return new CE(getMessage());
          case 15: return new XCN(getMessage());
          case 16: return new CE(getMessage());
          case 17: return new EI(getMessage());
          case 18: return new TS(getMessage());
          case 19: return new Varies(getMessage());
          case 20: return new Varies(getMessage());
          case 21: return new Varies(getMessage());
          case 22: return new XON(getMessage());
          case 23: return new XAD(getMessage());
          case 24: return new XCN(getMessage());
          default: return null;
       }
   }


}

