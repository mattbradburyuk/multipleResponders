# Multiple Responder flows to one Initiating flows - code example

A CorDapp to test out having multiple responder flows to a single initiating flow.

- There are 3 Parties (+ the notary)

- Party A can initiate a flow to either Party B or Party C

- Party B and Party C implement bespoke responder flows to the same Initiator flow from Party A

See below for set up and http calls to interact with the cordapp.


## Split into Modules

To enable different responder flows the initiator and responder flows have been separated into different modules:

Note, I have used a CommonInitiator and CommonResponder super class for each of the initiators and responders which gives more flexibility if there were more initiators or responders, but that's not necessary for the pattern to work.


#### cordapp

Common functionality eg vault query (not much else))

#### cordapp-contracts-states 

The usual split out of states and contracts from the main cordapp


#### cordapp-partyA-initiator

Flows to allow a party to initiate a Flow (also web end points)

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
Note, there is a logging line to indicate that it is partyB's bespoke flow responding

#### cordapp-partyC-responder

Bespoke flow for Party C to respond to Initiator_A.



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

Note, there is a logging line to indicate that it is partyB's bespoke flow responding

## Setting up build Dependencies

The build dependencies for each module are specified in each module's build.gradle file as follows:

#### multipleResponders

```gradle
cordapp project(":cordapp")
cordapp project(":cordapp-contracts-states")
cordapp project(":cordapp-partyA-initiator")
cordapp project(":cordapp-partyB-responder")
cordapp project(":cordapp-partyC-responder")
```



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
Note that the responders need cordapp-partyA-initiator dependency to be able to reference the initiator in the @InitiatedBy annotation :

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

If it goes wrong, or you want to clean up the nodes try 
    
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

To interact with the app use: 

http://localhost:10007/api/initiate/topartyB - start a flow from PartyA to Party B

http://localhost:10007/api/initiate/topartyC - start a flow from PartyA to Party C

http://localhost:10010/api/vault/getStates - see what turns up in PartyB vault

http://localhost:10013/api/vault/getStates - see what turns up in PartyC vault

## 

(Note, this repo is based on the Cordapp Template from R3: https://github.com/corda/cordapp-template-kotlin)

MB note to self: git push -u origin master