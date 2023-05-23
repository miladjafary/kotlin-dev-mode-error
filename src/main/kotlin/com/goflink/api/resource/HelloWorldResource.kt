package com.goflink.api.resource

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import java.util.*

@Path("")
class HelloWorldResource {
    @GET
    @Path("/hello")
    fun hello(): String {
        return "Hello world     qw"
    }
}