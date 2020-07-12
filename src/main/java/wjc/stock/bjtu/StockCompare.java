package wjc.stock.bjtu;

import java.util.Comparator;


//将股票列表按一定顺序排列
public class StockCompare implements Comparator {

	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg0 instanceof StockInfo && arg1 instanceof StockInfo){
			int code0 = Integer.parseInt(((StockInfo)arg0).getCode());
			int code1 = Integer.parseInt(((StockInfo)arg1).getCode());
			if(code0 > code1){
				return 1;
			}else if(code0 < code1){
				return -1;
			}else 
				return 0;
		}
		return 0;
	}

}
