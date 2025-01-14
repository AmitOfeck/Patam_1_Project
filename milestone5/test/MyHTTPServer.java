package test;

import test.RequestParser.RequestInfo;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.*;
import java.util.Map;

public class MyHTTPServer extends Thread implements HTTPServer {

    private ConcurrentHashMap<String, Servlet> getHandlers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> postHandlers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> deleteHandlers = new ConcurrentHashMap<>();

    private ExecutorService handlerPool;

    private ServerSocket serverSocket;

    private volatile boolean serverShutdown = false;

    private final int serverPort;

    private final int poolSize;

    public MyHTTPServer(int port, int nThreads) {
        handlerPool = Executors.newFixedThreadPool(nThreads);
        this.serverPort = port;
        this.poolSize = nThreads;
    }

    public void addServlet(String httpCommand, String uri, Servlet servlet) {
        if (uri == null || servlet == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                getHandlers.put(uri, servlet);
                break;
            case "POST":
                postHandlers.put(uri, servlet);
                break;
            case "DELETE":
                deleteHandlers.put(uri, servlet);
                break;
        }
    }

    public void removeServlet(String httpCommand, String uri) {
        if (uri == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                getHandlers.remove(uri);
                break;
            case "POST":
                postHandlers.remove(uri);
                break;
            case "DELETE":
                deleteHandlers.remove(uri);
                break;
        }
    }

    public void run() {
        try (ServerSocket socket = new ServerSocket(serverPort)) {
            this.serverSocket = socket;
            socket.setSoTimeout(1000);

            while (!serverShutdown) {
                try {
                    Socket clientSocket = socket.accept();

                    handlerPool.submit(() -> {
                        try {
                            Thread.sleep(125);
                            BufferedReader requestReader = createBufferedReader(clientSocket);

                            RequestInfo request = RequestParser.parseRequest(requestReader);
                            ConcurrentHashMap<String, Servlet> servletMap;

                            if (request != null) {
                                switch (request.getHttpCommand()) {
                                    case "GET":
                                        servletMap = getHandlers;
                                        break;
                                    case "POST":
                                        servletMap = postHandlers;
                                        break;
                                    case "DELETE":
                                        servletMap = deleteHandlers;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unsupported HTTP command: " + request.getHttpCommand());
                                }

                                String longestMatchingUri = "";
                                Servlet matchedServlet = null;
                                for (Map.Entry<String, Servlet> entry : servletMap.entrySet()) {
                                    if (request.getUri().startsWith(entry.getKey()) && entry.getKey().length() > longestMatchingUri.length()) {
                                        longestMatchingUri = entry.getKey();
                                        matchedServlet = entry.getValue();
                                    }
                                }

                                if (matchedServlet != null) {
                                    matchedServlet.handle(request, clientSocket.getOutputStream());
                                }
                            }

                            requestReader.close();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    if (serverShutdown) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedReader createBufferedReader(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        int availableBytes = inputStream.available();
        byte[] buffer = new byte[availableBytes];
        int bytesRead = inputStream.read(buffer, 0, availableBytes);

        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(buffer, 0, bytesRead)
                )
        );
    }

    public void close() {
        serverShutdown = true;
        handlerPool.shutdownNow();
    }
}
