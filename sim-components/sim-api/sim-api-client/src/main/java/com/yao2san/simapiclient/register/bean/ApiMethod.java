package com.yao2san.simapiclient.register.bean;

import lombok.Data;

import java.lang.reflect.Parameter;
import java.util.List;

@Data
public class ApiMethod {
    private String methodName;

    private String className;

    private List<ApiMethodParameter> parameters;

    private String returnType;
}
