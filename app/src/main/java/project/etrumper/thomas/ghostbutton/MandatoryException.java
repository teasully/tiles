package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 3/7/2016.
 */
public class MandatoryException extends Exception{

    MandatoryException(){
        super();
    }

    MandatoryException(String message){
        super(message);
    }

    MandatoryException(Throwable cause, String message){
        super(message, cause);
    }

    MandatoryException(Throwable cause){
        super(cause);
    }

}
