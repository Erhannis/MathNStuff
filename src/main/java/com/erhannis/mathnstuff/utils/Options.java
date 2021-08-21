/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author erhannis
 */
public class Options implements Serializable {
    private LinkedHashMap<String, Object> data = new LinkedHashMap<>();
    
    public synchronized Object set(String key, Object value) {
        return data.put(key, value);
    }

    public synchronized Object remove(String key) {
        return data.remove(key);
    }
    
    /**
     * 
     * @param key
     * @param def
     * @param insert whether to insert the default if nonexistent
     * @return 
     */
    public synchronized Object getOrDefault(String key, Object def, boolean insert) {
        if (data.containsKey(key)) {
            Object value = data.remove(key);
            data.put(key, value);
            return value;
        } else {
            if (insert) {
                data.put(key, def);
            }
            return def;
        }
    }
    
    /**
     * {@link #getOrDefault(java.lang.String, java.lang.Object, boolean)}, inserting the default
     * @param key
     * @param def
     * @return 
     */
    public synchronized Object getOrDefault(String key, Object def) {
        return getOrDefault(key, def, true);
    }
    
    public synchronized Object get(String key) {
        if (data.containsKey(key)) {
            Object value = data.remove(key);
            data.put(key, value);
            return value;
        } else {
            return null;
        }
    }
    
    public synchronized Object peek(String key) {
        return data.get(key);
    }
    
    //TODO Update Recent?
    public synchronized boolean has(String key) {
        return data.containsKey(key);
    }

    public synchronized ArrayList<Entry<String,Object>> getRecentEntries() {
        ArrayList<Entry<String,Object>> result = new ArrayList<Entry<String,Object>>(data.entrySet());
        Collections.reverse(result);
        return result;
    }
    
    public static Options demandOptions(String filename) {
        //TODO Probably should store as json or something
        File optionsFile = new File(filename);
        if (!optionsFile.exists()) {
            return new Options();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(optionsFile))) {
            return (Options)ois.readObject();
        } catch (Throwable ex) {
            Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
            try {
                // Back up options
                Files.copy(optionsFile.toPath(), new File(filename+"_bak_"+System.currentTimeMillis()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex1) {
                //TODO This could be bad, as it might lead to your options getting erased.  Perhaps die instead?
                System.err.println("WARNING: Failed to load, OR back up, existing options.  Options may be overwritten the next time an option is changed.");
                Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return new Options();
        }
    }
    
    public static void saveOptions(Options options, String filename) throws IOException {
        //TODO Probably should store as json or something
        File optionsFile = new File(filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(optionsFile))) {
            oos.writeObject(options);
        }
    }
}
