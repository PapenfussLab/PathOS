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
 *<p>Represents an HL7 NSC message segment (Application Status Change). 
 * This segment has the following fields:</p>
 * <ul>
     * <li>NSC-1: Application Change Type (IS) <b> </b>
     * <li>NSC-2: Current CPU (ST) <b>optional </b>
     * <li>NSC-3: Current Fileserver (ST) <b>optional </b>
     * <li>NSC-4: Current Application (HD) <b>optional </b>
     * <li>NSC-5: Current Facility (HD) <b>optional </b>
     * <li>NSC-6: New CPU (ST) <b>optional </b>
     * <li>NSC-7: New Fileserver (ST) <b>optional </b>
     * <li>NSC-8: New Application (HD) <b>optional </b>
     * <li>NSC-9: New Facility (HD) <b>optional </b>
 * </ul>
 */
@SuppressWarnings("unused")
public class NSC extends AbstractSegment {

    /** 
     * Creates a new NSC segment
     */
    public NSC(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                              this.add(IS.class, true, 1, 4, new Object[]{ getMessage(), new Integer(409) }, "Application Change Type");
                                  this.add(ST.class, false, 1, 30, new Object[]{ getMessage() }, "Current CPU");
                                  this.add(ST.class, false, 1, 30, new Object[]{ getMessage() }, "Current Fileserver");
                                  this.add(HD.class, false, 1, 30, new Object[]{ getMessage() }, "Current Application");
                                  this.add(HD.class, false, 1, 30, new Object[]{ getMessage() }, "Current Facility");
                                  this.add(ST.class, false, 1, 30, new Object[]{ getMessage() }, "New CPU");
                                  this.add(ST.class, false, 1, 30, new Object[]{ getMessage() }, "New Fileserver");
                                  this.add(HD.class, false, 1, 30, new Object[]{ getMessage() }, "New Application");
                                  this.add(HD.class, false, 1, 30, new Object[]{ getMessage() }, "New Facility");
       } catch(HL7Exception e) {
          log.error("Unexpected error creating NSC - this is probably a bug in the source code generator.", e);
       }
    }



    /**
     * Returns
     * NSC-1: "Application Change Type" - creates it if necessary
     */
    public IS getApplicationChangeType() { 
		IS retVal = this.getTypedField(1, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NSC-1: "Application Change Type" - creates it if necessary
     */
    public IS getNsc1_ApplicationChangeType() { 
		IS retVal = this.getTypedField(1, 0);
		return retVal;
    }



    /**
     * Returns
     * NSC-2: "Current CPU" - creates it if necessary
     */
    public ST getCurrentCPU() { 
		ST retVal = this.getTypedField(2, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NSC-2: "Current CPU" - creates it if necessary
     */
    public ST getNsc2_CurrentCPU() { 
		ST retVal = this.getTypedField(2, 0);
		return retVal;
    }



    /**
     * Returns
     * NSC-3: "Current Fileserver" - creates it if necessary
     */
    public ST getCurrentFileserver() { 
		ST retVal = this.getTypedField(3, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NSC-3: "Current Fileserver" - creates it if necessary
     */
    public ST getNsc3_CurrentFileserver() { 
		ST retVal = this.getTypedField(3, 0);
		return retVal;
    }



    /**
     * Returns
     * NSC-4: "Current Application" - creates it if necessary
     */
    public HD getCurrentApplication() { 
		HD retVal = this.getTypedField(4, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NSC-4: "Current Application" - creates it if necessary
     */
    public HD getNsc4_CurrentApplication() { 
		HD retVal = this.getTypedField(4, 0);
		return retVal;
    }



    /**
     * Returns
     * NSC-5: "Current Facility" - creates it if necessary
     */
    public HD getCurrentFacility() { 
		HD retVal = this.getTypedField(5, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NSC-5: "Current Facility" - creates it if necessary
     */
    public HD getNsc5_CurrentFacility() { 
		HD retVal = this.getTypedField(5, 0);
		return retVal;
    }



    /**
     * Returns
     * NSC-6: "New CPU" - creates it if necessary
     */
    public ST getNewCPU() { 
		ST retVal = this.getTypedField(6, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NSC-6: "New CPU" - creates it if necessary
     */
    public ST getNsc6_NewCPU() { 
		ST retVal = this.getTypedField(6, 0);
		return retVal;
    }



    /**
     * Returns
     * NSC-7: "New Fileserver" - creates it if necessary
     */
    public ST getNewFileserver() { 
		ST retVal = this.getTypedField(7, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NSC-7: "New Fileserver" - creates it if necessary
     */
    public ST getNsc7_NewFileserver() { 
		ST retVal = this.getTypedField(7, 0);
		return retVal;
    }



    /**
     * Returns
     * NSC-8: "New Application" - creates it if necessary
     */
    public HD getNewApplication() { 
		HD retVal = this.getTypedField(8, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NSC-8: "New Application" - creates it if necessary
     */
    public HD getNsc8_NewApplication() { 
		HD retVal = this.getTypedField(8, 0);
		return retVal;
    }



    /**
     * Returns
     * NSC-9: "New Facility" - creates it if necessary
     */
    public HD getNewFacility() { 
		HD retVal = this.getTypedField(9, 0);
		return retVal;
    }
    
    /**
     * Returns
     * NSC-9: "New Facility" - creates it if necessary
     */
    public HD getNsc9_NewFacility() { 
		HD retVal = this.getTypedField(9, 0);
		return retVal;
    }





    /** {@inheritDoc} */   
    protected Type createNewTypeWithoutReflection(int field) {
       switch (field) {
          case 0: return new IS(getMessage(), new Integer( 409 ));
          case 1: return new ST(getMessage());
          case 2: return new ST(getMessage());
          case 3: return new HD(getMessage());
          case 4: return new HD(getMessage());
          case 5: return new ST(getMessage());
          case 6: return new ST(getMessage());
          case 7: return new HD(getMessage());
          case 8: return new HD(getMessage());
          default: return null;
       }
   }


}

