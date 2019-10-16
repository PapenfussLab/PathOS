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


package org.petermac.hl7.model.v251.group;

import org.petermac.hl7.model.v251.segment.*;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.model.*;

/**
 * <p>Represents a NMD_N02_CLOCK_AND_STATS_WITH_NOTES group structure (a Group object).
 * A Group is an ordered collection of message segments that can repeat together or be optionally in/excluded together.
 * This Group contains the following elements:  
 * </p>
 * <ul>
                         * <li>1: NMD_N02_CLOCK (a Group object) <b>optional  </b></li>
                         * <li>2: NMD_N02_APP_STATS (a Group object) <b>optional  </b></li>
                         * <li>3: NMD_N02_APP_STATUS (a Group object) <b>optional  </b></li>
 * </ul>
 */
//@SuppressWarnings("unused")
public class NMD_N02_CLOCK_AND_STATS_WITH_NOTES extends AbstractGroup {

    /** 
     * Creates a new NMD_N02_CLOCK_AND_STATS_WITH_NOTES group
     */
    public NMD_N02_CLOCK_AND_STATS_WITH_NOTES(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init(factory);
    }

    private void init(ModelClassFactory factory) {
       try {
                                  this.add(NMD_N02_CLOCK.class, false, false, false);
                                  this.add(NMD_N02_APP_STATS.class, false, false, false);
                                  this.add(NMD_N02_APP_STATUS.class, false, false, false);
       } catch(HL7Exception e) {
          log.error("Unexpected error creating NMD_N02_CLOCK_AND_STATS_WITH_NOTES - this is probably a bug in the source code generator.", e);
       }
    }

    /** 
     * Returns "2.5.1"
     */
    public String getVersion() {
       return "2.5.1";
    }



    /**
     * Returns
     * CLOCK (a Group object) - creates it if necessary
     */
    public NMD_N02_CLOCK getCLOCK() { 
       NMD_N02_CLOCK retVal = getTyped("CLOCK", NMD_N02_CLOCK.class);
       return retVal;
    }




    /**
     * Returns
     * APP_STATS (a Group object) - creates it if necessary
     */
    public NMD_N02_APP_STATS getAPP_STATS() { 
       NMD_N02_APP_STATS retVal = getTyped("APP_STATS", NMD_N02_APP_STATS.class);
       return retVal;
    }




    /**
     * Returns
     * APP_STATUS (a Group object) - creates it if necessary
     */
    public NMD_N02_APP_STATUS getAPP_STATUS() { 
       NMD_N02_APP_STATUS retVal = getTyped("APP_STATUS", NMD_N02_APP_STATUS.class);
       return retVal;
    }




}

