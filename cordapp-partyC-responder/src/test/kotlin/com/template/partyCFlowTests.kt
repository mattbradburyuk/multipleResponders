package com.template

import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.getOrThrow
import net.corda.testing.node.MockNetwork
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Created by MatthewBradbury on 04/09/2018.
 */
class partyCFlowTests{


    private val network = MockNetwork(listOf("com.template"))

    val aX500 = CordaX500Name("PartyA","London","GB")
    val bX500 = CordaX500Name("PartyB","New York","US")
    val cX500 = CordaX500Name("PartyC","Paris","FR")

    private val a = network.createPartyNode(aX500)
    private val b = network.createNode(bX500)
    private val c = network.createNode(cX500)

    init {
        listOf(a, b, c).forEach {
            it.registerInitiatedFlow(Responder_A::class.java)
        }
    }

    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `partyA to PartyC test`() {

        val flow = Initiator_A("partyA data to PartyC","PartyC","Paris","FR")

        val future = a.startFlow(flow)

        network.runNetwork()

        future.getOrThrow()

    }



}