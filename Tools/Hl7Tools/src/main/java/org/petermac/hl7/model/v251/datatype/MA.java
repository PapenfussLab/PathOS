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
 * The Original Code is "MA.java".  Description:
 * "Composite class MA"
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
 * <p>Represents an HL7 MA (Multiplexed Array) data type. 
 * This type consists of the following components:</p>
 * <ul>
 * <li>Sample 1 From Channel 1 (NM)
 * <li>Sample 1 From Channel 2 (NM)
 * <li>Sample 1 From Channel N (NM)
 * <li>Sample 2 From Channel 1 (NM)
 * <li>Sample 2 From Channel N (NM)
 * <li>Sample N From Channel N (NM)
 * </ul>
 */
@SuppressWarnings("unused")
public class MA extends AbstractComposite {

    private Type[] data;

    /** 
     * Creates a new MA type
     */
    public MA(Message message) {
        super(message);
        init();
    }

    private void init() {
        data = new Type[6];    
        data[0] = new NM(getMessage());
        data[1] = new NM(getMessage());
        data[2] = new NM(getMessage());
        data[3] = new NM(getMessage());
        data[4] = new NM(getMessage());
        data[5] = new NM(getMessage());
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
     * Returns Sample 1 From Channel 1 (component 1).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getSample1FromChannel1() {
       return getTyped(0, NM.class);
    }

    
    /**
     * Returns Sample 1 From Channel 1 (component 1).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getMa1_Sample1FromChannel1() {
       return getTyped(0, NM.class);
    }


    /**
     * Returns Sample 1 From Channel 2 (component 2).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getSample1FromChannel2() {
       return getTyped(1, NM.class);
    }

    
    /**
     * Returns Sample 1 From Channel 2 (component 2).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getMa2_Sample1FromChannel2() {
       return getTyped(1, NM.class);
    }


    /**
     * Returns Sample 1 From Channel N (component 3).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getSample1FromChannelN() {
       return getTyped(2, NM.class);
    }

    
    /**
     * Returns Sample 1 From Channel N (component 3).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getMa3_Sample1FromChannelN() {
       return getTyped(2, NM.class);
    }


    /**
     * Returns Sample 2 From Channel 1 (component 4).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getSample2FromChannel1() {
       return getTyped(3, NM.class);
    }

    
    /**
     * Returns Sample 2 From Channel 1 (component 4).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getMa4_Sample2FromChannel1() {
       return getTyped(3, NM.class);
    }


    /**
     * Returns Sample 2 From Channel N (component 5).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getSample2FromChannelN() {
       return getTyped(4, NM.class);
    }

    
    /**
     * Returns Sample 2 From Channel N (component 5).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getMa5_Sample2FromChannelN() {
       return getTyped(4, NM.class);
    }


    /**
     * Returns Sample N From Channel N (component 6).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getSampleNFromChannelN() {
       return getTyped(5, NM.class);
    }

    
    /**
     * Returns Sample N From Channel N (component 6).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public NM getMa6_SampleNFromChannelN() {
       return getTyped(5, NM.class);
    }



}

