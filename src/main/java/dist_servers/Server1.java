package dist_servers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class Server1 {
    private static final int SERVER_PORT = 5001;
    private static final int ADMIN_PORT = 7001;
    private static final int CLIENT_PORT = 6001;
    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final AdminHandler adminHandler = new AdminHandler(ADMIN_PORT);
    private static final ClientHandler clientHandler = new ClientHandler(CLIENT_PORT);

    public static ClientHandler getClientHandler() { return clientHandler; }

    public static void main(String[] args) {
        executorService.submit(() -> ServerHandler.startServer(SERVER_PORT));
        executorService.submit(adminHandler::startAdmin);
        executorService.submit(clientHandler::startClient);
    }
}