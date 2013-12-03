package encyrption;

//-------------------------------------------------------------------------
/**
*  Any activity that deals with encrypt and decrypting a file should implement
*  this interface. This makes sure that once a file is downloaded by the 
*  DBDownloadTask, it can call the handleSecuirtyMethod and not worry
*  whether to encrypt or decrypt the file, the activity can take care or it.
*
*  @author Bishwamoy Sinha Roy
*  @version Nov 12, 2013
*/
public interface Security {	
	void handleSecurity(String fileLoc, String fileName);	
}
