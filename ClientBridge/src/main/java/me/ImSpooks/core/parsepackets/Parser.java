package me.ImSpooks.core.parsepackets;

import lombok.Getter;
import me.ImSpooks.core.helpers.AtomicObject;
import me.ImSpooks.core.helpers.StringHelpers;
import me.ImSpooks.core.packets.init.Packet;
import org.tinylog.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */
public class Parser {

    private static final int PHP_VERSION = 74;

    private final Class<? extends Packet> aClass;
    private int tabs = 0;
    @Getter private final File file;

    @Getter private String output;


    public Parser(Class<? extends Packet> aClass, String pathToClass, int tabs) {
        this.file = new File(pathToClass.replace("/", File.separator));
        if (!file.exists())
            throw new NullPointerException("Given class doesnt has a valid reference file.");
//        else
//            Logger.debug("Reference file for packet " + aClass.getSimpleName() + " has been found.");

        this.aClass = aClass;
        this.tabs = tabs;
        this.output = output();
    }

    // ugly code but dont care
    private String output() {
        StringBuilder builder = new StringBuilder();


        a(builder, "<?php");
        c(builder);
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < tabs; i++)
            prefix.append("../");
        a(builder, String.format("require_once \"%s\";", prefix.toString() + "init/Packet.php"));
        a(builder, String.format("require_once \"%s\";", prefix.toString() + "init/channels/WrappedInputStream.php"));
        a(builder, String.format("require_once \"%s\";", prefix.toString() + "init/channels/WrappedOutputStream.php"));
        c(builder);

        a(builder, "/**");
        a(builder, String.format(" * Created by Nick on %s.", new SimpleDateFormat("dd MMM yyyy").format(new Date())));
        a(builder, " * Copyright © ImSpooks");
        a(builder, " */");
        c(builder);

        a(builder, String.format("class %s extends Packet {", aClass.getSimpleName()));
        c(builder);

        Map<String, String> fields = new LinkedHashMap<>();
        for (Field field : aClass.getDeclaredFields()) {
            field.setAccessible(true);

            String var = javaToPhpType(field.getType());

            String type = "public";
            if (Modifier.isPrivate(field.getModifiers())) type = "private";
            else if (Modifier.isProtected(field.getModifiers())) type = "protected";
            if (Modifier.isStatic(field.getModifiers())) type += " static";

            if (PHP_VERSION >= 74)
                a(builder, String.format("%s %s $%s;", type, var, field.getName()), 1);
            else
                a(builder, String.format("%s%s $%s;", !var.isEmpty() ? String.format("/** @var %s */ \t", var) : "", type, field.getName()), 1);
            fields.put("$" + field.getName(), var);
        }
        c(builder);

//        a(builder, "/**", 1);
//        a(builder, String.format(" * %s constructor.", aClass.getSimpleName()), 1);
//        fields.forEach((key, value) -> {
//            if (!value.isEmpty())
//                a(builder, String.format(" * @param %s %s", value, key), 1);
//        });
//        a(builder, " */", 1);

        AtomicObject<String> parameters = new AtomicObject<>("");
        fields.forEach((key, value) -> parameters.set(parameters.get() + String.format("%s %s = %s, ", value, key, defaultValueFromPhpType(value))));

        a(builder, String.format("public function __construct(%s) {", parameters.get().substring(0, parameters.get().length() - 2)), 1);
        fields.keySet().forEach(key -> this.a(builder, String.format("$this->%s = %s;", key.replace("$", ""), key), 2));
        a(builder, "}", 1);


