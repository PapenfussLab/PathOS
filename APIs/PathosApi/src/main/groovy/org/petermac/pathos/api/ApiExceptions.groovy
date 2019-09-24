package org.petermac.pathos.api

class PathosApiException extends Exception {
    PathosApiException(String msg) {
        super(msg)
    }
}

class UnsupportedDomain extends PathosApiException {
    UnsupportedDomain(String msg) {
        super(msg)
    }
}

class UnsupportedAction extends PathosApiException {
    UnsupportedAction(String msg) {
        super(msg)
    }
}

class BadData extends PathosApiException {
    BadData(String msg) {
        super(msg)
    }
}
