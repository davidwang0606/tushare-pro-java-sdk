/**             
* ����ʱ�䣺 2010-02-18                                
*/ 
package wjc.stock.bjtu;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.io.*;

import finance.stock.bjtu.Constant;
/**
* ͨ��sohu��վ������һ����Ʊ��ÿ�ռ۸�������txt�ļ��ڲ������ṩ�ӿڿ��Դ�txt�ļ� 
* ��ȡ��Ʊ�ļ۸��������ڴ��С�

* @author ������
* @version 1.0
* @see wjc.stock.bjtu.StockDayListMsn
* @see wjc.stock.bjtu.DaytimeStock
* @see wjc.stock.bjtu.StockInfo
*/
public class StockDayList {
	public static String SohuBase = "http://stock.business.sohu.com/q/hp.php?code=";
	public static String DayDir = "Day";
	//��Ʊ��Ϣ
	StockInfo stock;
	//ÿ�ռ۸��б�
	LinkedList daylist;
	//��Ȩ��Ϣ�б�
	StockBonusDispatchList dispatchlist = null;
	
	/**
	* ����StockInfo�ڹ�Ʊ���ƣ�������Ʊÿ�ռ۸����
	* @param s����Ʊ�۸����Ƽ��������
	*/ 
	public StockDayList(StockInfo s){
		stock = s;
		daylist = new LinkedList();
	}
	
	/**
     * ���ݹ�Ʊ��ÿ�����ݣ����̼ۣ��ɽ������������ÿ�յĳɽ����ۣ����ɽ�����
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
	//���������б�
	public LinkedList getDayList(){
		return daylist;
	}
	
	public int getStockLength(){
		return daylist.size();
	}
	/**
     * ��ȡ��Ʊ����������ݵ�StockDayList����ͨ���ڴ������е����ݷ��� 
     * @param length �����������
     * @return StockDayList�������û���㹻�ù�Ʊ�����򷵻ؿ�
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
     * ��Txt�ļ��У���ȡ��Ʊ���length������
     * @param length �����������
     * @return length �ɹ����ļ��ж�ȡ��ÿ�����ݵ�����
     */
	//�Ӻ���ǰ��ȡ�ļ��м�¼�����ݣ�����ʵ�ʶ�ȡ����
	public int readFromFile(int length){
		RandomAccessFile file;
		try {
			file = new RandomAccessFile(DayDir+File.separator+stock.getCode()+".day","r");
			long start = file.getFilePointer();
			System.out.println(start);
			//��������ְ����ļ����������س����з���
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
//                    �����ȡ��Ϊ����ʱ
                    if(result==null){
                    	continue;
                    }
                    // ����ÿ������
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
     * ��Txt�ļ��У���ȡdate1��date2�չ�Ʊ����,����date1��date2
     * @param date1 ��ʼ����
     * @param date2 ��������
     * @return int �ɹ����ļ��ж�ȡ��ÿ�����ݵ�����,���date1����date2�򷵻�0
     */
	//��ȡdate1,date2֮��Ĺ�Ʊ����
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
			//���Ҫ��ȡ�������ڼ�¼��ʼǰ�����ش���
			if(cmpDay(date1,array[0])==-1){
				r.close();
				return 0;
			}
			
			
			//�������е������ڿ�ʼ����ǰʱ���������¶�������
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
     * ��Txt�ļ��У��������ڴ��й�Ʊ���ݵĻ���������һ���Ĺ�Ʊ������
     * @param num ���ӵ�����
     * @return int �ɹ����ļ������ӵ�ÿ�����ݵ�����
     */
	//�ٴζ���num������
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
		
			//�������е������ڿ�ʼ����ǰʱ���������¶�������
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
				String content= HtmlParse.getContent(h.getContent(), 0, "table", "����");
				if(content == null)
					return false;
//				System.out.println(content);
				int index=0;
				String line = HtmlParse.getContent(content, index, "tr");
				index = index+line.length();
				line = HtmlParse.getContent(content, index, "tr");
				Vector v;
				while(line != null){
					
					//����ÿ�л�õ����ݣ���line��	
					v=HtmlParse.parseTR(line);
					index = content.indexOf((String)v.get(0));
//					System.out.println(v);
			//		String[] array= v.toArray();
					DaytimeStock daytimestock = new DaytimeStock(v);
//					System.out.println(daytimestock);
					daylist.addLast(daytimestock);
//					System.out.println(line);
					//ÿ�д������
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
	//����������ݲ����ڣ������ļ��в��洢
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
	//�����ļ�����
	public int update(){
//		���ļ���ȡ���һ�е�����
		int i=readFromFile(1);
		if(i == 0)
			return 0;
		String lastDay=((DaytimeStock)(daylist.getFirst())).getDay();
		System.out.println("lastDay:"+lastDay);
//		������������
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
	//�Ƚ�����day1,day2;���day1��day2�󷵻�1�����day1����day2����0�����day1��day2ǰ����-1,���෵��Ϊ����
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
	//�������ǿ��ָ��
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
		StockInfo s = new StockInfo("600001","��������");
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
