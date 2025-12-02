package relead.relead_schoolmanagement.exceptions;

public class AppExceptions {

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message); //404
        }
    }

    public static class ResourceConflictException extends RuntimeException {
        public ResourceConflictException(String message) {
            super(message); //409
        }
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message); //400
        }
    }
}
