package me.ImSpooks.core.database.data;

/**
 * Created by Nick on 03 okt. 2019.
 * Copyright © ImSpooks
 */
public class DataValue {

	private Object value;

	public DataValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "PlayerDataValue [value=" + value + "]";
	}
}
