package com.yao2san.sim.gateway.route.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@EqualsAndHashCode(callSuper = true)
@Data
public class RouterMeta extends HashMap {

    private String version;
}
