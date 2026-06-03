package fh.swen.swen2tourplanner.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found: id=" + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
