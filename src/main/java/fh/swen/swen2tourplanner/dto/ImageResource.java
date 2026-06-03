package fh.swen.swen2tourplanner.dto;

import org.springframework.core.io.Resource;

public record ImageResource(
        Resource resource,
        String contentType,
        String filename
) {
}
