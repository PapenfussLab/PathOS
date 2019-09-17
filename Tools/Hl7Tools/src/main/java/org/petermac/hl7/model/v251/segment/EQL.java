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
 *<p>Represents an HL7 EQL message segment (Embedded Query Language). 
 * This segment has the following fields:</p>
 * <ul>
     * <li>EQL-1: Query Tag (ST) <b>optional </b>
     * <li>EQL-2: Query/Response Format Code (ID) <b> </b>
     * <li>EQL-3: EQL Query Name (CE) <b> </b>
     * <li>EQL-4: EQL Query Statement (ST) <b> </b>
 * </ul>
 */
@SuppressWarnings("unused")
public class EQL extends AbstractSegment {

    /** 
     * Creates a new EQL segment
     */
    public EQL(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(ST.class, false, 1, 32, new Object[]{ getMessage() }, "Query Tag");
                                              this.add(ID.class, true, 1, 1, new Object[]{ getMessage(), new Integer(106) }, "Query/Response Format Code");
                                  this.add(CE.class, true, 1, 250, new Object[]{ getMessage() }, "EQL Query Name");
                                  this.add(ST.class, true, 1, 4096, new Object[]{ getMessage() }, "EQL Query Statement");
       } catch(HL7Exception e) {
          log.error("Unexpected error creating EQL - this is probably a bug in the source code generator.", e);
       }
    }



    /**
     * Returns
     * EQL-1: "Query Tag" - creates it if necessary
     */
    public ST getQueryTag() { 
		ST retVal = this.getTypedField(1, 0);
		return retVal;
    }
    
    /**
     * Returns
     * EQL-1: "Query Tag" - creates it if necessary
     */
    public ST getEql1_QueryTag() { 
		ST retVal = this.getTypedField(1, 0);
		return retVal;
    }



    /**
     * Returns
     * EQL-2: "Query/Response Format Code" - creates it if necessary
     */
    public ID getQueryResponseFormatCode() { 
		ID retVal = this.getTypedField(2, 0);
		return retVal;
    }
    
    /**
     * Returns
     * EQL-2: "Query/Response Format Code" - creates it if necessary
     */
    public ID getEql2_QueryResponseFormatCode() { 
		ID retVal = this.getTypedField(2, 0);
		return retVal;
    }



    /**
     * Returns
     * EQL-3: "EQL Query Name" - creates it if necessary
     */
    public CE getEQLQueryName() { 
		CE retVal = this.getTypedField(3, 0);
		return retVal;
    }
    
    /**
     * Returns
     * EQL-3: "EQL Query Name" - creates it if necessary
     */
    public CE getEql3_EQLQueryName() { 
		CE retVal = this.getTypedField(3, 0);
		return retVal;
    }



    /**
     * Returns
     * EQL-4: "EQL Query Statement" - creates it if necessary
     */
    public ST getEQLQueryStatement() { 
		ST retVal = this.getTypedField(4, 0);
		return retVal;
    }
    
    /**
     * Returns
     * EQL-4: "EQL Query Statement" - creates it if necessary
     */
    public ST getEql4_EQLQueryStatement() { 
		ST retVal = this.getTypedField(4, 0);
		return retVal;
    }





    /** {@inheritDoc} */   
    protected Type createNewTypeWithoutReflection(int field) {
       switch (field) {
          case 0: return new ST(getMessage());
          case 1: return new ID(getMessage(), new Integer( 106 ));
          case 2: return new CE(getMessage());
          case 3: return new ST(getMessage());
          default: return null;
       }
   }


}

