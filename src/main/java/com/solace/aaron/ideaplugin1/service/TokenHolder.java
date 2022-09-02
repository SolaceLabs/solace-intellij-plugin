package com.solace.aaron.ideaplugin1.service;

import java.util.Properties;

public class TokenHolder {

    public static Properties props = new Properties();

    static {
        props.put("token", "");
        props.put("baseUrl", "solace-sso.solace.cloud");
        
        props.put("timeFormat", "relative");
    }
}
