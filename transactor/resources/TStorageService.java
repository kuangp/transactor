package transactor.resources;

import java.net.URI;

public interface TStorageService {

    public void store(Object state, URI USL);

    public Object get(URI USL);
}
