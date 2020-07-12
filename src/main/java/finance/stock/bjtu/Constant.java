package finance.stock.bjtu;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wjc.stock.bjtu.DaytimeStock;
import wjc.stock.bjtu.Html;
import wjc.stock.bjtu.StockDayListMsn;
import wjc.stock.bjtu.StockInfo;

public class Constant {
	public static float FLOATINVALID = Float.NaN;
	public static String INVALIDNUM = "--";
	public static String STRNULL = "";
	//配置复权的全局参数，如果参数为1读取价格全部为后复权，如果参数为0则读取时不复权
	public static int restoration = 1;
	//设置相对强弱时采用的对比天数
	public static int compareDay = 21;
	public static int compareAvg = 7;
	//分时交易数据库
	public static String DB_STOCKTRADE = "stock_trade";
	
	public static enum CompositeIndex {  
		     Shangzheng,Shenzheng,Zhonghe  
		 }; 
	public static float getNum(String word) {
		if(word.equals(INVALIDNUM) == false && word.equals(STRNULL) == false && word.equals("*****")==false) {
			word= word.replace(",", "");
			return  Float.parseFloat(word);
		}else
			return FLOATINVALID;
	}
	public static float getNumWithChi(String word) {
		if(word.equals(INVALIDNUM) == false && word.equals(STRNULL) == false && word.equals("*****")==false) {
			word= word.replace(",", "");
			if(word.indexOf("万亿")!=-1){
				return Float.parseFloat(word.replaceAll("万亿", ""))*1000000000000f;
			}
			else if(word.indexOf("万")!=-1)
			{
				return Float.parseFloat(word.replaceAll("万", ""))*10000f;
			}else if(word.indexOf("亿")!=-1)
			{
				return Float.parseFloat(word.replaceAll("亿", ""))*100000000f;
			}
			return  Float.parseFloat(word);
		}else
			return FLOATINVALID;
	}
	public static void printVector(Vector[] v){
		if(v == null){
			return;
		}
		int i;
		for(i=0;i<v.length;i++){
			System.out.println(v[i]);
		}
	}
	
	public static StockDayListMsn getCompositeIndex(CompositeIndex c,int n){
		if(n<0){
			System.out.println("getCompositeIndex:"+"n<0");
			return null;
		}
		StockDayListMsn compositeIndexList = null;
		if(c == CompositeIndex.Shangzheng){
			compositeIndexList = new StockDayListMsn(new StockInfo("sh000001","上证指数"));
		}else if(c == CompositeIndex.Shenzheng){
			compositeIndexList = new StockDayListMsn(new StockInfo("sz399001","深证成指"));
		}else if(c == CompositeIndex.Zhonghe){
			return null;
		}
		
		if(n>0)
			compositeIndexList.readFromFile(n);
		
		return compositeIndexList;
	}
	
	public static StockDayListMsn getCompositeIndex(CompositeIndex c,String startDate,String endDate){
		StockDayListMsn compositeIndexList = null;
		if(c == CompositeIndex.Shangzheng){
			compositeIndexList = new StockDayListMsn(new StockInfo("sh000001","上证指数"));
		}else if(c == CompositeIndex.Shenzheng){
			compositeIndexList = new StockDayListMsn(new StockInfo("sz399001","深证成指"));
		}else if(c == CompositeIndex.Zhonghe){
			return null;
		}
		compositeIndexList.readFromFile(startDate, endDate);
		return compositeIndexList;
		
	}
	
