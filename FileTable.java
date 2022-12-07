import java.util.Vector;
/*
Maintains the file table shared among all user threads.
 */
public class FileTable {
    private Vector <FileTableEntry> table;    // the actual entity of this file table
    private Directory dir;                    // the root directory

    private final static int UNUSED = 0;
    private final static int USED = 1;
    private final static int READ = 2;
    private final static int WRITE = 3;
    private final static int DELETE = 4;

    public FileTable(Directory directory) {
        table = new Vector<FileTableEntry>();   // creates the new file table
        dir = directory;                        // dir gets reference to the directory
    }

  public synchronized FileTableEntry falloc(String filename, String mode) {
    short iNumber = -1;
    Inode inode = null;
    while(true) {
        iNumber = (filename.equals("/") ? 0 : dir.namei(filename));
        if(iNumber < 0) {
            if(!mode.equals("r")) {
                iNumber = dir.ialloc(filename);
                inode = new Inode(iNumber);
                inode.flag = READ;
                break;
            } else {
                return null;
            }
        } else { //inode exists
            inode = new Inode(iNumber);
            if(mode.compareTo("r") == 0) { //if the mode is r
                if((inode.flag == USED) || (inode.flag == UNUSED)) {
                    inode.flag = USED;
                    break;
                }

                try {
                    wait();
                } catch(InterruptedException e) {
                    System.out.println("ERROR");
                }
            } else {
                if((inode.flag == WRITE) || (inode.flag == UNUSED)) {
                    inode.flag = READ;
                    break;
                }

                if((inode.flag == READ) || (inode.flag == USED)) {
                    inode.flag = WRITE;
                    break;
                }

                try {
                    wait();
                } catch(InterruptedException e) {
                    System.out.println("ERROR");
                }
            }
        }
    }
    inode.count++;
    inode.toDisk((short) iNumber);
    FileTableEntry entry = new FileTableEntry(inode, iNumber, mode);
    table.addElement(entry);
    return entry;
  }

  public synchronized boolean ffree(FileTableEntry e) {
    boolean succesfullyRemovedFromTable = table.removeElement(e);
    if (succesfullyRemovedFromTable) {
        Inode inode = e.inode;
        if(inode.flag == READ || inode.flag == WRITE) {
            inode.flag = USED;
        } else {
            inode.flag = UNUSED;
        }
        inode.count--;
        inode.toDisk(e.iNumber);
        notify();
    }
    return succesfullyRemovedFromTable;
  }

  public synchronized boolean fempty()
  {
     return table.isEmpty();
  }

  public synchronized Inode retrieveInodeRef(short iNumber) {
    for(int i = 0; i < this.table.size(); i++) {
        FileTableEntry tempFte = table.elementAt(i);
        if(tempFte.iNumber == iNumber) {
            return tempFte.inode;
        }
    }
    return null;
  }
}
