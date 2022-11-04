package com.company.components;

import com.company.keys_management.Domain;
import com.company.keys_management.DomainKeys;
import com.company.keys_management.QuorumAttestation;
import com.company.keys_management.Trust;

import java.io.Serializable;
import java.util.LinkedList;

public class KSMMessage implements Serializable {
    private static final long serialVersionUID = -4640231671279024431L;
    private Integer operation = -1;
    private String request_message = "";
    private String response_message = "";
    private Domain domain = null;
    private DomainKeys domain_keys = null;
    private String trust_id ;
    private String hostname;
    private Trust new_trust;
    private Trust old_trust;
    private DomainKeys new_domain_keys;

    /**
     * operation == 1  -> encrypt message - Host request (setResponseMessage with encrypted message)
     * operation == 2  -> decrypt message - Host request (setResponseMessage with decrypted message)
     * operation == 3  -> create domain keys, sign initial trust and sign referer domain - KMS request
     * operation == 4  -> create domain keys, ..., domain - HSM response
     * operation == 5  -> verify and sign trusts - KMS request
     * operation == 6  -> verify and sign trusts - HSM response
     * operation == 7  -> rotate domain keys - KMS request
     * operation == 8  -> rotate domain keys - HSM response
     * **/

    // operation == 1 && operation == 2
    public KSMMessage(Integer operation, String request_message) {
        this.operation = operation;
        this.request_message = request_message;
    }

    // operation == 3 (domain to sign) && operation == 4 (signed domain and trust) && operation == 7 (domain to rotate keys) && operation == 8 (domain with rotated keys)
    public KSMMessage(Integer operation, Domain domain, String hostname) {
        this.operation = operation;
        this.domain = domain;
        this.hostname = hostname;
    }

    // operation == 5 ( sign trust ) && operation == 6 (signed trust)
    public KSMMessage(Integer operation, Trust new_trust, Trust old_trust) {
        this.operation = operation;
        this.new_trust = new_trust;
        this.old_trust = old_trust;
    }

    public KSMMessage(Integer operation, Trust new_trust, DomainKeys new_domain_keys, Trust old_trust) {
        this.operation = operation;
        this.new_trust = new_trust;
        this.new_domain_keys = new_domain_keys;
        this.old_trust = old_trust;
    }

    public void setTrustId(String trust_id) {
        this.trust_id = trust_id;
    }

    public String getRequestMessage() {
        return request_message;
    }

    public String getResponseMessage() {
        return response_message;
    }

    public void setResponseMessage(String response_message) {
        this.response_message = response_message;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Trust getNewTrust() {
        return new_trust;
    }

    public Trust getOldTrust() {
        return old_trust;
    }

    public Domain getDomain() {
        return domain;
    }

    public String getTrustId() {
        return trust_id;
    }

    public DomainKeys getNewDomainKeys() {
        return new_domain_keys;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