	public static boolean isLast5LowestIn30(StockDayListMsn list,int index,int low,int total){
		if(list == null || list.getDayList().size()<=index|| index <0) {
			return false;
		}
		int l = list.getDayList().size()-1;
		if(l-index<=low) {
			return true;
		}
		float lowest = ((DaytimeStock)(list.getDayList().get(index))).getLowestPrice();
		for (int d=index+1;d<index+low;d++){
			if(lowest>((DaytimeStock)(list.getDayList().get(d))).getLowestPrice()){
				lowest = ((DaytimeStock)(list.getDayList().get(d))).getLowestPrice();
			}
		}
		
		for (int d = index+low;d<l&&d<index+total;d++){
			if(lowest>((DaytimeStock)(list.getDayList().get(d))).getLowestPrice()){
				return false;
			}
		}
		
		return true;
	}
	//获取日期列表
	public static String[] getTransactionDate(String startDate,String endDate){
		StockDayListMsn stocklist = getCompositeIndex(CompositeIndex.Shangzheng,startDate,endDate);
		if(stocklist ==null)
			return null;
		int number = stocklist.getDayList().size();
//		System.out.println(number);
		if(number == 0)
			return null;
		String[] daylist = new String[number];
		LinkedList list = stocklist.getDayList();
		int last = number-1;
		for(int n = 0;n<number;n++,last--){
			daylist[n]= ((DaytimeStock)list.get(last)).getDay();
		}
		return daylist;
	}
	//获取首个为空的日期列表
	public static String[] getTransactionDateFirstNull(String startDate,String endDate){
		String[] daylist = getTransactionDate(startDate,endDate);
		int number = daylist.length;
		String[] daylistnew = new String[number+1];
		daylistnew[0] = "";
		for(int n=1;n<=number;n++){
			daylistnew[n] = daylist[n-1];
		}
		return daylistnew;
	}
	//通过判断股价最高点/最低点持续天数，获取个股的高低点价格列表
	static public StockDayListMsn getStageExtremunList(StockDayListMsn stocklist,int step){
		if(stocklist == null || step <= 0){
			System.out.println("TechinalMethod:getStageExtremunList input error");
			return null;
		}
		LinkedList link = stocklist.getDayList();
		if(link == null || link.size()<=0){
			System.out.println("TechinalMethod.getStageExtremunList.list:null");
			return null;
		}
		int num = link.size();
		StockDayListMsn s = stocklist.getStockDayListWithNull();
		//status为查找点位阶段:0为混沌模式，1为查找高点模式，2为查找低点模式
		int status = 0;
		int index = num - 1;
		//持续高点位持续时间
		int HpersistDay = 0;
		int LpersistDay = 0;
		float high = ((DaytimeStock)(link.get(index))).getHighestPrice();
		float low = ((DaytimeStock)(link.get(index))).getLowestPrice();
		for(int n=index-1; n>=0; n--){
			DaytimeStock d = (DaytimeStock)(link.get(n));
			//初次混沌模式下查找
			if(status == 0){
				float tempHigh = d.getHighestPrice();
				float tempLow = d.getLowestPrice();
				//如果当日高点高于阶段高点，则设定新高点，否则高点持续时间加1
				if(tempHigh > high){
					high = tempHigh;
					HpersistDay = 0;
				}else{
					HpersistDay++;
				}
				//如果当日低点低于阶段低点，则设定新低点，否则低点持续时间加1
				if(tempLow < low){
					low = tempLow;
					LpersistDay = 0;
				}else{
					LpersistDay++;
				}
				//如果高点持续时间等于给定值，且不等于低点持续时间，则进入低点查询模式
				if(HpersistDay>=step && HpersistDay!=LpersistDay){
					status = 2;
					continue;
				}
				else if (LpersistDay>=step && HpersistDay!=LpersistDay){
					status = 1;
					continue;
				}
			}
			//1为查找高点模式
			if(status == 1){
				float tempHigh = d.getHighestPrice();
				if(tempHigh > high){
					high = tempHigh;
					HpersistDay = 0;
				}else{
					HpersistDay++;
				}
				if(HpersistDay>=step){
					DaytimeStock dhigh = (DaytimeStock)(link.get(n+HpersistDay));
					dhigh.setComment("High");
					s.insertFirst(dhigh);
//					System.out.println(dhigh);
					//HpersistDay = 0;
					status = 2;
					LpersistDay = 0;
					n = n + HpersistDay-1;
					low = ((DaytimeStock)(link.get(n))).getLowestPrice();
					HpersistDay = 0;
					continue;
				}
			}
			//2为查找低点模式
			if(status == 2){
				float tempLow = d.getLowestPrice();
				if(tempLow < low){
					low = tempLow;
					LpersistDay = 0;
				}else{
					LpersistDay++;
				}
				if(LpersistDay>=step){
					DaytimeStock dlow = (DaytimeStock)(link.get(n+LpersistDay));
					dlow.setComment("Low");
					s.insertFirst(dlow);
//					System.out.println(dlow);
					//HpersistDay = 0;
					status = 1;
					HpersistDay = 0;
					n = n + LpersistDay-1;
					high = ((DaytimeStock)(link.get(n))).getHighestPrice();
					LpersistDay = 0;
					continue;
				}
			}
		}
		return s;
		
	}
	
