package me.ImSpooks.core.server.database.sql;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Nick on 04 okt. 2019.
 * Copyright © ImSpooks
 */

public enum SqlDataTypes {
    CHAR(char.class, Character.class),
    TEXT(String.class),
    JSON(),

    TINYINT(byte.class, Byte.class),
    SMALLINT(short.class, Short.class),
    INT(int.class, Integer.class),
    BIGINT(long.class, Long.class),
    DECIMAL(BigDecimal.class),

    DOUBLE_PRECISION(BigDecimal.class),
    DOUBLE(double.class, Double.class),
    FLOAT(float.class, Float.class),
    BOOLEAN(boolean.class, Boolean.class),

    BINARY(byte[].class, Byte[].class),

    DATE(java.sql.Date.class),
    TIME(java.sql.Time.class),
    TIMESTAMP(java.sql.Timestamp.class),
    ;

    @Getter private final Class[] typeClass;

    SqlDataTypes(Class... typeClass) {
        this.typeClass = typeClass;
    }

    public String getSqlType() {
        return this.name().replace("_", " ").toUpperCase();
    }

    public static SqlDataTypes[] CACHE = values();

    public static SqlDataTypes getFromClass(Class clazz) {
        for (SqlDataTypes sqlDataTypes : CACHE) {
            for (Class typeClass : sqlDataTypes.typeClass) {
                if (typeClass == clazz) {
                    return sqlDataTypes;
                }
            }
        }
        throw new NullPointerException(String.format("No sql type found with class \'%s\'", clazz.getName()));
    }
}