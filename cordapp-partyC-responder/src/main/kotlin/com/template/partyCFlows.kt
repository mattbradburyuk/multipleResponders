package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.requireThat
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.transactions.SignedTransaction

/**
 * Created by MatthewBradbury on 04/09/2018.
 */


@InitiatedBy(Initiator_A::class)
class Responder_A(counterpartySession: FlowSession) : CommonResponder(counterpartySession)



open class CommonResponder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {

        logger.info("MB: response from partyCFlows")
        logger.info("MB:  ${serviceHub.myInfo.legalIdentities.single().name} Responder flow called by: ${counterpartySession.counterparty.name }")

        val signedTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a Template transaction" using (output is TemplateState)
            }
        }

        subFlow(signedTransactionFlow)

    }
}