package it.chrccl.carrozzeriaonline.model;

import lombok.Getter;

@Getter
public enum Partner {

    CARLINK("CARLINK"),

    SAVOIA("SAVOIA");

    private final String name;

    Partner(String name) {
        this.name = name;
    }

}
