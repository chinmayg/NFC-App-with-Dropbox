package encyrption;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

// -------------------------------------------------------------------------
/**
 *  A helper class whose object can be used to encrypt and decrypt a file using
 *  xor-cipher
 *
 *  @author Bishwamoy Sinha Roy
 *  @version Nov 11, 2013
 */
public class EncryptionHelper
{
    private int keyLength_;

    // ----------------------------------------------------------
    /**
     * Create a new EncyptionHelper object.
     * @param keyLength : how long the keys can be
     */
    public EncryptionHelper(int keyLength)
    {
        keyLength_ = keyLength;
    }

    // ----------------------------------------------------------
    /**
     * access the keyLength
     * @return : the keyLength
     */
    public int getKeyLength()
    {
        return keyLength_;
    }

    // ----------------------------------------------------------
    /**
     * modify the keyLength
     * @param keyLength : new length
     */
    public void setKeyLength(int keyLength)
    {
        this.keyLength_ = keyLength;
    }

 // ----------------------------------------------------------
    /**
     * generate a random string to be used as a key
     */
    private String genKey()
    {
    	Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < getKeyLength(); i++) {
            char c = (char)(r.nextInt(94) + 33);
            sb.append(c);
        }
        return sb.toString();
        //return RandomStringUtils.randomAlphanumeric(keyLength_);
    	//return "roy";
    }

    // ----------------------------------------------------------
    /**
     * Encrypt a file
     * @param source : location of file to encrypt
     * @param dest : where to the write the encrypted file
     * @return : key used in ecryption
     */
    public String encrypt(String source, String dest)
    {
        String key = genKey();
        if( XORCipher(source, dest, key) )
        {
            return key;
        }
        else
        {
            return "";
        }
    }

    // ----------------------------------------------------------
    /**
     * Decrypt a File using xorcipher
     * @param source : the encrypted file's location
     * @param dest : where to write the decrypted file
     * @param key : key used when encrypting
     * @return : true if successful
     */
    public boolean decrypt(String source, String dest, String key)
    {
        return XORCipher(source, dest, key);
    }

    // ----------------------------------------------------------
    /**
     * Decrypt/Encrypt a File using xorcipher
     * @param source : the source
     * @param dest : the destination
     * @param key : key used for encrypting and decrypting
     * @return : true if successful
     */
    private boolean XORCipher(String source, String dest, String key)
    {
        try
        {
            BufferedReader fileIn =
                new BufferedReader(new FileReader(source));
            PrintWriter fileOut =
                new PrintWriter(new FileWriter(dest));

            char[] keyBuf = key.toCharArray();
            int kl = keyBuf.length;

            String line = "";
            while ((line = fileIn.readLine()) != null)
            {
                char[] lineBuf = line.toCharArray();

                int ml = lineBuf.length;
                char[] newmsg = new char[ml];

                for (int i = 0; i < ml; i++)
                {
                    newmsg[i] = (char)(lineBuf[i] ^ keyBuf[i % kl]);
                }

                String temp = new String(newmsg);
                System.out.println(temp);
                fileOut.println(temp);
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
