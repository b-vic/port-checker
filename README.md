# port-checker
Connect to destination address(es) to validate connectivity.  Supports ip address or FQDN (fully qualified domain name).

This class is intentionally using a default package and all in 1 file to simplify being compiled remotely such as in a limited access environment.

Support for java 8 is intentional to allow legacy environment support.  Several alternatives exist but aren't always available.  All you need is java and/or javac.

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
> 127.0.0.1,6970
> 
> 127.0.0.1	9222
> 
> #Test env
> 
> 127.0.0.1,9528
> 
> //Also works with :
> 
> www.google.com:443

