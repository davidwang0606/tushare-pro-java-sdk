package wjc.stock.bjtu;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import finance.stock.bjtu.Constant;
import com.github.tusharepro.core.entity.DailyEntity;

//记录某一股票当日数据
public class DaytimeStock {
	public static int JUMP_UP = 1;//向上跳空
	public static int JUMP_DOWN = 2;//向下跳空
	public static int JUMP_EQUAL = 3;//平开
	
	protected String day;           //日期
	protected float openPrice;      //开盘价
	protected float highestPrice;   //最高价
	protected float lowestPrice;    //最低价
	protected float closePrice;     //收盘价 
	protected float handoverMount;  //成交量
	protected float handoverPrice;  //成家总金额
	protected float avgHandoverPrice; //平均成交价
	protected AverageDay avgDay = null;//均线
	protected float referenceStrenth = Float.NaN;
	
	public float getReferenceStrenth() {
		return referenceStrenth;
	}

	//扩展参数
	private String comment = null;
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public DaytimeStock(DailyEntity entity){
		LocalDate ldate = entity.getTradeDate();
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.day = dtf2.format(ldate);
		//this.day = entity.getTradeDate();
		this.openPrice = entity.getOpen().floatValue();
		this.highestPrice = entity.getHigh().floatValue();
		this.lowestPrice = entity.getLow().floatValue();
		this.closePrice = entity.getClose().floatValue();
		this.handoverMount = entity.getVol().floatValue();
		this.handoverPrice = entity.getAmount().floatValue();
		this.avgHandoverPrice = handoverPrice*10/handoverMount;
	}
	
	public DaytimeStock(String day, float openPrice, float highestPrice, float lowestPrice, float closePrice, float handoverMount, float handoverPrice) {
//		super();
		this.day = day;
		this.openPrice = openPrice;
		this.highestPrice = highestPrice;
		this.lowestPrice = lowestPrice;
		this.closePrice = closePrice;
		this.handoverMount = handoverMount;
		this.handoverPrice = handoverPrice;
		this.avgHandoverPrice = handoverPrice/handoverMount;
	}
	public DaytimeStock(String[] daylist){
		this.day=daylist[0];
		this.openPrice = Float.parseFloat(daylist[1]);
		this.highestPrice = Float.parseFloat(daylist[2]);
		this.lowestPrice = Float.parseFloat(daylist[3]);
		this.closePrice = Float.parseFloat(daylist[4]);
		daylist[5] = daylist[5].replace(",", "");
		this.handoverMount = Float.parseFloat(daylist[5]);
		daylist[6] = daylist[6].replace(",", "");
		this.handoverPrice = Float.parseFloat(daylist[6]);
		this.avgHandoverPrice = handoverPrice/handoverMount;
	}
	public DaytimeStock(Vector v){
		this.day = (String)(v.get(0));
		this.openPrice = Float.parseFloat((String)v.get(1));
		this.highestPrice = Float.parseFloat((String)v.get(2));
		this.lowestPrice = Float.parseFloat((String)v.get(3));
		this.closePrice = Float.parseFloat((String)v.get(4));
		this.handoverMount = Float.parseFloat(((String)v.get(5)).replace("手", "").replace(",", ""));
		this.handoverPrice = Float.parseFloat(((String)v.get(6)).replace("万", "00").replace(",", ""));
		this.avgHandoverPrice = handoverPrice/handoverMount;
	}
	//以某一天为参照物，判断开盘的类型，包括向上跳空及向下跳空
	public int getJumpType(DaytimeStock d){
		float open = d.getOpenPrice();
		if(open > closePrice){
			return JUMP_UP;
		}else if(open < closePrice){
			return JUMP_DOWN;
		}else 
			return JUMP_EQUAL;
	}
	//判断涨跌的幅度
	public float getRange(DaytimeStock d){
		float lastclose = d.getClosePrice();	
		return (closePrice-lastclose)/lastclose;
	}
	
