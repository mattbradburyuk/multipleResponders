

# Work in progress - not done yet


# Multiple Responder flows to one Initiating flows - code example

A CorDapp to test out having multiple responder flow to a single initiating flow.



## Split into Modules

To enable different responder flows the initiator and responder flows have been separated into different modules:

Note, I have used a CommonInitiator and CommonResponder super class for each of the initiators and responders which gives more flexibility if there were more initiators or responders, but that's not necessary for the pattern to work.


#### cordapp

Common functionality eg vault query (not much else))

#### cordapp-contracts-states 

The usual split out of states and contracts from the main cordapp


#### cordapp-partyA-initiator

Flows and web end points to allow a party to initiate a Flow

```kotlin
@InitiatingFlow
@StartableByRPC
class Initiator_A(D:String,  O: String, L: String, C:String) : CommonInitiator(D, O, L, C)

open class CommonInitiator(val data: String, val O: String, val L: String, val C:String) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // do stuff
    }
}
```


#### cordapp-partyB-responder

Bespoke flow for Party B to respond to Initiator_A.

There is a logging line to indicate that it is partyB's bespoke flow responding

```kotlin
@InitiatedBy(Initiator_A::class)
class Responder_A(counterpartySession: FlowSession) : CommonResponder(counterpartySession)

open class CommonResponder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {

        logger.info("MB: response from partyBFlows")

        // Do other stuff
    }
}
```


#### cordapp-partyC-responder

Bespoke flow for Party C to respond to Initiator_A.

There is a logging line to indicate that it is partyC's bespoke flow responding

```kotlin
@InitiatedBy(Initiator_A::class)
class Responder_A(counterpartySession: FlowSession) : CommonResponder(counterpartySession)

open class CommonResponder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {

        logger.info("MB: response from partyCFlows")
        // Do other stuff
    }
}
```


##Dependencies

The build dependencies for each module are as follows

#### multipleResponders


#### cordapp

```gradle
cordapp project(":cordapp-contracts-states")
```

#### cordapp-partyA-initiator

```gradle
cordapp project(":cordapp")
cordapp project(":cordapp-contracts-states")
```

#### cordapp-partyB-responder and cordapp-partyC-responder

```gradle
cordapp project(":cordapp")
cordapp project(":cordapp-partyA-initiator")
cordapp project(":cordapp-contracts-states")
```
the responders need cordapp-partyA-initiator to be able to referene the initiator in the @initiated:

```kotlin
@InitiatedBy(Initiator_A::class)
class Responder_A()

```








## Getting Set Up

To get started, clone this repository with:

    git clone https://github.com/mattbradburyuk/multipleResponders.git

And change directories to the newly cloned repo

     

## Building the CorDapp template:

**Unix:** 

     ./gradlew deployNodes

**Windows:**

     gradlew.bat deployNodes

Note: You'll need to re-run this build step after making any changes to
the template for these to take effect on the node.

If it goes wrong try 
    
    killall java -9
    
    ./gradlew clean
    
then retry to buildNodes

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

## http calls



http://localhost:10013/api/vault/getStates - see what turns up in PartyC vault

## 

(Note, this repo is based on the Cordapp Template from R3: https://github.com/corda/cordapp-template-kotlin)

MB note to self: git push -u origin master