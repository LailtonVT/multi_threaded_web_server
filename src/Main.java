import java.io.IOException;

import server.WebServer;

/**
 * Classe Main
 * 
 * Ponto de entrada do programa. Inicializa o servidor web multi-threaded.
 * 
 * @author
 * Lailton Vilarim Tenorio
 * Data: 2024-08-23
 */
public class Main {
    public static void main(String[] args) {
        WebServer server = new WebServer(8080, 4);  // Servidor na porta 8080 com 4 threads
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
