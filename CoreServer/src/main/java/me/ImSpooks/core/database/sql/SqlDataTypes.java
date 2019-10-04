package me.ImSpooks.core.database.sql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Nick on 04 okt. 2019.
 * Copyright © ImSpooks
 */
@RequiredArgsConstructor
public enum SqlDataTypes {
    CHAR(char.class),
    TEXT(String.class),

    TINYINT(byte.class),
    SMALLINT(short.class),
    INT(int.class),
    BIGINT(long.class),
    DECIMAL(BigDecimal.class),

    DOUBLE_PRECISION(BigDecimal.class),
    REAL(float.class),
    BOOLEAN(boolean.class),

    BINARY(byte[].class),

    DATE(java.sql.Date.class),
    TIME(java.sql.Time.class),
    TIMESTAMP(java.sql.Timestamp.class),
    ;

    @Getter private final Class typeClass;

    public String getSqlType() {
        return this.name().replace("_", " ");
    }

    public static SqlDataTypes[] CACHE = values();

    public static SqlDataTypes getFromClass(Class clazz) {
        for (SqlDataTypes sqlDataTypes : CACHE) {
            if (sqlDataTypes.typeClass == clazz) {
                return sqlDataTypes;
            }
        }
        throw new NullPointerException(String.format("No sql type found with class \'%s\'", clazz.getSimpleName()));
    }
}