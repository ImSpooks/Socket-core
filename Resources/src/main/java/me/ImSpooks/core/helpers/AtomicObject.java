package me.ImSpooks.core.helpers;

/**
 * Created by Nick on 01 okt. 2019.
 * Copyright © ImSpooks
 */
public class AtomicObject<T> {

	private T value;

	public AtomicObject(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}
}
