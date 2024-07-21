package com.fanap.fanrp.pat.patemailserver.utils;

import java.io.Serializable;

public class Hashable implements Serializable {
    private int objectHashCode;

    public int getObjectHashCode() {
        if (objectHashCode == 0) {
            objectHashCode = System.identityHashCode(this);
        }
        return objectHashCode;
    }

    public void setObjectHashCode(int objectHashCode) {
        this.objectHashCode = objectHashCode;
    }
}