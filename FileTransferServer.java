import java.io.*;
import java.net.*;
public class FileTransferServer {
    private static final int PORT = 5000;
    private static final String SAVE_DIR = "received_files";
    public static void main(String[] args) {
        File saveDir = new File(SAVE_DIR);
        if(!saveDir.exists()) saveDir.mkdirs();
        System.out.println("Server starting on port: " + PORT);
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Wating for uploads.Saving into ./" + SAVE_DIR);
            while (true) { 
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Connected " + clientSocket.getRemoteSocketAddress());
                new Thread(()->uploadHandler(clientSocket)).start();
            }
        } catch(IOException e) {
            System.err.println("Failed to start: " + e.getMessage());
        }
    }

    private static void uploadHandler(Socket socket) {
        try(DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {
            String fileName = in.readUTF();
            Long fileSize = in.readLong();
            String safeName = new File(fileName).getName();
            File file = new File(SAVE_DIR,safeName);
            System.out.println("[Server] Receiving \"" + safeName + "\" (" + fileSize + " bytes)");
            try(DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
                byte[] buffer = new byte[4096];
                long remaining = fileSize;
                while(remaining > 0) {
                    int toRead = (int)Math.min(buffer.length,remaining);
                    int bytesRead = in.read(buffer,0,toRead);
                    if(bytesRead == -1) {
                        throw new EOFException("Connection closed before file is transfered");
                    }
                    out.write(buffer,0,bytesRead);
                    remaining -= bytesRead;
                }
            }
            System.out.println("File saved to : " + file.getPath());
        } catch(IOException e) {
            System.err.println("Upload Failed: " +e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {

            }
        }
    }
}
