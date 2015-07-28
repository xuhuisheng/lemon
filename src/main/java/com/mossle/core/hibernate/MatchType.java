package com.mossle.core.hibernate;

/**
 * MatchType.
 * 
 * @author Lingo
 */
public enum MatchType {
    /** equals. */
    EQ,
    /** like. */
    LIKE,
    /** less than. */
    LT,
    /** greater than. */
    GT,
    /** less equals. */
    LE,
    /** greater equals. */
    GE,
    /** in. */
    IN,
    /** NOT. */
    NOT,
    /** IS NULL. */
    INL,
    /** NOT NULL. */
    NNL,
    /** unknown. */
    UNKNOWN;
}
