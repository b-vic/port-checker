import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PortChecker {

    private static final Pattern VALID_START_OF_LINE = Pattern.compile("^[a-z0-9]", Pattern.CASE_INSENSITIVE);
    private static final Pattern SERVER_PORT_DELIMITERS = Pattern.compile("[ ,\t:]+");

    private List<DestinationAddress> destinationAddresses = new ArrayList<>();

    /**
     * Usage: java PortChecker 127.0.0.1 8080 OR java PortChecker ./filename.txt
     */
    public static void main(String[] args) {
        PortChecker portChecker = new PortChecker(args);
        portChecker.connectToAddresses();
    }

    public PortChecker(String[] args) {
        this.destinationAddresses = processInputArgs(args);
    }

    private List<DestinationAddress> processInputArgs(String[] args) {
        final List<DestinationAddress> addressList;
        switch (args.length) {
            case 1:
                addressList = readAndFilterLines(args[0]);
                break;
            case 2:
                addressList = Arrays.asList(new DestinationAddress(args[0], args[1]));
                break;
            default:
                throw new RuntimeException("Incorrect usage. Arguments either: hostnameOrIpAddress port OR ./filename.txt");
        }
        return addressList;

    }

    private void connectToAddresses() {
        int successCount =
                destinationAddresses.stream()
                        .parallel()
                        .map(PortChecker::tryConnect)
                        .mapToInt(result -> result ? 1 : 0)
                        .sum();
        System.out.printf("==========%nSuccessfully connected to: %d of %d addresses %n", successCount, destinationAddresses.size());
    }

    private List<DestinationAddress> readAndFilterLines(String inputFile) {
        final Path pathToFile = getFile(inputFile);
        final List<DestinationAddress> addressLines;
        try {
            addressLines = Files.readAllLines(pathToFile).stream()
                    .filter(VALID_START_OF_LINE.asPredicate())
                    .map(DestinationAddress::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.printf("Aborting due to : %s%n", e.getMessage());
            throw new RuntimeException(e);
        }
        return addressLines;
    }

    private Path getFile(String inputFile) {
        final Path pathToFile = Paths.get(inputFile);
        if (!Files.exists(pathToFile)) {
            throw new RuntimeException(String.format("There is a problem accessing the input file: %s", inputFile));
        }
        return pathToFile;
    }

    private static boolean tryConnect(DestinationAddress destinationAddress) {
        try (Socket socket = new Socket(destinationAddress.getHost(), destinationAddress.getPort());
             OutputStream outputStream = socket.getOutputStream()) {
            outputStream.flush();
            System.out.printf("Success connecting to %s%n", destinationAddress);
            return true;
        } catch (IOException e) {
            System.out.printf("Failed connecting to %s%n", destinationAddress);
            return false;
        }
    }

    static class DestinationAddress {

        private final String host;
        private final int port;

        public DestinationAddress(String host, String port) {
            this.host = host;
            this.port = Integer.parseInt(port);
        }

        public DestinationAddress(String hostAndPort) {
            //Allow any of the usual delimiters: space(s), comma, tab or colon:
            String[] values = SERVER_PORT_DELIMITERS.split(hostAndPort);
            this.host = values[0];
            this.port = Integer.parseInt(values[1]);
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        @Override
        public String toString() {
            return "host=" + this.getHost() + ", port=" + this.getPort();
        }
    }
}
