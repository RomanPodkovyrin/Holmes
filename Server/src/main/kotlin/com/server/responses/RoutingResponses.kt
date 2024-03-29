package com.server.responses

enum class RoutingResponses(val message: String) {
    PING("Ping from Holmes Server"),
    DOES_NOT_EXIST("Does not Exist"),
    RECEIVED("Received"),
    ALREADY_PROCESSED("Already Processed"),
    FAILED("Processing Failed")
}