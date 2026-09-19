import java.io.*;
import java.net.*;

public class FileTransferClient {
    private static final int PORT = 5000;
    private static final String HOST = "127.0.0.1";
    public static void main(String[] args) {
        if(args.length!=-1) {
            System.out.println("Java File Transfer Tool");
        }
        File file = new File(args[0]);
        if(!file.exists() || !file.isFile()) {
            System.out.println("File not found " + file.getPath());
            return;
        }
        try(Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PORT));
            System.out.println("Client " + socket.getRemoteSocketAddress() + " connected");
            try (
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file));
            ) {
                out.writeUTF(file.getName());
                //self-framing
                out.writeLong(file.length());
                //exact byte count to follow
                //Now stream the actual bytes in fixed size chunks
                //Never load whole file into memory at once as a -
                //read file could be larger than available ram
                byte[] buffer = new byte[4096];
                int bytesRead;
                int totalSent = 0;
                //fileIn i.e. DataInputStream is reading the input file into the buffer
                while((bytesRead = fileIn.read(buffer)) != -1) {
                    out.write(buffer,0,bytesRead);
                    //first bytesRead files will be sent 
                    totalSent += bytesRead;
                }
                out.flush();
                System.out.println("Upload complete : " + totalSent + " bytes sent");
            }
        } catch(IOException e) {
            System.err.println("Upload Failed : " + e.getMessage());
        }
    }
}