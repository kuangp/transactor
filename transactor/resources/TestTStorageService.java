package transactor.resources;

import java.io.*;
import transactor.resources.TStorageService;
import java.net.URI;

public class TestTStorageService implements TStorageService {

    public void store(Object state, URI USL) {
        if (USL.getScheme().equals("file")) {
            try {
                FileOutputStream fileOut = new FileOutputStream(USL.getPath());
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(state);
                out.close();
                fileOut.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Object get(URI USL) {
        Object savedState = null;
        if (USL.getScheme().equals("file")) {
            try {
                FileInputStream fileIn = new FileInputStream(USL.getPath());
                ObjectInputStream ois = new ObjectInputStream(fileIn);
                savedState = ois.readObject();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return savedState;
    }
}
