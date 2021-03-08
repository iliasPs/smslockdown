package com.ip.smslockdown.models;

import lombok.Builder;
import lombok.With;

@Builder
public class SmsCode {

    @With
    public int code;

}
