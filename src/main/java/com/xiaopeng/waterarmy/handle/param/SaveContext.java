package com.xiaopeng.waterarmy.handle.param;

public class SaveContext<T> {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public SaveContext(T data) {
        this.data = data;
    }
}