	public float getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(float closePrice) {
		this.closePrice = closePrice;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public float getHandoverMount() {
		return handoverMount;
	}
	public void setHandoverMount(float handoverMount) {
		this.handoverMount = handoverMount;
	}
	public float getHandoverPrice() {
		return handoverPrice;
	}
	public void setHandoverPrice(float handoverPrice) {
		this.handoverPrice = handoverPrice;
	}
	public float getHighestPrice() {
		return highestPrice;
	}
	public void setHighestPrice(float highestPrice) {
		this.highestPrice = highestPrice;
	}
	public float getLowestPrice() {
		return lowestPrice;
	}
	public void setLowestPrice(float lowestPrice) {
		this.lowestPrice = lowestPrice;
	}
	public float getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(float openPrice) {
		this.openPrice = openPrice;
	}
	
	public void setAvgDay(AverageDay avgDay){
		this.avgDay = avgDay;
	}
	public AverageDay getAvgDay(){
		return avgDay;
	}
	//通过五日前数据，十日前数据，计算出今日数据
	public boolean createAvgDay(DaytimeStock lastday,DaytimeStock b5, DaytimeStock b10){
		float a5p = lastday.getAvgDay().getAveragePrice5();  //昨日的五日均价
		float a10p = lastday.getAvgDay().getAveragePrice5();  //昨日的十日均价
		float a5m = lastday.getAvgDay().getAverageMount5();   //昨日的五日均量
		float a10m = lastday.getAvgDay().getAverageMount10();  //昨日的十日均量
		AverageDay avd = new AverageDay();
		avd.setAveragePrice5((a5p*5-b5.getClosePrice()+closePrice)/5);
		avd.setAveragePrice10((a10p*10-b10.getClosePrice()+closePrice)/10);
		avd.setAverageMount5((a5m*5-b5.getHandoverMount()+handoverMount)/5);
		avd.setAverageMount10((a10m*10-b10.getHandoverMount()+handoverMount)/10);
		avgDay = avd;
		return true;
	}
	
	public boolean createAvgDay(LinkedList list){
		if(list.size() < 10) {
			return false;
		}
		int i = list.indexOf(this);
		if(i < 10 )
			return false;
		ListIterator liter = list.listIterator(i-9);
		float sum5p = 0;
		float sum10p = 0;
		float sum5m = 0;
		float sum10m = 0;
		for(int j=0; j<5&&liter.hasNext();j++){
			Object o = liter.next();
			if(!(o instanceof DaytimeStock)){
				return false;
			}
			DaytimeStock ds = (DaytimeStock)o;
			sum10p += ds.getClosePrice();            //将前十日的成交量，成交价相加
			sum10m += ds.getHandoverMount();
			
		}
		for(int j=0; j<5&&liter.hasNext();j++){
			Object o = liter.next();
			if(!(o instanceof DaytimeStock)){
				return false;
			}
			DaytimeStock ds = (DaytimeStock)o;
			sum10p += ds.getClosePrice();            //将前十日的成交量，成交价相加
			sum10m += ds.getHandoverMount();
			sum5p += ds.getClosePrice();
			sum5m += ds.getHandoverMount();
		}	
		AverageDay avd = new AverageDay();
		avd.setAveragePrice5(sum5p/5);
		avd.setAveragePrice10(sum10p/10);
		avd.setAverageMount5(sum5m/5);
		avd.setAverageMount10(sum10m/10);
		avgDay = avd;
		return true;
	}
	
	public boolean createAvgDayDown(LinkedList list)
	{
		if(list.size() < 10) {
			return false;
		}
		int i = list.indexOf(this);
		ListIterator liter = list.listIterator(i);
		float sum5p = 0;
		float sum10p = 0;
		float sum5m = 0;
		float sum10m = 0;
		for(int j=0; j<5&&liter.hasNext();j++){
			Object o = liter.next();
			if(!(o instanceof DaytimeStock)){
				return false;
			}
			DaytimeStock ds = (DaytimeStock)o;
			sum10p += ds.getClosePrice();            //将前十日的成交量，成交价相加
			sum10m += ds.getHandoverMount();
			sum5p += ds.getClosePrice();
			sum5m += ds.getHandoverMount();
			
		}
		for(int j=0; j<5&&liter.hasNext();j++){
			Object o = liter.next();
			if(!(o instanceof DaytimeStock)){
				return false;
			}
			DaytimeStock ds = (DaytimeStock)o;
			sum10p += ds.getClosePrice();            //将前十日的成交量，成交价相加
			sum10m += ds.getHandoverMount();

		}	
		AverageDay avd = new AverageDay();
		avd.setAveragePrice5(sum5p/5);
		avd.setAveragePrice10(sum10p/10);
		avd.setAverageMount5(sum5m/5);
		avd.setAverageMount10(sum10m/10);
		avgDay = avd;
		return true;
		
	}
	//计算指数的相对强度
	public boolean createReferenceStrenth(LinkedList list)
	{
		float totalRef = 0.0f;
		
		if(list.size()< Constant.compareAvg+Constant.compareDay)
		{
			return false;
		}
		int i = list.indexOf(this);
		if(list.size()<i+Constant.compareAvg+Constant.compareDay)
		{
			return false;
		}
		for(int n = 0; n< Constant.compareAvg; n++)
		{
			Object o1 = list.get(n+i);
			Object o2 = list.get(n+i+Constant.compareDay);
			
			if(!(o1 instanceof DaytimeStock) || !(o2 instanceof DaytimeStock))
			{
				System.out.println("createReferenceStrenth Error:");
				return false;
			}
			totalRef = totalRef + ((DaytimeStock)o1).closePrice/((DaytimeStock)o2).closePrice-1;
			
		}
		
		referenceStrenth = totalRef/Constant.compareAvg*100;
		
		
		return true;
	}
	
	public String toString(){
		String s = day+" O:"+openPrice+" H:"+highestPrice+" L:"+lowestPrice+" C:"+closePrice+" Hm:"+handoverMount+" Hp:"+handoverPrice+" AvgPrice:"+avgHandoverPrice;
		if(avgDay!=null){
			s = s.toString()+" "+avgDay.toString();	
		}
		if(comment != null){
			s = s.toString()+" Comment:"+comment;
		}
			return s;
	}
	public String format(){
		return day+" "+openPrice+" "+highestPrice+" "+lowestPrice+" "+closePrice+" "+handoverMount+" "+handoverPrice;
	}
	
	public Vector toVector(){
		Vector v = new Vector();
		DecimalFormat myformat=new java.text.DecimalFormat("0.00");
		v.add(day);
		v.add(String.valueOf(openPrice));
		v.add(String.valueOf(highestPrice));
		v.add(String.valueOf(lowestPrice));
		v.add(String.valueOf(closePrice));
		v.add(String.valueOf(handoverMount));
		v.add(String.valueOf(handoverPrice));
		v.add(myformat.format(avgHandoverPrice));
		return v;
	}
	
	public void AdjustPrice(float plusPrefix,float addPrefix){
		openPrice = openPrice*plusPrefix+addPrefix;
		highestPrice = highestPrice*plusPrefix+addPrefix;
		lowestPrice = lowestPrice*plusPrefix+addPrefix;
		closePrice = closePrice*plusPrefix+addPrefix;
		avgHandoverPrice = avgHandoverPrice*plusPrefix+addPrefix;
	}
	
	public static void main(String[] args){
		StockInfo s = new StockInfo("000826","桑德环境");
		StockDayList list = new StockDayList(s);
		list.readFromFile(50);
		DaytimeStock a = (DaytimeStock)(list.getDayList().get(0));
		a.createReferenceStrenth(list.daylist);
		System.out.println(a.referenceStrenth);
	}
	
}
