# port-checker
Connect to destination address(es) on a server and port to validate connectivity from command line.  Supports IP or FQDN (fully qualified domain name) address.

Several alternatives to this exist but are not always available nor processed in parallel.  All you need is java to run and javac if you can't upload a .class file to your server.

Useful for server migrations, firewalls, and permissions testing.

## Usage

**Compile:**

javac PortChecker.java

**Run:**

java PortChecker 127.0.0.1 8080

_OR_

java PortChecker ./pathTofile.txt

## Sample input file with supported comments and host + port separators

> //Tab
> 
> 127.0.0.1      8000
> 
> #Space:
> 
> 127.0.0.1 9080
>
> --comma
> 
> 127.0.0.1,6970
> 
> 127.0.0.1,9528
> 
> //Also works with :
> 
> www.google.com:443
> 

## Sample output demonstrating parallel processing

_Notice successes are now at the top since failures take longer to occur_

> 
> Success connecting to host=127.0.0.1, port=9528
> 
> Success connecting to host=www.google.com, port=443
> 
> Failed connecting to host=127.0.0.1, port=9000
> 
> Failed connecting to host=127.0.0.1, port=9080
>
> Failed connecting to host=127.0.0.1, port=6970
>
> ==========
> 
> Successfully connected to: 2 of 5 addresses
> 

