package org.petermac.hl7

import ca.uhn.hl7v2.DefaultHapiContext
import ca.uhn.hl7v2.parser.CustomModelClassFactory

class LocalHapiContext extends DefaultHapiContext {
    LocalHapiContext() {
        super()
        def cmf = new CustomModelClassFactory("org.petermac.hl7.model")
        setModelClassFactory(cmf);
    }
}
