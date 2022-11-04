package com.company.keys_management;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.LinkedList;

public class EntitiesPublicKeys implements Serializable {
    private static final long serialVersionUID = -8860862611601502934L;
    LinkedList<PublicKey> operators_public_keys = new LinkedList<>();
    LinkedList<PublicKey> hsms_public_keys = new LinkedList<>();

    public EntitiesPublicKeys(LinkedList<PublicKey> operators_public_keys, LinkedList<PublicKey> hsms_public_keys) {
        this.operators_public_keys = operators_public_keys;
        this.hsms_public_keys = hsms_public_keys;
    }

    public LinkedList<PublicKey> getHsmsPublicKeys() {
        return hsms_public_keys;
    }

    public LinkedList<PublicKey> getOperatorsPublicKeys() {
        return operators_public_keys;
    }
}
