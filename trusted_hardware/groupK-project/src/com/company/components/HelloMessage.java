package com.company.components;

import java.io.Serializable;
import java.security.PublicKey;

public class HelloMessage implements Serializable {
    private static final long serialVersionUID = 7832105599242878043L;
    String clientUsername;
    PublicKey pub_key;

    public HelloMessage(String clientUsername, PublicKey pub_key) {
        this.clientUsername = clientUsername;
        this.pub_key = pub_key;
    }

    public PublicKey getPubKey() {
        return pub_key;
    }

    public String getClientUsername() {
        return clientUsername;
    }
}
