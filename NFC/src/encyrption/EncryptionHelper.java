package encyrption;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

// -------------------------------------------------------------------------
/**
 * A helper class whose objects can be used to encrypt and decrypt a file using
 * xor-cipher
 * 
 * @author Bishwamoy Sinha Roy
 * @version Nov 11, 2013
 */
public class EncryptionHelper {
	private int keyLength_;
	static final String alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	// ----------------------------------------------------------
	/**
	 * Create a new EncyptionHelper object.
	 * 
	 * @param keyLength
	 *            : how long the keys can be
	 */
	public EncryptionHelper(int keyLength) {
		keyLength_ = keyLength;
	}

	// ----------------------------------------------------------
	/**
	 * access the keyLength
	 * 
	 * @return : the keyLength
	 */
	public int getKeyLength() {
		return keyLength_;
	}

	// ----------------------------------------------------------
	/**
	 * modify the keyLength
	 * 
	 * @param keyLength
	 *            : new length
	 */
	public void setKeyLength(int keyLength) {
		this.keyLength_ = keyLength;
	}

	// ----------------------------------------------------------
	/**
	 * generate a random string to be used as a key
	 */
	private String genKey() {
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getKeyLength(); i++) {
			char c = alphanumeric.charAt(r.nextInt(alphanumeric.length()));
			sb.append(c);
		}
		return sb.toString();
	}

	// ----------------------------------------------------------
	/**
	 * Encrypt a file
	 * 
	 * @param source
	 *            : location of file to encrypt
	 * @param dest
	 *            : where to the write the encrypted file
	 * @return : key used in ecryption
	 */
	public String encrypt(String source, String dest) {
		String key = genKey();
		if (XORCipher(source, dest, key)) {
			return key;
		} else {
			return "";
		}
	}

	// ----------------------------------------------------------
	/**
	 * Decrypt a File using xorcipher
	 * 
	 * @param source
	 *            : the encrypted file's location
	 * @param dest
	 *            : where to write the decrypted file
	 * @param key
	 *            : key used when encrypting
	 * @return : true if successful
	 */
	public boolean decrypt(String source, String dest, String key) {
		return XORCipher(source, dest, key);
	}

	// ----------------------------------------------------------
	/**
	 * Decrypt/Encrypt a File using xorcipher
	 * 
	 * @param source
	 *            : the source
	 * @param dest
	 *            : the destination
	 * @param key
	 *            : key used for encrypting and decrypting
	 * @return : true if successful
	 */
	private boolean XORCipher(String source, String dest, String key) {
        try
        {
            FileReader fileIn = new FileReader(source);
            PrintWriter fileOut = new PrintWriter(new FileWriter(dest));

            char[] keyBuf = key.toCharArray();
            int kl = keyBuf.length;

            int line = 0;
            int charNum = 0;
            while ((line = fileIn.read()) != -1)
            {
                char lineBuf = (char)line;
                char newmsg = (char)(lineBuf ^ keyBuf[charNum % kl]);

                System.out.print(newmsg);
                fileOut.print(newmsg);
                charNum++;
            }
            fileOut.close();
            fileIn.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
	}
}
