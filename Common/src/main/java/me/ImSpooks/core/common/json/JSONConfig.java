package me.ImSpooks.core.common.json;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.tinylog.Logger;

import java.io.*;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class JSONConfig {

    @Getter @Setter private static String path = System.getProperty("user.dir");
    @Getter private final File file;
    private JSONObject main;


    public JSONConfig(String dir, String fileName)  {
        this(new File(path + File.separator + dir.replace("/", File.separator)), new File(path + File.separator + dir.replace("/", File.separator), fileName));
    }

    public JSONConfig(String fileName)  {
        this(path, fileName);
    }

    public JSONConfig(File parent, File file) {
        if (!parent.exists()) {
            parent.mkdir();
        }

        this.file = file;

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Failed to create file");
                }
            } catch (IOException e) {
                Logger.error(e);
            }

            this.main = new JSONObject();
            this.save();
        }

        try {
            this.main = (JSONObject) new JSONParser().parse(new FileReader(this.file));
        } catch (IOException | ParseException e) {
            Logger.error(e);
        }
    }

    public void save() {
        try {
            Writer out = new FileWriter(this.file, false);
            try {
                String input = this.main.toJSONString();
                out.write(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(input)));
            } finally {
                out.close();
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void expect(String key, Object defVal) {
        Object val = this.main.get(key);
        if (val == null) {
            this.main.put(key, defVal);
            this.save();
        }
    }

    @SuppressWarnings("unchecked")
    public void set(String key, String val) {
        this.main.put(key, val);
    }

    public void set(String key, boolean val) {
        this.main.put(key, val);
    }

    @SuppressWarnings("unchecked")
    public void set(String key, JSONObject val) {
        this.main.put(key, val);
    }

    @SuppressWarnings("unchecked")
    public void set(String key, int val) {
        this.main.put(key, val);
    }

    @SuppressWarnings("unchecked")
    public void set(String key, JSONArray value) {
        this.main.put(key, value);
    }

    public String getString(String key) {
        Object val = this.main.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Key " + key + " was not found");
        }
        return (String) val;
    }

    public JSONObject getObject(String key) {
        Object val = this.main.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Key " + key + " was not found");
        }
        return (JSONObject) val;
    }

    public int getInt(String key) {
        Object val = this.main.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Key " + key + " was not found");
        }
        if (val instanceof Integer) {
            return (Integer) val;
        }
        return ((Long) val).intValue();
    }

    public Double getDouble(String key) {
        Object val = this.main.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Key " + key + " was not found");
        }
        if (val instanceof Double) {
            return (Double) val;
        }
        return ((Long) val).doubleValue();
    }

    public JSONArray getArray(String key) {
        Object val = this.main.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Key " + key + " was not found");
        }
        return (JSONArray) val;
    }

    public boolean getBoolean(String key) {
        Object val = this.main.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Key " + key + " was not found");
        }
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        return false;
    }
}
