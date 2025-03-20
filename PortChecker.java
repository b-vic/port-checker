import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class PortChecker {
    /**
     * Called as: java PortChecker 127.0.0.1 8080 OR java PortChecker ./filename.txt
     */
    public static void main(String[] args) {

        String inputFile = null;
        if (args.length == 1) {
            inputFile = args[0];
        } else if (args.length != 2) {
            System.err.println("Incorrect usage. Arguments either: hostnameOrIpAddress port OR ./filename.txt");
            System.exit(-1);
        }

        //If we have a file, read and process one line at a time:
        if (Objects.nonNull(inputFile)) {
            processAddressesFromFile(inputFile);
        } else {
            //Only one address so inline call:
            tryConnect(new DestinationAddress(args[0], Integer.parseInt(args[1])));
        }
    }

    private static void processAddressesFromFile(String inputFile) {
        try (BufferedReader reader = new BufferedReader
                (new InputStreamReader(new FileInputStream(inputFile)))) {
            //Loop through file and try to connect to all addresses
            reader.lines()
                    .filter(s -> s.matches("^[a-z0-9A-Z]+.*")) //allow comments / empty lines
                    .map(DestinationAddress::new)
                    .forEach(PortChecker::tryConnect);
        } catch (IOException e) {
            System.err.println("Problem processing input file: " + inputFile);
            throw new RuntimeException(e);
        }
    }

    private static void tryConnect(DestinationAddress destinationAddress) {
        //Try with resources for auto closing:
        try (Socket socket = new Socket(destinationAddress.getHost(), destinationAddress.getPort());
             OutputStream outputStream = socket.getOutputStream()) {
            outputStream.flush();
            System.out.printf("Success connecting to %s%n", destinationAddress);
        } catch (IOException e) {
            System.out.printf("Failed connecting to %s%n", destinationAddress);
        }
    }

    static class DestinationAddress {

        private final String host;
        private final int port;

        public DestinationAddress(String hostAndPort) {
            //Allow any of the usual delimiters: space(s), comma or tab:
            String[] values = hostAndPort.split("[ ,\t:]+");
            this.host = values[0];
            this.port = Integer.parseInt(values[1]);
        }

        public DestinationAddress(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        @Override
        public String toString() {
            return "host=" + getHost() + ", port=" + getPort();
        }
    }
}
