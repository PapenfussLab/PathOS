package org.petermac.babble.api

/**
 *
 * Base class for exceptions thrown by the Babble API.
 *
 */
class BabbleApiException extends Exception {
    BabbleApiException(String msg) {
        super(msg)
    }
}

/**
 *
 * The data being passed in is badly formed,
 * or has illegal values.
 *
 */
class BadData extends BabbleApiException {
    BadData(String msg) {
        super(msg)
    }
}

/**
 *
 * When configuring a source, translator or destination,
 * a required parameter is missing.
 *
 */
class RequiredParameterMissing extends BabbleApiException {
    RequiredParameterMissing(String msg) {
        super(msg)
    }
}

/**
 *
 * When configuring a source, translator or destination,
 * an unknown parameter has been passed.
 *
 */
class UnknownParameterName extends BabbleApiException {
    UnknownParameterName(String msg) {
        super(msg)
    }
}

/**
 *
 * When configuring a source, translator or destination,
 * a combination of parameters has been supplied that does
 * not make sense.
 *
 */
class InvalidParameterCombination extends BabbleApiException {
    InvalidParameterCombination(String msg) {
        super(msg)
    }
}

/**
 *
 * When configuring a source, translator or destination,
 * a parameter value has been passed which is of the wrong
 * type or out of range.
 *
 */
class InvalidParameterValue extends BabbleApiException {
    InvalidParameterValue(String msg) {
        super(msg)
    }
}

