package io.github.arkery.tickethub.CustomUtils;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hackish Table implementation
 * This does not directly support .ToString or .Equals
 *
 * @author arkery
 */
@NoArgsConstructor
public class BasicConcurrentTable<X,Y,Value> implements Serializable {

    private ConcurrentHashMap<X, HashMap<Y, Value>> data = new ConcurrentHashMap<>();
    private HashSet<X> xValues = new HashSet<>(); //Table must have unique x values
    private HashSet<Y> yValues = new HashSet<>(); //Table must have unique y values

    /**
     * Add value to table
     *
     * @param x     x coor
     * @param y     y coor
     * @param value value to be stored at (x,y)
     */
    public void add(X x, Y y, Value value){

        if(this.data.containsKey(x)){
            if(!this.yValues.contains(y)){
                this.data.get(x).put(y, value);
                this.yValues.add(y);
            }
        }else{
            if(!this.xValues.contains(x) && !this.yValues.contains(y)){
                HashMap<Y, Value> yValue = new HashMap<>();
                yValue.put(y, value);
                this.data.put(x, yValue);
                this.xValues.add(x);
                this.yValues.add(y);
            }
        }
    }

    /**
     * Remove an entry from table
     *
     * @param x x coor
     * @param y y coor
     */
    public void remove(X x, Y y){

        if(this.xValues.contains(x) && this.yValues.contains(y)){
        
            this.data.get(x).remove(y);
            this.yValues.remove(y);

            if(this.data.get(x).isEmpty()){
                this.data.remove(x);
                this.xValues.remove(x);
            }
        }
    }

    /**
     * replace value at (x,y)
     *
     * @param x     x coor
     * @param y     y coor
     * @param value new value to be replaced with
     */
    public void replace(X x, Y y, Value value){
        this.data.get(x).replace(y, value);
    }

    public boolean contains(X x, Y y){
        if(this.xValues.contains(x)){
            if(this.yValues.contains(y)){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    /**
     * Get a value at given x & y
     *
     * @param x x coor
     * @param y y coor
     * @return  the value at x & y
     */
    public Value get(X x, Y y){
        return this.data.get(x).get(y);
    }

    public List<Value> getAll(){
        List<Value> dataList = new ArrayList<>();

        for(HashMap<Y, Value> i: this.data.values()){
            for(Value value: i.values()){
                dataList.add(value);
            }
        }

        return dataList;
    }

    /**
     * Get all values at x
     *
     * @param x x coor
     * @return  an UNSORTED list containing all values at x
     */
    public List<Value> getAllX(X x) {
        List<Value> dataList = new ArrayList<>();

        for (Value i : this.data.get(x).values()) {
            dataList.add(i);
        }

        return dataList;
    }

    /**
     * Get all values at y
     *
     * @param y y coor
     * @return  an UNSORTED list containing all values at y
     */
    public List<Value> getAllY(Y y){
        List<Value> dataList = new ArrayList<>();

        for(HashMap<Y, Value> i: this.data.values()){
            if(i.containsKey(y)){
                dataList.add(i.get(y));
            }
        }

        return dataList;
    }

    /**
     * size of table
     *
     * @return the size of the table
     */
    public int size(){
        int size = 0;

        for(HashMap<Y, Value> i: this.data.values()){
            size = size + i.size();
        }

        return size;
    }

    /**
     * Clear everything
     */
    public void clear(){
        this.data.clear();
    }

    /**
     * Check if Table is Empty
     *
     * @return true if empty, false if not.
     */
    public boolean isEmpty(){
        if(this.data.size() == 0){
            return true;
        }
        else{
            return false;
        }
    }

}
