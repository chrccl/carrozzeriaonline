package it.chrccl.carrozzeriaonline.model.dao;

import lombok.Getter;

@Getter
public enum Partner {

    CARLINK("CARLINK"),

    SAVOIA("SAVOIA"),

    INTERNAL("INTERNAL");

    private final String name;

    Partner(String name) {
        this.name = name;
    }

}
