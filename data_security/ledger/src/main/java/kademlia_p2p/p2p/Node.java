package p2p;

import sun.security.pkcs11.wrapper.Constants;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.*;
import java.util.Map;

public class Node {
    protected Contact ourContact;
    protected BucketList bucketList;
    protected final int PORT = 8001;

    public Contact getContact() {
        return ourContact;
    }

    public BucketList getBucketList() {
        return bucketList;
    }

    public Node(Contact contact) {
        ourContact = contact;
        bucketList = new BucketList(contact.id);

    }

    public void ping(Contact sender) throws Exception{
        //get sender ip and send ourContact with random 160-bit idk, node replies the random id
        rpcClient client = new rpcClient(sender.ip,PORT);
        try {
            BigInteger randomId = new BigInteger(160,new Random());
            client.ping(ourContact,sender.ip,randomId);
            //TODO update contact info
        } finally {
            client.shutdown();
        }
    }

    //TODO ver se ta bem
    public void store(Contact sender, Id key ,String val ) throws Exception {
        boolean isCached=false; //valores que ele manda por argumento
        int expirationTimeSec=0; //ele manda por argumento
        if(sender.id != ourContact.id) {
            bucketList.addContact(sender);
            if(isCached)
                cacheStorage.Set(key, val, expirationTimeSec);
            else{
                SendKeyValuesIfNewContact(sender);
                storage.Set(key, val, Constants.EXPIRATION_TIME_SECONDS);//acho que Expiration_time_seconds variavel global
            }

        }
        else
            System.out.println("sender shouldn't be yourself");
        System.out.println("store string ->  |");
    }

    //TODO ver se ta bem
    public Map<List<Contact>, String > findValue(Contact sender, Id key) throws Exception {
        Map<List<Contact>, String> toReturn = new HashMap<List<Contact>, String>();
        if(sender.id != ourContact.id){
            SendKeyValuesIfNewContact(sender);
            bucketList.addContact(sender);
            if(storage.Contains(key))
                toReturn.put(null , storage.get(key)); //return null, storage.get(key);
            else if(CacheStorage.Contains(key))
                toReturn.put(null , CacheStorage.get(key)); //return null, CacheStorage.get(key);
            else //exclude sender
                toReturn.put(bucketList.GetCloseContacts(key, sender.id), null);
        }
        else
            System.out.println("Sender shouldn't be ourself");
        return toReturn;
    }

    public Map<List<Contact>, String > findNode(Contact sender, Id key) {
        Map<List<Contact>, String> toReturn = new HashMap<List<Contact>, String>();
        if(sender.id!=ourContact.id)
            try {
                bucketList.addContact(sender);
                KBucket closestKBucket = bucketList.findClosestKBucket(key);
                ArrayList<Contact> contacts = closestKBucket.getClosestNodes(key);
                toReturn.put(contacts, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        else {
            System.out.println("Sender shouldn't be you");
            return null;
        }
        return toReturn;
    }

    // TODO Lookup Algorithm, diz que ainda se vai ver uma implementação paralela
    /*public static void AddRangeDistinctBy(List<Object>target, Enum<Object> src ){
        for (Object item: src) {
            if(target.contains(item))
                target.add(item);
        }
    }*/

    //TODO  decidir entre esta (alternativa) ou a outra
    public void GetAltCloseAndFar (List <Contact> contactsToQuery, List<Contact> closer , List<Contact> farther){
        for(Contact contact: contactsToQuery){
            Node contactNode = null;// nodes.Single( n => n.OurContact == contact)
            List<Contact> closeContactsOfContactedNode = contactNode.bucketList.getCloseContacts(key, router.Node.OurContact.Id).ExceptBy(contactsToQuery, c=> c.Id.Value);
            for(Contact cloceContactofContactedNode: closeContactsOfContactedNode) {
                if((cloceContactofContactedNode.id^key)<distance)
                    closer.AddDistinctBy(closeContactsOfContactedNode, c=> c.Id.Value);
                else
                    farther.AddDistinctBy(closeContactsOfContactedNode, c=> c.id.Value);
            }
        }
    }

    //TODO arranjar ,secalhar e melhor fazer uma classe para retornar isto, acho que ta mal...
    public Map<List<Contact>, Map<Contact,String> > RpcFindNodes(Id key, Contact contact){
        Map<List<Contact>, Integer> newContacts = new TreeMap<List<Contact>, Integer>() ;
        Map<Contact,String> toReturn = null;
        newContacts.put(Contact.Protocol.FindNode(ourContact, key));
        Dht.HandleError(((TreeMap<List<Contact>, Integer>) newContacts).firstKey(), contact);
        List<Contact> Ret=((TreeMap<List<Contact>, Integer>) newContacts).firstKey ();
        return new HashMap<List<Contact>, Map<Contact, String>>().put(Ret,null); //newContacts, null,null
    }

    //TODO nao tou a perceber esta...
    //(List<T> target, IEnumerable<T> src, Func<T,T,bool>equalityComparer)
    public static  void AddRangeDistinctBy(ArrayList target, Enum src){
        for(Object s: src.values()){ //pelo que vi na net isto (.values()) é uma cena do compilador e ao compilar supostamente nao da erro
            //if(s.(q=>equalityComparer(q,item)))
                target.add(s);
        }
    }
}
