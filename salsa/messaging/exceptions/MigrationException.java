
package salsa.messaging.exceptions;

public class MigrationException extends RuntimeException{
    public MigrationException(){
	super();
    }
    public MigrationException(String detail){
	super(detail);
    }
}
