package wjc.stock.bjtu;

public class ShareHolderNum {
	DateInfo date;            //统计日期
	int shareholderNum;       //股东户数
	int avgStock;             //平均持股数
	//
	public ShareHolderNum(String d,String n,String a){
		date = new DateInfo(d);
		try{
		shareholderNum = Integer.parseInt(n);
		avgStock = Integer.parseInt(a);
		}catch(NumberFormatException e){
			shareholderNum = Integer.MAX_VALUE;
			avgStock = Integer.MAX_VALUE;
		}
	}
	
	public ShareHolderNum(String d,int n,int a){
		date = new DateInfo(d);
		shareholderNum = n;
		avgStock = a;
	}
	
	public int getAvgStock() {
		return avgStock;
	}
	public void setAvgStock(int avgStock) {
		this.avgStock = avgStock;
	}
	public DateInfo getDate() {
		return date;
	}
	public void setDate(DateInfo date) {
		this.date = date;
	}
	public int getShareholderNum() {
		return shareholderNum;
	}
	public void setShareholderNum(int shareholderNum) {
		this.shareholderNum = shareholderNum;
	}
	
	public int getByNum(int i){
		if(i == 1){
			return shareholderNum;
		}
		else if(i == 2){
			return avgStock;
		}else{
			return Integer.MAX_VALUE;
		}
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(date.toString());
		buf.append(" "+String.valueOf(shareholderNum));
		buf.append(" "+String.valueOf(avgStock));
		return buf.toString();
	}
	public static void main(String[] args){
		ShareHolderNum shm = new ShareHolderNum("2008-09-30",23004,12343);
		System.out.println(shm);
	}
}
