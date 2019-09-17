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
 *<p>Represents an HL7 NPU message segment (Bed Status Update). 
 * This segment has the following fields:</p>
 * <ul>
     * <li>NPU-1: Bed Location (PL) <b> </b>
     * <li>NPU-2: Bed Status (IS) <b>optional </b>
 * </ul>
 */
@SuppressWarnings("unused")
public class NPU extends AbstractSegment {

    /** 
     * Creates a new NPU segment
     */
    public NPU(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(PL.class, true, 1, 80, new Object[]{ getMessage() }, "Bed Location");
                                              this.add(IS.class, false, 1, 1, new Object[]{ getMessage(), new Integer(116) }, "Bed Status");
       } catch(HL7Exception e) {
          log.error("Unexpected error creating NPU - this is probably a bug in the source code generator.", e);
       }
    }



    /**
     * Returns
     * NPU-1: "Bed Location" - creates it if necessary
     */
    public PL getBedLocation() { 
		PL retVal = this.getTypedField(1, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NPU-1: "Bed Location" - creates it if necessary
     */
    public PL getNpu1_BedLocation() { 
		PL retVal = this.getTypedField(1, 0);
		return retVal;
    }



    /**
     * Returns
     * NPU-2: "Bed Status" - creates it if necessary
     */
    public IS getBedStatus() { 
		IS retVal = this.getTypedField(2, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NPU-2: "Bed Status" - creates it if necessary
     */
    public IS getNpu2_BedStatus() { 
		IS retVal = this.getTypedField(2, 0);
		return retVal;
    }





    /** {@inheritDoc} */   
    protected Type createNewTypeWithoutReflection(int field) {
       switch (field) {
          case 0: return new PL(getMessage());
          case 1: return new IS(getMessage(), new Integer( 116 ));
          default: return null;
       }
   }


}

