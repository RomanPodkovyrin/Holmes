package com.server.responses

enum class RoutingResponses(val message: String) {
    PING("ping"),
    DOES_NOT_EXIST("Does not Exist"),
    RECEIVED("Received"),
    ALREADY_PROCESSED("Already Processed"),
}