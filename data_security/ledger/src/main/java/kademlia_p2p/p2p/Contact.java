package p2p;

import java.net.Inet4Address;
import java.util.Date;

public class Contact {
    public Date lastSeen;
    public Id id;
    public Inet4Address ip;

    public Contact(Id contactId){
        id = contactId;
        touch();
    }
    public void setLastSeen(Date d){
        lastSeen = d;
    }

    public Date getLastSeen(){
        return lastSeen;
    }

    public void touch(){
        lastSeen = new Date();
    }
}
