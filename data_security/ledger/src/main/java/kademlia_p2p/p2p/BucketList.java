package p2p;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class BucketList {
    protected final int B = 5; //Recommended value by the specification
    protected List<KBucket> buckets;
    protected Id ourId;

    public BucketList(Id id){
        ourId = id;
        buckets = new ArrayList<KBucket>();
        buckets.add(new KBucket());
    }

    public List<KBucket> getBuckets(){
        return buckets;
    }

    public Id getId(){
        return ourId;
    }

    public void addContact(Contact contact) throws Exception{
        if(contact.id == ourId)
            return;
        contact.touch();

        ReadWriteLock lock = new ReentrantReadWriteLock();
        lock.writeLock().lock();
        try {
            //ODO get the kbucket that has the contact id in range
            KBucket kbucket = getKBucket(contact.id);
            if(kbucket.contains(contact)){
                //Replace the existing contact, updating network info
                //and lastSeen. Move contact to tail of the list
                kbucket.replaceContact(contact);
            }
            else if(kbucket.isBucketFull()){
                if(canSplit(kbucket)){
                    //Split the bucket and try to insert contact again
                    Pair pair = kbucket.split();
                    KBucket k1 = pair.getValue0();
                    KBucket k2 = pair.getValue1();
                    int i = buckets.indexOf(kbucket);
                    k1.touch();
                    k2.touch();
                    buckets.add(i,k1);
                    buckets.add(i+1,k2);
                    addContact(contact);
                }
                else {
                    //TODO Ping the oldest contact, if it doesn't answer replace it
                }
            }
            else {
                kbucket.addContact(contact);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean canSplit(KBucket kbucket) {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        lock.writeLock().lock();
        try {
            return kbucket.inRange(ourId) || ((kbucket.depth() % B) != 0);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public KBucket getKBucket(Id id) throws Exception{
        ReadWriteLock lock = new ReentrantReadWriteLock();
        lock.writeLock().lock();
        try {
            for(KBucket b : buckets){
                if(b.inRange(id))
                    return b;
            }
            throw new Exception("Couldn't find the required bucket");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public KBucket findClosestKBucket(Id key){
        Map<BigInteger, KBucket> map = new HashMap<BigInteger, KBucket>();
        Map<BigInteger, KBucket> treeMap = new TreeMap<BigInteger, KBucket>(map);
        for(KBucket b : buckets) {
            treeMap.put(b.xor(key),b);
        }
        KBucket closest = treeMap.get(((TreeMap<BigInteger, KBucket>) treeMap).firstKey());
        return closest;
    }
}