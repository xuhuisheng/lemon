package com.mossle.form.operation;

import java.util.Map;

public interface Operation<T> {
    T execute(Map<String, String[]> parameters);
}
