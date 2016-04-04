import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * A class that starts the client and connects it to the server
 * @author Luke Cross
 */
public class HashingClient {
	
	String hostAddress;
	int portNumber;
	File file;
	
	SocketChannel channel;
	
	/**
	 * Constructor initialising the client
	 * @param hostAddress The address of the server
	 * @param portNumber The port number to run the server on
	 * @param file The file to be send to the server
	 */
	public HashingClient(String hostAddress, int portNumber, File file) {
		this.hostAddress = hostAddress;
		this.portNumber = portNumber;
		this.file = file;
	}
	
	/**
	 * A method that connects the client to the server
	 */
	public void connect() {
		try {
			channel = SocketChannel.open();
			channel.configureBlocking(true);
			channel.connect(new InetSocketAddress(HOST_ADDRESS, PORT_NUMBER));
			
			System.out.println("Client is connecting to: " + HOST_ADDRESS + " on port: " + PORT_NUMBER);
			
			while (!channel.finishConnect()){
	            System.out.println("Client is connecting...");
	        }
			
		} catch (IOException e) {
			System.err.println("Unable to connect to: " + HOST_ADDRESS + " on port: " + PORT_NUMBER);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * A method that reads in the hash send back from the server and returns it as a byte array
	 * @return The hash of the file
	 */
	public byte[] getFileHash() {
		if (channel == null) {
			connect();
		}
		
		try {
			sendFile();
			
			ByteBuffer hashBuffer = ByteBuffer.allocate(HashingUtils.BUFFER_SIZE);
			ByteArrayOutputStream hashOut = new ByteArrayOutputStream();
			
			System.out.println("\nClient is waiting for server to send hash...\n");
			
			while (channel.read(hashBuffer) != -1) {
				hashBuffer.flip();
				int remaining = hashBuffer.remaining();
				byte[] remainingBytes = new byte[remaining];
				hashBuffer.get(remainingBytes, 0, remaining);
				hashOut.write(remainingBytes);
				hashBuffer.clear();
			}
			
			return hashOut.toByteArray();
			
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + file.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Could not read file: " + file.getAbsolutePath());
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * A method that sends the file to the server
	 * @throws IOException When the channel is unable to write the file
	 */
	private void sendFile() throws IOException {
		FileInputStream fileIn = new FileInputStream(file);
		FileChannel fileChannel = fileIn.getChannel();
		
		System.out.println("\nClient sending data...");
		
		ByteBuffer fileBuffer = ByteBuffer.allocate(HashingUtils.BUFFER_SIZE);
		while (fileChannel.read(fileBuffer) != -1) {
			fileBuffer.flip();
			channel.write(fileBuffer);
			fileBuffer.clear();
		}
		
		channel.shutdownOutput();
		fileIn.close();
	}
	
	/**
	 * A method that closes the connection to the server
	 */
	public void disconnect() {
		try {
			channel.close();
		} catch (IOException e) {
			System.err.println("Could not close connection");
			e.printStackTrace();
		}
	}
	
	public static final String HOST_ADDRESS = "127.0.0.1";
	public static final int PORT_NUMBER = 1024;

	public static void main(String[] args) {
		String filePath;
		if (args.length != 0) {
			filePath = args[0]; //Client can specify a file to send to the server
		} else {
			filePath = "src/fileToSend";
		}
		
		File file = new File(filePath);
		
		if (!file.isDirectory() && file.exists()) {
			HashingClient client = new HashingClient(HOST_ADDRESS, PORT_NUMBER, file);
			client.connect();
			byte[] hash = client.getFileHash();
			
			System.out.println("The server returned: " + HashingUtils.outputHex(hash)); //Checking the server sent back the correct hash
			System.out.println("The correct hash is: " + HashingUtils.outputHex(HashingUtils.getCorrectFileHash(filePath)));
			
			client.disconnect();
			
		}
		
	}

}
