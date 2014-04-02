package gc;

import java.io.*;


/**
 * <p>Title: Dummy output Stream</p>
 * <p>Description: It does nothing.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */

public class GCDummyOutputStream extends OutputStream {
  public GCDummyOutputStream() {}
  public void write(int parm1) throws java.io.IOException {}
  public void write(byte[] b) {}
  public void write(byte[] b,int off,int len) {}
  public void flush() {}
  public void close() {}
}