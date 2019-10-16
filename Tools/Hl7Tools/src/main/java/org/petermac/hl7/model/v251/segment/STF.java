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
 *<p>Represents an HL7 STF message segment (Staff Identification). 
 * This segment has the following fields:</p>
 * <ul>
     * <li>STF-1: Primary Key Value - STF (CE) <b>optional </b>
     * <li>STF-2: Staff Identifier List (CX) <b>optional repeating</b>
     * <li>STF-3: Staff Name (XPN) <b>optional repeating</b>
     * <li>STF-4: Staff Type (IS) <b>optional repeating</b>
     * <li>STF-5: Administrative Sex (IS) <b>optional </b>
     * <li>STF-6: Date/Time of Birth (TS) <b>optional </b>
     * <li>STF-7: Active/Inactive Flag (ID) <b>optional </b>
     * <li>STF-8: Department (CE) <b>optional repeating</b>
     * <li>STF-9: Hospital Service - STF (CE) <b>optional repeating</b>
     * <li>STF-10: Phone (XTN) <b>optional repeating</b>
     * <li>STF-11: Office/Home Address/Birthplace (XAD) <b>optional repeating</b>
     * <li>STF-12: Institution Activation Date (DIN) <b>optional repeating</b>
     * <li>STF-13: Institution Inactivation Date (DIN) <b>optional repeating</b>
     * <li>STF-14: Backup Person ID (CE) <b>optional repeating</b>
     * <li>STF-15: E-Mail Address (ST) <b>optional repeating</b>
     * <li>STF-16: Preferred Method of Contact (CE) <b>optional </b>
     * <li>STF-17: Marital Status (CE) <b>optional </b>
     * <li>STF-18: Job Title (ST) <b>optional </b>
     * <li>STF-19: Job Code/Class (JCC) <b>optional </b>
     * <li>STF-20: Employment Status Code (CE) <b>optional </b>
     * <li>STF-21: Additional Insured on Auto (ID) <b>optional </b>
     * <li>STF-22: Driver's License Number - Staff (DLN) <b>optional </b>
     * <li>STF-23: Copy Auto Ins (ID) <b>optional </b>
     * <li>STF-24: Auto Ins. Expires (DT) <b>optional </b>
     * <li>STF-25: Date Last DMV Review (DT) <b>optional </b>
     * <li>STF-26: Date Next DMV Review (DT) <b>optional </b>
     * <li>STF-27: Race (CE) <b>optional </b>
     * <li>STF-28: Ethnic Group (CE) <b>optional </b>
     * <li>STF-29: Re-activation Approval Indicator (ID) <b>optional </b>
     * <li>STF-30: Citizenship (CE) <b>optional repeating</b>
     * <li>STF-31: Death Date and Time (TS) <b>optional </b>
     * <li>STF-32: Death Indicator (ID) <b>optional </b>
     * <li>STF-33: Institution Relationship Type Code (CWE) <b>optional </b>
     * <li>STF-34: Institution Relationship Period (DR) <b>optional </b>
     * <li>STF-35: Expected Return Date (DT) <b>optional </b>
     * <li>STF-36: Cost Center Code (CWE) <b>optional repeating</b>
     * <li>STF-37: Generic Classification Indicator (ID) <b>optional </b>
     * <li>STF-38: Inactive Reason Code (CWE) <b>optional </b>
 * </ul>
 */
@SuppressWarnings("unused")
public class STF extends AbstractSegment {

