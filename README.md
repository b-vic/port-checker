# port-checker
Connect to destination address(es) (host and a port) to validate connectivity.  Call by ip address or hostname.

This class is intentionally using default package and all in 1 file to simplify being compiled remotely.

Support for java 8 is intentional to allow legacy environment support.

## Usage

**Compile:**

javac PortChecker.java

**Run:**

java PortChecker 127.0.0.1 8080

_OR_

java PortChecker ./pathTofile.txt

## Sample input file

> 127.0.0.1      9000
> 
> 127.0.0.1 9080
> 
> 127.0.0.1,69700
> 
> 127.0.0.1	9222
> 
> #Test env
> 
> 127.0.0.1,9528
> 
> //Also works with :
> 
> localhost:9223

