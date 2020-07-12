package wjc.stock.bjtu;

public class StockInfoAddition {
	private StockInfo stock;
	private float addition;
	
	public StockInfoAddition(StockInfo stock,float addition){
		this.stock = stock;
		this.addition = addition;
	}

	public StockInfo getStock() {
		return stock;
	}



	public void setStock(StockInfo stock) {
		this.stock = stock;
	}

	public float getAddition() {
		return addition;
	}
	public void setAddition(float addition) {
		this.addition = addition;
	}
	
	public String toString(){
		return stock.toString() + ":"+ addition;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
