package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startFlow
import net.corda.core.serialization.SerializationWhitelist
import net.corda.webserver.services.WebServerPluginRegistry
import java.util.function.Function
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

// *****************
// * API Endpoints *
// *****************
@Path("template")
class TemplateApi(val rpcOps: CordaRPCOps) {
    // Accessible at /api/template/templateGetEndpoint.
    @GET
    @Path("templateGetEndpoint")
    @Produces(MediaType.APPLICATION_JSON)
    fun templateGetEndpoint(): Response {
        return Response.ok("Template GET endpoint.").build()
    }
}

@Path("initiate")
class InitiateApi(val rpcOps: CordaRPCOps) {
    // Accessible at /api/template/templateGetEndpoint.
    @GET
    @Path("partyA")
    @Produces(MediaType.APPLICATION_JSON)
    fun PartyAEndpoint(): Response {

        rpcOps.startFlow(::Initiator).returnValue.get()

        return Response.ok("partyA Initiator called").build()
    }

    // Accessible at /api/template/templateGetEndpoint.
    @GET
    @Path("partyB")
    @Produces(MediaType.APPLICATION_JSON)
    fun PartyBEndpoint(): Response {

        rpcOps.startFlow(::Initiator).returnValue.get()

        return Response.ok("partyB Initiator called").build()
    }


}



// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Flow implementation goes here

        logger.info("MB: Initiator called")

    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Flow implementation goes here


    }
}

// ***********
// * Plugins *
// ***********
class TemplateWebPlugin : WebServerPluginRegistry {
    // A list of lambdas that create objects exposing web JAX-RS REST APIs.
    override val webApis: List<Function<CordaRPCOps, out Any>> = listOf(Function(::TemplateApi), Function(::InitiateApi))
    //A list of directories in the resources directory that will be served by Jetty under /web.
    // This template's web frontend is accessible at /web/template.
    override val staticServeDirs: Map<String, String> = mapOf(
        // This will serve the templateWeb directory in resources to /web/template
        "template" to javaClass.classLoader.getResource("templateWeb").toExternalForm()
    )
}

// Serialization whitelist.
class TemplateSerializationWhitelist : SerializationWhitelist {
    override val whitelist: List<Class<*>> = listOf(TemplateData::class.java)
}

// This class is not annotated with @CordaSerializable, so it must be added to the serialization whitelist, above, if
// we want to send it to other nodes within a flow.
data class TemplateData(val payload: String)
