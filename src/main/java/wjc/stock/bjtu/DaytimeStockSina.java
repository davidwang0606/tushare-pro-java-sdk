package wjc.stock.bjtu;

import java.util.Vector;

public class DaytimeStockSina extends DaytimeStock {
	
	public DaytimeStockSina(Vector v){
		super(v);
		float a;
		a = lowestPrice;
		lowestPrice = closePrice;
		closePrice = a;
		handoverMount = handoverMount/100;
		handoverPrice = handoverPrice/100;
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
