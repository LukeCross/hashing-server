import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A class representing the thread that the server runs on when a connection is established
 * @author Luke Cross
 */
public class HashingServerThread extends Thread {
	
	private SocketChannel channel;
	
	/**
	 * Constructor initialising the channel
	 * @param channel The connection
	 */
	public HashingServerThread(SocketChannel channel) {
		this.channel = channel;
	}
	
	/**
	 * A method that calls the methods to receive the file and send the hash
	 */
	public void run() {
		try {
			byte[] hash = hashData();
			System.out.println("Server sending hash digest:\n" + HashingUtils.outputHex(hash));
			sendHash(hash);
			
		} catch (NoSuchAlgorithmException e) {
			System.err.println("There is no such algorithm: " + HashingUtils.HASHING_ALGORITHM);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * A method that receives the client's file and creates a hash of it
	 * @return The hash of the received file
	 * @throws NoSuchAlgorithmException When the hashing algorithm does not exist
	 * @throws IOException When the channel is unable to read the file
	 */
	private byte[] hashData() throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance(HashingUtils.HASHING_ALGORITHM);
		
		System.out.println("Server waiting for client to send data...\n");
		
		ByteBuffer fileBuffer = ByteBuffer.allocate(HashingUtils.BUFFER_SIZE);
		
		while (channel.read(fileBuffer) != -1) {
			fileBuffer.flip();
			digest.update(fileBuffer);
			fileBuffer.clear();
		}
		
		return digest.digest();
	}
	
	/**
	 * A method that sends the hash of the file down the channel
	 * @param hash The hash of the client's file
	 * @throws IOException When the channel is unable to write the hash
	 */
	private void sendHash(byte[] hash) throws IOException {
		ByteBuffer hashBuffer = ByteBuffer.allocate(HashingUtils.BUFFER_SIZE);
		ByteArrayInputStream hashOut = new ByteArrayInputStream(hash);
		ReadableByteChannel hashChannel = Channels.newChannel(hashOut);
		
		while (hashChannel.read(hashBuffer) != -1) {
			hashBuffer.flip();
			channel.write(hashBuffer);
			hashBuffer.clear();
		}
		
		channel.shutdownOutput();
		
		System.out.println("\nServer has sent the hash");
	}
	
}
