package it.chrccl.carrozzeriaonline.model.entities;

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
