package monk.commerce.task.Exception;

public class CouponAPIException extends RuntimeException{
    private final int stausCode;

    public CouponAPIException(int stausCode, String message) {
        super(message);
        this.stausCode = stausCode;
    }

    public int getStatusCode() {
        return stausCode;
    }
}
