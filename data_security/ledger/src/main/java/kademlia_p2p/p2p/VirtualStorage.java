package p2p;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

//TODO arranjar
public class VirtualStorage {
    protected ConcurrentHashMap<BigInteger, StoreValue> store;

    public VirtualStorage(){
        store = new ConcurrentHashMap<BigInteger, StoreValue>();
    }

    public boolean contains(Id key){
        return store.containsKey(key);
    }

    public String get(Id key){
        return store.get(key).Value;
    }
    public String get(BigInteger key){
        return store.get(key).Value;
    }

}
