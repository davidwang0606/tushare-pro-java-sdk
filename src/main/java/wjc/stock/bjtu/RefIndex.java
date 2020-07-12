package wjc.stock.bjtu;

import java.util.HashMap;
import java.util.LinkedList;

import finance.stock.bjtu.Constant.CompositeIndex;

public class RefIndex {
	private HashMap hmap = new HashMap();
	private CompositeIndex index = null;
	private int datenum = 0;
	public RefIndex(CompositeIndex i, int num){
		if(num <= 0){
			return;
		}
		index = i;
		StockInfo s = null;
		if(i == CompositeIndex.Shangzheng){
			s = new StockInfo("sh000001", "上证指数");
			StockDayListMsn list = new StockDayListMsn(s);
			int readnum = list.readFromFile(num);
			if(readnum<num){
				num = readnum;
			}
			list.createReferenceStrength();
			LinkedList l = list.getDayList();
			for(int n=0;n<num;n++){
				DaytimeStock d = (DaytimeStock)(l.get(n));
				String str = d.getDay();
				hmap.put(str, d);
			}
		}else if (i== CompositeIndex.Shenzheng){
			s = new StockInfo("sz399001", "深证成指");
			StockDayListMsn list = new StockDayListMsn(s);
			int readnum = list.readFromFile(num);
			if(readnum<num){
				num = readnum;
			}
			list.createReferenceStrength();
			LinkedList l = list.getDayList();
			for(int n=0;n<num;n++){
				DaytimeStock d = (DaytimeStock)(l.get(n));
				String str = d.getDay();
				hmap.put(str, d);
			}
		}else if (i== CompositeIndex.Zhonghe){
			s = new StockInfo("sh000300", "沪深300");
			StockDayListMsn list = new StockDayListMsn(s);
			int readnum = list.readFromFile(num);
			if(readnum<num){
				num = readnum;
			}
			list.createReferenceStrength();
			LinkedList l = list.getDayList();
			for(int n=0;n<num;n++){
				DaytimeStock d = (DaytimeStock)(l.get(n));
				String str = d.getDay();
				hmap.put(str, d);
			}
		}
	}
	
	public float getRef(String date){
		DaytimeStock d = (DaytimeStock)(hmap.get(date));
		if(d == null){
			return Float.MAX_VALUE;
		}
		return d.getClosePrice();
	}
	public float getRef(DateInfo date){
		DaytimeStock d = (DaytimeStock)(hmap.get(date.toString()));
		if(d == null){
			return Float.MAX_VALUE;
		}
		return d.getClosePrice();
		
	}
	public float getPer(String start,String end){
		DaytimeStock dstart = (DaytimeStock)(hmap.get(start));
		DaytimeStock dend = (DaytimeStock)(hmap.get(end));
		if(dstart == null || dend == null){
			return Float.MAX_VALUE;
		}
		return (dend.getClosePrice()-dstart.getClosePrice())/dstart.getClosePrice();
	}
	public DaytimeStock getRefDay(String date)
	{
		DaytimeStock d = (DaytimeStock)(hmap.get(date));
		if(d == null){
			return null;
		}
		return d;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RefIndex r = new RefIndex(CompositeIndex.Shangzheng,10);
		System.out.println(r.getRef("2012-12-22"));
	}

}
