/**             
* 创建时间： 2010-02-18                                
*/ 
package wjc.stock.bjtu;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.io.*;

import finance.stock.bjtu.Constant;
/**
* 通过sohu网站，下载一个股票的每日价格数据至txt文件内部，并提供接口可以从txt文件 
* 读取股票的价格数据至内存中。

* @author 王建超
* @version 1.0
* @see wjc.stock.bjtu.StockDayListMsn
* @see wjc.stock.bjtu.DaytimeStock
* @see wjc.stock.bjtu.StockInfo
*/
public class StockDayList {
	public static String SohuBase = "http://stock.business.sohu.com/q/hp.php?code=";
	public static String DayDir = "Day";
	//股票信息
	StockInfo stock;
	//每日价格列表
	LinkedList daylist;
	//除权信息列表
	StockBonusDispatchList dispatchlist = null;
	
	/**
	* 根据StockInfo内股票名称，创建股票每日价格对象
	* @param s：股票价格名称及代码对象
	*/ 
	public StockDayList(StockInfo s){
		stock = s;
		daylist = new LinkedList();
	}
	
	/**
     * 根据股票的每日数据（收盘价，成交量），计算出每日的成交均价，及成交均量
     * @return none
     */
	public void createAvg(){
		if(daylist.size() < 10){
			return;
		}
		
		ListIterator liter = daylist.listIterator(10);
		while(liter.hasNext()){
			((DaytimeStock)liter.next()).createAvgDay(daylist);
		}
	}
	
	public void createAvgDown(){
		if(daylist.size() < 10){
			return;
		}
//		((DaytimeStock)daylist.getFirst()).createAvgDayDown(daylist);
		ListIterator liter = daylist.listIterator(10);
		ListIterator first = daylist.listIterator();
		while(liter.hasNext()){
			((DaytimeStock)first.next()).createAvgDayDown(daylist);
			liter.next();
		}
	}
	
	public void setDayList(LinkedList l){
		if(l.get(0) instanceof DaytimeStock){
			daylist = l;
		}
	}
	//返回日期列表
	public LinkedList getDayList(){
		return daylist;
	}
	
