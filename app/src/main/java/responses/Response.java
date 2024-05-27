package responses;

public class Response<T> {
    public boolean success;
    public String message;
    public T body;

    public Response(boolean success) {
        this.success = success;
    }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Response(boolean success, String message, T body) {
        this.success = success;
        this.message = message;
        this.body = body;
    }
}
