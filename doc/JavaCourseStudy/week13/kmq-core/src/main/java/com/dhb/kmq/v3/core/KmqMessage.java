package com.dhb.kmq.v3.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@AllArgsConstructor
@Data
public class KmqMessage<T> {

    private HashMap<String,Object> headers;

    private T body;

}
