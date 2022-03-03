package com.yao2san.sim.gateway.config;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sys_config")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Config implements Serializable {

    @Id
    private Long id;

    @Column
    private String code;

    @Column
    private String value;

    @Column
    private String name;

    @Column
    private String description;

}