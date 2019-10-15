package me.ImSpooks.core.packets.init.channels;

import lombok.Getter;
import me.ImSpooks.core.helpers.Global;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by Nick on 30 Sep 2019.
 * Copyright © ImSpooks
 */
public class WrappedOutputStream {

    @Getter private final List<Object> out;

    public WrappedOutputStream() {
        this.out = new ArrayList<>();
    }

    public void write(byte[] data) throws IOException {
        this.out.add(data);
    }

    public void write(int byt) throws IOException {
        this.out.add(byt);
    }

    public void writeBoolean(boolean b) throws IOException {
        this.out.add(b);
    }

    public void writeDouble(double d) throws IOException {
        this.out.add(d);
    }

    public void writeFloat(float f) throws IOException {
        this.out.add(f);
    }

    public void writeInt(int i) throws IOException {
        this.out.add(i);
    }

    public void writeLong(long l) throws IOException {
        this.out.add(l);
    }

    public void writeShort(short s) throws IOException {
        this.out.add(s);
    }

    public void writeString(String s) throws IOException {
        this.out.add(s);
    }

    public void writeFile(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        Files.write(file.toPath(), fileBytes);
        this.write(fileBytes);
    }

    public void writeTypePrefixed(Object o) throws IOException {
        if (o == null) {
            this.write(-1);
        }
        else if (o instanceof String) {
            this.write(0);
            this.writeString((String) o);
        }
        else if (o instanceof Integer) {
            this.write(1);
            this.writeInt((Integer) o);
        }
        else if (o instanceof Long) {
            this.write(2);
            this.writeLong((Long) o);
        }
        else if (o instanceof Double) {
            this.write(3);
            this.writeDouble((Double) o);
        }
        else if (o instanceof Float) {
            this.write(4);
            this.writeFloat((Float) o);
        }
        else if (o instanceof Boolean) {
            this.write(5);
            this.writeBoolean((Boolean) o);
        }
        else if (o instanceof byte[]) {
            this.write(6);
            this.writeInt(((byte[]) o).length);
            this.out.add(o);
        }
        else if (o instanceof List) {
            List<?> list = (List<?>) o;
            this.write(7);
            this.writeInt(list.size());
            for (Object inList : list) {
                this.writeTypePrefixed(inList);
            }
        }
        else if (o instanceof Map) {
            HashMap<?, ?> map = (HashMap<?, ?>) o;
            if (o instanceof LinkedHashMap)
                this.write(8);
            else this.write(9);
            this.writeString(Global.GSON.toJson(map));
        }
        else {
            throw new UnsupportedOperationException(String.format("Cannot write data with class \'%s\'", o.getClass().getSimpleName()));
        }
    }
}
