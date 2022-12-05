public class Directory {
	private static int maxChars = 30;	// max characters of each file name
	
	// Directory entries
	private int fsizes[];		// each element stores a different file size.
	private char fnames[][];	// each element stores a different file name.
	
	public Directory( int maxInumber ) {		// directory constructor
		fsizes = new int[maxInumber]; 			// maxInumber = max files
		for ( int i = 0; i < maxInumber; i++ )
			fsizes[i] = 0;					// all file size initialized to 0
			fnames = new char[maxInumber][maxChars];
			String root = "/";				// entry(inode) 0 is "/"
			fsizes[0] = root.length( );		// fsizes[0] is the size of "/".
			root.getChars( 0, fsizes[0], fnames[0], 0 );// fnames[0] includes "/"
		}
	
	public int bytes2directory( byte data[] ) {
		// assumes data[] received directory information from disk
		// initializes the Directory instance with this data[]
		int max;
		if (data.length <= fsizes.length)
			max = data.length;
		else
			max = fsizes.length;
		
		int intOffset = 0;
		int charOffset = 0;
		for (int i = 0; i < max; i++, intOffset += 4, charOffset += 2) {
			fsizes[i] = SysLib.bytes2int(data, intOffset);
			fnames[i] = new char[maxChars];	// placeholder (see below)

			/*
			not sure how we were supposed to figure:
			- that the offset 4(fsizes.length - 1) is where the data for fnames
			  start in data[]
			- how much to increment offset for char, if not simply by 2 to match
			  1 char = 2 bytes
			*/
		}

		return max;

		// Reference (can't copy):
		/*
		int offset = 0;
		for ( int i = 0; i < fsizes.length; i++, offset+=4 )
			fsizes[i] = SysLib.bytes2int( data, offset );
		for ( int i = 0; i < fnames.length; i++, offset += maxChars * 2 ) {
			String fname = new String( data, offset, maxChars * 2 );
			fname.getChars( 0, fsizes[i], fnames[i], 0 );
		}
		*/
	}
	
	public byte[] directory2bytes( ) {
		// converts and return Directory information into a plain byte array
		// this byte array will be written back to disk
		// note: only meaningfull directory information should be converted
		// into bytes.
		byte[] data = new byte[fnames.length];
		//for (int i = 0; i < data.length; i++)
			//data[i] = ;
		return data;

		// must understand roadblocks in bytes2directory() before attempting
	}
	
	public short ialloc( String filename ) {
		// filename is the one of a file to be created.
		// allocates a new inode number for this filename (-1 if none)
		
		// finds the first empty index to populate
		int index = -1;
		for (int i = 0; i < fsizes.length; i++) {
			if (fsizes[i] != 0) {
				index = i;
				break;
			}
		}
		if (index == -1)
			SysLib.cerr("Error: Directory is full. Please delete something.");

		// allocates accordingly
		else {
			if (filename.length() <= maxChars)
				fsizes[index] = filename.length();
			else
				fsizes[index] = maxChars;

			for (int i = 0; i < fsizes[index]; i++)
				fnames[index][i] = filename.charAt(i);
		}

		// returns index if successful, else -1
		return (short)index;
	}
	
	public boolean ifree( short iNumber ) {
		// deallocates this inumber (inode number)
		// the corresponding file will be deleted.
		
		int index = (int)iNumber;
		if (index > fsizes.length || fsizes[index] == 0)
			return false;
		
		fsizes[index] = 0;
		fnames[index] = new char[maxChars];
		return true;
	}
	
	public short namei( String filename ) {
		// returns the inumber corresponding to this filename (-1 if none)
		
		for (int i = 0; i < fsizes.length; i++) {
			if (fnames[i].toString() == filename)
				return (short)i;
		}
		return (short)-1;
	}
}