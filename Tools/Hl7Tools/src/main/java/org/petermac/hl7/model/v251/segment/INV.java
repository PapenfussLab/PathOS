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
 *<p>Represents an HL7 INV message segment (Inventory Detail). 
 * This segment has the following fields:</p>
 * <ul>
     * <li>INV-1: Substance Identifier (CE) <b> </b>
     * <li>INV-2: Substance Status (CE) <b> repeating</b>
     * <li>INV-3: Substance Type (CE) <b>optional </b>
     * <li>INV-4: Inventory Container Identifier (CE) <b>optional </b>
     * <li>INV-5: Container Carrier Identifier (CE) <b>optional </b>
     * <li>INV-6: Position on Carrier (CE) <b>optional </b>
     * <li>INV-7: Initial Quantity (NM) <b>optional </b>
     * <li>INV-8: Current Quantity (NM) <b>optional </b>
     * <li>INV-9: Available Quantity (NM) <b>optional </b>
     * <li>INV-10: Consumption Quantity (NM) <b>optional </b>
     * <li>INV-11: Quantity Units (CE) <b>optional </b>
     * <li>INV-12: Expiration Date/Time (TS) <b>optional </b>
     * <li>INV-13: First Used Date/Time (TS) <b>optional </b>
     * <li>INV-14: On Board Stability Duration (TQ) <b>optional </b>
     * <li>INV-15: Test/Fluid Identifier(s) (CE) <b>optional repeating</b>
     * <li>INV-16: Manufacturer Lot Number (ST) <b>optional </b>
     * <li>INV-17: Manufacturer Identifier (CE) <b>optional </b>
     * <li>INV-18: Supplier Identifier (CE) <b>optional </b>
     * <li>INV-19: On Board Stability Time (CQ) <b>optional </b>
     * <li>INV-20: Target Value (CQ) <b>optional </b>
 * </ul>
 */
@SuppressWarnings("unused")
public class INV extends AbstractSegment {

