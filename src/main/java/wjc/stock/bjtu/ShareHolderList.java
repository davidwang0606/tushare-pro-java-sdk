package wjc.stock.bjtu;
import java.util.*;

public class ShareHolderList {
	public String code;//股票代码
	public List title = new ArrayList();//表格各项名称
	public Vector content= new Vector();//存储ShareHoldNum的列表
	
	public ShareHolderList(String c,String[][] con){
		code = new String(c);
		int start = 0;
		while(con[0][start]==null || con[0][start].equals(""))
			start++;
//		System.out.println("start:"+start);
//		System.out.println(con[0][start]);
//		String s = con[0][start].trim();
//		System.out.println(s);
		title.add(con[0][start].trim());
		title.add(con[0][start+1].trim());
		title.add(con[0][start+2].trim());
	
		for(int i=1; i<con.length && con[i]!=null;i++) {
			content.add(new ShareHolderNum(con[i][start],con[i][start+1],con[i][start+2]));
		}
		
	}
	//根据name项比较表格中两个日期对应的数据比值
	public float compare(DateInfo date1,DateInfo date2,String name){
		int index = -1;
		for(int i=1; i<title.size();i++){
			if(name.equals(title.get(i))){
				index = i;
				break;
			}		
		}
		if(index == -1){
			System.out.println("ShareHolderNum:compare index ==-1");
			return Float.MAX_VALUE;
		}
		
		int horizon1 = -1;
		int horizon2 = -1;
		for(int i=0; i<content.size(); i++){
//			System.out.println(((ShareHolderNum)(content.get(i))).getDate());
			if(date1.equals(((ShareHolderNum)(content.get(i))).getDate())){
				horizon1 = i;
			}
			if(date2.equals(((ShareHolderNum)(content.get(i))).getDate())){
				horizon2 = i;
			}
		}
		if(horizon1 == -1 || horizon2 == -1){
			System.out.println("ShareHolderNum:compare horizon12 ==-1");
			return Float.MAX_VALUE; 
		}
		
		float one = ((ShareHolderNum)(content.get(horizon1))).getByNum(index);
		float two = ((ShareHolderNum)(content.get(horizon2))).getByNum(index);
		return one/two;
		
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append("stock:"+code+"\n");
		buf.append("title:"+title.toString()+"\n");
		for(int i=0; i<content.size();i++){
			buf.append((ShareHolderNum)(content.get(i))+"\n");
		}
		return buf.toString();
	}
	
}
