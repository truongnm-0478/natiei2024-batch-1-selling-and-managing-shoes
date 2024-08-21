package group1.intern.repository.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WhereClauseType {
    EQUAL("="),
    NOT_EQUAL("<>"),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    LIKE("LIKE"),
    LIKE_IGNORE_CASE("LIKE"),
    IN("IN"),
    NOT_IN("NOT IN"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL");
    
    private final String value;

    public boolean isLikeType() {
        return this == LIKE || this == LIKE_IGNORE_CASE;
    }

    public boolean isLikeIgnoreCaseType() {
        return this == LIKE_IGNORE_CASE;
    }

    public boolean isNoNeedParamType(){
        return this == IS_NULL || this == IS_NOT_NULL;
    }
}
