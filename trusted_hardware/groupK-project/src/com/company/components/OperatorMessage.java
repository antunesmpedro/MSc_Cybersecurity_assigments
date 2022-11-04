package com.company.components;

import com.company.keys_management.Domain;
import com.company.keys_management.Trust;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * operation == 1  -> (createDomain) - [Operator request]
 * operation == 2  -> (createDomain) retrieve host list, hsms list and operators list to choose - [KMS response]
 * operation == 11 -> (createDomain) chosen host, hsms and operators - [Operator request]
 * operation == 3  -> (updateDomain) - [Operator request]
 * operation == 4  -> (updateDomain) retrieve domains belongs to the operator list to operator choose - [KMS response]
 * operation == 5  -> (updateDomain) new entities list to update chosen domain - [Operator request]
 * operation == 6  -> (updateDomain Attest new Entities) - [KMS request]
 * operation == 7  -> (updateDomain Attest new Entities) - [Operator response]
 * operation == 8  -> (removeDomain) - [Operator request]
 * operation == 9  -> (removeDomain) retrieve domains list - [KMS response]
 * operation == 10 -> (removeDomain) chosen domain to remove - [Operator request]
 * operation == 12 -> (rotateKeys) - [Operator request]
 * operation == 13 -> (rotateKeys) - retrieve domains belongs to the operator list to operator choose - [KMS response]
 * operation == 14 -> (rotateKeys) - rotate keys to chosen domain - [Operator request]
 * **/

public class OperatorMessage implements Serializable {
    private static final long serialVersionUID = 4380601055123500014L;
    private Integer operation = -1;
    private String hostname = "";
    private LinkedList<String> hsm_names;
    private LinkedList<String> operator_names;
    private LinkedList<String> hostnames;
    private Integer quorum_size;
    Domain domain = null;
    HashMap<Integer, Domain> domains = null;
    private Trust new_trust = null;
    private Trust old_trust = null;

    Integer host_handler;


    // operation == 11
    public OperatorMessage(Integer operation, String hostname, LinkedList<String> hsm_names, LinkedList<String> operator_names, Integer quorum_size) {
        this.operation = operation;
        this.hostname = hostname;
        this.hsm_names = hsm_names;
        this.operator_names = operator_names;
        this.quorum_size = quorum_size;
    }

    // operation == 2
    public OperatorMessage(Integer operation, LinkedList<String> hostnames, LinkedList<String> hsm_names, LinkedList<String> operator_names) {
        this.operation = operation;
        this.hostnames = hostnames;
        this.hsm_names = hsm_names;
        this.operator_names = operator_names;
    }

    // operation == 3 && operation == 8 && operation == 1 && operation == 12
    public OperatorMessage(Integer operation) {
        this.operation = operation;
    }

    // operation == 4
    public OperatorMessage(Integer operation, HashMap<Integer, Domain> domains, LinkedList<String> hsms, LinkedList<String> operators) {
        this.operation = operation;
        this.domains = domains;
        this.hsm_names = hsms;
        this.operator_names = operators;
    }

    // operation == 5
    public OperatorMessage(Integer operation, Domain domain, LinkedList<String> new_hsms, LinkedList<String> new_operators) {
        this.operation = operation;
        this.domain = domain;
        this.hsm_names = new_hsms;
        this.operator_names = new_operators;
    }

    // operation == 6
    public OperatorMessage(Integer operation, Trust new_trust, Trust old_trust, String hostname) {
        this.operation = operation;
        this.new_trust = new_trust;
        this.old_trust = old_trust;
        this.hostname = hostname;
    }

    // operation == 7
    public OperatorMessage(Integer operation, Trust new_trust, Trust old_trust) {
        this.operation = operation;
        this.new_trust = new_trust;
        this.old_trust = old_trust;
    }

    // operation == 9 && operation == 13
    public OperatorMessage(Integer operation, HashMap<Integer, Domain> domains) {
        this.operation = operation;
        this.domains = domains;
    }

    // operation == 10
    public OperatorMessage(Integer operation, Domain domain) {
        this.operation = operation;
        this.domain = domain;
    }

    // operation == 14
    public OperatorMessage(Integer operation, Domain domain, Integer host_handler) {
        this.operation = operation;
        this.domain = domain;
        this.host_handler = host_handler;
    }

    public Integer getOperation() {
        return operation;
    }

    public Integer getHostHandler() { return host_handler; }
    public LinkedList<String> getHsmNames() {
        return hsm_names;
    }

    public LinkedList<String> getOperatorNames() {
        return operator_names;
    }

    public LinkedList<String> getHostnames() {
        return hostnames;
    }

    public String getHostname() {
        return hostname;
    }

    public Integer getQuorumSize() {
        return quorum_size;
    }

    public HashMap<Integer, Domain> getDomains() {
        return domains;
    }

    public Trust getOldTrust() {
        return old_trust;
    }
    public Trust getNewTrust() {
        return new_trust;
    }
    public Domain getDomain() {
        return domain;
    }
}