    /** 
     * Creates a new STF segment
     */
    public STF(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Primary Key Value - STF");
                                  this.add(CX.class, false, 0, 250, new Object[]{ getMessage() }, "Staff Identifier List");
                                  this.add(XPN.class, false, 0, 250, new Object[]{ getMessage() }, "Staff Name");
                                              this.add(IS.class, false, 0, 2, new Object[]{ getMessage(), new Integer(182) }, "Staff Type");
                                              this.add(IS.class, false, 1, 1, new Object[]{ getMessage(), new Integer(1) }, "Administrative Sex");
                                  this.add(TS.class, false, 1, 26, new Object[]{ getMessage() }, "Date/Time of Birth");
                                              this.add(ID.class, false, 1, 1, new Object[]{ getMessage(), new Integer(183) }, "Active/Inactive Flag");
                                  this.add(CE.class, false, 0, 250, new Object[]{ getMessage() }, "Department");
                                  this.add(CE.class, false, 0, 250, new Object[]{ getMessage() }, "Hospital Service - STF");
                                  this.add(XTN.class, false, 0, 250, new Object[]{ getMessage() }, "Phone");
                                  this.add(XAD.class, false, 0, 250, new Object[]{ getMessage() }, "Office/Home Address/Birthplace");
                                  this.add(DIN.class, false, 0, 276, new Object[]{ getMessage() }, "Institution Activation Date");
                                  this.add(DIN.class, false, 0, 276, new Object[]{ getMessage() }, "Institution Inactivation Date");
                                  this.add(CE.class, false, 0, 250, new Object[]{ getMessage() }, "Backup Person ID");
                                  this.add(ST.class, false, 0, 40, new Object[]{ getMessage() }, "E-Mail Address");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Preferred Method of Contact");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Marital Status");
                                  this.add(ST.class, false, 1, 20, new Object[]{ getMessage() }, "Job Title");
                                  this.add(JCC.class, false, 1, 20, new Object[]{ getMessage() }, "Job Code/Class");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Employment Status Code");
                                              this.add(ID.class, false, 1, 1, new Object[]{ getMessage(), new Integer(136) }, "Additional Insured on Auto");
                                  this.add(DLN.class, false, 1, 25, new Object[]{ getMessage() }, "Driver's License Number - Staff");
                                              this.add(ID.class, false, 1, 1, new Object[]{ getMessage(), new Integer(136) }, "Copy Auto Ins");
                                  this.add(DT.class, false, 1, 8, new Object[]{ getMessage() }, "Auto Ins. Expires");
                                  this.add(DT.class, false, 1, 8, new Object[]{ getMessage() }, "Date Last DMV Review");
                                  this.add(DT.class, false, 1, 8, new Object[]{ getMessage() }, "Date Next DMV Review");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Race");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Ethnic Group");
                                              this.add(ID.class, false, 1, 1, new Object[]{ getMessage(), new Integer(136) }, "Re-activation Approval Indicator");
                                  this.add(CE.class, false, 0, 250, new Object[]{ getMessage() }, "Citizenship");
                                  this.add(TS.class, false, 1, 8, new Object[]{ getMessage() }, "Death Date and Time");
                                              this.add(ID.class, false, 1, 1, new Object[]{ getMessage(), new Integer(136) }, "Death Indicator");
                                  this.add(CWE.class, false, 1, 250, new Object[]{ getMessage() }, "Institution Relationship Type Code");
                                  this.add(DR.class, false, 1, 52, new Object[]{ getMessage() }, "Institution Relationship Period");
                                  this.add(DT.class, false, 1, 8, new Object[]{ getMessage() }, "Expected Return Date");
                                  this.add(CWE.class, false, 0, 250, new Object[]{ getMessage() }, "Cost Center Code");
                                              this.add(ID.class, false, 1, 1, new Object[]{ getMessage(), new Integer(136) }, "Generic Classification Indicator");
                                  this.add(CWE.class, false, 1, 250, new Object[]{ getMessage() }, "Inactive Reason Code");
       } catch(HL7Exception e) {
          log.error("Unexpected error creating STF - this is probably a bug in the source code generator.", e);
       }
    }



    /**
     * Returns
     * STF-1: "Primary Key Value - STF" - creates it if necessary
     */
    public CE getPrimaryKeyValueSTF() { 
		CE retVal = this.getTypedField(1, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-1: "Primary Key Value - STF" - creates it if necessary
     */
    public CE getStf1_PrimaryKeyValueSTF() { 
		CE retVal = this.getTypedField(1, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Staff Identifier List (STF-2).
     */
    public CX[] getStaffIdentifierList() {
    	CX[] retVal = this.getTypedField(2, new CX[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Staff Identifier List (STF-2).
     */
    public CX[] getStf2_StaffIdentifierList() {
    	CX[] retVal = this.getTypedField(2, new CX[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Staff Identifier List (STF-2).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStaffIdentifierListReps() {
    	return this.getReps(2);
    }


    /**
     * Returns a specific repetition of
     * STF-2: "Staff Identifier List" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CX getStaffIdentifierList(int rep) { 
		CX retVal = this.getTypedField(2, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-2: "Staff Identifier List" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CX getStf2_StaffIdentifierList(int rep) { 
		CX retVal = this.getTypedField(2, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Staff Identifier List (STF-2).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf2_StaffIdentifierListReps() {
    	return this.getReps(2);
    }


    /**
     * Inserts a repetition of
     * STF-2: "Staff Identifier List" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CX insertStaffIdentifierList(int rep) throws HL7Exception { 
        return (CX) super.insertRepetition(2, rep);
    }


    /**
     * Inserts a repetition of
     * STF-2: "Staff Identifier List" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CX insertStf2_StaffIdentifierList(int rep) throws HL7Exception { 
        return (CX) super.insertRepetition(2, rep);
    }


    /**
     * Removes a repetition of
     * STF-2: "Staff Identifier List" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CX removeStaffIdentifierList(int rep) throws HL7Exception { 
        return (CX) super.removeRepetition(2, rep);
    }


    /**
     * Removes a repetition of
     * STF-2: "Staff Identifier List" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CX removeStf2_StaffIdentifierList(int rep) throws HL7Exception { 
        return (CX) super.removeRepetition(2, rep);
    }



    /**
     * Returns all repetitions of Staff Name (STF-3).
     */
    public XPN[] getStaffName() {
    	XPN[] retVal = this.getTypedField(3, new XPN[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Staff Name (STF-3).
     */
    public XPN[] getStf3_StaffName() {
    	XPN[] retVal = this.getTypedField(3, new XPN[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Staff Name (STF-3).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStaffNameReps() {
    	return this.getReps(3);
    }


    /**
     * Returns a specific repetition of
     * STF-3: "Staff Name" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public XPN getStaffName(int rep) { 
		XPN retVal = this.getTypedField(3, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-3: "Staff Name" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public XPN getStf3_StaffName(int rep) { 
		XPN retVal = this.getTypedField(3, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Staff Name (STF-3).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf3_StaffNameReps() {
    	return this.getReps(3);
    }


    /**
     * Inserts a repetition of
     * STF-3: "Staff Name" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XPN insertStaffName(int rep) throws HL7Exception { 
        return (XPN) super.insertRepetition(3, rep);
    }


    /**
     * Inserts a repetition of
     * STF-3: "Staff Name" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XPN insertStf3_StaffName(int rep) throws HL7Exception { 
        return (XPN) super.insertRepetition(3, rep);
    }


    /**
     * Removes a repetition of
     * STF-3: "Staff Name" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XPN removeStaffName(int rep) throws HL7Exception { 
        return (XPN) super.removeRepetition(3, rep);
    }


    /**
     * Removes a repetition of
     * STF-3: "Staff Name" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XPN removeStf3_StaffName(int rep) throws HL7Exception { 
        return (XPN) super.removeRepetition(3, rep);
    }



    /**
     * Returns all repetitions of Staff Type (STF-4).
     */
    public IS[] getStaffType() {
    	IS[] retVal = this.getTypedField(4, new IS[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Staff Type (STF-4).
     */
    public IS[] getStf4_StaffType() {
    	IS[] retVal = this.getTypedField(4, new IS[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Staff Type (STF-4).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStaffTypeReps() {
    	return this.getReps(4);
    }


    /**
     * Returns a specific repetition of
     * STF-4: "Staff Type" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getStaffType(int rep) { 
		IS retVal = this.getTypedField(4, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-4: "Staff Type" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public IS getStf4_StaffType(int rep) { 
		IS retVal = this.getTypedField(4, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Staff Type (STF-4).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf4_StaffTypeReps() {
    	return this.getReps(4);
    }


    /**
     * Inserts a repetition of
     * STF-4: "Staff Type" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertStaffType(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(4, rep);
    }


    /**
     * Inserts a repetition of
     * STF-4: "Staff Type" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS insertStf4_StaffType(int rep) throws HL7Exception { 
        return (IS) super.insertRepetition(4, rep);
    }


    /**
     * Removes a repetition of
     * STF-4: "Staff Type" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeStaffType(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(4, rep);
    }


    /**
     * Removes a repetition of
     * STF-4: "Staff Type" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public IS removeStf4_StaffType(int rep) throws HL7Exception { 
        return (IS) super.removeRepetition(4, rep);
    }




    /**
     * Returns
     * STF-5: "Administrative Sex" - creates it if necessary
     */
    public IS getAdministrativeSex() { 
		IS retVal = this.getTypedField(5, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-5: "Administrative Sex" - creates it if necessary
     */
    public IS getStf5_AdministrativeSex() { 
		IS retVal = this.getTypedField(5, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-6: "Date/Time of Birth" - creates it if necessary
     */
    public TS getDateTimeOfBirth() { 
		TS retVal = this.getTypedField(6, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-6: "Date/Time of Birth" - creates it if necessary
     */
    public TS getStf6_DateTimeOfBirth() { 
		TS retVal = this.getTypedField(6, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-7: "Active/Inactive Flag" - creates it if necessary
     */
    public ID getActiveInactiveFlag() { 
		ID retVal = this.getTypedField(7, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-7: "Active/Inactive Flag" - creates it if necessary
     */
    public ID getStf7_ActiveInactiveFlag() { 
		ID retVal = this.getTypedField(7, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Department (STF-8).
     */
    public CE[] getDepartment() {
    	CE[] retVal = this.getTypedField(8, new CE[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Department (STF-8).
     */
    public CE[] getStf8_Department() {
    	CE[] retVal = this.getTypedField(8, new CE[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Department (STF-8).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getDepartmentReps() {
    	return this.getReps(8);
    }


    /**
     * Returns a specific repetition of
     * STF-8: "Department" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getDepartment(int rep) { 
		CE retVal = this.getTypedField(8, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-8: "Department" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getStf8_Department(int rep) { 
		CE retVal = this.getTypedField(8, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Department (STF-8).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf8_DepartmentReps() {
    	return this.getReps(8);
    }


    /**
     * Inserts a repetition of
     * STF-8: "Department" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertDepartment(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(8, rep);
    }


    /**
     * Inserts a repetition of
     * STF-8: "Department" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertStf8_Department(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(8, rep);
    }


    /**
     * Removes a repetition of
     * STF-8: "Department" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeDepartment(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(8, rep);
    }


    /**
     * Removes a repetition of
     * STF-8: "Department" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeStf8_Department(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(8, rep);
    }



    /**
     * Returns all repetitions of Hospital Service - STF (STF-9).
     */
    public CE[] getHospitalServiceSTF() {
    	CE[] retVal = this.getTypedField(9, new CE[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Hospital Service - STF (STF-9).
     */
    public CE[] getStf9_HospitalServiceSTF() {
    	CE[] retVal = this.getTypedField(9, new CE[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Hospital Service - STF (STF-9).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getHospitalServiceSTFReps() {
    	return this.getReps(9);
    }


    /**
     * Returns a specific repetition of
     * STF-9: "Hospital Service - STF" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getHospitalServiceSTF(int rep) { 
		CE retVal = this.getTypedField(9, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-9: "Hospital Service - STF" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getStf9_HospitalServiceSTF(int rep) { 
		CE retVal = this.getTypedField(9, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Hospital Service - STF (STF-9).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf9_HospitalServiceSTFReps() {
    	return this.getReps(9);
    }


    /**
     * Inserts a repetition of
     * STF-9: "Hospital Service - STF" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertHospitalServiceSTF(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(9, rep);
    }


    /**
     * Inserts a repetition of
     * STF-9: "Hospital Service - STF" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertStf9_HospitalServiceSTF(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(9, rep);
    }


    /**
     * Removes a repetition of
     * STF-9: "Hospital Service - STF" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeHospitalServiceSTF(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(9, rep);
    }


    /**
     * Removes a repetition of
     * STF-9: "Hospital Service - STF" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeStf9_HospitalServiceSTF(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(9, rep);
    }



    /**
     * Returns all repetitions of Phone (STF-10).
     */
    public XTN[] getPhone() {
    	XTN[] retVal = this.getTypedField(10, new XTN[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Phone (STF-10).
     */
    public XTN[] getStf10_Phone() {
    	XTN[] retVal = this.getTypedField(10, new XTN[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Phone (STF-10).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getPhoneReps() {
    	return this.getReps(10);
    }


    /**
     * Returns a specific repetition of
     * STF-10: "Phone" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public XTN getPhone(int rep) { 
		XTN retVal = this.getTypedField(10, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-10: "Phone" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public XTN getStf10_Phone(int rep) { 
		XTN retVal = this.getTypedField(10, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Phone (STF-10).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf10_PhoneReps() {
    	return this.getReps(10);
    }


    /**
     * Inserts a repetition of
     * STF-10: "Phone" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XTN insertPhone(int rep) throws HL7Exception { 
        return (XTN) super.insertRepetition(10, rep);
    }


    /**
     * Inserts a repetition of
     * STF-10: "Phone" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XTN insertStf10_Phone(int rep) throws HL7Exception { 
        return (XTN) super.insertRepetition(10, rep);
    }


    /**
     * Removes a repetition of
     * STF-10: "Phone" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XTN removePhone(int rep) throws HL7Exception { 
        return (XTN) super.removeRepetition(10, rep);
    }


    /**
     * Removes a repetition of
     * STF-10: "Phone" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XTN removeStf10_Phone(int rep) throws HL7Exception { 
        return (XTN) super.removeRepetition(10, rep);
    }



    /**
     * Returns all repetitions of Office/Home Address/Birthplace (STF-11).
     */
    public XAD[] getOfficeHomeAddressBirthplace() {
    	XAD[] retVal = this.getTypedField(11, new XAD[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Office/Home Address/Birthplace (STF-11).
     */
    public XAD[] getStf11_OfficeHomeAddressBirthplace() {
    	XAD[] retVal = this.getTypedField(11, new XAD[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Office/Home Address/Birthplace (STF-11).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getOfficeHomeAddressBirthplaceReps() {
    	return this.getReps(11);
    }


    /**
     * Returns a specific repetition of
     * STF-11: "Office/Home Address/Birthplace" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public XAD getOfficeHomeAddressBirthplace(int rep) { 
		XAD retVal = this.getTypedField(11, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-11: "Office/Home Address/Birthplace" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public XAD getStf11_OfficeHomeAddressBirthplace(int rep) { 
		XAD retVal = this.getTypedField(11, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Office/Home Address/Birthplace (STF-11).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf11_OfficeHomeAddressBirthplaceReps() {
    	return this.getReps(11);
    }


    /**
     * Inserts a repetition of
     * STF-11: "Office/Home Address/Birthplace" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XAD insertOfficeHomeAddressBirthplace(int rep) throws HL7Exception { 
        return (XAD) super.insertRepetition(11, rep);
    }


    /**
     * Inserts a repetition of
     * STF-11: "Office/Home Address/Birthplace" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XAD insertStf11_OfficeHomeAddressBirthplace(int rep) throws HL7Exception { 
        return (XAD) super.insertRepetition(11, rep);
    }


    /**
     * Removes a repetition of
     * STF-11: "Office/Home Address/Birthplace" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XAD removeOfficeHomeAddressBirthplace(int rep) throws HL7Exception { 
        return (XAD) super.removeRepetition(11, rep);
    }


    /**
     * Removes a repetition of
     * STF-11: "Office/Home Address/Birthplace" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public XAD removeStf11_OfficeHomeAddressBirthplace(int rep) throws HL7Exception { 
        return (XAD) super.removeRepetition(11, rep);
    }



    /**
     * Returns all repetitions of Institution Activation Date (STF-12).
     */
    public DIN[] getInstitutionActivationDate() {
    	DIN[] retVal = this.getTypedField(12, new DIN[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Institution Activation Date (STF-12).
     */
    public DIN[] getStf12_InstitutionActivationDate() {
    	DIN[] retVal = this.getTypedField(12, new DIN[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Institution Activation Date (STF-12).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getInstitutionActivationDateReps() {
    	return this.getReps(12);
    }


    /**
     * Returns a specific repetition of
     * STF-12: "Institution Activation Date" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public DIN getInstitutionActivationDate(int rep) { 
		DIN retVal = this.getTypedField(12, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-12: "Institution Activation Date" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public DIN getStf12_InstitutionActivationDate(int rep) { 
		DIN retVal = this.getTypedField(12, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Institution Activation Date (STF-12).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf12_InstitutionActivationDateReps() {
    	return this.getReps(12);
    }


    /**
     * Inserts a repetition of
     * STF-12: "Institution Activation Date" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public DIN insertInstitutionActivationDate(int rep) throws HL7Exception { 
        return (DIN) super.insertRepetition(12, rep);
    }


    /**
     * Inserts a repetition of
     * STF-12: "Institution Activation Date" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public DIN insertStf12_InstitutionActivationDate(int rep) throws HL7Exception { 
        return (DIN) super.insertRepetition(12, rep);
    }


    /**
     * Removes a repetition of
     * STF-12: "Institution Activation Date" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public DIN removeInstitutionActivationDate(int rep) throws HL7Exception { 
        return (DIN) super.removeRepetition(12, rep);
    }


    /**
     * Removes a repetition of
     * STF-12: "Institution Activation Date" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public DIN removeStf12_InstitutionActivationDate(int rep) throws HL7Exception { 
        return (DIN) super.removeRepetition(12, rep);
    }



    /**
     * Returns all repetitions of Institution Inactivation Date (STF-13).
     */
    public DIN[] getInstitutionInactivationDate() {
    	DIN[] retVal = this.getTypedField(13, new DIN[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Institution Inactivation Date (STF-13).
     */
    public DIN[] getStf13_InstitutionInactivationDate() {
    	DIN[] retVal = this.getTypedField(13, new DIN[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Institution Inactivation Date (STF-13).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getInstitutionInactivationDateReps() {
    	return this.getReps(13);
    }


    /**
     * Returns a specific repetition of
     * STF-13: "Institution Inactivation Date" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public DIN getInstitutionInactivationDate(int rep) { 
		DIN retVal = this.getTypedField(13, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-13: "Institution Inactivation Date" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public DIN getStf13_InstitutionInactivationDate(int rep) { 
		DIN retVal = this.getTypedField(13, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Institution Inactivation Date (STF-13).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf13_InstitutionInactivationDateReps() {
    	return this.getReps(13);
    }


    /**
     * Inserts a repetition of
     * STF-13: "Institution Inactivation Date" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public DIN insertInstitutionInactivationDate(int rep) throws HL7Exception { 
        return (DIN) super.insertRepetition(13, rep);
    }


    /**
     * Inserts a repetition of
     * STF-13: "Institution Inactivation Date" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public DIN insertStf13_InstitutionInactivationDate(int rep) throws HL7Exception { 
        return (DIN) super.insertRepetition(13, rep);
    }


    /**
     * Removes a repetition of
     * STF-13: "Institution Inactivation Date" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public DIN removeInstitutionInactivationDate(int rep) throws HL7Exception { 
        return (DIN) super.removeRepetition(13, rep);
    }


    /**
     * Removes a repetition of
     * STF-13: "Institution Inactivation Date" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public DIN removeStf13_InstitutionInactivationDate(int rep) throws HL7Exception { 
        return (DIN) super.removeRepetition(13, rep);
    }



    /**
     * Returns all repetitions of Backup Person ID (STF-14).
     */
    public CE[] getBackupPersonID() {
    	CE[] retVal = this.getTypedField(14, new CE[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Backup Person ID (STF-14).
     */
    public CE[] getStf14_BackupPersonID() {
    	CE[] retVal = this.getTypedField(14, new CE[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Backup Person ID (STF-14).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getBackupPersonIDReps() {
    	return this.getReps(14);
    }


    /**
     * Returns a specific repetition of
     * STF-14: "Backup Person ID" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getBackupPersonID(int rep) { 
		CE retVal = this.getTypedField(14, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-14: "Backup Person ID" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getStf14_BackupPersonID(int rep) { 
		CE retVal = this.getTypedField(14, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Backup Person ID (STF-14).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf14_BackupPersonIDReps() {
    	return this.getReps(14);
    }


    /**
     * Inserts a repetition of
     * STF-14: "Backup Person ID" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertBackupPersonID(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(14, rep);
    }


    /**
     * Inserts a repetition of
     * STF-14: "Backup Person ID" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertStf14_BackupPersonID(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(14, rep);
    }


    /**
     * Removes a repetition of
     * STF-14: "Backup Person ID" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeBackupPersonID(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(14, rep);
    }


    /**
     * Removes a repetition of
     * STF-14: "Backup Person ID" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeStf14_BackupPersonID(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(14, rep);
    }



    /**
     * Returns all repetitions of E-Mail Address (STF-15).
     */
    public ST[] getEMailAddress() {
    	ST[] retVal = this.getTypedField(15, new ST[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of E-Mail Address (STF-15).
     */
    public ST[] getStf15_EMailAddress() {
    	ST[] retVal = this.getTypedField(15, new ST[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of E-Mail Address (STF-15).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getEMailAddressReps() {
    	return this.getReps(15);
    }


    /**
     * Returns a specific repetition of
     * STF-15: "E-Mail Address" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public ST getEMailAddress(int rep) { 
		ST retVal = this.getTypedField(15, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-15: "E-Mail Address" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public ST getStf15_EMailAddress(int rep) { 
		ST retVal = this.getTypedField(15, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of E-Mail Address (STF-15).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf15_EMailAddressReps() {
    	return this.getReps(15);
    }


    /**
     * Inserts a repetition of
     * STF-15: "E-Mail Address" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ST insertEMailAddress(int rep) throws HL7Exception { 
        return (ST) super.insertRepetition(15, rep);
    }


    /**
     * Inserts a repetition of
     * STF-15: "E-Mail Address" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ST insertStf15_EMailAddress(int rep) throws HL7Exception { 
        return (ST) super.insertRepetition(15, rep);
    }


    /**
     * Removes a repetition of
     * STF-15: "E-Mail Address" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ST removeEMailAddress(int rep) throws HL7Exception { 
        return (ST) super.removeRepetition(15, rep);
    }


    /**
     * Removes a repetition of
     * STF-15: "E-Mail Address" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ST removeStf15_EMailAddress(int rep) throws HL7Exception { 
        return (ST) super.removeRepetition(15, rep);
    }




    /**
     * Returns
     * STF-16: "Preferred Method of Contact" - creates it if necessary
     */
    public CE getPreferredMethodOfContact() { 
		CE retVal = this.getTypedField(16, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-16: "Preferred Method of Contact" - creates it if necessary
     */
    public CE getStf16_PreferredMethodOfContact() { 
		CE retVal = this.getTypedField(16, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-17: "Marital Status" - creates it if necessary
     */
    public CE getMaritalStatus() { 
		CE retVal = this.getTypedField(17, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-17: "Marital Status" - creates it if necessary
     */
    public CE getStf17_MaritalStatus() { 
		CE retVal = this.getTypedField(17, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-18: "Job Title" - creates it if necessary
     */
    public ST getJobTitle() { 
		ST retVal = this.getTypedField(18, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-18: "Job Title" - creates it if necessary
     */
    public ST getStf18_JobTitle() { 
		ST retVal = this.getTypedField(18, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-19: "Job Code/Class" - creates it if necessary
     */
    public JCC getJobCodeClass() { 
		JCC retVal = this.getTypedField(19, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-19: "Job Code/Class" - creates it if necessary
     */
    public JCC getStf19_JobCodeClass() { 
		JCC retVal = this.getTypedField(19, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-20: "Employment Status Code" - creates it if necessary
     */
    public CE getEmploymentStatusCode() { 
		CE retVal = this.getTypedField(20, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-20: "Employment Status Code" - creates it if necessary
     */
    public CE getStf20_EmploymentStatusCode() { 
		CE retVal = this.getTypedField(20, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-21: "Additional Insured on Auto" - creates it if necessary
     */
    public ID getAdditionalInsuredOnAuto() { 
		ID retVal = this.getTypedField(21, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-21: "Additional Insured on Auto" - creates it if necessary
     */
    public ID getStf21_AdditionalInsuredOnAuto() { 
		ID retVal = this.getTypedField(21, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-22: "Driver's License Number - Staff" - creates it if necessary
     */
    public DLN getDriverSLicenseNumberStaff() { 
		DLN retVal = this.getTypedField(22, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-22: "Driver's License Number - Staff" - creates it if necessary
     */
    public DLN getStf22_DriverSLicenseNumberStaff() { 
		DLN retVal = this.getTypedField(22, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-23: "Copy Auto Ins" - creates it if necessary
     */
    public ID getCopyAutoIns() { 
		ID retVal = this.getTypedField(23, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-23: "Copy Auto Ins" - creates it if necessary
     */
    public ID getStf23_CopyAutoIns() { 
		ID retVal = this.getTypedField(23, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-24: "Auto Ins. Expires" - creates it if necessary
     */
    public DT getAutoInsExpires() { 
		DT retVal = this.getTypedField(24, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-24: "Auto Ins. Expires" - creates it if necessary
     */
    public DT getStf24_AutoInsExpires() { 
		DT retVal = this.getTypedField(24, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-25: "Date Last DMV Review" - creates it if necessary
     */
    public DT getDateLastDMVReview() { 
		DT retVal = this.getTypedField(25, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-25: "Date Last DMV Review" - creates it if necessary
     */
    public DT getStf25_DateLastDMVReview() { 
		DT retVal = this.getTypedField(25, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-26: "Date Next DMV Review" - creates it if necessary
     */
    public DT getDateNextDMVReview() { 
		DT retVal = this.getTypedField(26, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-26: "Date Next DMV Review" - creates it if necessary
     */
    public DT getStf26_DateNextDMVReview() { 
		DT retVal = this.getTypedField(26, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-27: "Race" - creates it if necessary
     */
    public CE getRace() { 
		CE retVal = this.getTypedField(27, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-27: "Race" - creates it if necessary
     */
    public CE getStf27_Race() { 
		CE retVal = this.getTypedField(27, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-28: "Ethnic Group" - creates it if necessary
     */
    public CE getEthnicGroup() { 
		CE retVal = this.getTypedField(28, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-28: "Ethnic Group" - creates it if necessary
     */
    public CE getStf28_EthnicGroup() { 
		CE retVal = this.getTypedField(28, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-29: "Re-activation Approval Indicator" - creates it if necessary
     */
    public ID getReActivationApprovalIndicator() { 
		ID retVal = this.getTypedField(29, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-29: "Re-activation Approval Indicator" - creates it if necessary
     */
    public ID getStf29_ReActivationApprovalIndicator() { 
		ID retVal = this.getTypedField(29, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Citizenship (STF-30).
     */
    public CE[] getCitizenship() {
    	CE[] retVal = this.getTypedField(30, new CE[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Citizenship (STF-30).
     */
    public CE[] getStf30_Citizenship() {
    	CE[] retVal = this.getTypedField(30, new CE[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Citizenship (STF-30).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getCitizenshipReps() {
    	return this.getReps(30);
    }


    /**
     * Returns a specific repetition of
     * STF-30: "Citizenship" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getCitizenship(int rep) { 
		CE retVal = this.getTypedField(30, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-30: "Citizenship" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getStf30_Citizenship(int rep) { 
		CE retVal = this.getTypedField(30, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Citizenship (STF-30).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf30_CitizenshipReps() {
    	return this.getReps(30);
    }


    /**
     * Inserts a repetition of
     * STF-30: "Citizenship" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertCitizenship(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(30, rep);
    }


    /**
     * Inserts a repetition of
     * STF-30: "Citizenship" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertStf30_Citizenship(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(30, rep);
    }


    /**
     * Removes a repetition of
     * STF-30: "Citizenship" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeCitizenship(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(30, rep);
    }


    /**
     * Removes a repetition of
     * STF-30: "Citizenship" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeStf30_Citizenship(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(30, rep);
    }




    /**
     * Returns
     * STF-31: "Death Date and Time" - creates it if necessary
     */
    public TS getDeathDateAndTime() { 
		TS retVal = this.getTypedField(31, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-31: "Death Date and Time" - creates it if necessary
     */
    public TS getStf31_DeathDateAndTime() { 
		TS retVal = this.getTypedField(31, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-32: "Death Indicator" - creates it if necessary
     */
    public ID getDeathIndicator() { 
		ID retVal = this.getTypedField(32, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-32: "Death Indicator" - creates it if necessary
     */
    public ID getStf32_DeathIndicator() { 
		ID retVal = this.getTypedField(32, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-33: "Institution Relationship Type Code" - creates it if necessary
     */
    public CWE getInstitutionRelationshipTypeCode() { 
		CWE retVal = this.getTypedField(33, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-33: "Institution Relationship Type Code" - creates it if necessary
     */
    public CWE getStf33_InstitutionRelationshipTypeCode() { 
		CWE retVal = this.getTypedField(33, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-34: "Institution Relationship Period" - creates it if necessary
     */
    public DR getInstitutionRelationshipPeriod() { 
		DR retVal = this.getTypedField(34, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-34: "Institution Relationship Period" - creates it if necessary
     */
    public DR getStf34_InstitutionRelationshipPeriod() { 
		DR retVal = this.getTypedField(34, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-35: "Expected Return Date" - creates it if necessary
     */
    public DT getExpectedReturnDate() { 
		DT retVal = this.getTypedField(35, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-35: "Expected Return Date" - creates it if necessary
     */
    public DT getStf35_ExpectedReturnDate() { 
		DT retVal = this.getTypedField(35, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Cost Center Code (STF-36).
     */
    public CWE[] getCostCenterCode() {
    	CWE[] retVal = this.getTypedField(36, new CWE[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Cost Center Code (STF-36).
     */
    public CWE[] getStf36_CostCenterCode() {
    	CWE[] retVal = this.getTypedField(36, new CWE[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Cost Center Code (STF-36).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getCostCenterCodeReps() {
    	return this.getReps(36);
    }


    /**
     * Returns a specific repetition of
     * STF-36: "Cost Center Code" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CWE getCostCenterCode(int rep) { 
		CWE retVal = this.getTypedField(36, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * STF-36: "Cost Center Code" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CWE getStf36_CostCenterCode(int rep) { 
		CWE retVal = this.getTypedField(36, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Cost Center Code (STF-36).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getStf36_CostCenterCodeReps() {
    	return this.getReps(36);
    }


    /**
     * Inserts a repetition of
     * STF-36: "Cost Center Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CWE insertCostCenterCode(int rep) throws HL7Exception { 
        return (CWE) super.insertRepetition(36, rep);
    }


    /**
     * Inserts a repetition of
     * STF-36: "Cost Center Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CWE insertStf36_CostCenterCode(int rep) throws HL7Exception { 
        return (CWE) super.insertRepetition(36, rep);
    }


    /**
     * Removes a repetition of
     * STF-36: "Cost Center Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CWE removeCostCenterCode(int rep) throws HL7Exception { 
        return (CWE) super.removeRepetition(36, rep);
    }


    /**
     * Removes a repetition of
     * STF-36: "Cost Center Code" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CWE removeStf36_CostCenterCode(int rep) throws HL7Exception { 
        return (CWE) super.removeRepetition(36, rep);
    }




    /**
     * Returns
     * STF-37: "Generic Classification Indicator" - creates it if necessary
     */
    public ID getGenericClassificationIndicator() { 
		ID retVal = this.getTypedField(37, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-37: "Generic Classification Indicator" - creates it if necessary
     */
    public ID getStf37_GenericClassificationIndicator() { 
		ID retVal = this.getTypedField(37, 0);
		return retVal;
    }



    /**
     * Returns
     * STF-38: "Inactive Reason Code" - creates it if necessary
     */
    public CWE getInactiveReasonCode() { 
		CWE retVal = this.getTypedField(38, 0);
		return retVal;
    }
    
    /**
     * Returns
     * STF-38: "Inactive Reason Code" - creates it if necessary
     */
    public CWE getStf38_InactiveReasonCode() { 
		CWE retVal = this.getTypedField(38, 0);
		return retVal;
    }





    /** {@inheritDoc} */   
    protected Type createNewTypeWithoutReflection(int field) {
       switch (field) {
          case 0: return new CE(getMessage());
          case 1: return new CX(getMessage());
          case 2: return new XPN(getMessage());
          case 3: return new IS(getMessage(), new Integer( 182 ));
          case 4: return new IS(getMessage(), new Integer( 1 ));
          case 5: return new TS(getMessage());
          case 6: return new ID(getMessage(), new Integer( 183 ));
          case 7: return new CE(getMessage());
          case 8: return new CE(getMessage());
          case 9: return new XTN(getMessage());
          case 10: return new XAD(getMessage());
          case 11: return new DIN(getMessage());
          case 12: return new DIN(getMessage());
          case 13: return new CE(getMessage());
          case 14: return new ST(getMessage());
          case 15: return new CE(getMessage());
          case 16: return new CE(getMessage());
          case 17: return new ST(getMessage());
          case 18: return new JCC(getMessage());
          case 19: return new CE(getMessage());
          case 20: return new ID(getMessage(), new Integer( 136 ));
          case 21: return new DLN(getMessage());
          case 22: return new ID(getMessage(), new Integer( 136 ));
          case 23: return new DT(getMessage());
          case 24: return new DT(getMessage());
          case 25: return new DT(getMessage());
          case 26: return new CE(getMessage());
          case 27: return new CE(getMessage());
          case 28: return new ID(getMessage(), new Integer( 136 ));
          case 29: return new CE(getMessage());
          case 30: return new TS(getMessage());
          case 31: return new ID(getMessage(), new Integer( 136 ));
          case 32: return new CWE(getMessage());
          case 33: return new DR(getMessage());
          case 34: return new DT(getMessage());
          case 35: return new CWE(getMessage());
          case 36: return new ID(getMessage(), new Integer( 136 ));
          case 37: return new CWE(getMessage());
          default: return null;
       }
   }


}