	//获取最近高低点个数
	static public StockDayListMsn getLatestExtremunList(StockDayListMsn stocklist,int getnum,String kind){
		LinkedList link = stocklist.getDayList();
		if(link == null || link.size()<=0){
			System.out.println("TechinalMethod.getStageExtremunList.list:null");
			return null;
		}
		int num = link.size();
		StockDayListMsn s = stocklist.getStockDayListWithNull();
		if((num < getnum*2-1)||(num==getnum*2-1&&(((DaytimeStock)(stocklist.getDayList().get(0))).getComment()!=null)&&(((DaytimeStock)(stocklist.getDayList().get(0))).getComment().equals(kind))==false)){
			System.out.println("getLatestExtremunList:false");
			return null;
		}
		int start;
		if(((DaytimeStock)(stocklist.getDayList().get(0))).getComment().equals(kind)){
			start = 0;
		}else
			start = 1;
		
		for(int n=0; n<getnum; n++){
			s.insertLast((DaytimeStock)(stocklist.getDayList().get(start)));
			start = start+2;
		}
		return s;
	}
	
	
	public static String[] AllPlateList = {
	    "券商信托",
		"汽车行业",
		"材料行业",
		"造纸印刷",
		"医药行业",
		"塑胶制品",
		"交运设备",
		"纺织服装",
		"航天航空",
		"电子信息",
		"电子元件",
		"仪器仪表",
		"通讯行业",
		"工艺商品",
		"综合行业",
		"文化传媒",
		"民航机场",
		"玻璃陶瓷",
		"国际贸易",
		"电力行业",
		"食品行业",
		"化工行业",
		"房地产",
		"公用事业",
		"商业百货",
		"工程建设",
		"有色金属",
		"输配电气",
		"水泥建材",
		"化纤行业",
		"家电行业",
		"机械行业",
		"酿酒行业",
		"农牧饲渔",
		"钢铁行业",
		"木业家具",
		"保险",
		"石油行业",
		"港口水运",
		"银行",
		"高速公路",
		"煤炭采选",
		"旅游酒店",
		"交运物流",
   };
	public static String[] AllPlateListNum = {
	    "24", //"券商信托",
		"79", //"汽车行业",
		"78", //"材料行业",
		"33", //"造纸印刷",
		"178",//"医药行业",
		"43", //"塑胶制品",
		"26", //"交运设备",
		"79", //"纺织服装",
		"11", //"航天航空",
		"135",//"电子信息",
		"132", //"电子元件",
		"36", //"仪器仪表",
		"73", //"通讯行业",
		"18", //"工艺商品",
		"37", //"综合行业",
		"38", //"文化传媒",
		"9" , //"民航机场",
		"26", //"玻璃陶瓷",
		"20", //"国际贸易",
		"56", //"电力行业",
		"50", //"食品行业",
		"158", //"化工行业",
		"144",//"房地产",
		"32", //"公用事业",
		"76", //"商业百货",
		"52", //"工程建设",
		"71", //"有色金属",
		"89", //"输配电气",
		"56", //"水泥建材",
		"28", //"化纤行业",
		"37", //"家电行业",
		"202",//"机械行业",
		"31", //"酿酒行业",
		"55", //"农牧饲渔",
		"40", //"钢铁行业",
		"19", //"木业家具",
		"4",//"保险",
		"24", //"石油行业",
		"31", //"港口水运",
		"16", //"银行",
		"18", //"高速公路",
		"37", //"煤炭采选",
		"32", //"旅游酒店",
		"29", //"交运物流",
   };
   public static String[][] getPlateDayArray(String startDate,String endDate){
	   String[] daylist = getTransactionDate(startDate,endDate);
	   int row = AllPlateList.length+1;
	   int column = daylist.length+2;
	   String[][] plateArray = new String[row][column];
	   for(int i = 0;i<daylist.length;i++){
		   plateArray[0][i] = daylist[i];
	   }
	   plateArray[0][column-2] = "板块类型";
	   plateArray[0][column-1] = "板块股个数";
	   for ( int n = 1;n<=row-1;n++){
		   plateArray[n][column-2] = AllPlateList[n-1];
		   plateArray[n][column-1] = AllPlateListNum[n-1];
	   }
//	   System.out.println(row);
//	   System.out.println(column);
//	   Constant.printArray(plateArray);
	   return plateArray;
   }

