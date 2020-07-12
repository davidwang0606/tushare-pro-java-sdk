package wjc.stock.bjtu;
import java.util.*;

public class DateInfo {
	int year;
	int month;
	int day;
	
	public DateInfo(int y,int m,int d) {
		year = y;
		month = m;
		day = d;
		
	}
	public DateInfo(String str) {
		str = str.trim();
		if(str == null) {
			System.out.println("DateInfo = null");
			return;
		}
		//采用20100101方式
		if(str.indexOf("-")==-1){
			if(str.length()!=8){
				System.out.println("DateInfo does not contain 8 digits");
				return;
			}
			year = Integer.parseInt(str.substring(0, 4));
			month = Integer.parseInt(str.substring(4, 6));
			day = Integer.parseInt(str.substring(6, 8));
			return;
		}
		//采用2010-01-01方式
		String[] list=str.split("-");
		if(list.length!=3) {
			System.out.println("DateInfo split not equal to 3");
			return;
		}
		year = Integer.parseInt(list[0]);
		month = Integer.parseInt(list[1]);
		day = Integer.parseInt(list[2]);
	}
	
	
	
/*	static public DateInfo toSohuDate(Calendar c) {
		DateInfo s = new DateInfo(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
		return s;
	}
	
	static String toSohuString(int year,int month,int day) {
		DateInfo sd = new DateInfo(year,month,day);
		return sd.toString();
	}
*/	
	public String toString(){
		String strm = String.valueOf(month);
		String strd = String.valueOf(day);
		if(month < 10) {
			strm = "0"+strm;
		}
		if(day < 10) {
			strd = "0"+strd;
		}
		
		return ""+year+"-"+strm+"-"+strd;
	}
	
	public Calendar toCalendar(){
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month, day);
		return c;
	}
	
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public boolean equals(Object date2){
		if(date2 instanceof DateInfo){
			DateInfo d2 = (DateInfo)date2;
			return(d2.getYear()==year && d2.getMonth()==month &&d2.getDay()==day);
		}
		return false;
	}
	//比较当前日期与 传入日期的大小，大于（即晚于）返回正值
	public int compare(DateInfo d){
		if((year - d.getYear())!=0){
			return year-d.getYear();
		}else if((month - d.getMonth())!=0){
			return month-d.getMonth();
		}else {
			return day-d.getDay();
		}
	}
	
	public DateInfo getPreseason()
	{
		int m,y,d;
		if(month > 3){
			y=year;
			m=month-3;
			if(m==3){
				d = 31;
			}else
			{
				d = 30;
			}
			
		}else{
			y=year-1;
			m=12;
			d=31;
		}
		DateInfo dinfo = new DateInfo(y,m,d);
		return dinfo;
	}
	//对于当前在季末未处理
	public DateInfo getLatestSeason(){
		int m,y,d;
		m = (month-1)/3*3;
		if(m == 0){
			m = 12;
			y = year -1;
		}else{
			y = year;
		}
		if(m == 6 || m == 9){
			d = 30;
		}else{
			d = 31;
		}
		DateInfo dinfo = new DateInfo(y,m,d);
		return dinfo;
	}
	
	public DateInfo getPreDayBelow28(int n)
	{
		if(n<0)
			return null;
		int newD = day-n;
		int newM = month;
		int newY = year;
		if(newD<=0)
		{
			newM = newM-1;
			if(newM <= 0 )
			{
				newM = 12;
				newY = year -1;
			}
			
			if(newM == 1 || newM == 3 ||newM == 5 ||newM == 7 ||newM == 8 ||newM == 10 || newM == 12)
			{
				newD = newD + 31;
			}
			else if(newM == 4 ||newM == 6 ||newM == 9 ||newM == 11)
			{
				newD = newD + 31;
			}else if(newM == 2 && year%4 == 0)
			{
				newD = newD + 29;
			}else
			{
				newD = newD+ 28;
			}
			
		}
		
		return new DateInfo(newY,newM,newD);
		
		
	}
	
	
	public DateInfo getPreYear(){
		int y = year-1;
		DateInfo dinfo = new DateInfo(y,month,day);
		return dinfo;
	}
	
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	public static void main(String[] args) {
		DateInfo s = new DateInfo("20090730");
		DateInfo s1 = new DateInfo("2012-01-1");
		System.out.println(s1.getPreDayBelow28(1));
//		System.out.println(s.getMonth());
//		Calendar c = s.toCalendar();
//		System.out.println(c.get(Calendar.YEAR));
//		System.out.println(s.compare(s1));
		
	}
	
	

}
