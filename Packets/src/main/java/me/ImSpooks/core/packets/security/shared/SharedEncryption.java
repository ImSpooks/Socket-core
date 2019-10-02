package me.ImSpooks.core.packets.security.shared;

import me.ImSpooks.core.packets.security.SecurityEncryption;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class SharedEncryption {
	private static SecurityEncryption encryption;

	public static void setEncryption(SecurityEncryption e) {
		if (e == null)
			throw new IllegalArgumentException("Encryption may not be null");
		encryption = e;
	}

	public static SecurityEncryption getEncryption() {
		return encryption;
	}
}