	public int getStockLength(){
		return daylist.size();
	}
	/**
     * 提取股票最近几日数据的StockDayList对象，通过内存中已有的数据返回 
     * @param length 最近的日期数
     * @return StockDayList对象，如果没有足够该股票数据则返回空
     */
	public StockDayList getLastDayList(int length){
		if(daylist.size() < length){
			return null;
		}
		else{
			int index = daylist.size() - length;
			LinkedList l = (LinkedList)(daylist.subList(index, daylist.size()-1));
			StockDayList sdl= new StockDayList(stock);
			sdl.setDayList(l);
			return sdl;
		}
	}
	/**
     * 从Txt文件中，提取股票最近length日数据
     * @param length 最近的日期数
     * @return length 成功从文件中读取的每日数据的天数
     */
	//从后向前读取文件中记录的数据，返回实际读取行数
	public int readFromFile(int length){
		RandomAccessFile file;
		try {
			file = new RandomAccessFile(DayDir+File.separator+stock.getCode()+".day","r");
			long start = file.getFilePointer();
			System.out.println(start);
			//最后三个字包括文件结束符，回车换行符，
            long cur = start + file.length()-1;
            System.out.println(cur);
            String result = "";
            file.seek(cur);
            int c=-1;
            int i=0;
            while(cur>=start){
                c = file.read();
                if(c=='\n'||c=='\r'){
                    file.seek(cur+1);
                    cur--;
                    result = file.readLine();
//                    如果读取行为空行时
                    if(result==null){
                    	continue;
                    }
                    // 处理每行数据
//                    System.out.println(result);
                    String[] s=result.split(" ");
                    DaytimeStock daytime = new DaytimeStock(s);
                    daylist.addFirst(daytime);
                    //
                    i++;
                    if(i>=length){
                    	file.close();
                    	return length;
                    }
                }
                cur--;
                file.seek(cur);
            }
            file.close();
			return i;
            
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println(stock);
			return 0;
		}
	}
	/**
     * 从Txt文件中，提取date1至date2日股票数据,包括date1和date2
     * @param date1 开始日期
     * @param date2 结束日期
     * @return int 成功从文件中读取的每日数据的天数,如果date1大于date2则返回0
     */
	//读取date1,date2之间的股票数据
	public int readFromFile(String date1,String date2){
		BufferedReader r; 
		try {
			
			if(cmpDay(date1,date2)>=0){
				return 0;
			}
			r = new BufferedReader(new FileReader(DayDir+File.separator+stock.getCode()+".day"));
			String line = r.readLine();
			line = r.readLine();
			String[] array = line.split(" ");
			int i = 0;
			//如果要读取的日期在记录开始前，返回错误。
			if(cmpDay(date1,array[0])==-1){
				r.close();
				return 0;
			}
			
			
			//当读入行的日期在开始日期前时，继续向下读入数据
			while(cmpDay(date1,array[0])==1){
				line = r.readLine();
				array = line.split(" ");
			}
			
			while(cmpDay(date2,array[0])>=0){
				DaytimeStock daytime = new DaytimeStock(array);
				daylist.addLast(daytime);
				i++;
				line = r.readLine();
				if(line == null){
					break;
				}
				System.out.println(line);
				array = line.split(" ");
				
			}
			r.close();
			return i;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	public float getRange(int start, int end){
		DaytimeStock day = (DaytimeStock)(daylist.get(end));
		return day.getRange((DaytimeStock)(daylist.get(start)));
	}
	/**
     * 从Txt文件中，在现有内存中股票数据的基础上增加一定的股票数据量
     * @param num 增加的天数
     * @return int 成功从文件中增加的每日数据的天数
     */
	//再次读入num行数据
	public int addStockLine(int num){
		if(daylist == null){
			return 0;
		}
		DaytimeStock daytime = (DaytimeStock)(daylist.getLast());
		String lastday = daytime.getDay();
		
		int i = 0;
		
		BufferedReader r; 
		try {
			
			
			r = new BufferedReader(new FileReader(DayDir+File.separator+stock.getCode()+".day"));
			String line = r.readLine();
			line = r.readLine();
			String[] array = line.split(" ");
		
			//当读入行的日期在开始日期前时，继续向下读入数据
			while(cmpDay(lastday,array[0]) >= 0){
				line = r.readLine();
				array = line.split(" ");
			}
			
			while(i < num){
				DaytimeStock day = new DaytimeStock(array);
				daylist.addLast(day);
				i++;
				line = r.readLine();
				if(line == null){
					break;
				}
				array = line.split(" ");
				
			}
			r.close();
			return i;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
		
	}
	
	public StockInfo getStock(){
		return stock;
	}
	
	public boolean downloadFromSohu(){
		daylist.clear();
		String u=SohuBase+stock.getCode();
		try {
			URL url = new URL(u);
			Html h = new Html(url);
			boolean b = h.download();
			if(b) {
				String content= HtmlParse.getContent(h.getContent(), 0, "table", "开盘");
				if(content == null)
					return false;
//				System.out.println(content);
				int index=0;
				String line = HtmlParse.getContent(content, index, "tr");
				index = index+line.length();
				line = HtmlParse.getContent(content, index, "tr");
				Vector v;
				while(line != null){
					
					//处理每行获得的数据，在line中	
					v=HtmlParse.parseTR(line);
					index = content.indexOf((String)v.get(0));
//					System.out.println(v);
			//		String[] array= v.toArray();
					DaytimeStock daytimestock = new DaytimeStock(v);
//					System.out.println(daytimestock);
					daylist.addLast(daytimestock);
//					System.out.println(line);
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
	//如果日期数据不存在，则建立文件夹并存储
	public boolean save() throws IOException{
		File dir = new File(DayDir);
		if(!(dir.exists())){
			System.out.println("not exist");
			dir.mkdir();
		}
		File file = new File(DayDir+File.separator+stock.getCode()+".day");
	/*	if(file.exists()){
			System.out.println(stock.toString()+"exist");
			return false;
		}*/
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		writer.println(stock.toString());
		int index = 0;
		while(index < daylist.size()){
			writer.println(((DaytimeStock)daylist.get(index)).format());
			index++;
		}
		writer.close();
		return true;
	}
	//更新文件数据
	public int update(){
//		从文件读取最后一行的数据
		int i=readFromFile(1);
		if(i == 0)
			return 0;
		String lastDay=((DaytimeStock)(daylist.getFirst())).getDay();
		System.out.println("lastDay:"+lastDay);
//		下载网络数据
		boolean b = downloadFromSohu();
		if(b){
			int index = daylist.size()-1;
//			System.out.println("index:"+index);
			String day=((DaytimeStock)(daylist.get(index))).getDay();
//			System.out.println(this.toString());
//			System.out.println("day:"+day);
			while(cmpDay(day,lastDay)==1){
				index--;
				if(index < 0)
					break;
				day=((DaytimeStock)(daylist.get(index))).getDay();
			}
			index++;
			int writelines = daylist.size()-index;
			PrintWriter writer;
			try{
				writer = new PrintWriter(new FileWriter(new File(DayDir+File.separator+stock.getCode()+".day"),true));
				while(index < daylist.size()){
					writer.println(((DaytimeStock)daylist.get(index)).format());
//					System.out.println(((DaytimeStock)daylist.get(index)).format());
					index++;
				}
				writer.close();
				return writelines;
			}catch(IOException e){
				e.printStackTrace();
				return 0;
			}
		}
		return 0;
		
	}
	//比较日期day1,day2;如果day1在day2后返回1，如果day1等于day2返回0，如果day1在day2前返回-1,其余返回为错误
	public static int cmpDay(String day1,String day2){	
		int day1_Y=Integer.parseInt(day1.substring(0,4));
		int day1_M=Integer.parseInt(day1.substring(5,7));
		int day1_D=Integer.parseInt(day1.substring(8,10));
		int day2_Y=Integer.parseInt(day2.substring(0,4));
		int day2_M=Integer.parseInt(day2.substring(5,7));
		int day2_D=Integer.parseInt(day2.substring(8,10));
		if(day1_Y > day2_Y){
			return 1;
		}else if(day1_Y < day2_Y){
			return -1;
		}else if(day1_M > day2_M){
			return 1;
		}else if(day1_M < day2_M){
			return -1;
		}else if(day1_D > day2_D){
			return 1;
		}else if(day1_D < day2_D){
			return -1;
		}else{
			return 0;
		}
	}
	
	public void RefreshData(){
		File file = new File(DayDir+File.separator+stock.getCode()+".day");
		if(file.exists()){
			update();
		}else{
			boolean b=downloadFromSohu();
			if(b){
				try{
					save();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer(stock.toString());
		Iterator iter = daylist.iterator();
		while(iter.hasNext()){
			buffer.append("\n");
			buffer.append(iter.next());
		}
		return buffer.toString();
	}
	//创建相对强弱指标
	public void createReferenceStrength(){
		if(daylist.size() < Constant.compareAvg + Constant.compareDay){
			return;
		}
//		((DaytimeStock)daylist.getFirst()).createAvgDayDown(daylist);
		boolean result= true;
		ListIterator first = daylist.listIterator();
		while(first.hasNext() && result){
			result = ((DaytimeStock)first.next()).createReferenceStrenth(daylist);
		}
	}
	
	public static void main(String[] args) {
		StockInfo s = new StockInfo("600001","邯郸钢铁");
		StockDayList list = new StockDayList(s);
		list.readFromFile("20080613","20080625");
//		list.readFromFile(20);
//		list.createAvg();
		System.out.println(list);
		System.out.println("add one");
		int i = list.addStockLine(10);
		System.out.println(list);
		System.out.println(i);
//		list.downloadFromSohu();
//		list.RefreshData();
		System.out.println("update:");
//		 TODO Auto-generated method stub
/*		StockReader reader = new StockReader("shanghai");
		if(!(reader.open())){
			System.out.println("read error");
			return;
		}
		int size=reader.getSize();
		StockDayList list;
		System.out.println("total:"+reader.getSize());
		int i=0;
		Iterator shcode = reader.getCode().iterator();
		while(shcode.hasNext()){
			String code = (String)shcode.next();
			list =new StockDayList(new StockInfo(code,reader.codeToName(code)));
			list.readFromFile(10);
		
			System.out.println(list.toString());
			i++;
			System.out.println("read:"+((float)i/size));
			try {
				list.save();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}*/
	
	}
}
