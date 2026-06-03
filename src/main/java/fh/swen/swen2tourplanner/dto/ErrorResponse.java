package fh.swen.swen2tourplanner.dto;

public record ErrorResponse(
        int status,
        String message
) {
}
