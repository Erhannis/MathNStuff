/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import com.erhannis.mathnstuff.MeUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author erhannis
 */
public class Options implements Serializable {
    /**
     * A hook back to a given option value; slightly easier to use than passing around a whole Options everywhere,
     * but still permits code to react to changes in options.<br/>
     * <br/>
     * WARNING: if you get one by calling {@link #getOrDefaultLive(java.lang.String, java.lang.Object, boolean) } ,
     * with a default you DON'T insert into the Options, {@link #hasChanged() } will immediately report `true`, and
     * {@link #fetch() } will overwrite your default.<br/>
     * <br/>
     * Retains a reference to the Options.<br/>
     * Not entirely thread safe - probably won't crash, but may not give entirely consistent results between threads.<br/>
     * <br/>
     * //TODO Make Alting etc?
     * @param <T> 
     */
    public static class LiveOption<T> {
        final Options options;
        final String key;
        T cached;
        
        private LiveOption(Options options, String key, T cached) {
            this.options = options;
            this.key = key;
            this.cached = cached;
        }
        
        public static <K> LiveOption<K> dummy(K value) {
            return new LiveOption<K>(null, null, value) {
                @Override
                public K fetch() {
                    return this.cached;
                }

                @Override
                public boolean hasChanged() {
                    return false;
                }

                @Override
                public LiveOption<K> copy() {
                    return this;
                }
            };
        }
        
        /**
         * Returns the cached value of the option.
         * @return cached value
         */
        public T val() {
            return this.cached;
        }
        
        /**
         * Refreshed the cached value of the option, from the Options, and returns the value.
         * @return 
         */
        public T fetch() {
            this.cached = (T)this.options.get(key);
            return this.cached;
        }
        
        /**
         * Reports whether the value of the option is different than cached, according to Objects.equals(X,Y).<br/>
         * Note that this still pulls the value, so unless you have processing
         * you need to do iff change, you might as well use {@link #fetch() }.
         * @return 
         */
        public boolean hasChanged() {
            T current = (T)this.options.get(key);
            return !Objects.equals(this.cached, current);
        }
        
        /**
         * Makes a shallow copy of the LiveOption, so different threads can
         * update their own cached version without treading on each other's toes.
         * @return a shallow copy of the LiveOption.
         */
        public LiveOption<T> copy() {
            return new LiveOption<T>(options, key, cached);
        }
    }
    
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
     * Returns a {@link LiveOption}, a 
     * 
     * @param key
     * @param def
     * @param insert whether to insert the default if nonexistent
     * @return 
     */
    public synchronized <T> LiveOption<T> getOrDefaultLive(String key, T def, boolean insert) {
        if (data.containsKey(key)) {
            T value = (T)data.remove(key);
            data.put(key, value);
            return new LiveOption<T>(this, key, value);
        } else {
            if (insert) {
                data.put(key, def);
            }
            return new LiveOption<T>(this, key, def);
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

    /**
     * {@link #getOrDefaultLive(java.lang.String, java.lang.Object, boolean)}, inserting the default
     * @param key
     * @param def
     * @return 
     */
    public synchronized <T> LiveOption<T> getOrDefaultLive(String key, T def) {
        return this.<T>getOrDefaultLive(key, def, true);
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
                MeUtils.copyFile(optionsFile, new File(filename+"_bak_"+System.currentTimeMillis()));
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
