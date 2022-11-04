package p2p;

import java.util.ArrayList;
import java.util.List;

public class Dht {
    protected Router router;
    protected Storage originatorStorage;
    protected Protocol protocol;
    protected  Node node;

    public Dht(Router router){
        this.router = router;
    }
    public Router getRouter(){
        return router;
    }
    public Storage getOriginatorStorage(){return originatorStorage;}
    public Protocol getProtocol() {return protocol;}
    public Contact getContact() {return ourContact;}


    public Dht(Id id, Protocol protocol, Router router){
        originatorStorage = storageFactory();
        FinishInitialization(id, protocol, router);
    }

    protected void FinishInitialization(Id id, Protocol protocol, Router router){
        Id ourId = id;
        Contact ourContact = new Contact(id); //nao sei se é pa mandar so o id ou se ainda nao ta implementado com o protocolo
        Node node = new Node(ourContact);
        node.Dht= this;
        node.bucketList.Dht=this;
        this.protocol=protocol;
        this.router=router;
        this.router.node=node;
        this.router.Dht=this;
    }

    public void store(Id key, String val){
        touchBucketWithKey(key);
        originatorStorage.set(key,val);
        storeOnCloserContacts(key,val);
    }

    //TODO ver e completar
    //public (bool found, List<p2p.Contact> contacts, String val)
    public void FindValue(Id key){
        touchBucketWithKey(key);
        String ourVal;
        List<Contact> contactsQueried = new ArrayList<Contact>();
        if(originatorStorage.tryGetValue(key, ourVal)){
            //ret = (true, null, ourVal);
        }
        else {
            lookup = router.lookup(key, router.rpcFindValue);
            if (lookup != null) { //lookup.found
                //ret=(true, null, lookup.val);
                //storeTo = lookup.contacts.Where(c=> c!=lookup.foundBy).OrderBy(c=> c.p2p.Id^key).FirstOrDefault();

                if (storeTo != null) {
                    int separatingNodes = getSeparatingNodesCount(ourContact, storeTo);
                    RpcError error = storeTo.Protocol.store(node.ourContact, key, lookup.val, true, expTimeSec);
                    handleError(error, storeTo);
                }
            }
        }
        return ret;
    }

}
