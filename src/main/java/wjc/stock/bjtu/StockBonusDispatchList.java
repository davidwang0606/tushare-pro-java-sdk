package wjc.stock.bjtu;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class StockBonusDispatchList {
	
	public static String BonusDir = "BonusDispatch";
	public static String BonusBase = "http://f10.eastmoney.com/f10_v2/BonusFinancing.aspx?code=";
	StockInfo stock;
	LinkedList dispatchList;
	
	public StockInfo getStock() {
		return stock;
	}

	public void setStock(StockInfo stock) {
		this.stock = stock;
	}

	public LinkedList getDispatchList() {
		return dispatchList;
	}

	public void setDispatchList(LinkedList dispatchList) {
		this.dispatchList = dispatchList;
	}

	public StockBonusDispatchList(StockInfo s){
		stock = s;
		dispatchList = new LinkedList();
	}
	
	public String toString(){
		Iterator i = dispatchList.iterator();
		String s = "";
		while(i.hasNext()){
			s = s+i.next()+"\n";
		}
		return "股票代码:"+stock.getCode()+".股票名称:"+stock.getName()+"\n"+s;
	}
	
	public boolean downloadFromEastmoney(){
		dispatchList.clear();
		String code = stock.getCode();
		char first = code.charAt(0);
		String u="";
		if(first=='6'){
			u = BonusBase+"sh"+stock.getCode();
		} else if(first == '0'){
		    u = BonusBase+"sz"+stock.getCode();
		}else if(first == '3'){
		    u = BonusBase+"sz"+stock.getCode();
		}
		else {
			u = BonusBase+stock.getCode();
		}
		
		u=u+"&timetip=635665361905739610";
		
		try {
			URL url = new URL(u);
			Html h = new Html(url);
			boolean b = h.download("UTF-8");
			if(b) {
				String content= HtmlParse.getContent(h.getContent(), 0, "table", "公告日期");
				if(content == null)
					return false;
				System.out.println(content);
				int index=0;
				String line = HtmlParse.getContent(content, index, "tr");
				System.out.println(line);
				index = index+line.length();
				line = HtmlParse.getContent(content, index, "tr");
//				System.out.println("*****"+line);
				Vector v;
				while(line != null){
					System.out.println(line);
					//处理每行获得的数据，在line中	
					v=HtmlParse.parseTR2(line);
					/*修改index中的部分数据出错增加日期</td>内容*/
					/*2013-6-8增加+((String)v.get(0)).length()偏移量*/
					index = content.indexOf(((String)v.get(0)+"</td>"),index)+((String)v.get(0)).length();
					System.out.println(v);
					if(v.get(3).equals("--")){
						line = HtmlParse.getContent(content, index, "tr");
						continue;
					}
					BonusDispatch bonus = new BonusDispatch((String)(v.get(3)),(String)(v.get(1)));
				    dispatchList.addLast(bonus);

					//每行处理完毕
					line = HtmlParse.getContent(content, index, "tr");

				}
				return true;
			}else{
				return false;
			}
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean saveToExcel() throws IOException{
		ExcelAccesser exc = new ExcelAccesser(BonusDir+File.separator+stock.getCode()+"bonusDispatch.xls");
		exc.open(0);
		int index = 0;
		while(index < dispatchList.size()){
			try {
				exc.insertRow(1, ((BonusDispatch)(dispatchList.getLast())).toVector());
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			dispatchList.removeLast();
		}
		System.out.println("list size:"+dispatchList.size());
		exc.close();
		return true;
	}
	
	public int updateToExcel(){
		boolean b = downloadFromEastmoney();
		int downloadsize = dispatchList.size()-1;
		int index = 0;
		int lines = 0;
		if(dispatchList.size() == 0){
			System.out.println(""+stock.getName()+"没有新除权数据");
			return 0;
		}
		
		ExcelAccesser exc = new ExcelAccesser(BonusDir+File.separator+stock.getCode()+"bonusDispatch.xls");
		exc.open(0);
		
//		System.out.println("index:"+index);
		String excelDay=exc.read(0, 1);
		if(excelDay == null){
			System.out.println("没有旧除权数据");
			index = dispatchList.size();
			while(index > 0){
				index--;
				try {
					exc.insertRow(1,((BonusDispatch)(dispatchList.get(index))).toVector());
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exc.close();
					return 0;
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exc.close();
					return 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exc.close();
					return 0;
				}
			}
			
			exc.close();
			return downloadsize+1;
		}
		
		String day=((BonusDispatch)(dispatchList.get(index))).getDay();
		if(b){
			while(StockDayList.cmpDay(day,excelDay)==1){
				index++;
				if(index > downloadsize)
					break;
				day=((BonusDispatch)(dispatchList.get(index))).getDay();
			}
			lines = index;
			while(index > 0){
				index--;
				try {
					exc.insertRow(1,((BonusDispatch)(dispatchList.get(index))).toVector());
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exc.close();
					return 0;
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exc.close();
					return 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exc.close();
					return 0;
				}
			}
			exc.close();
			return lines;
		}
		return 0;
	}
	
	public void RefreshData(){
		File file = new File(BonusDir+File.separator+stock.getCode()+"bonusDispatch.xls");
		if(file.exists()){
			System.out.println(stock.getName()+"download");
			updateToExcel();
		}else{
			boolean b=downloadFromEastmoney();
			if(b){
				try{
					saveToExcel();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	} 
	
	public int readFromFile(int length){
		if(length <=0){
			System.out.println("StockBonusDispatchList:readFromFile ="+length);
			return 0;
		}
		ExcelAccesser exc = new ExcelAccesser(BonusDir+File.separator+stock.getCode()+"bonusDispatch.xls");
		boolean openState = false;
		openState = exc.openOnly(0);
		if(!openState){
			System.out.println("StockBonusDispatchList:readFromFile open File "+stock.getCode()+" fail");
			return 0;
		}
		int index = 0;
		while(index < length){
			int row = 1+index;
			String date = exc.read(0, row);
			String md = exc.read(1, row);
			if(md==null){
				exc.close();
				return index;	
			}
		//	float avgHandoverPrice = Float.parseFloat(exc.read(8, row));
			if(date!=null){
				 BonusDispatch daytime = new BonusDispatch(date,md);
                // daylist.addFirst(daytime);
				 dispatchList.addLast(daytime);
			}else{
				exc.close();
				return index;
			}
			index++;
		}
		exc.close();
		return index;
	}
//	读取在date1前的除权数据
	public int readFromFile(String date1){

		System.out.println(""+stock.getName());
		ExcelAccesser exc = new ExcelAccesser(BonusDir+File.separator+stock.getCode()+"bonusDispatch.xls");
		boolean openState = false;
		openState = exc.openOnly(0);
		if(!openState){
			System.out.println("StockBonusDispatchList:readFromFile open File "+stock.getCode()+" fail");
			return 0;
		}
		int row = 1;
		int readline = 0;
		String date = exc.read(0, row);
		//当读入日期非空，而且 excel日期大于等于截止日期date1则继续向下读取
		while(date!= null && StockDayList.cmpDay(date,date1)>=0){
			
			String md = exc.read(1, row);
			if(md==null){
				exc.close();
				return readline;	
			}

			if(date!=null){
				BonusDispatch daytime = new BonusDispatch(date,md);
               // daylist.addFirst(daytime);
				 dispatchList.addLast(daytime);
			}else{
				exc.close();
				return readline;
			}
			row++;
			readline++;
			date = exc.read(0, row);
		}
		
	
		exc.close();

//		while(cmpDay(date1,date)==1){
//			line = r.readLine();
//			array = line.split(" ");
//		}
		
		return readline;
	}
	
	public void parseMethod(){
		if(dispatchList == null || dispatchList.size() == 0){
			return;
		}
		for(int n = 0;n<dispatchList.size();n++){
			((BonusDispatch)(dispatchList.get(n))).parseMethod();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StockInfo s = new StockInfo("000651","格力电器");
		StockBonusDispatchList dis = new StockBonusDispatchList(s);
//		dis.downloadFromEastmoney();
//		System.out.println(dis);
//		try {
//			dis.saveToExcel();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		dis.RefreshData();
//		System.out.println(dis);
	}

}
