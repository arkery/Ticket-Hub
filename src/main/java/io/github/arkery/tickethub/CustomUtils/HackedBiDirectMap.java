package io.github.arkery.tickethub.CustomUtils;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Hackish Bi Directional Map
 * @author arkery
 */
@NoArgsConstructor
public class HackedBiDirectMap<A, B> implements Serializable {

    private HashMap<Object, Object> data = new HashMap<>();

    /**
     * Replace a key by searching map with the value
     *
     * @param value     The value used to search
     * @param newKey    The new key that is replacing the existing key
     */
    public void replaceKey(B value, A newKey){
        if(this.data.containsKey(value)){
            this.data.replace(value, newKey);
            this.data.remove(newKey);
            this.data.put(newKey, value);
        }
    }

    /**
     * Replace a key by searching map with the key
     *
     * @param key       The key used to search
     * @param newValue  The new value that is replacing the existing value
     */
    public void replaceValue(A key, B newValue){
        if(this.data.containsKey(key)){
            this.data.replace(key, newValue);
            this.data.remove(newValue);
            this.data.put(newValue, key);
        }
    }

    /**
     * Add an entry to the map
     * It does not add if the object already exists in the map
     *
     * @param key   The key
     * @param value The value
     */
    public void add(A key, B value){
        if(!this.data.containsKey(key) && !this.data.containsKey(value)){
            this.data.put(key, value);
            this.data.put(value, key);
        }
    }


    /**
     * remove an entry in the map using the key
     *
     * @param key key used to remove entry in map
     */
    public void removeKey(A key){
        B b = (B) this.data.get(key);
        if(this.data.containsKey(key) && this.data.containsKey(b)){
            this.data.remove(key);
            this.data.remove(b);
        }
    }


    /**
     * remove an entry in the map using the value
     *
     * @param value value used to remove entry in map
     */
    public void removeValue(B value){
        A a = (A) this.data.get(value);
        if(this.data.containsKey(value) && this.data.containsKey(a)){
            this.data.remove(value);
            this.data.remove(a);
        }
    }

    /**
     * Clear everything in the map
     */
    public void clear(){
        this.data = new HashMap<>();
    }

    /**
     * Gets the key using the value
     *
     * @param value the value used to search for the key
     * @return      the key - if it can't find it - returns the parameter that user inputs
     */
    public Object getKey(B value){
        if(this.data.containsKey(value)){
            return this.data.get(value);
        }else{
            return value;
        }
    }

    /**
     * Gets the value using the key
     *
     * @param key the key used to search for the value
     * @return  the value - if it can't find it - returns the parameter that user inputs
     */
    public Object getValue(A key){
        if(this.data.containsKey(key)){
            return this.data.get(key);
        }else{
            return key;
        }
    }

    /**
     * Checks if the key exists
     *
     * @param key the key that's being searched for.
     * @return    true if found, false if not
     */
    public boolean containsKey(A key){
        if(this.data.containsKey(key)){
            return true;
        }else{
            return false;
        }

    }

    /**
     * Checks if the value exists
     *
     * @param value the value that's being searched for.
     * @return      true if found, false if not
     */
    public boolean containsValue(B value){
        if(this.data.containsKey(value)){
            return true;
        }else{
            return false;
        }

    }

    /**
     * The amount of entries in the map
     *
     * @return The amount of entries (Excluding duplicates)
     */
    public int size(){
        return this.data.size() / 2;
    }

}
