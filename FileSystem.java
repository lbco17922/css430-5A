/*
Acknowledgements:
	Boilerplate provided in Prog5.pdf by Prof. Robert Palmer
	Implemented by Jaimi Chong and Lionel Cheng
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
    public FileSystem(int diskBlocks) {
        superblock = new SuperBlock(diskBlocks);
        directory = new Directory(superblock.totalInodes);
        filetable = new FileTable(directory);

        FileTableEntry dir = open("/", "r");

        int dirSize = fsize(dir);
        if(dirSize > 0) {
            byte[] data = new byte[dirSize];
            read(dir, data);
            directory.bytes2directory(data);
        }
        close(dir);
    }

    void sync() {
        // optional?  not mentioned by Test5.java or Prog5.pdfx
    }

    // formats the contents of Disk.java's data[]
    // int files = max # of files to be created (i.e. # of inodes to be allocated)
    int format( int files ) {
        if(files > 0) {
            this.superblock.format(files); //format disk based on # of files
            this.directory = new Directory(files); //update dir and filetable
            this.filetable = new FileTable(this.directory);
            return 0;
        }
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

        FileTableEntry ftEnt = filetable.falloc(filename, mode);
        if (ftEnt != null && mode.equals("w") && !deallocAllBlocks(ftEnt))
            return null;
        return ftEnt;
    }

    private boolean deallocAllBlocks(FileTableEntry ftEnt) {
        if (ftEnt == null)
            return false;
        
        // deallocate them...somehow
        return true;
    }

    // reads up to Disk's buffer.length bytes from the file indicated by fd,
    //  starting at the position currently indicated by the seek pointer
    int read( FileTableEntry ftEnt, byte[] buffer ) {
        int startingOffset = ftEnt.seekPtr;
        int fileSize = fsize(ftEnt);
        int bufferRemaining = buffer.length;

        while (ftEnt.seekPtr < fileSize && bufferRemaining > 0) {
            // initialize temp entities and read disk into them
            int tempBlockId = ftEnt.inode.findTargetBlock(ftEnt.seekPtr);
            if (tempBlockId == -1)
                return ftEnt.seekPtr;
            byte[] tempBuffer = new byte[Disk.blockSize];
            SysLib.rawread(tempBlockId, tempBuffer);
            
            // find value to increment by (the shortest of the remaining amounts)
            int tempOffset = ftEnt.seekPtr % Disk.blockSize;
            int blockRemaining = Disk.blockSize - tempOffset;
            int fileRemaining = fileSize - ftEnt.seekPtr;
            int shortestOfRemaining = Math.min(Math.min(blockRemaining, fileRemaining), bufferRemaining);
            
            // copy tempBuffer to buffer and prepare the next loop
            System.arraycopy(tempBuffer, tempOffset, buffer, ftEnt.seekPtr, shortestOfRemaining);
            ftEnt.seekPtr += shortestOfRemaining;
            bufferRemaining -= shortestOfRemaining;
        }
        // return total read
        return ftEnt.seekPtr - startingOffset;
    }

    // appends to the end or overwrites the contents of Disk's buffer[] to the
    //  file indicated by fd, starting at the position currently indicated by
    //  the seek pointer
    int write( FileTableEntry ftEnt, byte[] buffer ) {
        int startingOffset = ftEnt.seekPtr;
        int fileSize = fsize(ftEnt);
        int bufferRemaining = buffer.length;

        while (ftEnt.seekPtr < fileSize && bufferRemaining > 0) {
            // initialize temp entities
            int tempBlockId = ftEnt.inode.findTargetBlock(ftEnt.seekPtr);
            if (tempBlockId == -1)
                return ftEnt.seekPtr;
            byte[] tempBuffer = new byte[Disk.blockSize];
            
            // find value to increment by (the shortest of the remaining amounts)
            int tempOffset = ftEnt.seekPtr % Disk.blockSize;
            int blockRemaining = Disk.blockSize - tempOffset;
            int fileRemaining = fileSize - ftEnt.seekPtr;
            int shortestOfRemaining = Math.min(Math.min(blockRemaining, fileRemaining), bufferRemaining);
            
            // copy buffer to tempBuffer, write them into disk, and prepare the next loop
            System.arraycopy(buffer, ftEnt.seekPtr, tempBuffer, tempOffset, shortestOfRemaining);
            SysLib.rawwrite(tempBlockId, tempBuffer);
            ftEnt.seekPtr += shortestOfRemaining;
            bufferRemaining -= shortestOfRemaining;
        }
        // return total read
        return ftEnt.seekPtr - startingOffset;
    }

    // updates the seek pointer corresponding to fd
    int seek( FileTableEntry ftEnt, int offset, int whence ) {
        if(whence == SEEK_SET) { //set to offset # of bytes from beginning of file
            ftEnt.seekPtr = offset;
        } else if (whence == SEEK_CUR) { //set to offset # of bytes from curr position
            ftEnt.seekPtr += offset;
        } else if (whence == SEEK_END) { //set to offset # bytes from end file
            ftEnt.seekPtr = fsize(ftEnt) + offset;
        }

        if(ftEnt.seekPtr > fsize(ftEnt)) { //if exceeds file size, cap it
            ftEnt.seekPtr = fsize(ftEnt);
        }
        if(ftEnt.seekPtr < 0) { //if negative seek ptr, set to zero
            ftEnt.seekPtr = 0;
        }
        return ftEnt.seekPtr;
    }

    // closes the file corresponding to fd, commits all of its file
    //  transactions and unregisters fd from the user file descriptor table of
    //  the calling thread's TCB
    int close( FileTableEntry ftEnt ) {
        ftEnt.count--; //decrement thread count
        if(ftEnt.count <= 0) {
            boolean successfulFree = this.filetable.ffree(ftEnt); //free from disk
            if(!successfulFree) {
                return -1;
            }
        }
        ftEnt.inode.toDisk(ftEnt.iNumber);
        return 0;
    }
    
    // destroys the file specified by String filename.
    int delete( String filename ) {
        short iNumberToDelete = this.directory.namei(filename);
        if(iNumberToDelete < 0) { //filename DNE
            return -1; 
        } else {
            Inode inode = this.filetable.retrieveInodeRef(iNumberToDelete); //Get respective inode for filename
            inode.flag = 4; //Flag for pending delete
            if(inode.count == 0) {
                this.directory.ifree(iNumberToDelete); //if no threads share this entry, deallocate
            }
            return 0;
        }
    }

    // returns the size in bytes of the file indicated by fd
    int fsize( FileTableEntry ftEnt ) {
        return ftEnt.inode.length;
    }
}
