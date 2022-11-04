package p2p;

import java.math.BigInteger;

public class Id {
    protected BigInteger id;
    protected final int ID_LENGTH = 160;

    public Id(BigInteger id){
        this.id = id;
    }

    public Id(byte[] data){
        if(data.length == ID_LENGTH){
            this.id = new BigInteger(data);
        }
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public BigInteger getId(){
        return id;
    }
}