   public static String[][] getPlateDayArray(int n){
	   if(n<=0){
		   System.out.println("getPlateDayArray n<=0" );
		   return null;
	   }
	   StockDayListMsn compositeIndexList = getCompositeIndex(CompositeIndex.Shangzheng,n);
	   String dayInfoEnd = ((DaytimeStock)(compositeIndexList.getDayList().get(0))).getDay();
//	   System.out.println(dayInfoEnd);
	   String dayInfo = ((DaytimeStock)(compositeIndexList.getDayList().get(n-1))).getDay();
//	   System.out.println(dayInfo);
	   String[][] lastArray = getPlateDayArray(dayInfo,dayInfoEnd);
	   return lastArray;
   }
	
   public static void printArray(String[][] array){
	   for(int i = 0;i<array.length;i++){
		   String s = "";
		   for(int j=0; j<array[0].length;j++){
			   s = s + array[i][j]+" ";
		   }
		   System.out.println(s);
	   }
   }
   
   public static void printArray(float[][] array){
	   for(int i = 0;i<array.length;i++){
		   String s = "";
		   for(int j=0; j<array[0].length;j++){
			   s = s + array[i][j]+" ";
		   }
		   System.out.println(s);
	   }
   }
   
   public static String isFitDisturb(StockDayListMsn stocklist,int index,int step,int maxlength){
	   if(stocklist == null || index >= stocklist.getStockLength()){
		   System.out.println("isFitDisturb : error");
		   return null;
	   }
	   float curPrice = ((DaytimeStock)(stocklist.getDayList().get(index))).getClosePrice();
	   float highestPrice = curPrice*1.25f;
	   float lowestPrice = curPrice*0.75f;
	   int length = stocklist.getStockLength();
	   index = index + step;
	   
	   while(index <= maxlength && index <length){
		   DaytimeStock d = (DaytimeStock)(stocklist.getDayList().get(index));
		   if(d.getHighestPrice()>highestPrice || d.getLowestPrice()<lowestPrice){
			   return null;
		   }
		   index = index + step;
	   }
	   	   
	   return new String("OK");
   }
   
   public static String getEastMoneyNews(StockInfo stock,int value)
   {
	   String StockCode = stock.getCode();
	   String[] array = new String[value];
	   String cq = new String("http://guba.eastmoney.com/list,"+StockCode+",1,f.html");
	   int i = 0;
	   try {
			URL u = new URL(cq);
			Html h = new Html(u);
			boolean b = h.download("UTF-8");
			if(b) {
				Pattern p = Pattern.compile("<span .*最后更新</span>");
				
				Matcher m = p.matcher(h.getContent());

				m.find();
				Pattern pValue = Pattern.compile("<span .*</span>");
				
				Matcher mm = pValue.matcher(h.getContent());
				
				mm.find(m.end());
			//	System.out.println("**********************************************");
				while(mm.find() && value >0){
				//	System.out.println(mm.group());
					array[i] =getEastmoneyNewsAndDate(mm.group());
				//	System.out.println(array[i]);
					i++;
					value --;
										
				}
			}
		} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	   
		String s = new String();
		for(int j=0;j<i;j++)
		{
			s = s+array[j]+" ";
		}
		
	   return s;
   }
   
