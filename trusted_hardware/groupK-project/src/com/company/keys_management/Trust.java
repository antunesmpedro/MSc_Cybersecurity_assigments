package com.company.keys_management;

import java.io.Serializable;
import java.util.LinkedList;
import java.security.Signature;

public class Trust implements Serializable {
    private static final long serialVersionUID = 1759782651542523084L;
    private String id;
    private EntitiesPublicKeys domain_public_keys;
    private Integer quorum_signs_required;
    private LinkedList<QuorumAttestation> new_entities_attestation = new LinkedList<>();
    private String previous_hash;
    private byte[] signature;


    // ---------------------------------------------------------------------

    public Trust(String id, EntitiesPublicKeys domain_public_keys, Integer quorum_signs_required) {
        this.id = id;
        this.domain_public_keys = domain_public_keys;
        this.quorum_signs_required = quorum_signs_required;
    }

    public Trust(String id, EntitiesPublicKeys domain_public_keys, Integer quorum_signs_required, byte[] signature) {
        this.id = id;
        this.domain_public_keys = domain_public_keys;
        this.quorum_signs_required = quorum_signs_required;
        this.signature = signature;
    }

    public Trust(String id, EntitiesPublicKeys domain_public_keys, Integer quorum_signs_required, LinkedList<QuorumAttestation> new_entities_attestation) {
        this.id = id;
        this.domain_public_keys = domain_public_keys;
        this.quorum_signs_required = quorum_signs_required;
        this.new_entities_attestation = new_entities_attestation;
    }

    // ---------------------------------------------------------------------


    public String getId() {
        return id;
    }

    public EntitiesPublicKeys getDomainPublicKeys() {
        return domain_public_keys;
    }

    public Integer getQuorumSignsRequired() {
        return quorum_signs_required;
    }

    public String getPreviousHash() {
        return previous_hash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setPreviousHash(String hash) {
        this.previous_hash = hash;
    }
    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public LinkedList<QuorumAttestation> getNewEntitiesAttestation() {
        return new_entities_attestation;
    }
}
