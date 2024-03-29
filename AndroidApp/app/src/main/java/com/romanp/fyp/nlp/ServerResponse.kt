package com.romanp.fyp.nlp

enum class ServerResponse(val message: String) {
    PING("Ping from Holmes Server"),
    DOES_NOT_EXIST("Does not Exist"),
    RECEIVED("Received"),
    ALREADY_PROCESSED("Already Processed"),
    FAILED("Processing Failed")
}