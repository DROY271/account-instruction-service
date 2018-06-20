package com.cognizant.samples.ai;

import java.util.ArrayList;
import java.util.List;

public class RowDescriptor {

    private List<Object[]> columns = new ArrayList<>();

    public RowDescriptor column(String name, int type) {
        columns.add(new Object[]{name, type});
        return this;
    }

    int getType(int index) {
        // index is based on JDBC and starts from 1.
        return (Integer)columns.get(index - 1)[1];
    }

    String getName(int index) {
        // index is based on JDBC and starts from 1.
        return (String)columns.get(index - 1)[0];
    }

    int getColumnCount() {
        return columns.size();
    }

}
