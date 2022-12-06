public class FileSystem {
    // required system calls
    /*
    int SysLib.format(int files);
    int fd = SysLib.open(String fileName, String mode);
    int read(int fd, byte buffer[]);
    int write(int fd, byte buffer[]);
    int seek(int fd, int offset, int whence);
    int close(int fd);
    int delete(String fileName);
    int fsize(int fd);
    */

    private SuperBlock superblock;
    private Directory directory;
    private FileTable filetable;

    final int SEEK_SET = 0;
    final int SEEK_CUR = 1;
    final int SEEK_END = 2;

    // constructor
    public FileSystem(int diskBlocks) {   // where is it called?
        superblock = new SuperBlock(diskBlocks);
        directory = new Directory(superblock.inodeBlocks);
        filetable = new FileTable(directory);

        // understanding of Directory stuff depends on familiarity with FileTable
    }

    void sync() {
        // optional?  not mentioned by Prog5.pdf
    }

    // formats the contents of Disk.java's data[]
    // int files = max # of files to be created (i.e. # of inodes to be allocated)
    int format( int files ) {
        //SysLib.format(files);   // need to implement in SysLib, and by extension Kernel

        //if (successful)
            //return 0;
        
        return -1;  // failure
    }
    
    // opens the file specified by String filename in the given String mode
    //  allocates a new file descriptor "fd" to the file
    FileTableEntry open( String filename, String mode ) {
        /*
        r   = read only
        w   = write only
        w+  = read/write
        a   = append
        
        SysLib.open(filename, mode) // need to implement in SysLib, and by extension Kernel
            if (not found in w, w+, or a)
                create file
            if (not found in r)
                SysLib.open returns -1;
            
            if (successfully opened/created) {
                use a file descriptor between the range 3 and 31, since
                0-2 are already reserved for standard input, output, and error
            }

            if (calling thread's user file descriptor table is full) {
                if (mode.equals("a"))
                    seek pointer is initialized to the end of the file
                else // (mode.equals("r") || mode.equals("w") || mode.equals("w+"))
                    seek pointer is initialized to 0

                SysLib.open returns -1;
            }
        */
    }

    // reads up to Disk's buffer.length bytes from the file indicated by fd,
    //  starting at the position currently indicated by the seek pointer
    int read( FileTableEntry ftEnt, byte[] buffer ) {
        /*
        SysLib.read
            if (bytes remaining between curr seek pointer and end of file < buffer.length)
                reads as many bytes as possible
                puts them into the beginning of Disk's? buffer[]
            increments the seek pointer by the number of bytes to have been read
            return = number of bytes that have been read, or -1 if error
        */

        //if (success)
            //return 0;

        return -1;  // error
    }

    // appends to the end or overwrites the contents of Disk's buffer[] to the
    //  file indicated by fd, starting at the position currently indicated by
    //  the seek pointer
    int write( FileTableEntry ftEnt, byte[] buffer ) {
        /*
        SysLib.write
            increments the seek pointer by the number of bytes to be written
            returns = number of bytes to have been written, or -1 upon error
        */

        //if (success)
            //return 0;

        return -1;  // error
    }

    // updates the seek pointer corresponding to fd
    int seek( FileTableEntry ftEnt, int offset, int whence ) {
        /*
        if (whence == SEEK_SET)
            set the file's seek pointer to offset bytes from the beginning of the file
        if (whence == SEEK_CUR)
            set the file's seek pointer to += the offset (may be negative)
        if (whence == SEEK_END)
            set the file's seek pointer to the file size + the offset.
             negative values or values beyond the file size set by the user
             should be clamped by to 0 or the end of the file, respectively,
             and still return a sucess
        */

        //if (success)
            //return 0;

        return -1;  // error
    }

    // closes the file corresponding to fd, commits all of its file
    //  transactions and unregisters fd from the user file descriptor table of
    //  the calling thread's TCB
    int close( FileTableEntry ftEnt ) {
        //if (success)
            //return 0;

        return -1;  // error
    }
    
    // destroys the file specified by String filename.
    int delete( String filename ) {
        /*
        if (currently open)
            wait until the last open of it is closed before deletion
            prevents additional openings while waiting
        */
        
        //if (success)
            //return 0;

        return -1;  // error
    }

    // returns the size in bytes of the file indicated by fd
    int fsize( FileTableEntry ftEnt ) {
        //if (success)
            //return 0;

        return -1;  // error
    }

    // References
    // note that format(), close(), fsize(), and delete() all return
    //  boolean here, but are described as returning int in Prog5.pdf.
    /*
    private SuperBlock superblock;
    private Directory directory;
    private FileTable filetable;

    public FileSystem( int diskBlocks ) {
        superblock = new SuperBlock( diskBlocks );
        directory = new Directory( superblock.inodeBlocks );
        filetable = new FileTable( directory );
        
        // read the "/" file from disk
        FileTableEntry dirEnt = open( "/", "r" );
        int dirSize = fsize( dirEnt );
        if (dirSize > 0 ) {
            byte[] dirData = new byte[dirSize];
            read( dirEnt, dirData );
            directory.bytes2directory( dirData );
        }
        close( dirEnt );
    }

    void sync() {}
    boolean format( int files ) {}

    FileTableEntry open( String filename, String mode ) {
        FileTableEntry ftEnt = filetable.falloc( filename, mode );
        if ( mode.equals( "w" ) )   //  release all blocks belonging to this file
            if ( deallocAllBlocks( ftEnt ) == false )
                // need to implement
                return null;
        return ftEnt;
    }
    
    boolean close( FileTableEntry ftEnt ) {}
    int fsize( FileTableEntry ftEnt ) {}
    int read( FileTableEntry ftEnt, byte[] buffer ) {}
    int write( FileTableEntry ftEnt, byte[] buffer ) {}
    boolean delete( String filename ) {}
    int seek( FileTableEntr ftEnt, int offset, int whence ) {}
    */
}
