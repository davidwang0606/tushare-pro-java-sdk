package wjc.stock.bjtu;

import java.net.MalformedURLException;
import java.net.URL;

public class SearchEngine {
	private String baseStr = null;
    private String[] searchList;

    private String content = null;
    
    public SearchEngine(String str,String[] list){
    	baseStr = str ;
    	searchList = list;
    }
    
    public String getStoctInfoSearchResult(StockInfo s){
    	String cq = baseStr + s.getName();
    	String result = "";
		try {
			URL u = new URL(cq);
			Html h = new Html(u);
			boolean b = h.download("UTF-8");
			if(b) {
//				System.out.println(h.getContent());
				String content =h.getContent();
//				int index=0;
				for(int i=0;i<searchList.length;i++){
					if(content.indexOf(searchList[i])!=-1){
						result = result + searchList[i]+ " ";
					}
				}
				
			//	System.out.println(line);

			}else {
				System.out.println("download error");
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return result;
    }

    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base = new String("http://www.baidu.com/s?wd=");
		StockInfo s = new StockInfo("002476", "宝莫股份 ");
		String[] list = {"民营银行","重组","页岩气"};
		SearchEngine sa = new SearchEngine(base,list);
		String str = sa.getStoctInfoSearchResult(s);
		System.out.println(str);
	}

}
