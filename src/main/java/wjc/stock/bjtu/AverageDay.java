package wjc.stock.bjtu;

//记录股票的均量及均价
public class AverageDay {
	private float averagePrice5;
	private float averagePrice10;
	private float averageMount5;
	private float averageMount10;
	
	public AverageDay(){
		this.averagePrice5 = 0;
		this.averagePrice10 = 0;
		this.averageMount5 = 0;
		this.averageMount10 = 0;
	}
	
	
	public AverageDay( float averagePrice5, float averagePrice10, float averageMount5, float averageMount10) {
		super();
//		this.date = date;
		this.averagePrice5 = averagePrice5;
		this.averagePrice10 = averagePrice10;
		this.averageMount5 = averageMount5;
		this.averageMount10 = averageMount10;
	}
	public float getAverageMount10() {
		return averageMount10;
	}
	public void setAverageMount10(float averageMount10) {
		this.averageMount10 = averageMount10;
	}
	public float getAverageMount5() {
		return averageMount5;
	}
	public void setAverageMount5(float averageMount5) {
		this.averageMount5 = averageMount5;
	}
	public float getAveragePrice10() {
		return averagePrice10;
	}
	public void setAveragePrice10(float averagePrice10) {
		this.averagePrice10 = averagePrice10;
	}
	public float getAveragePrice5() {
		return averagePrice5;
	}
	public void setAveragePrice5(float averagePrice5) {
		this.averagePrice5 = averagePrice5;
	}
	
	public String toString(){
		return "P5:"+averagePrice5+" P10:"+averagePrice10+" M5:"+averageMount5+" M10:"+averageMount10;
	}
}
