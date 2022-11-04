package com.company.keys_management;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.HashMap;

public class DomainKeys implements Serializable {
    private static final long serialVersionUID = 8536122217138589765L;
    private String master_key_token;                       // cipher master key
    private HashMap<PublicKey, String> domain_keys_tokens;     // public keys that can decrypt master key


    // ---------------------------------------------------------------------

    public DomainKeys(String token, HashMap<PublicKey, String> domain_keys_tokens) {
        this.master_key_token = token;
        this.domain_keys_tokens = domain_keys_tokens;
    }

    // ---------------------------------------------------------------------

    public String getMasterToken() {
        return master_key_token;
    }


    public HashMap<PublicKey, String> getDomainKeys() {
        return domain_keys_tokens;
    }

}
