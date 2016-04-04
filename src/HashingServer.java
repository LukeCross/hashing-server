import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * A class that starts the server and listens on a thread
 * @author Luke Cross
 */
public class HashingServer {
	
	String hostAddress;
	int portNumber;
	
	/**
	 * Constructor initialising the server
	 * @param hostAddress The address of the server
	 * @param portNumber The port number to run the server on
	 */
	public HashingServer(String hostAddress, int portNumber) {
		this.hostAddress = hostAddress;
		this.portNumber = portNumber;
	}
	
	/**
	 * A method that opens the server socket
	 */
	public void initialise() {
		ServerSocketChannel channel = null;
		try {
			channel = ServerSocketChannel.open();
			Selector selector = Selector.open();
			
			channel.configureBlocking(false);
			channel.bind(new InetSocketAddress(hostAddress, portNumber));
			
			channel.register(selector, SelectionKey.OP_ACCEPT);
			
			System.out.println("Server listening on port: " + portNumber + "\n");
			
			listen(selector);
			
		} catch (IOException e) {
			System.err.println("Unable to connect to: " + hostAddress + " on port: " + portNumber);
			e.printStackTrace();
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				System.err.println("Could not close connection");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * A method that continually listens for new connections
	 * @param selector The server selector
	 * @throws IOException When the selector is unable to accept a connection
	 */
	private void listen(Selector selector) throws IOException {
		while (true) {
			selector.select();
			Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
			
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();
				
				if (key.isAcceptable()) {
					ServerSocketChannel server = (ServerSocketChannel) key.channel();
					SocketChannel client = server.accept(); //Accept the connection when a client connects
					client.configureBlocking(true);
					
					System.out.println("Server connected to client on port: " + portNumber);
					
					HashingServerThread hashingThread = new HashingServerThread(client);
					hashingThread.start();
				}
				
				keys.remove();
			}
		}
	}
	
	public static final String HOST_ADDRESS = "127.0.0.1";
	public static final int PORT_NUMBER = 1024;
	
	public static void main(String[] args) {
		HashingServer server = new HashingServer(HOST_ADDRESS, PORT_NUMBER);
		server.initialise();
		
	}

}
