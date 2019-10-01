package me.ImSpooks.core.packets.init;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 30 Sep 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class WrappedInputStream {

	@Getter private List<Object> in;
	private int index = 0;

	public WrappedInputStream(ArrayList in) {
		this.in = in;
	}

	public <T> T read(Class<T> clazz, boolean increment) throws IOException {
		T value = clazz.cast(this.in.get(index));
		if (increment)
			index++;
		return value;
	}

	public <T> T read(Class<T> clazz) throws IOException {
		return this.read(clazz, true);
	}

	public boolean readBoolean() throws IOException {
		return this.read(boolean.class);
	}

	public double readDouble() throws IOException {
		return this.read(double.class);
	}

	public float readFloat() throws IOException {
		return this.read(float.class);
	}

	public int readInt() throws IOException {
		int val;
		try {
			val = this.read(Integer.class, false);
		} catch (ClassCastException e) {
			try {
				val = (int) Math.round(this.read(Double.class, false));
			} catch (ClassCastException ex) {
				val = Math.round(this.read(Float.class, false));
			}
		}
		this.index++;
		return val;
	}

	public long readLong() throws IOException {
		long val;
		try {
			val = this.read(Long.class, false);
		} catch (ClassCastException e) {
			try {
				val = Math.round(this.read(Double.class, false));
			} catch (ClassCastException ex) {
				val = (long) Math.round(this.read(Float.class, false));
			}
		}
		this.index++;
		return val;
	}

	public short readShort() throws IOException {
		short val;
		try {
			val = this.read(Short.class, false);
		} catch (ClassCastException e) {
			try {
				val = (short) Math.round(this.read(Double.class, false));
			} catch (ClassCastException ex) {
				val = (short) Math.round(this.read(Float.class, false));
			}
		}
		this.index++;
		return val;
	}

	public byte[] readBytes() throws IOException {
		return this.read(byte[].class);
	}


	public String readString() throws IOException {
		return this.read(String.class);
	}
	public File readFile() throws IOException {
		return new File("");
	}

	public Object readTypePrefixed() throws IOException {
		int id = this.readInt();

		switch (id) {
			case -1:
				return null;
			case 0:
				return this.readString();
			case 1:
				return this.readInt();
			case 2:
				return this.readLong();
			case 3:
				return this.readDouble();
			case 4:
				return this.readBoolean();
			case 5:
				return this.in.get(index++);
			case 6:
				int size = this.readInt();
				List<Object> list = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					list.add(this.readTypePrefixed());
				}
				return list;
			default:
				throw new UnsupportedOperationException(String.format("Cannot read data with given id \'%s\'", id));
		}
	}
}
