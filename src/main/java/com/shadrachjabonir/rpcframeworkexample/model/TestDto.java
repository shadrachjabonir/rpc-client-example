package com.shadrachjabonir.rpcframeworkexample.model;

import java.io.Serializable;

public class TestDto implements Serializable{
    private static final long serialVersionUID = 3189277553321022691L;

    private String name;
    private Integer age;
    private Boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "TestDto{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                '}';
    }
}
