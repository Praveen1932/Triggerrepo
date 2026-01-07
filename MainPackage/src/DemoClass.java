import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MailUtil;
import com.matrixone.apps.domain.util.MapList;
import matrix.db.Context;
import matrix.util.StringList;

public class DemoClass {

	public MapList getPLMIssueObjects(Context context, String[] args) throws Exception
	{
		System.out.println("Calling getDemoObjects in JPO ------------>");
		
		
		String strtype="IZTPLMIssue";
		String selectPriority = "attribute[IZTPriority]";
		StringList lis= new StringList();
		lis.add(DomainConstants.SELECT_ID);
		lis.add(DomainConstants.SELECT_NAME);
		lis.add(DomainConstants.SELECT_DESCRIPTION);
		lis.add(DomainConstants.SELECT_CURRENT);
		lis.add(DomainConstants.SELECT_OWNER);
		lis.add(selectPriority);
		lis.add(DomainConstants.SELECT_ORIGINATED);
		
		MapList maplist=DomainObject.findObjects(context, strtype,"*", "",lis);
		System.out.println("maplist in JPO ------------>"+maplist);
		return maplist;
 
	}
	
	public int PLMIssueReviewChecktrigger(Context context, String[] args) throws Exception{
		
		String objid = args[0];
		String objpersonname = args[1];
		
		System.out.println(objpersonname);
		
		DomainObject obj = DomainObject.newInstance(context, objid);
		
		StringList lis = new StringList();
		lis.add(DomainConstants.SELECT_ID);
		
		String attrv = obj.getInfo(context, "attribute[IZTReviewRemark]");
		
		MapList connectedObjDetails = obj.getRelatedObjects(context, "IZTPLMIssueSupportAssignee", "Person", lis, null, true, true, (short) 0, null, null,0);
		
		System.out.println(connectedObjDetails);
		
		String personid = null;
		
		for (int i = 0; i < connectedObjDetails.size(); i++) {
		    Map map = (Map) connectedObjDetails.get(i);
		    
		    personid = (String) map.get("id");

		    System.out.println("ID: " + personid);
		}
		
		if (("Reject".equals(attrv) || "Accept".equals(attrv)) && !personid.equals(null)) {
			
			return 0;
		}
		
		else {
			
			return 1;
		}
		
	}
	
	public int IZTPLMCreateCheck(Context context, String[] args) throws Exception{
		
		String objectid = args[0];
		
		DomainObject obj = DomainObject.newInstance(context, objectid);
		
		StringList lis= new StringList();
		lis.add(DomainConstants.SELECT_ID);
		
		MapList connectedObjDetails = obj.getRelatedObjects(context, "IZTPLMIssueAssignee", "Person", lis, null, true, true, (short) 0, null, null,0);
		
		System.out.println(connectedObjDetails);
		
		String personid = null;
		
		for (int i = 0; i < connectedObjDetails.size(); i++) {
		    Map map = (Map) connectedObjDetails.get(i);
		    
		    personid = (String) map.get("id");

		    System.out.println("ID: " + personid);
		}

		
		if (!personid.equals(null)) {
			
			System.out.println("Hello");
			
			return 0;
		}
		
		else {
			
			System.out.println("Hi");
			
			return 1;
		}
		
	}
	
	public void PLMIssueReviewActiontrigger(Context context, String[] args) throws Exception{
		
		String objid = args[0];
		
		DomainObject obj = DomainObject.newInstance(context, objid);
		
		StringList objdetailowner = new StringList();
		
		objdetailowner.add(obj.getInfo(context, "owner"));
		
		StringList objdetailid = new StringList();
		
		objdetailid.add(obj.getInfo(context, "id"));
		
		String attrv = obj.getInfo(context, "attribute[IZTReviewRemark]");
		
		String owner = obj.getInfo(context, "owner");
		
		String objname = obj.getInfo(context, "name");
		
		
		StringList lis= new StringList();
		lis.add(DomainConstants.SELECT_ID);
		
		MapList connectedObjDetails = obj.getRelatedObjects(context, "IZTPLMIssueSupportAssignee", "Person", lis, null, true, true, (short) 0, null, null,0);
		
		System.out.println(connectedObjDetails);
		
		String personid = null;
		
		for (int i = 0; i < connectedObjDetails.size(); i++) {
		    Map map = (Map) connectedObjDetails.get(i);
		    
		    personid = (String) map.get("id");

		    System.out.println("ID: " + personid);
		}
		
		
		String current = obj.getInfo(context, "current");
		
		
		if("Reject".equals(attrv)) {
			
			
			obj.setState(context, "Closed");
			
			String subject = "PLM Issue Updated";
			String message = "Dear "+owner+" the PLM Issue "+objname+" has been reviewed and the object current state id "+ current +" found to be invalid. I have closed the issue as it was created due to a process error.";

			MailUtil.sendMessage(context, objdetailowner, null, null, subject, message, objdetailid);
		}
		else if ("Accept".equals(attrv)){
			
			obj.promote(context);
			
			String ownersubject = "PLM Issue Updated";
			String ownermessage = "Dear "+owner+" the PLM Issue "+objname+" has been reviewed and the object current state id "+ current +". Now it's under Support person";

			MailUtil.sendMessage(context, objdetailowner, null, null, ownersubject, ownermessage, objdetailid);
			
			String supportsubject = "PLM Issue Updated";
			String supportmessage = "Dear "+personid+" the PLM Issue "+objname+" has been reviewed and assigned this object to you current state id "+ current +". Now it's under Support person";

			MailUtil.sendMessage(context, objdetailowner, null, null, supportsubject, supportmessage, objdetailid);
			
		}
		
	}
}

