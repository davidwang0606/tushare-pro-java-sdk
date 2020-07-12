package wjc.stock.bjtu;
import java.util.*;

public class StockInfo {
	private String code;         //股票代码
	private String name;      //股票名称
	private String plate = null;     //股票板块
	private String flowCapital = null;
	
	
	public StockInfo(String c) {
		code = c;
	}
	public StockInfo(String c,String n) {
		code = c;
		name = n;
	}
	
	public StockInfo(String c,String n,String p){
		code = c;
		name = n;
		plate = p;
	}
	
	public StockInfo(String c,String n,String p,String f){
		code = c;
		name = n;
		plate = p;
		flowCapital = f;
	}
	
	public String getFlowCapital() {
		return flowCapital;
	}
	public void setFlowCapital(String flowCapital) {
		this.flowCapital = flowCapital;
	}
	
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String toString(){
		if(plate == null)
			return code+"_"+name;
		else
			return code+"_"+name+"_"+plate;
	}
	public static void main(String[] args) {
		StockInfo s = new StockInfo("600468","百利电器");
		StockDayList list = new StockDayList(s);
		list.downloadFromSohu();
		System.out.println("hello");
		
	}

}
