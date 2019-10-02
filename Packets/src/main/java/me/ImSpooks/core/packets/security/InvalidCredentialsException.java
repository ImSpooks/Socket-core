package me.ImSpooks.core.packets.security;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class InvalidCredentialsException extends Exception {

    private static final long serialVersionUID = 7964290696423373686L;

    public InvalidCredentialsException() {
        super();
    }

    public InvalidCredentialsException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public InvalidCredentialsException(String arg0) {
        super(arg0);
    }

    public InvalidCredentialsException(Throwable arg0) {
        super(arg0);
    }
}
