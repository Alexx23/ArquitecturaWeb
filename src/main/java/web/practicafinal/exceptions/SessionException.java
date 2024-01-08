package web.practicafinal.exceptions;

import web.practicafinal.utils.CustomLogger;

/**
 *
 * @author Alex
 */
public class SessionException extends HttpException {

    /*
        Excepción que indica cuando no se ha validado correctamente una request.
    */

    public SessionException (String str) {
        super(str);

        this.setHttpErrorCode(401);
        
        CustomLogger.warn(str);
    }

}
