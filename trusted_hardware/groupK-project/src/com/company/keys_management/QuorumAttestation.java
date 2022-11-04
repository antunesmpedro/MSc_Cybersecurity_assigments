package com.company.keys_management;

import java.io.Serializable;
import java.security.PublicKey;
import java.security.Signature;
import java.util.LinkedList;

public class QuorumAttestation implements Serializable {
    private static final long serialVersionUID = 4256310164327431298L;
    private PublicKey entity;
    private String entity_name;
    private LinkedList<byte[]> attested_operators_signs = new LinkedList<>();

    public QuorumAttestation(PublicKey entity, String entity_name) {
        this.entity = entity;
        this.entity_name = entity_name;
    }

    public PublicKey getEntity() {
        return entity;
    }

    public String getEntityName() {
        return entity_name;
    }

    public LinkedList<byte[]> getAttestedOperatorsSigns() {
        return attested_operators_signs;
    }

    public void setAttestedOperatorsSigns(LinkedList<byte[]> attested_operators_signs) {
        this.attested_operators_signs = attested_operators_signs;
    }

    public void attestEntity(byte[] sign) {
        this.attested_operators_signs.add(sign);
    }
}
