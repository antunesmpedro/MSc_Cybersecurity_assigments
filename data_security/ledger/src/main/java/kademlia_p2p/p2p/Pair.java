package p2p;

public class Pair{
    KBucket k1,k2;

    public Pair(KBucket k1,KBucket k2){
        this.k1 = k1;
        this.k2 = k2;
    }

    public KBucket getValue0(){
        return k1;
    }


    public KBucket getValue1(){
        return k2;
    }

}
