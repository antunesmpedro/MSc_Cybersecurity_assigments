package com.company.keys_management;

import java.io.Serializable;
import java.security.Signature;
import java.util.LinkedList;

public class Domain implements Serializable {
    private static final long serialVersionUID = 400645610624128345L;
    private Trust trust;
    private DomainKeys domain_keys;
    private byte[] signature;

    // ---------------------------------------------------------------------

    public Domain(Trust trust, DomainKeys domain_keys, byte[] signature) {
        this.trust = trust;
        this.domain_keys = domain_keys;
        this.signature = signature;
    }

    public Domain(Trust trust, DomainKeys domain_keys) {
        this.trust = trust;
        this.domain_keys = domain_keys;
    }

    public Domain(Trust trust) {
        this.trust = trust;
    }

    // ---------------------------------------------------------------------


    public Trust getTrust() {
        return trust;
    }

    public void setTrust(Trust trust) {
        this.trust = trust;
    }

    public void setDomainKeys(DomainKeys domain_keys) {
        this.domain_keys = domain_keys;
    }
    public DomainKeys getDomainKeys() {
        return domain_keys;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
