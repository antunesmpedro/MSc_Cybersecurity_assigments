package com.company;

import com.company.keys_management.Domain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class StoredDomains {
    private static HashMap<Integer, Domain> hosts_mapped_domains = new HashMap<>();

    public void insertDomain(Integer handler, Domain new_domain) {
        hosts_mapped_domains.put(handler, new_domain);
    }

    public void removeDomain(Integer handler) {
        hosts_mapped_domains.remove(handler);
    }

    public void replaceDomain(Integer handler, Domain updated_domain) {
        hosts_mapped_domains.replace(handler, updated_domain);
    }

    public Domain getDomain(Integer handler) {
        return hosts_mapped_domains.get(handler);
    }

    public Set<Integer> getHandlers() {
        return hosts_mapped_domains.keySet();
    }

    public boolean containsHandler(Integer handler) {
        return hosts_mapped_domains.containsKey(handler);
    }

    public Integer getStoredDomainsNumber() {
        return hosts_mapped_domains.size();
    }

    public HashMap<Integer, Domain> getStoredDomainsState() {
        return hosts_mapped_domains;
    }
}
