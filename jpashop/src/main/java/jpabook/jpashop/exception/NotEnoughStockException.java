package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {

    /**
     * RuntimeException의 메서드를 오버라이드함
     */
    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }

}
