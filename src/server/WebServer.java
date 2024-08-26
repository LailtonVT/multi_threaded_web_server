package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe WebServer
 * 
 * Representa um servidor web multi-threaded que aceita conexões simultâneas de clientes.
 * Utiliza um pool de threads para gerenciar múltiplas requisições ao mesmo tempo.
 * 
 * @author
 * Lailton Vilarim Tenorio
 * Data: 2024-08-23
 */
public class WebServer {
    private final int port;
    private final ExecutorService executor;

    /**
     * Construtor do WebServer.
     * 
     * @param port Porta onde o servidor irá ouvir.
     * @param threadPoolSize Número de threads no pool.
     */
    public WebServer(int port, int threadPoolSize) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Inicia o servidor, aceitando conexões de clientes e delegando-as para o pool de threads.
     * 
     * @throws IOException Se ocorrer um erro ao abrir o socket do servidor.
     */
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado na porta " + port);

            // Aceita conexões enquanto o executor não é encerrado
            while (!executor.isShutdown()) {
                Socket clientSocket = serverSocket.accept();

                // Cada requisição de cliente é tratada por uma nova tarefa
                executor.submit(new HttpRequestHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encerra o servidor, desligando o pool de threads.
     */
    public void shutdown() {
        executor.shutdown();
    }
}
