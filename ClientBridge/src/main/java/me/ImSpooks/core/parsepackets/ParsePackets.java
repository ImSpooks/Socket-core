package me.ImSpooks.core.parsepackets;

import me.ImSpooks.core.packets.collection.GlobalPacket;
import org.tinylog.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by Nick on 12 Oct 2019.
 * Copyright © ImSpooks
 */
public class ParsePackets {

    public static void main(String[] args) {
        new ParsePackets();
    }

    public ParsePackets() {
        try {
            String where = "me.ImSpooks.core.packets.";
            List<Class> classes = this.getClasses(where + "collection");
            //classes.removeAll(this.getClasses(where + "collection.network"));

            String from = (System.getProperty("user.dir") + "/Packets/src/main/java/").replace(File.separator, "/");
            String to = (System.getProperty("user.dir") + "/ClientBridge/src/main/php/client").replace(File.separator, "/");

            Map<String, String> parsed = new HashMap<>();
            for (Class aClass : classes) {
                if (aClass.getAnnotation(GlobalPacket.class) == null)
                    continue;

                String output = new Parser(aClass, from + aClass.getName().replace(".", "/") + ".java", "client.packets." + aClass.getPackage().getName().replace(where, "")).getOutput();
                //Logger.debug("aClass.getPackage().getName() = " + aClass.getPackage().getName());
                //Logger.debug("output = " + output);
                parsed.put(to + (".packets." + aClass.getName().replace(where, "")).replace(".", "/") + ".php", output);
            }

            parsed.forEach((key, value) -> {
                String[] split = key.split("/");

                String fileName = split[split.length - 1];
                String dir = key.replace("/" + fileName, "").replace("/", File.separator);

                File parent = new File(dir);
                if (!parent.exists()) {
                    Logger.info("Creating new folder... " + dir);
                    if (parent.mkdirs()) {
                        Logger.info("Created new folder.");
                    }
                }

                File file = new File(dir, fileName);

                boolean existed = true;
                if (!file.exists()) {
                    try {
                        Logger.info("Creating new file... " + fileName);
                        if (file.createNewFile()) {
                            existed = false;
                            Logger.info("Created new file.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (existed) {
                    StringBuilder output = new StringBuilder();
                    try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                        int ch = bufferedInputStream.read();
                        while (ch != -1) {
                            output.append((char) ch);
                            ch = bufferedInputStream.read();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StringBuilder toReplace = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        toReplace.append(output.toString().split("\n")[i]).append("\n");
                    }
                    output = new StringBuilder(output.toString().replace(toReplace, "").replace("\r", ""));
                    toReplace = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        toReplace.append(value.split("\n")[i]).append("\n");
                    }
                    String tmpValue = value.replace(toReplace, "").replace("\r", "");

                    if (output.toString().equalsIgnoreCase(tmpValue)) {
                        return;
                    }
                }

                try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                    bufferedOutputStream.write(value.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Logger.info(String.format("Converted %s packets to php code.", parsed.size()));
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */

    private List<Class> getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */

    private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
