package me.ImSpooks.core.packets.init.channels;

import lombok.Getter;
import me.ImSpooks.core.helpers.Global;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Nick on 30 Sep 2019.
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

	public void skip() {
		this.skip(1);
	}

	public void skip(int amount) {
		this.index += amount;
	}

	public <T> T read(Class<T> clazz) throws IOException {
		return this.read(clazz, true);
	}

	public boolean readBoolean() throws IOException {
		return this.read(Boolean.class);
	}

	public double readDouble() throws IOException {
		return this.read(Double.class);
	}

	public float readFloat() throws IOException {
		return (float) this.read(Double.class).doubleValue();
	}

	public int readInt() throws IOException {
		return Math.toIntExact(this.readLong());
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
		return (short) this.readInt();
	}

	@SuppressWarnings("unchecked")
	public byte[] readBytes() throws IOException {
		ArrayList<Double> byteList = this.read(ArrayList.class);

		byte[] byteArray = new byte[byteList.size()];
		for (int index = 0; index < byteList.size(); index++) {
			byte val;
			try {
				val = Byte.class.cast(byteList.get(index));
			} catch (ClassCastException e) {
				try {
					val = (byte) Long.class.cast(byteList.get(index)).longValue();
				} catch (ClassCastException ex) {
					val = (byte) Math.round(byteList.get(index));
				}
			}

			byteArray[index] = val;
		}

		return byteArray;
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
				return this.readFloat();
			case 5:
				return this.readBoolean();
			case 6:
				return this.in.get(index++);
			case 7: {
				int size = this.readInt();
				List<Object> list = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					list.add(this.readTypePrefixed());
				}
				return list;
			}
			case 8:
				return Global.GSON.fromJson(this.readString(), LinkedHashMap.class);
			case 9:
				return Global.GSON.fromJson(this.readString(), HashMap.class);
			default:
				throw new UnsupportedOperationException(String.format("Cannot read data with given id \'%s\'", id));
		}
	}
}
