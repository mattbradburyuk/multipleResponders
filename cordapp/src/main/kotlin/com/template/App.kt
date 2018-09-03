package com.template

import co.paralleluniverse.fibers.Suspendable
import com.template.TemplateContract.Companion.ID
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startFlow
import net.corda.core.serialization.SerializationWhitelist
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
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

        rpcOps.startFlow(::Initiator_A).returnValue.get()

        return Response.ok("partyA Initiator called").build()
    }

    // Accessible at /api/template/templateGetEndpoint.
    @GET
    @Path("partyB")
    @Produces(MediaType.APPLICATION_JSON)
    fun PartyBEndpoint(): Response {

        rpcOps.startFlow(::Initiator_B).returnValue.get()

        return Response.ok("partyB Initiator called").build()
    }

}



// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator_A : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Flow implementation goes here

        logger.info("MB: Initiator_A called")


        val partyCX500 = CordaX500Name("PartyC","Paris","FR")

        logger.info("MB: partyCX500 = $partyCX500")

        val me: Party = serviceHub.myInfo.legalIdentities.single()
        val partyCOrNull: Party? = serviceHub.networkMapCache.getPeerByLegalName(partyCX500)

        logger.info("MB: partyCorNull = $partyCOrNull")

        if (partyCOrNull != null) {

            logger.info("PartyC found")
        } else
        {
            logger.info("PartyC not found")
            throw(FlowException("PartyC not Found"))

        }
        val partyC: Party = partyCOrNull!!
        val state = TemplateState("Party A data", listOf(me, partyC))

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val tx = TransactionBuilder(notary)

        tx.addOutputState(state, TemplateContract.ID)
        tx.addCommand(TemplateContract.Commands.Action(), me.owningKey, partyC.owningKey)


        tx.verify(serviceHub)

        val ptx = serviceHub.signInitialTransaction(tx)
        val session = initiateFlow(partyC)

        val stx = subFlow(CollectSignaturesFlow(ptx, listOf(session)))

        val ftx = subFlow(FinalityFlow(stx))

    }
}


@InitiatingFlow
@StartableByRPC
class Initiator_B : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Flow implementation goes here

        logger.info("MB: Initiator_B called")

    }
}



@InitiatedBy(Initiator_A::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Flow implementation goes here
        val signedTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a Template transaction" using (output is TemplateState)
            }
        }

        subFlow(signedTransactionFlow)



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
