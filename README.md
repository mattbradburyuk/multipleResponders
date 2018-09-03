![Corda](https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png)

# Multiple Initiating flows to one initiatedBy flow - code example

A CorDapp to test out having a single responder flow to multiple initiating flows.

Two approaches for the responder:

1) Flow inheritance 

```kotlin
    @InitiatedBy(Initiator_A::class)
    class Responder_A(counterpartySession: FlowSession) : CommonResponder(counterpartySession)
    
    @InitiatedBy(Initiator_B::class)
    class Responder_B(counterpartySession: FlowSession) : CommonResponder(counterpartySession)
    
    open class CommonResponder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            //do stuff
        }
    }
```
2) Subflows 
```kotlin
    @InitiatedBy(Initiator_A2::class)
    class Responder_A2(val counterpartySession: FlowSession) : FlowLogic<Unit>(){
    
        @Suspendable
        override fun call() {
            val flow = CommonResponder_2(counterpartySession)
            subFlow(flow)
        }
    }
    
    @InitiatedBy(Initiator_B2::class)
    class Responder_B2(val counterpartySession: FlowSession) : FlowLogic<Unit>(){
    
        @Suspendable
        override fun call() {
            val flow = CommonResponder_2(counterpartySession)
            subFlow(flow)
        }
    }
    
    
    open class CommonResponder_2 (val counterpartySession: FlowSession) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            //do stuff
        }
    }
```

## Getting Set Up

To get started, clone this repository with:

     ** to add **

And change directories to the newly cloned repo:

     ** to add **

## Building the CorDapp template:

**Unix:** 

     ./gradlew deployNodes

**Windows:**

     gradlew.bat deployNodes

Note: You'll need to re-run this build step after making any changes to
the template for these to take effect on the node.

## Running the Nodes

Once the build finishes, change directories to the folder where the newly
built nodes are located:

     cd build/nodes

The Gradle build script will have created a folder for each node. You'll
see three folders, one for each node and a `runnodes` script. You can
run the nodes with:

**Unix:**

     ./runnodes

**Windows:**

    runnodes.bat

You should now have four Corda nodes running on your machine serving 
the template.


## Interacting with the CorDapp via HTTP

The nodes can be found using the following port numbers, defined in 
`build.gradle`, as well as the `node.conf` file for each node found
under `build/nodes/partyX`:

     PartyA: localhost:10007
     PartyB: localhost:10010
     PartyC: localhost:10013 

##http calls

http://localhost:10007/api/initiate/partyA - Flow from partyA to responder using inherited flows
http://localhost:10010/api/initiate/partyB - Flow from partyB to responder using inherited flows
http://localhost:10007/api/initiate/partyA2 - Flow from partyA to responder using subflows
http://localhost:10010/api/initiate/partyB2 - Flow from partyB to responder using subflows

http://localhost:10013/api/vault/getStates - see what turns up in PartyC vault