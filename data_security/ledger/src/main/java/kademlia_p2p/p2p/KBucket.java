package p2p;

import java.math.BigInteger;
import java.util.Date;
import java.util.*;
import java.nio.ByteBuffer;


public class KBucket {
    protected Date timeStamp;
    protected ArrayList<Contact> contacts;
    protected BigInteger low;
    protected BigInteger high;
    protected final int CONTACT_SIZE = 20;
    protected final int K=3; //number of k-closest nodes to return
    protected final BigInteger MAXSIZE = new BigInteger(
            ByteBuffer.wrap(new byte[8])
                    .putDouble(Math.pow(2,160))
                    .array());

    public KBucket() {
        contacts = new ArrayList<Contact>();
        low = BigInteger.ZERO;
        high = new BigInteger(MAXSIZE.toByteArray());
    }

    public KBucket(BigInteger low,BigInteger high){
        contacts = new ArrayList<Contact>();
        this.low = low;
        this.high = high;
    }

    public void touch(){
        timeStamp = new Date();

    }

    public void addContact(Contact contact){
        if(!isBucketFull()) {
            contacts.add(contact);
        } else {
            //o que faz quando contacts esta cheio?
        }

    }

    public BigInteger midpoint(){
        final int x = 2;
        return low.add(high).divide(BigInteger.valueOf(x));
    }

    public Pair split() {
        BigInteger midpoint = midpoint();
        KBucket k1 = new KBucket(low,midpoint);
        KBucket k2 = new KBucket(midpoint,high);
        for(Contact c : contacts){
            if(c.id.getId().compareTo(midpoint) == -1) //id < midpoint
                k1.addContact(c);
            else
                k2.addContact(c);
        }
        return new Pair(k1,k2);
    }

    //// Returns the number of bits that are common
    //// across all contacts in the bucket
    public int depth(){
        if(contacts.size() > 0){
            return getLongestCommonPrefix();
        }
        return 0;
    }

    public int getLongestCommonPrefix(){
        int depth = 0;
        while(allCharactersAreSame(depth)){
            depth++;
        }
        return depth;
    }

    private boolean allCharactersAreSame(int depth) {
        String first = contacts.get(0).id.toString();
        for(Contact c : contacts){
            if((depth > Math.pow(2,160)-1) ||
                    (c.id.toString().charAt(depth) !=
                            first.charAt(depth))){
                return false;
            }
        }
        return true;
    }

    ////Check if the given ID is in the bucket range
    public boolean inRange(Id testID){
        if((testID.id.compareTo(low) == 1 ||
                testID.id.compareTo(low) == 0) &&
                (testID.id.compareTo(high) == -1 ||
                        testID.id.compareTo(high) == 0)){
            return true;
        }
        return false;
    }

    ///Check if the bucket contains given contact
    public boolean contains(Contact c){
        if(contacts.contains(c))
            return true;
        else
            return false;
    }

    public void replaceContact(Contact c){
        //TODO replace existing contact updating the network info and LastSeen and move it to the end of the list
    }

    public boolean isBucketFull(){
        if(contacts.size() == CONTACT_SIZE)
            return true;
        else
            return false;
    }

    public BigInteger xor(Id key){
        BigInteger midpoint = midpoint();
        return key.id.xor(midpoint);
    }

    //bucket.contacts.orderby(c=< c.id^key).tolist()
    public ArrayList<Contact> getClosestNodes(Id key){
        ArrayList<Contact> closestNodes = new ArrayList<>();
        Map<BigInteger, Contact> map = new HashMap<>();
        Map<BigInteger, Contact> treeMap = new TreeMap<>(map);
        for(Contact c : contacts) {
            treeMap.put(c.id.id.xor(key.id),c);
        }
        //add K-closest nodes to the list
        for(int i = 0; i < K; i++){
            BigInteger k = ((TreeMap<BigInteger, Contact>) treeMap).firstKey();
            closestNodes.add(treeMap.get(k));
            treeMap.remove(k);
        }
        return closestNodes;
    }

}
