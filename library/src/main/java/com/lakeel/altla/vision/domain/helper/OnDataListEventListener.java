package com.lakeel.altla.vision.domain.helper;

public interface OnDataListEventListener<TData> {

    void onDataListEvent(DataListEvent<TData> event);
}