   public static String getEastmoneyFlowCapital(StockInfo stock)
   {
	   String StockCode = stock.getCode();
	   
	   char first = StockCode.charAt(0);
	   String Base = "http://f10.eastmoney.com/f10_v2/OperationsRequired.aspx?code=";
		String cq="";
		if(first=='6'){
			cq = Base+"sh"+stock.getCode();
		} else if(first == '0'){
			cq = Base+"sz"+stock.getCode();
		}else if(first == '3'){
			cq = Base+"sz"+stock.getCode();
		}
		else {
			cq = Base+stock.getCode();
		}
	   
	   float n = 0.0f;
	   try {
			URL u = new URL(cq);
			Html h = new Html(u);
			boolean b = h.download("UTF-8");
		//	System.out.println(h.getContent());
			if(b) {
				Pattern p = Pattern.compile("<span>流通股本.*</span>");
				
				Matcher m = p.matcher(h.getContent());

				boolean bl = m.find();
				if(bl)
					System.out.println(m.group());
				else {
					System.out.println("can not find group");
					return null;
				}
				String flowStock = m.group();
				//System.out.println(flowStock);
				Pattern pp = Pattern.compile("[0-9|,|.]*[0-9|,|.]");
				
				Matcher mm = pp.matcher(flowStock);
				
				mm.find();
				
				System.out.println(mm.group());
				
				n = getNum(mm.group());
				
//				Pattern pValue = Pattern.compile("<span .*</span>");
//				
//				Matcher mm = pValue.matcher(h.getContent());
//				
//				mm.find(m.end());

			}
		} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	   
		String s = Float.toString(n);

		System.out.println(s);
				
	   return s;
   }
   
   public static String getEastmoneyNewsAndDate(String s)
	{	
	   if(s == null)
	   {
		   return null;
	   }
	   int titleStart = s.indexOf("title=");
	   int titleEnd = s.indexOf(" >", titleStart);
	   if(titleStart == -1 || titleEnd == -1 )
		   return "";
	   String news = s.substring(titleStart+7, titleEnd-1);
	   
	   int dateStart = s.indexOf("<span class=\"l6\">");
	   int dateEnd = s.indexOf("</span>",dateStart);
	   if(dateStart == -1 || dateEnd == -1 )
		   return news;
	   news = s.substring(dateStart+17,dateEnd) +" " + news;
	   
	   return news;
	}
	
	public static void main(String[] args){
//		StockDayListMsn a = getCompositeIndex(CompositeIndex.Shangzheng,"2012-12-31","2013-03-01");
//		System.out.println(a);
//		String[] tranDate = getTransactionDate("2012-12-31","2013-03-01");
		
//		String[][] dayArray = getPlateDayArray("2013-02-20","2013-03-01");
//		printArray(dayArray);
//		System.out.println(dayArray[0][dayArray[1].length-1]);
//		for(int n=0;n<tranDate.length;n++)
//			System.out.println(tranDate[n]);
		StockInfo stock = new StockInfo("000498","山东路桥");
		Constant.getEastmoneyFlowCapital(stock);
//		StockDayListMsn list = new StockDayListMsn(stock);
//	    int i =list.readFromFile(130);
//	    StockDayListMsn ss = getStageExtremunList(list,5);
//	    System.out.println(ss);
//	    System.out.println("end");
//	    StockDayListMsn hss = getLatestExtremunList(ss,5,"Low");
//	    System.out.println(hss);
	}
}
