package web.practicafinal.exceptions;

import web.practicafinal.utils.CustomLogger;

/**
 *
 * @author Alex
 */
public class ValidateException extends HttpException {

    /*
    Excepción que indica cuando no se ha validado correctamente una request.
    */

    public ValidateException (String str) {
        super(str);

        this.setHttpErrorCode(400);
        
        CustomLogger.warn(str);
    }

}
