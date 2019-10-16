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
 * The Original Code is "CSU.java".  Description:
 * "Composite class CSU"
 * 
 * The Initial Developer of the Original Code is University Health Network. Copyright (C) 
 * 2013.  All Rights Reserved.
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

package org.petermac.hl7.model.v251.datatype;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.AbstractComposite;


/**
 * <p>Represents an HL7 CSU (Channel Sensitivity) data type. 
 * This type consists of the following components:</p>
 * <ul>
 * <li>Channel Sensitivity (NM)
 * <li>Unit of Measure Identifier (ST)
 * <li>Unit of Measure Description (ST)
 * <li>Unit of Measure Coding System (ID)
 * <li>Alternate Unit of Measure Identifier (ST)
 * <li>Alternate Unit of Measure Description (ST)
 * <li>Alternate Unit of Measure Coding System (ID)
 * </ul>
 */
@SuppressWarnings("unused")
public class CSU extends AbstractComposite {

    private Type[] data;

    /** 
     * Creates a new CSU type
     */
    public CSU(Message message) {
        super(message);
        init();
    }

    private void init() {
        data = new Type[7];    
        data[0] = new NM(getMessage());
        data[1] = new ST(getMessage());
        data[2] = new ST(getMessage());
        data[3] = new ID(getMessage(), 396);
        data[4] = new ST(getMessage());
        data[5] = new ST(getMessage());
        data[6] = new ID(getMessage(), 396);
    }


    /**
     * Returns an array containing the data elements.
     */
    public Type[] getComponents() { 
        return this.data; 
    }

    /**
     * Returns an individual data component.
     *
     * @param number The component number (0-indexed)
     * @throws DataTypeException if the given element number is out of range.
     */
    public Type getComponent(int number) throws DataTypeException { 

        try { 
            return this.data[number]; 
        } catch (ArrayIndexOutOfBoundsException e) { 
            throw new DataTypeException("Element " + number + " doesn't exist (Type " + getClass().getName() + " has only " + this.data.length + " components)"); 
        } 
    } 


    /**
     * Returns Channel Sensitivity (component 1).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getChannelSensitivity() {
       return getTyped(0, NM.class);
    }

    
    /**
     * Returns Channel Sensitivity (component 1).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getCsu1_ChannelSensitivity() {
       return getTyped(0, NM.class);
    }


    /**
     * Returns Unit of Measure Identifier (component 2).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getUnitOfMeasureIdentifier() {
       return getTyped(1, ST.class);
    }

    
    /**
     * Returns Unit of Measure Identifier (component 2).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getCsu2_UnitOfMeasureIdentifier() {
       return getTyped(1, ST.class);
    }


    /**
     * Returns Unit of Measure Description (component 3).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getUnitOfMeasureDescription() {
       return getTyped(2, ST.class);
    }

    
    /**
     * Returns Unit of Measure Description (component 3).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getCsu3_UnitOfMeasureDescription() {
       return getTyped(2, ST.class);
    }


    /**
     * Returns Unit of Measure Coding System (component 4).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ID getUnitOfMeasureCodingSystem() {
       return getTyped(3, ID.class);
    }

    
    /**
     * Returns Unit of Measure Coding System (component 4).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ID getCsu4_UnitOfMeasureCodingSystem() {
       return getTyped(3, ID.class);
    }


    /**
     * Returns Alternate Unit of Measure Identifier (component 5).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getAlternateUnitOfMeasureIdentifier() {
       return getTyped(4, ST.class);
    }

    
    /**
     * Returns Alternate Unit of Measure Identifier (component 5).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getCsu5_AlternateUnitOfMeasureIdentifier() {
       return getTyped(4, ST.class);
    }


    /**
     * Returns Alternate Unit of Measure Description (component 6).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getAlternateUnitOfMeasureDescription() {
       return getTyped(5, ST.class);
    }

    
    /**
     * Returns Alternate Unit of Measure Description (component 6).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getCsu6_AlternateUnitOfMeasureDescription() {
       return getTyped(5, ST.class);
    }


    /**
     * Returns Alternate Unit of Measure Coding System (component 7).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ID getAlternateUnitOfMeasureCodingSystem() {
       return getTyped(6, ID.class);
    }

    
    /**
     * Returns Alternate Unit of Measure Coding System (component 7).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ID getCsu7_AlternateUnitOfMeasureCodingSystem() {
       return getTyped(6, ID.class);
    }



}

