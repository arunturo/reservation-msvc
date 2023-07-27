package com.turo.domain

class DomainException(message: String): Exception(message)

class InvalidOperationException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}