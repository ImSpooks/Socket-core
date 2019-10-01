package me.ImSpooks.core.helpers;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by Nick on 01 okt. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class Compressor {

	private static final ThreadLocal<Deflater> COMPRESSOR = ThreadLocal.withInitial(() -> new Deflater(7));

	private static final ThreadLocal<Inflater> DECOMPRESSOR = ThreadLocal.withInitial(Inflater::new);

	public static byte[] compress(byte[] given) {
		Deflater compressor = COMPRESSOR.get();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		compressor.setInput(given);
		compressor.finish();
		byte[] buffer = new byte[512];
		do {
			int size = compressor.deflate(buffer);
			out.write(buffer, 0, size);
		} while (!compressor.finished());
		compressor.reset();
		return out.toByteArray();
	}

	public static byte[] decompress(byte[] compressed) {
		System.out.println("Decompress 1 1");
		Inflater decompressor = DECOMPRESSOR.get();
		System.out.println("Decompress 1 2");

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			decompressor.setInput(compressed);
			byte[] buffer = new byte[512];
			System.out.println("Decompress 2");
			do {
				System.out.println("Decompress 3 loop");
				int size = decompressor.inflate(buffer);
				out.write(buffer, 0, size);
				System.out.println(decompressor.finished());
			} while (!decompressor.finished());
			decompressor.reset();
			return out.toByteArray();
		} catch (DataFormatException e) {
			throw new RuntimeException(e);
		}
	}
}
