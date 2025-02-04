package dist_servers;

import javax.print.attribute.standard.Severity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributedSystem {
    private static final Map<Integer, ClientHandler> clients = new HashMap<Integer, ClientHandler>();

    static {
        clients.put(6001, Server1.getClientHandler());
        clients.put(6002, Server2.getClientHandler());
        clients.put(6003, Server3.getClientHandler());
    }

    public static synchronized Map<Integer, ClientHandler> getClients() { return clients; }

    public static void main(String[] args) {
        // FailureDetector failureDetector = new FailureDetector();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}