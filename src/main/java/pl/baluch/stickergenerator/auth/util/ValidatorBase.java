package pl.baluch.stickergenerator.auth.util;

import com.google.protobuf.AbstractMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValidatorBase<T extends AbstractMessage> {
    protected final Map<Predicate<T>, Function<T, String>> rules = new HashMap<>();
    public ValidationResult validate(T request){
        return new ValidationResult(
                rules.entrySet().stream()
                        .filter(rule -> !rule.getKey().test(request))
                        .map(rule -> rule.getValue().apply(request))
                        .toList()
        );
    }
}
