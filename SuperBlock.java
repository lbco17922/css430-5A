/*
Acknowledgements:
    Global variables provided in Prog5.pdf by Prof. Robert Palmer
    Implemented by Jaimi Chong
    Last edited on 12/06/2022
*/

public class SuperBlock {
    public int totalBlocks;     // the number of disk blocks
    public int totalInodes;     // the number of inodes
    public int freeList;        // the block number of the free list's head (zyBooks "free-frame list")
    //private final int defaultInodeBlocks = 64;  // optional? only appears in constr format() call
    Inode[] inodes;             // probably move this to FileSystem later

    public SuperBlock(int diskBlocks) {
        byte[] blocks = new byte[Disk.blockSize];
        SysLib.rawread( 0, blocks );
        totalBlocks = SysLib.bytes2int(blocks, 0);
        totalInodes = SysLib.bytes2int(blocks, 4);
        freeList    = SysLib.bytes2int(blocks, 8);

        if (totalBlocks == diskBlocks) //&& totalInodes > 0 && freeList >= 2)   ...why these?
            // disk contents are valid
            return;
        else {
            // disk contents must be formatted
            totalBlocks = diskBlocks;
            format();
            //format(defaultInodeBlocks);
        }        
    }

    // write totalBlocks, totalInodes, and freeList to Disk's data[]
    void sync() {
        byte[] blocks = new byte[Disk.blockSize];
        SysLib.int2bytes(totalBlocks, blocks, 0);
        SysLib.int2bytes(totalInodes, blocks, 4);
        SysLib.int2bytes(freeList, blocks, 8);
        SysLib.rawwrite( 0, blocks );
    }
    
    
    // typically called with 16 passed for int numBlocks?
    void format( ) {
        format(0);  //?
    }
    void format( int numBlocks ) {
        //initialize Inodes
        inodes = new Inode[numBlocks];
        for (short iNumber = 0; iNumber < numBlocks; iNumber++) {
            inodes[iNumber] = new Inode(iNumber);
            getFreeBlock(); // free block
        }
    }
    
    public int getFreeBlock( ) {
        // dequeue top block in freelist
        return freeList++;
    }
    
    public boolean returnBlock( int oldBlockNumber ) {
        // enqueue oldBlockNumber to top of freelist
        if (freeList == oldBlockNumber - 1) {
            freeList--;
            return true;
        }
        return false;
    }
}