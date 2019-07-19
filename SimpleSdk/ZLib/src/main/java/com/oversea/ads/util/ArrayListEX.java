
package com.oversea.ads.util;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 支持了增加数据自动排序的功能，显然是不完整的，慎用。 不要进行太多种类的操作，建议仅应用于add，以及remove；
 *
 * @author zhenchenggang
 *
 * @param <E>
 */
public class ArrayListEX<E> extends ArrayList<E> {
    /**
     * DOCUMENT ME!
     */
    HashMap<E, Integer> mHashMap = new HashMap<E, Integer>();

    /**
     * 自动按照key的大小排序
     *
     * @param object aaa
     * @param key aaa
     *
     * @return aaa
     */
    public boolean add(E object, int key) {
        int size = this.size();
        int i = 0;

        for (i = 0; i < size; i++) {
            E curObj = (E) this.get(i);
            Integer curKey = mHashMap.get(curObj);
            if(curKey == null) {
            	continue;
            }
            if (curKey > key) {
            	
                break;
            }
        }

        this.add(i, object);
        this.mHashMap.put(object, key);

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param location DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public E remove(int location) {
        E e = super.remove(location);
        this.mHashMap.remove(e);

        return e;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        super.clear();
        this.mHashMap.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param location DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getKey(int location) {
        E e = this.get(location);

        return mHashMap.get(e);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getKey(E e) {
        return mHashMap.get(e);
    }
}
