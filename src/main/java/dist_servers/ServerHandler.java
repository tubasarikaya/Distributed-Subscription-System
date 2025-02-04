package dist_servers;

import communication.ProtobufHandler;
import communication.SubscriberOuterClass.Subscriber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandler {
    private final int id;
    private static int port = 0;
    private static ConcurrentMap<Integer, Subscriber> subscriberData = new ConcurrentHashMap<>();
    private static Map<Integer, Socket> connectedServers = new HashMap<Integer, Socket>();
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static final int CONNECTION_RETRY_INTERVAL = 5000; // 5 saniye
    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public ServerHandler(int id, int port) {
        this.id = id;
        ServerHandler.port = port;
        ServerHandler.connectedServers = new HashMap<Integer, Socket>();
    }

    public static Map<Integer, Socket> getConnectedServers() {
        return connectedServers;
    }

    public int getId() {
        return id;
    }

    public static void startServer(int serverPort) {
        try {
            port = serverPort;
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Server started on port " + serverPort);
            executorService.submit(ServerHandler::connectToOtherServers);
            executorService.submit(ServerHandler::handleClientConnection);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientConnection() {
        while (true) {
            try {
                if (serverSocket == null || serverSocket.isClosed()) {
                    System.err.println("Server socket is closed. Stopping server client connection attempts.");
                    break;
                }
                clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                executorService.submit(() -> handleClient(clientSocket));
            } catch (SocketException e) {
                System.err.println("Client server socket is closed. No longer accepting clients.");
                break;
            } catch (IOException e) {
                System.err.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream())) {
            while (true) {
                try {
                    Subscriber sub = ProtobufHandler.receiveProtobufMessage(inputStream, Subscriber.class);

                    if (sub == null) {
                        System.err.println("Received null Subscriber message.");
                        continue;
                    } else {
                        System.out.println("Subscriber Message: " + sub);

                        ClientHandler.processSubscriber(subscriberData, sub);
                        subscriberData.put(sub.getID(), sub);

                        System.out.println("Server " + port + " subscriberları:");
                        for (int i = 0; i < subscriberData.size(); i++) {
                            System.out.println(subscriberData.get(i));
                        }
                    }
                } catch (EOFException e) {
                    System.out.println("Client connection closed gracefully.");
                    try {
                        clientSocket.close();
                    } catch (IOException ex) {
                        System.err.println("Error closing socket after EOF: " + ex.getMessage());
                    }
                    break;
                } catch (SocketException e) {
                    System.err.println("Socket error while handling client: " + e.getMessage());
                    try {
                        clientSocket.close();
                    } catch (IOException ex) {
                        System.err.println("Error closing socket after SocketException: " + ex.getMessage());
                    }
                    break;
                } catch (IOException e) {
                    System.err.println("IO error while handling client: " + e.getMessage());
                    try {
                        clientSocket.close();
                    } catch (IOException ex) {
                        System.err.println("Error closing socket after IOException: " + ex.getMessage());
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            System.err.println("Socket error on client socket: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error on client socket: " + e.getMessage());
        }
        finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket in finally: " + e.getMessage());
            }
        }
    }

    /* private static void handleClientRequest(Socket clientSocket){
        try(DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())){

            String request = input.readUTF();
            if("HEALTH_CHECK".equals(request)){
                System.out.println("Sağlık kontrolü isteği alındı: " + clientSocket.getInetAddress());
                return;
            } else if("GET_PRIMARY_PORT".equals(request)){
                if(isPrimary){
                    output.writeInt(ServerHandler.port);
                    System.out.println("Primary port sent to client: " + ServerHandler.port + " from primary");
                    primaryPort = ServerHandler.port;
                }else{
                    int pPort =  0;
                    for(ServerHandler sh : DistributedSystem.getServers()){
                        if(sh.getIsPrimary() && sh.getIsAlive()){
                            pPort = sh.getPort();
                            break;
                        }
                    }

                    output.writeInt(pPort);
                    System.out.println("Secondary port sent to client: " + pPort + " from secondary");
                }
            }
        }catch (IOException e){
            if (!(e instanceof EOFException)) {
                e.printStackTrace();
            } else {
                System.out.println("Sağlık kontrolü bağlantısı kapatıldı.");
            }
        }
    } */

    private static void connectToOtherServers() {
        while (!AdminHandler.getIsRunning()) {
            try {
                Thread.sleep(1000); // 1 saniye bekle
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        int[] otherPorts = {5001, 5002, 5003};
        for (int otherPort : otherPorts) {
            try {
                Socket socket = new Socket("localhost", otherPort);
                connectedServers.put(otherPort, socket);
                System.out.println("Port " + otherPort + " için socket başarıyla eklendi.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Connected to server on port " + otherPort);
            // executorService.submit(()->handleSync(socket));
        }

        try {
            Thread.sleep(CONNECTION_RETRY_INTERVAL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    /* private static void handleSync(Socket socket){
        try(DataInputStream input = new DataInputStream(socket.getInputStream());){
            while(true){
                Subscriber subscriber = ProtobufHandler.receiveProtobufMessage(input, Subscriber.class);
                if(subscriber != null){
                    addSubscriberData(subscriber);
                }

            }

        }catch (IOException e){
            e.printStackTrace();
        }
    } */


    /* private static boolean isConnected(int port) {
        try{
            return connectedServers.stream().anyMatch(socket -> socket.getPort() == port && socket.isConnected() && !socket.isClosed());
        }catch (Exception e){
            return false;
        }
    } */

    /* private static void syncData(Subscriber subscriber) {
        if (isPrimary) {
            for(Map<Integer, Socket> socket: connectedServers){
                try {
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    ProtobufHandler.sendProtobufMessage(output, subscriber);
                    System.out.println("Data sync to server with port " + socket.getPort());
                } catch (IOException e) {
                    System.err.println("Error during data synchronization with server. " + socket.getPort() + e.getMessage());
                }
            }
        }
    } */
}