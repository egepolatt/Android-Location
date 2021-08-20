package com.egepolat.location;

public class BaseContract {
    public String name;

    public BaseContract(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
