import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A Utility class consisting of static methods to hold reusable code
 * @author Luke Cross
 */
public class HashingUtils {

	public final static int BUFFER_SIZE = 8 * 1024;
	public final static String HASHING_ALGORITHM = "SHA-1";
	
	/**
	 * A method that takes a path to a file and performs a hash on it using MessageDigest
	 * @param filePath The path of the file you want to hash
	 * @return The hash of the file
	 */
	public static byte[] getCorrectFileHash(String filePath) {
		MessageDigest d = null;
		try {
			d = MessageDigest.getInstance(HASHING_ALGORITHM);
			FileInputStream fileIn = new FileInputStream(filePath);
			
			FileChannel fileChannel = fileIn.getChannel();
			ByteBuffer fileBuffer = ByteBuffer.allocate(HashingUtils.BUFFER_SIZE);
			while (fileChannel.read(fileBuffer) != -1) {
				fileBuffer.flip();
				d.update(fileBuffer);
				fileBuffer.clear();
			}
			
			fileIn.close();
			return d.digest();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * A method that takes a hash in byte form and returns a String in hexadecimal
	 * @param hash The byte array holding the hash to be output
	 * @return The hexadecimal string of the hash
	 */
	public static String outputHex(byte[] hash) {
		StringBuilder stringBuilder = new StringBuilder();
		
	    for (byte b : hash) {
	    	stringBuilder.append(String.format("%02X", b)); // constructs zero padded, two-width Hex string
	    }
	    
		return stringBuilder.toString();
	}
}
