package pl.baluch.stickergenerator.auth.util;

import java.util.List;

public record ValidationResult(List<String> errors) {
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String collectErrors() {
        return String.join(", ", errors);
    }
}
