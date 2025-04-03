import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.AbstractMap.SimpleEntry;

public class PortChecker {

    private static final Pattern VALID_START_OF_LINE = Pattern.compile("^[a-z0-9]", Pattern.CASE_INSENSITIVE);
    private static final Pattern SERVER_PORT_DELIMITERS = Pattern.compile("[ ,\t:]+");
    private final List<SimpleEntry<String, Integer>> destinationAddresses;
    private final long startTime;

    public PortChecker(String[] args) {
        this.destinationAddresses = processInputArgs(args);
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Usage: java PortChecker 127.0.0.1 8080 OR java PortChecker ./filename.txt
     */
    public static void main(String[] args) {
        PortChecker portChecker = new PortChecker(args);
        portChecker.connectToAddresses();
    }

    private List<SimpleEntry<String, Integer>> processInputArgs(String[] args) {
        final List<SimpleEntry<String, Integer>> addressList;
        switch (args.length) {
            case 1:
                addressList = readAndFilterLines(args[0]);
                break;
            case 2:
                addressList = Collections.singletonList(new SimpleEntry<>(args[0], Integer.parseInt(args[1])));
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
        System.out.printf("==========%nSuccessfully connected to: %d of %d addresses in %f seconds %n",
                successCount, destinationAddresses.size(), (System.currentTimeMillis() - startTime) / 1000.0);
    }

    private static boolean tryConnect(SimpleEntry<String, Integer> destinationAddress) {
        try (Socket socket = new Socket(destinationAddress.getKey(), destinationAddress.getValue());
             OutputStream outputStream = socket.getOutputStream()) {
            outputStream.flush();
            System.out.printf("Success connecting to %s%n", destinationAddress);
            return true;
        } catch (IOException e) {
            System.out.printf("Failed connecting to %s%n", destinationAddress);
            return false;
        }
    }

    private List<SimpleEntry<String, Integer>> readAndFilterLines(String inputFile) {
        final Path pathToFile = getFile(inputFile);
        List<SimpleEntry<String, Integer>> destinationAddressLines;
        try {
            destinationAddressLines = Files.readAllLines(pathToFile).stream()
                    .filter(VALID_START_OF_LINE.asPredicate())
                    .map(SERVER_PORT_DELIMITERS::split)
                    .filter(hostPortArray -> hostPortArray.length == 2)
                    .map(hostPort -> new SimpleEntry<>(hostPort[0], Integer.parseInt(hostPort[1])))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.printf("Aborting due to : %s%n", e.getMessage());
            throw new RuntimeException(e);
        }
        return destinationAddressLines;
    }

    private Path getFile(String inputFile) {
        final Path pathToFile = Paths.get(inputFile);
        if (!Files.exists(pathToFile)) {
            throw new RuntimeException(String.format("There is a problem accessing the input file: %s", inputFile));
        }
        return pathToFile;
    }
}