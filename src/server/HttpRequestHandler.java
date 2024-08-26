package server;

import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * Classe HttpRequestHandler
 * 
 * Representa uma tarefa que trata uma requisição HTTP. Para cada cliente que se conecta,
 * uma nova thread é criada para lidar com essa requisição.
 * Agora processa dois valores numéricos enviados como parâmetros na URL e retorna
 * as quatro operações básicas: soma, subtração, multiplicação e divisão.
 * 
 * Exemplo de URL:
 * http://localhost:8080?num1=10&num2=5
 * 
 * @author
 * Lailton Vilarim Tenorio
 * Data: 2024-08-23
 */
public class HttpRequestHandler implements Runnable {
    private final Socket clientSocket;

    /**
     * Construtor do HttpRequestHandler.
     * 
     * @param clientSocket O socket do cliente conectado.
     */
    public HttpRequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Implementação do método run da interface Runnable.
     * Processa a requisição HTTP do cliente e envia a resposta com as operações matemáticas.
     */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Lê a requisição HTTP e extrai os parâmetros da URL
            String requestLine = in.readLine();
            Map<String, String> params = extractParameters(requestLine);

            // Realiza as operações básicas se os parâmetros estiverem corretos
            if (params.containsKey("num1") && params.containsKey("num2")) {
                double num1 = Double.parseDouble(params.get("num1"));
                double num2 = Double.parseDouble(params.get("num2"));

                String result = calculateOperations(num1, num2);

                // Envia a resposta HTTP com o resultado das operações
                sendHttpResponse(out, result);
            } else {
                // Se os parâmetros não estiverem presentes, envia um erro
                sendHttpResponse(out, "Parâmetros 'num1' e 'num2' são obrigatórios na URL.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeClientSocket();
        }
    }

    /**
     * Extrai os parâmetros da linha de requisição HTTP.
     * 
     * @param requestLine A linha da requisição HTTP que contém a URL.
     * @return Um mapa contendo os parâmetros e seus valores.
     */
    private Map<String, String> extractParameters(String requestLine) {
        String[] parts = requestLine.split(" ");
        if (parts.length > 1 && parts[1].contains("?")) {
            String[] urlParts = parts[1].split("\\?");
            if (urlParts.length > 1) {
                return parseQuery(urlParts[1]);
            }
        }
        return Map.of(); // Retorna um mapa vazio se não houver parâmetros
    }

    /**
     * Converte a string da query em um mapa de parâmetros.
     * 
     * @param query A string da query contendo os parâmetros.
     * @return Um mapa contendo os parâmetros e seus valores.
     */
    private Map<String, String> parseQuery(String query) {
        return Map.of(
            query.split("=")[0], query.split("=")[1].split("&")[0],  // num1
            query.split("&")[1].split("=")[0], query.split("&")[1].split("=")[1]  // num2
        );
    }

    /**
     * Realiza as quatro operações básicas entre dois números.
     * 
     * @param num1 O primeiro número.
     * @param num2 O segundo número.
     * @return Uma string contendo os resultados das operações.
     */
    private String calculateOperations(double num1, double num2) {
        StringBuilder result = new StringBuilder();
        result.append("Resultados das Operações: \n");
        result.append("Soma: " + (num1 + num2) + "\n");
        result.append("Subtração: " + (num1 - num2) + "\n");
        result.append("Multiplicação: " + (num1 * num2) + "\n");
        if (num2 != 0) {
            result.append("Divisão: " + (num1 / num2) + "\n");
        } else {
            result.append("Divisão: Não é possível dividir por zero.\n");
        }
        return result.toString();
    }

    /**
     * Envia uma resposta HTTP contendo os resultados das operações.
     * 
     * @param out PrintWriter para enviar a resposta.
     * @param content O conteúdo da resposta.
     */
    private void sendHttpResponse(PrintWriter out, String content) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/plain");
        out.println("Connection: close");
        out.println();
        out.println(content);
    }

    /**
     * Fecha o socket do cliente.
     */
    private void closeClientSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