        StringBuilder getters = new StringBuilder();
        for (Method method : aClass.getDeclaredMethods()) {
            method.setAccessible(true);

            if (method.getName().startsWith("get")) {
                String field = StringHelpers.firstLower(method.getName().replace("get", ""));
                if (!fields.containsKey("$" + field))
                    continue;

                c(getters);
                a(getters, String.format("public function %s(): %s {", method.getName(), javaToPhpType(method.getReturnType())), 1);
                a(getters, String.format("return $this->%s;", field), 2);
                a(getters, "}", 1);
            }
            else {
                c(builder);
                // TODO

                StringBuilder output = new StringBuilder();
                try(BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(this.file))) {
                    int ch = bufferedInputStream.read();
                    while(ch != -1) {
                        output.append((char) ch);
                        ch = bufferedInputStream.read();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String[] words = output.toString().split(" ");

                Map<String, List<String>> methodParams = new LinkedHashMap<>();
                for (Class<?> parameterType : method.getParameterTypes()) {
                    methodParams.put(parameterType.getSimpleName(), new ArrayList<>());
                }


                for (int i = 0; i < words.length; i++) {
                    if (words[i].startsWith(method.getName())) {

                        String type = "public";
                        if (Modifier.isPrivate(method.getModifiers())) type = "private";
                        else if (Modifier.isProtected(method.getModifiers())) type = "protected";
                        if (Modifier.isStatic(method.getModifiers())) type += " static";
                        type += " function";

                        words[i] = type + " " + words[i];

                        Map<Integer, String> methodLines = new LinkedHashMap<>();
                        int index = 0;
                        int closers = 0;
                        boolean firstBracket = false;
                        for (int j = i; j < words.length; j++) {
                            String word = words[j];
                            StringBuilder tabs = new StringBuilder();
                            for (int a = 0; a < closers + 1 + (index > 0 ? 1 : 0); a++) {
                                tabs.append("    ");
                            }

                            if (word.contains("(") || methodParams.containsKey(word)) {
                                String targetWord = word;
                                if (word.contains("("))
                                    targetWord = word.split("\\(")[1];
                                if (methodParams.containsKey(targetWord.replaceAll("[^a-zA-Z0-9]", ""))) {
                                    String parameterName = words[j + 1].replace(",", "").replaceAll("\\)", "");
                                    //Logger.debug("Parameter {} found with key {}", targetWord, parameterName);
                                    methodParams.get(targetWord).add(parameterName);
                                }
                            }

                            if (word.contains("throws")) {
                                word = "";
                                for (int a = j; a < words.length; a++) {
                                    if (words[a].contains("{")) {
                                        words[a] = "{\n";
                                        break;
                                    }
                                    else
                                        words[a] = "";
                                }
                            }

                            if (word.contains("{")) {
                                if (!firstBracket)
                                    firstBracket = true;
                                else {
                                    closers++;
                                    Logger.debug("Opener found in {} on line {} in class {}", method.getName(), index, aClass.getName());
                                }
                            }
                            if (word.contains("}")) {
                                Logger.debug("Closer found in {} on line {} in class {}", method.getName(), index, aClass.getName());
                                closers--;
                            }

                            methodLines.putIfAbsent(index, tabs.toString());
                            if (closers < 0) {
                                methodLines.put(index, "    }");
                                break;
                            }

                            if (!word.isEmpty())
                                methodLines.put(index, (methodLines.get(index) + word + " "));
                            if (word.endsWith("\n")) {
                                methodLines.put(index, methodLines.get(index).substring(0, methodLines.get(index).length() - 2));
                                index++;
                            }
                        }

                        methodLines.values().forEach(string -> {
                            AtomicObject<String> line = new AtomicObject<>(string);
                            line.set(line.get().replace("Global.GSON.toJson", "json_encode"));
                            if (line.get().contains("Global.GSON.fromJson")) {
                                line.set(line.get().replace("Global.GSON.fromJson", "json_decode"));
                                line.set(line.get().replace("," + line.get().split(",")[1], "") + ");");
                            }

                            line.set(line.get().replace("this", "$this"));
                            line.set(line.get().replace(".", "->"));

                            methodParams.values().forEach(list -> list.forEach(value -> {
                                String[] split = line.get().split(" ");
                                for (String s : split) {
                                    int letterIndex = s.indexOf(value);
                                    if (letterIndex == -1)
                                        continue;
                                    if (letterIndex - 1 < 0 || s.charAt(letterIndex - 1) != '>') {
                                        line.set(StringHelpers.addChar(line.get(), '$', line.get().indexOf(value)));
                                    }
                                }


                            }));

                            System.out.println("line.get() = " + line.get());
                            a(builder, line.get());
                        });

                        break;
                    }
                }
            }
        }

        a(builder, getters.toString());

        a(builder, "}", 0);

        return builder.toString();
    }

    private void a(StringBuilder builder, String str, int tabs) {
        for (int i = 0; i < tabs; i++)
            builder.append("    ");
        builder.append(str).append("\n");
    }

    private void a(StringBuilder builder, String str) {
        this.a(builder, str, 0);
    }


    private void b(StringBuilder builder, String str, int tabs) {
        for (int i = 0; i < tabs; i++)
            builder.append("    ");
        builder.append(str);
    }

    private void b(StringBuilder builder, String str) {
        this.b(builder, str, 0);
    }

    private void c(StringBuilder builder) {
        builder.append("\n");
    }

    private String javaToPhpType(Class<?> type) {
        if (type == String.class)                                        return "string";
        else if (type == Integer.class || type == int.class)             return "int";
        else if (type == Float.class || type == float.class)             return "float";
        else if (type == Double.class || type == double.class)           return "double";
        else if (type == Byte.class || type == byte.class)               return "int";
        else if (type == Short.class || type == short.class)             return "int";
        else if (type == Long.class || type == long.class)               return "int";
        else if (type == Boolean.class || type == boolean.class)         return "bool";
        else if (type.isArray() || type.getName().endsWith("List"))      return "array";
        else if (type.getSimpleName().endsWith("Map"))                   return "array";
        else if (type == org.bson.Document.class)                        return "array";
        else                                                             return type.getSimpleName();
    }

    private String defaultValueFromPhpType(String phpType) {
        switch (phpType) {
            case "string":
                return "\"\"";
            case "int":
                return "0";
            case "double":
            case "float":
                return "0.0";
            case "bool":
                return "false";
            case "array":
                return "[]";
            default:
                return "null";
        }
    }
}