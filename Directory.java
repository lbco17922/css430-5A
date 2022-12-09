/*
Acknowledgements:
	Boilerplate provided in Prog5.pdf by Prof. Robert Palmer
	Implemented by Jaimi Chong
	Last edited on 12/08/22
*/

public class Directory {
	private static int maxChars = 30;	// max characters of each file name
	
	// Directory entries
	private int fsizes[];		// each element stores a different file size.
	private char fnames[][];	// each element stores a different file name.
	
	public Directory( int maxInumber ) {
		fsizes = new int[maxInumber]; 						// maxInumber = max files
		for ( int s = 0; s < maxInumber; s++ ) {
			fsizes[s] = 0;									// all file size initialized to 0
			fnames = new char[maxInumber][maxChars];
			String root = "/";								// entry(inode) 0 is "/"
			fsizes[0] = root.length( );						// fsizes[0] is the size of "/".
			root.getChars( 0, fsizes[0], fnames[0], 0 );	// fnames[0] includes "/"
		}
	}
	
	// assumes data[] received directory information from disk
	// initializes the Directory instance with this data[]
	public int bytes2directory( byte data[] ) {
		// offset accounts for the data's structure, which follows the following pattern:
		// { fsize[0] 			| fsize[1] 			 | ... | fsize[fsize.length-1] |
		//	 fname[0][maxChars] | fsize[1][maxChars] | ... | fname[fname.length-1][maxChars] }
		
		// vars incremented in loop below
		int i;								// offset incrementation based on:
		int intOffset = 0;					// 1 int  = 4 bytes
		int charOffset = fsizes.length;		// 1 char = 2 bytes

		for (i = 0; i < fsizes.length; i++, intOffset += 4, charOffset += 2 * maxChars) {
			fsizes[i] = SysLib.bytes2int(data, intOffset);
			
			String fname = new String(data, charOffset, maxChars);
			fname.getChars(0, fsizes[i], fnames[i], 0);
		}
		
		// checks if fsizes and fnames were fully populated
		if (i == fsizes.length)
			return 0;
		return -1;
	}
	
	// converts and return Directory information into a plain byte array
	//  this byte array will be written back to disk
	// note: only meaningful directory information should be converted
	//  into bytes.
	public byte[] directory2bytes( ) {
		// initialize byte[] to return
		// see bytes2directory() for explanations on data's structure and offset incrememntation
		int totalOffset = 4 + (2 * maxChars);
		byte[] data = new byte[fsizes.length * totalOffset];
		
		int intOffset = 0;
		int charOffset = fsizes.length;

		for (int i = 0; i < fsizes.length; i++, intOffset += 4, charOffset += 2 * maxChars) {
			SysLib.int2bytes(fsizes[i], data, intOffset);

			String fname = new String(fnames[i], 0, fsizes[i]);
			byte[] tempData = fname.getBytes();
			System.arraycopy(tempData, 0, data, charOffset, tempData.length)
		}
		return data;
	}
	
	// filename is the one of a file to be created.
	// allocates a new inode number for this filename (-1 if none)
	public short ialloc( String filename ) {
		// finds the first empty iNumber to populate
		short iNumber = -1;
		for (short s = 0; s < fsizes.length; s++) {
			if (fsizes[s] == 0) {
				iNumber = s;
				break;
			}
		}
		if (iNumber == -1)
			SysLib.cerr("Error: Directory is full. Please delete something.");

		// allocates accordingly
		else {
			if (filename.length() <= maxChars)
				fsizes[iNumber] = filename.length();
			else
				fsizes[iNumber] = maxChars;

			for (short s = 0; s < fsizes[iNumber]; s++)
				fnames[iNumber][s] = filename.charAt(s);
		}

		// returns iNumber if successful, else -1
		return iNumber;
	}
	
	// deallocates this inumber (inode number)
	// the corresponding file will be deleted.
	public boolean ifree( short iNumber ) {
		if (iNumber > fsizes.length || fsizes[iNumber] == 0)
			return false;
		
		fsizes[iNumber] = 0;
		fnames[iNumber] = new char[maxChars];
		return true;
	}
	
	// returns the inumber corresponding to this filename (-1 if none)
	public short namei( String filename ) {
		for (short s = 0; s < fsizes.length; s++) {
			if (fnames[s].toString().equals(filename))
				return s;
		}
		return -1;
	}
}