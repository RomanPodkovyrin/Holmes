package com.romanp.fyp.nlp

enum class ServerResponse(val message: String) {
    PING("ping"),
    DOES_NOT_EXIST("Does not Exist"),
    RECEIVED("Received"),
    ALREADY_PROCESSED("Already Processed"),
}