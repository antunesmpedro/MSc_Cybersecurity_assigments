import java.util.List;

package p2p;

public class Router {

    protected Node node;

    public Router(Node node){
        this.node = node;
        (new Thread(new rpcServer(node.PORT))).start();
    }


    //lookup algorithm

    //
    //public override(boolean found, List<Contact> contacts, Contact foundBy, String val){
    public List<Contact> Lookup(Id key,) throws Exception {
        boolean giveMeAll = false;
        boolean haveWork= true;
        List<Contact> ret, contactedNodes, closerContacts, fartherContacts,closerUncontactedNodes, fartherUncontactedNodes = new ArrayList<Contact>();
        List<Contact> nodesToQuery = new ArrayList<Contact>();
        List<Contact> allnodes = node.bucketList.getKBucket(key).contacts;
        nodesToQuery = allnodes.Constants.ALPHA;
        //começa por ir bucar os  contactos do nos que estao mais "perto" que o nosso e guarda em: closercontacts
        for(Contact c : nodesToQuery) {
            if(c.id.xor(key)<node.ourContact.id.xor(key))
                closerContacts.add(c);
            else
                fartherContacts.add(c);
        }
        farthercontacts.add(allnodes.skip(constants.alpha));
        contactedNodes.addRangeDistinctBy(nodesToQuery, (a,b) =>a.id==b.id);
        queryResult = Query(key, nodesToQuery, rpcCall, closerContacts, fartherContacts);//queryresult e tipo String??
        if(queryResult!=null) return queryResult;
        while(ret.size()<Constants.k &&haveWork){
              closerUncontactedNodes = closerContacts;
              closerUncontactedNodes.removeIf(c->!(contactedNodes.contains(c)));
              fartherUncontactedNodes= fartherContacts;
              fartherUncontactedNodes.removeIf(c->!(contactedNodes.contains(c)));
              boolean haveCloser = closerUncontactedNodes.size()>0;
              boolean haveFarther = fartherUncontactedNodes.size()>0;
              if(haveCloser | haveFarther)
                  newNodesToQuery=closeUncontactedNodes.Constants.ALPHA;
                  contactedNodes.AddRangeDistinctBy(newNodesToQuery, (a,b) =>a.id==b.id);
                  queryResult=Query(key, newNodesToQuery, rpcCall, closerContacts, fartherContacts);
                  if(queryResult!=null) return queryResult;
                  else if(haveFarther){
                      newNodesToQuery = fratherUncontactedNodes.Constants.ALPHA;
                      contactedNodes.AddRangeDistinctBy(fartherUncontactedNodes, (a,b)=> a.id==b.id);
                      queryResult = Query(key, newNodesToQuery, rpcCall, closerContacts, fartherContacts);
                      if(queryResult!=null) return queryResult;
        }
        }
        }
        Hash<Contact>
        return (false, (giveMeAll ? ret: ret.Take(Constants.k).OrderBy(c=> c.id^key).ToList()),null, null);
        }
    }


}
