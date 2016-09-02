

/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.pathos.curate

/**
 * Created by seleznev andrei on 2/05/2016.
 */
public interface Taggable {
    static	hasMany = [ tags: Tag ]
}




