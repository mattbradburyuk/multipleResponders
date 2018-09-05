package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startFlow
import net.corda.core.transactions.TransactionBuilder
import net.corda.webserver.services.WebServerPluginRegistry
import java.util.function.Function
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


/**
 * Created by MatthewBradbury on 04/09/2018.
 */

// *****************
// * API Endpoints *
// *****************

@Path("initiate")
class InitiateApi(val rpcOps: CordaRPCOps) {

    @GET
    @Path("topartyB")
    @Produces(MediaType.APPLICATION_JSON)
    fun toPartyBEndpoint(): Response {

        rpcOps.startFlow(::Initiator_A,"PartyA data to Party B", "PartyB","New York","US" ).returnValue.get()

        return Response.ok("partyA Initiator called").build()
    }

    @GET
    @Path("topartyC")
    @Produces(MediaType.APPLICATION_JSON)
    fun toPartyCEndpoint(): Response {

        rpcOps.startFlow(::Initiator_A,"PartyA data to PartyC", "PartyC","Paris","FR" ).returnValue.get()

        return Response.ok("partyA Initiator called").build()
    }

}

// *********
// * Flows *
// *********

@InitiatingFlow
@StartableByRPC
class Initiator_A(D:String,  O: String, L: String, C:String) : CommonInitiator(D, O, L, C)


open class CommonInitiator(val data: String, val O: String, val L: String, val C:String) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Flow implementation goes here

        logger.info("MB: CommonInitiator called")

        val x500 = CordaX500Name(O ,L, C)

        val me: Party = serviceHub.myInfo.legalIdentities.single()
        val partyOrNull: Party? = serviceHub.networkMapCache.getPeerByLegalName(x500)

        logger.info("MB: partyOrNull = $partyOrNull")

        if (partyOrNull != null) {
            logger.info("Party $x500 found")
        } else {
            logger.info("Party $x500 not found")
            throw(FlowException("Party $x500 not Found"))
        }
        val party: Party = partyOrNull!!
        val state = TemplateState(data, listOf(me, party))

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val tx = TransactionBuilder(notary)
        tx.addOutputState(state, TemplateContract.ID)
        tx.addCommand(TemplateContract.Commands.Action(), me.owningKey, party.owningKey)
        tx.verify(serviceHub)

        val ptx = serviceHub.signInitialTransaction(tx)
        val session = initiateFlow(party)
        val stx = subFlow(CollectSignaturesFlow(ptx, listOf(session)))
        val ftx = subFlow(FinalityFlow(stx))

    }
}

// ***********
// * Plugins *
// ***********
class InitiatorWebPlugin : WebServerPluginRegistry {
    // A list of lambdas that create objects exposing web JAX-RS REST APIs.
    override val webApis: List<Function<CordaRPCOps, out Any>> = listOf(Function(::InitiateApi))
    //A list of directories in the resources directory that will be served by Jetty under /web.
    // This template's web frontend is accessible at /web/template.
    override val staticServeDirs: Map<String, String> = mapOf(
            // This will serve the templateWeb directory in resources to /web/template
            "templatex" to javaClass.classLoader.getResource("templateWeb").toExternalForm()
    )
}