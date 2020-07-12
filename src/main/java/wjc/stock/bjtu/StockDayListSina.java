package wjc.stock.bjtu;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import finance.stock.bjtu.Constant;

public class StockDayListSina extends StockDayList implements Runnable {
	public static String SinaBase = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/";
	public static String DayDir = "DayExcel";
	public static String StartDay = "&begin_day=2014-02-21";
	
	
	
	public StockDayListSina(StockInfo s) {
		super(s);
		// TODO Auto-generated constructor stub
	}
	
	public boolean downloadFromSina(){
		daylist.clear();
		String code = stock.getCode();
		char first = code.charAt(0);
		String u;
		if(stock.getCode().startsWith("s")){
			u = SinaBase+stock.getCode().substring(2)+"/type/S.phtml";
		}
		else{
			u = SinaBase+stock.getCode()+".phtml";
		}
		
		
		try {
			URL url = new URL(u);
			Html h = new Html(url);
			boolean b = h.download("gb2312");
			if(b) {
//				System.out.println("download over");
//				System.out.println(h.getContent());
				String content= HtmlParse.getContent(h.getContent(), 0, "table", "开盘价");
				if(content == null)
					return false;
//				System.out.println(content);
				
				int index=0;
				String line = HtmlParse.getContent(content, index, "tr");
				index = index+line.length();
				line = HtmlParse.getContent(content, index, "tr");
		//		System.out.println(line);
				index = index+line.length();
				line = HtmlParse.getContent(content, index, "tr");
//				System.out.println("*****"+line);
				Vector v;
				while(line != null){
//					System.out.println("parsing line");
	//				System.out.println(line);
					//处理每行获得的数据，在line中	
					v=HtmlParse.parseTRSina(line);
					/*修改index中的部分数据出错增加日期</td>内容*/
			//		index = content.indexOf(((String)v.get(0)+"</div></td>"),index);
//					System.out.println(v);
			//		String[] array= v.toArray();
					DaytimeStock daytimestock = new DaytimeStockSina(v);
		//			System.out.println(daytimestock);
					daylist.addLast(daytimestock);
//					if(daytimestock.getDay().endsWith("2009-08-31")){
//						System.out.println("2009-8-31");
//					}
//					System.out.println(line);
					index = index+line.length();
					//每行处理完毕
					line = HtmlParse.getContent(content, index, "tr");
					
//					System.out.println("content start");
//					System.out.println(content);
//					System.out.println("content end");
//					System.out.println(index);
				}
//				System.out.println("daylist size:"+daylist.size());
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
		ExcelAccesser exc = new ExcelAccesser(DayDir+File.separator+stock.getCode()+".xls");
		exc.open(0);
		int index = 0;
		while(index < daylist.size()){
			try {
				exc.insertRow(1, ((DaytimeStock)daylist.getLast()).toVector());
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			daylist.removeLast();
		}
		System.out.println("list size:"+daylist.size());
		exc.close();
		return true;
	}
	
	public boolean saveToExcel(String Dir) throws IOException{
		ExcelAccesser exc = new ExcelAccesser(Dir+File.separator+stock.getCode()+".xls");
		exc.open(0);
		int index = 0;
		while(index < daylist.size()){
			try {
				exc.insertRow(1, ((DaytimeStock)daylist.getLast()).toVector());
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			daylist.removeLast();
		}
		//System.out.println("list size:"+daylist.size());
		exc.close();
		return true;
	}
	
	public int updateToExcel(){
		boolean b = downloadFromSina();
		int downloadsize = daylist.size()-1;
		int index = 0;
		int lines = 0;
		if(daylist.size() == 0){
			System.out.println(""+stock.getName()+"没有新数据");
			return 0;
		}
		
		ExcelAccesser exc = new ExcelAccesser(DayDir+File.separator+stock.getCode()+".xls");
		exc.open(0);
		
//		System.out.println("index:"+index);
		String excelDay=exc.read(0, 1);
		if(excelDay == null){
			System.out.println("没有数据");
			exc.close();
			return 0;
		}
		
		String day=((DaytimeStock)(daylist.get(index))).getDay();
		if(b){
			while(cmpDay(day,excelDay)==1){
				index++;
				if(index > downloadsize)
					break;
				day=((DaytimeStock)(daylist.get(index))).getDay();
			}
			lines = index;
			while(index > 0){
				index--;
				try {
					exc.insertRow(1,((DaytimeStock)(daylist.get(index))).toVector());
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
		File file = new File(DayDir+File.separator+stock.getCode()+".xls");
		if(file.exists()){
			System.out.println(stock.getName()+"download");
			updateToExcel();
		}else{
			boolean b=downloadFromSina();
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
			System.out.println("StockDayListSina:readFromFile ="+length);
			return 0;
		}
		ExcelAccesser exc = new ExcelAccesser(DayDir+File.separator+stock.getCode()+".xls");
		boolean openState = false;
		openState = exc.openOnly(0);
		if(!openState){
			System.out.println("StockDayListSina:readFromFile open File "+stock.getCode()+" fail");
			return 0;
		}
		int index = 0;
		while(index < length){
			int row = 1+index;
			String date = exc.read(0, row);
			String sOpenPrice = exc.read(1, row);
			String sHighestPrice = exc.read(2, row);
			String sLowestPrice = exc.read(3, row);
			String sClosePrice = exc.read(4, row);
			String sHandoverMount = exc.read(5, row);
			String sHandoverPrice = exc.read(6, row);
			if(sOpenPrice==null||sHighestPrice==null||sLowestPrice==null||sClosePrice==null||sHandoverMount==null||sHandoverPrice==null){
				exc.close();
				return index;	
			}
			float openPrice = Float.parseFloat(sOpenPrice);
			float highestPrice = Float.parseFloat(sHighestPrice);
			float lowestPrice = Float.parseFloat(sLowestPrice);
			float closePrice = Float.parseFloat(sClosePrice);
			float handoverMount = Float.parseFloat(sHandoverMount);
			float handoverPrice = Float.parseFloat(sHandoverPrice);
		//	float avgHandoverPrice = Float.parseFloat(exc.read(8, row));
			if(date!=null){
				 DaytimeStock daytime = new DaytimeStock(date,openPrice,highestPrice,lowestPrice,closePrice,handoverMount,handoverPrice);
                // daylist.addFirst(daytime);
				 daylist.addLast(daytime);
			}else{
				exc.close();
				return index;
			}
			index++;
		}
		exc.close();
		//2013-06-08增加后复权全局控制参数
		if(Constant.restoration == 1){
			backwardPrice();
		}
		
		return index;
	}
	//读取date1和date2之间的数据,未完成
	public int readFromFile(String date1,String date2){
		if(cmpDay(date1,date2)>0){
			return 0;
		}
		System.out.println(""+stock.getName());
		ExcelAccesser exc = new ExcelAccesser(DayDir+File.separator+stock.getCode()+".xls");
		boolean openState = false;
		openState = exc.openOnly(0);
		if(!openState){
			System.out.println("StockDayListSina:readFromFile open File "+stock.getCode()+" fail");
			return 0;
		}
		int row = 1;
		int readline = 0;
		String date = exc.read(0, row);
		//当读入日期非空，而且 excel日期大于截止日期date2则继续向下读取
		while(date!= null && cmpDay(date,date2)>0){
			row = row +1;
			date = exc.read(0, row);
		}
		if(date == null){
			exc.close();
			return 0;
		}
		//当读入日期非空，而且 excel日期大于等于截止日期date1则继续向下读取
		while(date!= null && cmpDay(date,date1)>=0){
			
			String sOpenPrice = exc.read(1, row);
			String sHighestPrice = exc.read(2, row);
			String sLowestPrice = exc.read(3, row);
			String sClosePrice = exc.read(4, row);
			String sHandoverMount = exc.read(5, row);
			String sHandoverPrice = exc.read(6, row);
			if(sOpenPrice==null||sHighestPrice==null||sLowestPrice==null||sClosePrice==null||sHandoverMount==null||sHandoverPrice==null){
				exc.close();
				return readline;	
			}
			float openPrice = Float.parseFloat(sOpenPrice);
			float highestPrice = Float.parseFloat(sHighestPrice);
			float lowestPrice = Float.parseFloat(sLowestPrice);
			float closePrice = Float.parseFloat(sClosePrice);
			float handoverMount = Float.parseFloat(sHandoverMount);
			float handoverPrice = Float.parseFloat(sHandoverPrice);
			
			if(date!=null){
				 DaytimeStock daytime = new DaytimeStock(date,openPrice,highestPrice,lowestPrice,closePrice,handoverMount,handoverPrice);
               // daylist.addFirst(daytime);
				 daylist.addLast(daytime);
			}else{
				exc.close();
				return readline;
			}
			row++;
			readline++;
			date = exc.read(0, row);
		}
		
	
		exc.close();
		//2013-06-08增加后复权全局控制参数
		if(Constant.restoration == 1){
			backwardPrice();
		}
//		while(cmpDay(date1,date)==1){
//			line = r.readLine();
//			array = line.split(" ");
//		}
		
		return readline;
	}
	public void run() {
		// TODO Auto-generated method stub
		RefreshData();
	}
	
	//根据输入的参数，将已有数据排序,order为1时从小到大排列，order为2时为从大到小排列
	public boolean orderByClosePrice(int order){
		if(daylist == null || daylist.size() == 0){
			System.out.println("StockDayListMsn.orderByClosePrice:null");
			return false;
		}
		if(daylist.size() == 1){
			return true;
		}
		if(order == 1){
			int listSize = daylist.size();
			for(int i = 1;i<listSize;i++){
				float iclosePrice = ((DaytimeStock)(daylist.get(i))).getClosePrice();
				for(int j = 0;j<=i;j++){
					if(iclosePrice < ((DaytimeStock)(daylist.get(j))).getClosePrice()){
						DaytimeStock sIndex = ((DaytimeStock)(daylist.get(i)));
						daylist.remove(i);
						daylist.add(j, sIndex);
						break;						
					}
				}				
			}
		}else if(order == 2){
			int listSize = daylist.size();
			for(int i = 1;i<listSize;i++){
				float iclosePrice = ((DaytimeStock)(daylist.get(i))).getClosePrice();
				for(int j = 0;j<=i;j++){
					if(iclosePrice > ((DaytimeStock)(daylist.get(j))).getClosePrice()){
						DaytimeStock sIndex = ((DaytimeStock)(daylist.get(i)));
						daylist.remove(i);
						daylist.add(j, sIndex);
						break;						
					}
				}				
			}
		}
		return true;
	}
	
	public StockDayListSina getStockDayListWithNull(){
		return new StockDayListSina(new StockInfo(this.getStock().getCode(),this.getStock().getName()));
	}
	
	public boolean insertFirst(DaytimeStock d){
		if(d == null){
			return false;
		}
		daylist.addFirst(d);
		return true;
	}
	
	public boolean insertLast(DaytimeStock d){
		if(d == null){
			return false;
		}
		daylist.addLast(d);
		return true;
	}
	
	public void backwardPrice(){
		if(daylist == null || daylist.size()==0){
			return;
		}
		int daysize = daylist.size();
		String firstDay = ((DaytimeStock)(daylist.getLast())).getDay();
		dispatchlist = new StockBonusDispatchList(stock);
		int n = dispatchlist.readFromFile(firstDay);
		//当前读取日期列表中没有除权分配，则直接返回
		if(n == 0){
			return;
		}
		dispatchlist.parseMethod();
		//当前读取日期列表中有n次分红数据,则每次对超过分红日期数据进行复权调整
		for(int k=0;k<n;k++){
			BonusDispatch bonusk= (BonusDispatch)(dispatchlist.getDispatchList().get(k));
			String bonusDay = bonusk.getDay();
			float plusPrefix = (bonusk.getPlusPrefix()+10.0f)/10.0f;
			float addPrefix = bonusk.getAddPrefix()/10.0f;
			for(int i = 0; i<daysize; i++) {
				String sDay = ((DaytimeStock)(daylist.get(i))).getDay();
				if(cmpDay(sDay,bonusDay)>=0){
					((DaytimeStock)(daylist.get(i))).AdjustPrice(plusPrefix, addPrefix);
				}else{
					break;
				}	
			}
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StockInfo s = new StockInfo("002514","宝馨科技");
		StockDayListSina stockList = new StockDayListSina(s);
		stockList.downloadFromSina();
		System.out.println(stockList);
		stockList.RefreshData();
//		StockInfo s = new StockInfo("000002","万科");
//		StockDayListSina stockList = new StockDayListSina(s);
//		stockList.downloadFromSina();
//		System.out.println(stockList);
		
//		StockInfo s = new StockInfo("sh000001","上证指数");
//		StockDayListSina stockList = new StockDayListSina(s);
//		stockList.downloadFromSina();
//		System.out.println(stockList);
		
	}

}
