package web.practicafinal.exceptions;

/**
 *
 * @author Alex
 */
public class HttpException extends Exception {

    private int httpErrorCode;

    public HttpException (String str) {
        super(str);
    }

    public int getHttpErrorCode() {
        return this.httpErrorCode;
    }
    
    public void setHttpErrorCode(int code) {
        this.httpErrorCode = code;
    }

}