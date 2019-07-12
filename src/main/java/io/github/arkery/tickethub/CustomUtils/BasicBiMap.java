package io.github.arkery.tickethub.CustomUtils;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Hackish Bi Directional Map
 * This does not directly support .ToString or .Equals
 *
 * @author arkery
 */
@NoArgsConstructor
public class BasicBiMap<A, B> implements Serializable {

    private HashMap<A, B> KeyToValue = new HashMap<>();
    private HashMap<B, A> ValueToKey = new HashMap<>();

    /**
     * Replace a key by searching map with the value
     *
     * @param value     The value used to search
     * @param newKey    The new key that is replacing the existing key
     */
    public void replaceKey(B value, A newKey){
       if(this.ValueToKey.containsKey(value)){
           this.KeyToValue.replace(this.ValueToKey.get(value), value);
           this.ValueToKey.replace(value, newKey);
       }
    }

    /**
     * Replace a value by searching map with the key
     *
     * @param key       The key used to search
     * @param newValue  The new value that is replacing the existing value
     */
    public void replaceValue(A key, B newValue){
        if(this.KeyToValue.containsKey(key)){
            this.ValueToKey.replace(this.KeyToValue.get(key), key);
            this.KeyToValue.replace(key, newValue);
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
        if(!this.KeyToValue.containsKey(key) && !this.ValueToKey.containsKey(value)){
            this.KeyToValue.put(key, value);
            this.ValueToKey.put(value, key);
        }
    }

    /**
     * remove an entry in the map using the key
     *
     * @param key key used to remove entry in map
     */
    public void removeKey(A key){
        if(this.KeyToValue.containsKey(key)){
            B value = this.KeyToValue.get(key);
            this.KeyToValue.remove(key);
            this.ValueToKey.remove(value);
        }
    }
    
    /**
     * remove an entry in the map using the value
     *
     * @param value value used to remove entry in map
     */
    public void removeValue(B value){
        if(this.ValueToKey.containsKey(value)){
            A key = this.ValueToKey.get(value);
            this.KeyToValue.remove(key);
            this.ValueToKey.remove(value);
        }
    }

    /**
     * Clear everything in the map
     */
    public void clear(){
        this.KeyToValue.clear();
        this.ValueToKey.clear();
    }

    /**
     * Gets the key using the value
     *
     * @param value the value used to search for the key
     * @return      the key
     */
    public A getKey(B value){
        return this.ValueToKey.get(value);
    }

    /**
     * Gets the value using the key
     *
     * @param key the key used to search for the value
     * @return  the value
     */
    public B getValue(A key) {
        return this.KeyToValue.get(key);
    }

    /**
     * Checks if the key exists
     *
     * @param key the key that's being searched for.
     * @return    true if found, false if not
     */
    public boolean containsKey(A key){
        return this.KeyToValue.containsKey(key) && this.ValueToKey.containsValue(key);
    }

    /**
     * Checks if the value exists
     *
     * @param value the value that's being searched for.
     * @return      true if found, false if not
     */
    public boolean containsValue(B value){
        return this.ValueToKey.containsKey(value) && this.KeyToValue.containsValue(value);
    }

    /**
     * The amount of entries in the map
     *
     * @return The amount of entries (Excluding duplicates)
     */
    public int size(){
        return this.KeyToValue.size();
    }

    /**
     * Checks if BiMap is Empty
     *
     * @return true if empty, false if not.
     */
    public boolean isEmpty(){
        if(this.KeyToValue.size() == 0 && this.ValueToKey.size() == 0){
            return true;
        }
        else{
            return false;
        }
    }

}
