package org.petermac.yaml

class YamlException extends Exception {
    YamlException(String msg) {
        super(msg)
    }
}

class BadlyFormedType extends YamlException {
    BadlyFormedType(String msg) {
        super(msg)
    }
}

class TypeError extends YamlException {
    TypeError(String msg) {
        super(msg)
    }
}

class RequiredParameterMissing extends YamlException {
    RequiredParameterMissing(String msg) {
        super(msg)
    }
}

class UnknownParameterName extends YamlException {
    UnknownParameterName(String msg) {
        super(msg)
    }
}

class InvalidParameterCombination extends YamlException {
    InvalidParameterCombination(String msg) {
        super(msg)
    }
}

class InvalidParameterValue extends YamlException {
    InvalidParameterValue(String msg) {
        super(msg)
    }
}
