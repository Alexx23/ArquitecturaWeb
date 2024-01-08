package web.practicafinal.exceptions;

import web.practicafinal.utils.CustomLogger;

/**
 *
 * @author Alex
 */
public class UnauthorizedException extends HttpException {

    /*
        Excepción que indica cuando el usuario no tiene permisos.
    */

    public UnauthorizedException () {
        super("Acceso no autorizado");

        this.setHttpErrorCode(403);
        
        CustomLogger.warn("Acceso no autorizado");
    }

}
