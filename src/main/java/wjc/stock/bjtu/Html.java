package wjc.stock.bjtu;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Html {
	private URL url;
	private	StringBuffer content = new StringBuffer();
	
	public Html(URL u) {
		url = u;
	}
	
	public void setURL(URL u) {
		url = u;
	}
	
	public URL getURL(){
		return url;	
	}
	
	public boolean download() {
		try{	
			URLConnection uconn = url.openConnection();
			uconn.connect();
			InputStream in = uconn.getInputStream();
			InputStreamReader inReader = new InputStreamReader(in);
			System.out.println(inReader.getEncoding());
			BufferedReader reader = new BufferedReader(inReader);
			int bytes = 0;
			String s;
			while((s=reader.readLine())!=null){
				content.append(s);
				content.append('\n');
				bytes = bytes + s.length();
			}
			in.close();
			System.out.println("download "+bytes+" bytes.");
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean download(String charset){
		int failureTimes =0;
		while(failureTimes<5)
		{
			if(failureTimes>1)
			{
				try {
					Thread.sleep(300000*failureTimes);					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			content = new StringBuffer();
			try{	
				URLConnection uconn = url.openConnection();
				uconn.setConnectTimeout(300000);
				uconn.setReadTimeout(300000);
				uconn.connect();
				InputStream in = uconn.getInputStream();
				InputStreamReader inReader = new InputStreamReader(in,charset);
	//			System.out.println(inReader.getEncoding());
				BufferedReader reader = new BufferedReader(inReader);
				int bytes = 0;
				String s;
				while((s=reader.readLine())!=null){
					content.append(s);
					content.append('\n');
					bytes = bytes + s.length();
				}
				in.close();
				System.out.println("download "+bytes+" bytes.");
				return true;
			}
			catch(SocketTimeoutException ste){
				ste.printStackTrace();
				System.out.println("SocketTimeoutException:超时异常");
				failureTimes = failureTimes+1;
				continue;
			}
			catch(IOException e){
				e.printStackTrace();
				System.out.println("IOException:超时异常");
				failureTimes = failureTimes+1;
				continue;
			}
		}
		return false;
		
	}
	
	public String getContent() {
		return content.toString();
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
/*		String bjtu = new String("http://quote3.eastmoney.com/f10.asp?StockCode=600686&stock_name=金龙汽车&f10=012");
		try {
			URL u = new URL(bjtu);
			Html h = new Html(u);
			boolean b = h.download();
			if(b) {*/
//				System.out.println(h.getContent());
/*				String content =HtmlParse.getContent(h.getContent(), 0, new String("table"),new String("股东名称"));
				System.out.println(content.trim());
				String trs = HtmlParse.getContent(content, 0, "tr",new String("汽车工业"));
				System.out.println(trs.trim());
				Vector v = HtmlParse.parseTR(trs);
				System.out.println(v.toString());*/
/*				for(int i=0; i<v.size(); i++) {
					System.out.println(v.);
				}*/
	/*			String word = HtmlParse.removeAllEndTag(content);
				System.out.println(word);
				String wordnospace = HtmlParse.removeHtmlSpace(word);
				System.out.println(wordnospace);
				String cont = HtmlParse.BetweenWithBR(h.getContent(), "股东户数", "机构持股明细");
				System.out.println(cont);
				String [][]brv = HtmlParse.getBRTable(cont, HtmlParse.sep);
				for(int i=0; i<brv.length; i++)
				System.out.println(Arrays.toString(brv[i]));*/
//				System.out.println(brv[0]);
//				System.out.println(brv[1]);
//				System.out.println(brv.length);
				/*ShareHolderList list = new ShareHolderList(600686,brv);
				System.out.println(list);
				float result = list.compare(new DateInfo("2008-03-31"), new DateInfo("2007-12-31"), "户均持股");
				System.out.println(result);
			}else {
				System.out.println("download error");
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
//		StockReader reader = new StockReader("zhongxiaoban");
//		if(!(reader.load())){
//			System.out.println("read error");
//			return;
//		}
//		Iterator shcode = reader.getCode().iterator();
//		PrintWriter w;
//		try {
//			w = new PrintWriter(new FileWriter("Analyse3"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//		float result;
//		DateInfo date1 = new DateInfo("2008-03-31");
//		DateInfo date2 = new DateInfo("2007-12-31");
////		for(int)
//		while(shcode.hasNext()){
//			String code = (String)(shcode.next());
//			System.out.println(code);
//			String name = reader.codeToName(code);
//			System.out.println(name);
//			result = EastMoneyAnalyze.compareStockNum(code,name, date1, date2);
//			if(result < 0.9){
//				w.println(code+" "+name+":"+result);
//			}
//		}
//		
////		reader.close();
//		w.close();
//		float result = EastMoneyAnalyze.compareStockNum(600686,"金龙汽车", new DateInfo("2008-03-31"), new DateInfo("2007-12-31"));
//		System.out.println("600686:"+result);
		String cq = new String("http://market.finance.sina.com.cn/downxls.php?date=2015-08-06&symbol=sh600000");
		try {
			URL u = new URL(cq);
			Html h = new Html(u);
			boolean b = h.download("gb2312");
			System.out.println(h.content);
//			if(b) {
//				System.out.println(h.getContent());
////				String content =HtmlParse.getContent(h.getContent(), 0, new String("table"),new String("公告日期"));
////				System.out.println(content.trim());
//////				String trs = HtmlParse.getContent(content, 0, "tr",new String("汽车工业"));
//////				System.out.println(trs.trim());
////				int index=0;
////				String line = HtmlParse.getContent(content, index, "tr");
////				System.out.println(line);
//				Pattern p = Pattern.compile("<span .*最后更新</span>");
//				int i = 0;
//				Matcher m = p.matcher(h.getContent());
////				while(m.find()){
////					i++;
////				}
////				System.out.println(i);
//				m.find();
//				Pattern pValue = Pattern.compile("<span .*</span>");
//				
//				Matcher mm = pValue.matcher(h.getContent());
//				
//				mm.find(m.end());
//				System.out.println("**********************************************");
//				while(mm.find()){
//					i++;
//					System.out.println(mm.group());
//				}
//				
//				
//				StringBuffer sb = new StringBuffer(); 
//
//				//m.appendTail(sb);
//				System.out.println("**********************************************");
//				System.out.println(m.group());
//				System.out.println(i);
//				
//
//			}else {
//				System.out.println("download error");
//			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
