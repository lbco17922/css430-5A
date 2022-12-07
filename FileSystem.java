/*
Acknowledgements:
	Boilerplate provided in Prog5.pdf by Prof. Robert Palmer
	Implemented by Jaimi Chong and Lionel Cheng (probably both of us; big file)
	Last edited on 12/05/22
*/

public class FileSystem {
    private SuperBlock superblock;
    private Directory directory;
    private FileTable filetable;

    final int SEEK_SET = 0;
    final int SEEK_CUR = 1;
    final int SEEK_END = 2;

    // constructor
    public FileSystem(int diskBlocks) {   // where is it called?
        superblock = new SuperBlock(diskBlocks);
        directory = new Directory(superblock.totalInodes);
        filetable = new FileTable(directory);

        // LIONEL:
        // Finish writing...whatever is happening here lol
        // Reference only; do NOT copy:
        /*

        // read the "/" file from disk
        FileTableEntry dirEnt = open( "/", "r" );
        int dirSize = fsize( dirEnt );
        if (dirSize > 0 ) {
            byte[] dirData = new byte[dirSize];
            read( dirEnt, dirData );
            directory.bytes2directory( dirData );
        }
        close( dirEnt );

        */
    }

    void sync() {
        // optional?  not mentioned by Prog5.pdf
        // JAIMI:
        //SysLib.sync();
    }

    // formats the contents of Disk.java's data[]
    // int files = max # of files to be created (i.e. # of inodes to be allocated)
    int format( int files ) {
        // JAIMI:
        SysLib.format(files);
        //if (successful)
            //return 0;
        return -1;
    }
    
    // opens the file specified by String filename in the given String mode
    //  allocates a new file descriptor "fd" to the file
    FileTableEntry open( String filename, String mode ) {
        /*

        Modes:
            r   = read only
            w   = write only
            w+  = read/write
            a   = append

        */

        // LIONEL:
        // Given String mode, ensure SysLib.open() is called in the inner if statement under the right circumstances
            // JAIMI:
            // (see inner if statement)

        
        // Reference only; do NOT copy:
        /*

        FileTableEntry ftEnt = filetable.falloc( filename, mode );
        if ( mode.equals( "w" ) )   //  release all blocks belonging to this file
        if ( deallocAllBlocks( ftEnt ) == false ) {
                // JAIMI:
                // Write the inner if statement contents; includes SysLib.open(filename), maybe more
                return null;
            }

        return ftEnt; 

        */
    }

    // reads up to Disk's buffer.length bytes from the file indicated by fd,
    //  starting at the position currently indicated by the seek pointer
    int read( FileTableEntry ftEnt, byte[] buffer ) {
        // LIONEL:
        // Obtain StringBuffer s from FileTableEntry ftEnt and byte[] buffer
        
        // JAIMI:
        //SysLib.cin(s);
        //if (success)
            //return 0;
        return -1;
    }

    // appends to the end or overwrites the contents of Disk's buffer[] to the
    //  file indicated by fd, starting at the position currently indicated by
    //  the seek pointer
    int write( FileTableEntry ftEnt, byte[] buffer ) {
        // LIONEL:
        // Obtain StringBuffer s from FileTableEntry ftEnt and byte[] buffer
        
        // JAIMI:
        //SysLib.cout(s);
        //if (success)
            //return 0;
        return -1;
    }

    // updates the seek pointer corresponding to fd
    int seek( FileTableEntry ftEnt, int offset, int whence ) {
        // LIONEL:
        // Obtain int fd from FileTableEntry ftEnt

        // JAIMI:
        //SysLib.seek(fd, offset, whence)
        //if (success)
            //return 0;
        return -1;
    }

    // closes the file corresponding to fd, commits all of its file
    //  transactions and unregisters fd from the user file descriptor table of
    //  the calling thread's TCB
    int close( FileTableEntry ftEnt ) {
        // LIONEL:
        // obtain fd from ftEnt
        
        // JAIMI:
        //SysLib.close(fd);
        //if (success)
            //return 0;
        return -1;
    }
    
    // destroys the file specified by String filename.
    int delete( String filename ) {
        // JAIMI:
        SysLib.delete(filename);
        //if (success)
            //return 0;
        return -1;
    }

    // returns the size in bytes of the file indicated by fd
    int fsize( FileTableEntry ftEnt ) {
        // LIONEL:
        // Obtain int fd from FileTableEntry ftEnt
        
        // JAIMI:
        //SysLib.size(fd);
        //if (success)
            //return 0;
        return -1;
    }
}