    /** 
     * Creates a new INV segment
     */
    public INV(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(CE.class, true, 1, 250, new Object[]{ getMessage() }, "Substance Identifier");
                                  this.add(CE.class, true, 0, 250, new Object[]{ getMessage() }, "Substance Status");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Substance Type");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Inventory Container Identifier");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Container Carrier Identifier");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Position on Carrier");
                                  this.add(NM.class, false, 1, 20, new Object[]{ getMessage() }, "Initial Quantity");
                                  this.add(NM.class, false, 1, 20, new Object[]{ getMessage() }, "Current Quantity");
                                  this.add(NM.class, false, 1, 20, new Object[]{ getMessage() }, "Available Quantity");
                                  this.add(NM.class, false, 1, 20, new Object[]{ getMessage() }, "Consumption Quantity");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Quantity Units");
                                  this.add(TS.class, false, 1, 26, new Object[]{ getMessage() }, "Expiration Date/Time");
                                  this.add(TS.class, false, 1, 26, new Object[]{ getMessage() }, "First Used Date/Time");
                                  this.add(TQ.class, false, 1, 200, new Object[]{ getMessage() }, "On Board Stability Duration");
                                  this.add(CE.class, false, 0, 250, new Object[]{ getMessage() }, "Test/Fluid Identifier(s)");
                                  this.add(ST.class, false, 1, 200, new Object[]{ getMessage() }, "Manufacturer Lot Number");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Manufacturer Identifier");
                                  this.add(CE.class, false, 1, 250, new Object[]{ getMessage() }, "Supplier Identifier");
                                  this.add(CQ.class, false, 1, 20, new Object[]{ getMessage() }, "On Board Stability Time");
                                  this.add(CQ.class, false, 1, 20, new Object[]{ getMessage() }, "Target Value");
       } catch(HL7Exception e) {
          log.error("Unexpected error creating INV - this is probably a bug in the source code generator.", e);
       }
    }



    /**
     * Returns
     * INV-1: "Substance Identifier" - creates it if necessary
     */
    public CE getSubstanceIdentifier() { 
		CE retVal = this.getTypedField(1, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-1: "Substance Identifier" - creates it if necessary
     */
    public CE getInv1_SubstanceIdentifier() { 
		CE retVal = this.getTypedField(1, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Substance Status (INV-2).
     */
    public CE[] getSubstanceStatus() {
    	CE[] retVal = this.getTypedField(2, new CE[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Substance Status (INV-2).
     */
    public CE[] getInv2_SubstanceStatus() {
    	CE[] retVal = this.getTypedField(2, new CE[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Substance Status (INV-2).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getSubstanceStatusReps() {
    	return this.getReps(2);
    }


    /**
     * Returns a specific repetition of
     * INV-2: "Substance Status" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getSubstanceStatus(int rep) { 
		CE retVal = this.getTypedField(2, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * INV-2: "Substance Status" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getInv2_SubstanceStatus(int rep) { 
		CE retVal = this.getTypedField(2, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Substance Status (INV-2).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getInv2_SubstanceStatusReps() {
    	return this.getReps(2);
    }


    /**
     * Inserts a repetition of
     * INV-2: "Substance Status" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertSubstanceStatus(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(2, rep);
    }


    /**
     * Inserts a repetition of
     * INV-2: "Substance Status" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertInv2_SubstanceStatus(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(2, rep);
    }


    /**
     * Removes a repetition of
     * INV-2: "Substance Status" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeSubstanceStatus(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(2, rep);
    }


    /**
     * Removes a repetition of
     * INV-2: "Substance Status" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeInv2_SubstanceStatus(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(2, rep);
    }




    /**
     * Returns
     * INV-3: "Substance Type" - creates it if necessary
     */
    public CE getSubstanceType() { 
		CE retVal = this.getTypedField(3, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-3: "Substance Type" - creates it if necessary
     */
    public CE getInv3_SubstanceType() { 
		CE retVal = this.getTypedField(3, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-4: "Inventory Container Identifier" - creates it if necessary
     */
    public CE getInventoryContainerIdentifier() { 
		CE retVal = this.getTypedField(4, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-4: "Inventory Container Identifier" - creates it if necessary
     */
    public CE getInv4_InventoryContainerIdentifier() { 
		CE retVal = this.getTypedField(4, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-5: "Container Carrier Identifier" - creates it if necessary
     */
    public CE getContainerCarrierIdentifier() { 
		CE retVal = this.getTypedField(5, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-5: "Container Carrier Identifier" - creates it if necessary
     */
    public CE getInv5_ContainerCarrierIdentifier() { 
		CE retVal = this.getTypedField(5, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-6: "Position on Carrier" - creates it if necessary
     */
    public CE getPositionOnCarrier() { 
		CE retVal = this.getTypedField(6, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-6: "Position on Carrier" - creates it if necessary
     */
    public CE getInv6_PositionOnCarrier() { 
		CE retVal = this.getTypedField(6, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-7: "Initial Quantity" - creates it if necessary
     */
    public NM getInitialQuantity() { 
		NM retVal = this.getTypedField(7, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-7: "Initial Quantity" - creates it if necessary
     */
    public NM getInv7_InitialQuantity() { 
		NM retVal = this.getTypedField(7, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-8: "Current Quantity" - creates it if necessary
     */
    public NM getCurrentQuantity() { 
		NM retVal = this.getTypedField(8, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-8: "Current Quantity" - creates it if necessary
     */
    public NM getInv8_CurrentQuantity() { 
		NM retVal = this.getTypedField(8, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-9: "Available Quantity" - creates it if necessary
     */
    public NM getAvailableQuantity() { 
		NM retVal = this.getTypedField(9, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-9: "Available Quantity" - creates it if necessary
     */
    public NM getInv9_AvailableQuantity() { 
		NM retVal = this.getTypedField(9, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-10: "Consumption Quantity" - creates it if necessary
     */
    public NM getConsumptionQuantity() { 
		NM retVal = this.getTypedField(10, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-10: "Consumption Quantity" - creates it if necessary
     */
    public NM getInv10_ConsumptionQuantity() { 
		NM retVal = this.getTypedField(10, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-11: "Quantity Units" - creates it if necessary
     */
    public CE getQuantityUnits() { 
		CE retVal = this.getTypedField(11, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-11: "Quantity Units" - creates it if necessary
     */
    public CE getInv11_QuantityUnits() { 
		CE retVal = this.getTypedField(11, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-12: "Expiration Date/Time" - creates it if necessary
     */
    public TS getExpirationDateTime() { 
		TS retVal = this.getTypedField(12, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-12: "Expiration Date/Time" - creates it if necessary
     */
    public TS getInv12_ExpirationDateTime() { 
		TS retVal = this.getTypedField(12, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-13: "First Used Date/Time" - creates it if necessary
     */
    public TS getFirstUsedDateTime() { 
		TS retVal = this.getTypedField(13, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-13: "First Used Date/Time" - creates it if necessary
     */
    public TS getInv13_FirstUsedDateTime() { 
		TS retVal = this.getTypedField(13, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-14: "On Board Stability Duration" - creates it if necessary
     */
    public TQ getOnBoardStabilityDuration() { 
		TQ retVal = this.getTypedField(14, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-14: "On Board Stability Duration" - creates it if necessary
     */
    public TQ getInv14_OnBoardStabilityDuration() { 
		TQ retVal = this.getTypedField(14, 0);
		return retVal;
    }


    /**
     * Returns all repetitions of Test/Fluid Identifier(s) (INV-15).
     */
    public CE[] getTestFluidIdentifierS() {
    	CE[] retVal = this.getTypedField(15, new CE[0]);
    	return retVal;
    }


    /**
     * Returns all repetitions of Test/Fluid Identifier(s) (INV-15).
     */
    public CE[] getInv15_TestFluidIdentifierS() {
    	CE[] retVal = this.getTypedField(15, new CE[0]);
    	return retVal;
    }


    /**
     * Returns a count of the current number of repetitions of Test/Fluid Identifier(s) (INV-15).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getTestFluidIdentifierSReps() {
    	return this.getReps(15);
    }


    /**
     * Returns a specific repetition of
     * INV-15: "Test/Fluid Identifier(s)" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getTestFluidIdentifierS(int rep) { 
		CE retVal = this.getTypedField(15, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * INV-15: "Test/Fluid Identifier(s)" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getInv15_TestFluidIdentifierS(int rep) { 
		CE retVal = this.getTypedField(15, rep);
		return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Test/Fluid Identifier(s) (INV-15).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getInv15_TestFluidIdentifierSReps() {
    	return this.getReps(15);
    }


    /**
     * Inserts a repetition of
     * INV-15: "Test/Fluid Identifier(s)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertTestFluidIdentifierS(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(15, rep);
    }


    /**
     * Inserts a repetition of
     * INV-15: "Test/Fluid Identifier(s)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertInv15_TestFluidIdentifierS(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(15, rep);
    }


    /**
     * Removes a repetition of
     * INV-15: "Test/Fluid Identifier(s)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeTestFluidIdentifierS(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(15, rep);
    }


    /**
     * Removes a repetition of
     * INV-15: "Test/Fluid Identifier(s)" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeInv15_TestFluidIdentifierS(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(15, rep);
    }




    /**
     * Returns
     * INV-16: "Manufacturer Lot Number" - creates it if necessary
     */
    public ST getManufacturerLotNumber() { 
		ST retVal = this.getTypedField(16, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-16: "Manufacturer Lot Number" - creates it if necessary
     */
    public ST getInv16_ManufacturerLotNumber() { 
		ST retVal = this.getTypedField(16, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-17: "Manufacturer Identifier" - creates it if necessary
     */
    public CE getManufacturerIdentifier() { 
		CE retVal = this.getTypedField(17, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-17: "Manufacturer Identifier" - creates it if necessary
     */
    public CE getInv17_ManufacturerIdentifier() { 
		CE retVal = this.getTypedField(17, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-18: "Supplier Identifier" - creates it if necessary
     */
    public CE getSupplierIdentifier() { 
		CE retVal = this.getTypedField(18, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-18: "Supplier Identifier" - creates it if necessary
     */
    public CE getInv18_SupplierIdentifier() { 
		CE retVal = this.getTypedField(18, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-19: "On Board Stability Time" - creates it if necessary
     */
    public CQ getOnBoardStabilityTime() { 
		CQ retVal = this.getTypedField(19, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-19: "On Board Stability Time" - creates it if necessary
     */
    public CQ getInv19_OnBoardStabilityTime() { 
		CQ retVal = this.getTypedField(19, 0);
		return retVal;
    }



    /**
     * Returns
     * INV-20: "Target Value" - creates it if necessary
     */
    public CQ getTargetValue() { 
		CQ retVal = this.getTypedField(20, 0);
		return retVal;
    }
    
    /**
     * Returns
     * INV-20: "Target Value" - creates it if necessary
     */
    public CQ getInv20_TargetValue() { 
		CQ retVal = this.getTypedField(20, 0);
		return retVal;
    }





    /** {@inheritDoc} */   
    protected Type createNewTypeWithoutReflection(int field) {
       switch (field) {
          case 0: return new CE(getMessage());
          case 1: return new CE(getMessage());
          case 2: return new CE(getMessage());
          case 3: return new CE(getMessage());
          case 4: return new CE(getMessage());
          case 5: return new CE(getMessage());
          case 6: return new NM(getMessage());
          case 7: return new NM(getMessage());
          case 8: return new NM(getMessage());
          case 9: return new NM(getMessage());
          case 10: return new CE(getMessage());
          case 11: return new TS(getMessage());
          case 12: return new TS(getMessage());
          case 13: return new TQ(getMessage());
          case 14: return new CE(getMessage());
          case 15: return new ST(getMessage());
          case 16: return new CE(getMessage());
          case 17: return new CE(getMessage());
          case 18: return new CQ(getMessage());
          case 19: return new CQ(getMessage());
          default: return null;
       }
   }


}

