package com.ip.smslockdown.models;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User implements Serializable {

    private String fullName;
    private String address;
}
