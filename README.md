

# Work in progress - not done yet


# Multiple Responder flows to one Initiating flows - code example

A CorDapp to test out having multiple responder flow to a single initiating flow.





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