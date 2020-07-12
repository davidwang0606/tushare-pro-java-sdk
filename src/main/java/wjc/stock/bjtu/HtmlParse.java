package wjc.stock.bjtu;
import java.util.*;

public class HtmlParse {
	static public String sep = "│";
	static public String horizon="─";
	
	//在htmlFile字符串中，在index之后，查找在tab中的内容，其中还包含关键字key
	static public String getContent(String htmlFile,int index,String tab,String key){
		int firstindex;	
		LinkedList l1= new LinkedList();
		while(true){
			firstindex = htmlFile.indexOf(tab, index);
			if(firstindex == -1){
				return null;
			}
			index = firstindex + tab.length();
			if(!(l1.isEmpty())){
				if(htmlFile.charAt(firstindex-1) != '/'){
//					System.out.println(htmlFile.charAt(firstindex-1));
					int firstend = htmlFile.indexOf('>', firstindex)+1;
					l1.add(Integer.valueOf(firstend));
					continue;
				}else{
					int firstend = ((Integer)(l1.removeLast())).intValue();
					int lasthead = htmlFile.lastIndexOf('<',firstindex);
					String result = htmlFile.substring(firstend, lasthead);
//					System.out.println("************************");
//					System.out.println(result);
					if(result.indexOf(key) != -1) {
//						System.out.println("findout");
						return result;
					}		
				}
			}else {
				int firstend = htmlFile.indexOf('>', firstindex)+1;
				l1.add(Integer.valueOf(firstend));
				continue;
			}	
		}
	}
//	在htmlFile字符串中，在index之后，查找在标记tab中的内容
	static public String getContent2(String htmlFile,int index,String tab){
		int firstindex;	
		LinkedList l1= new LinkedList();
		while(true){
			//查找第一个索引处
			firstindex = htmlFile.indexOf(tab, index);
			if(firstindex == -1){
				return null;
			}
			index = firstindex + tab.length();
			if(!(l1.isEmpty())){
				if(htmlFile.charAt(firstindex-1) != '/'){
//					System.out.println(htmlFile.charAt(firstindex-1));
//					int firstend = htmlFile.indexOf('>', firstindex)+1;
//					l1.add(Integer.valueOf(firstend));
					continue;
				}else{
					int firstend = ((Integer)(l1.removeLast())).intValue();
					int lasthead = htmlFile.lastIndexOf('<',firstindex);
					String result = htmlFile.substring(firstend, lasthead);
//					System.out.println("************************");
//					System.out.println(result);
						return result;
				}
			}else {
				int firstend = htmlFile.indexOf('>', firstindex)+1;
				l1.add(Integer.valueOf(firstend));
				continue;
			}	
		}
	}
	
	static public String getContent(String htmlFile,int index,String tab) {
		String begin = "<" + tab;
		String end = "</" + tab + ">";
		int findex = htmlFile.indexOf(begin, index);
		if(findex == -1)
			return null;
		int eindex = htmlFile.indexOf(end, findex+begin.length());
//		System.out.println(htmlFile.length());
		String s = htmlFile.substring(findex,eindex+end.length());
//		System.out.println(s);
		return s;
	}
	//获取包含标记的内容
	static public String getContentWithTag(String htmlFile,int index,String tab) {
		int firstindex;	
		LinkedList l1= new LinkedList();
		while(true){
			firstindex = htmlFile.indexOf(tab, index);
			if(firstindex == -1){
				return null;
			}
			index = firstindex + tab.length();
			if(!(l1.isEmpty())){
				if(htmlFile.charAt(firstindex-1) != '/'){
//					System.out.println(htmlFile.charAt(firstindex-1));
					int firstend = htmlFile.lastIndexOf('<', firstindex);
					l1.add(Integer.valueOf(firstend));
					continue;
				}else{
					int firstend = ((Integer)(l1.removeLast())).intValue();
					int lasthead = htmlFile.indexOf('>',firstindex)+1;
					String result = htmlFile.substring(firstend, lasthead);
//					System.out.println("************************");
//					System.out.println(result);
						return result;
				}
			}else {
	//			int firstend = htmlFile.indexOf('>', firstindex)+1;
				int firstend = htmlFile.lastIndexOf('<', firstindex);
				l1.add(Integer.valueOf(firstend));
				continue;
			}	
		}
		
	}
	//获取在Index之后的第Order个标签内容
	static public String getContentWithTagByOrder(String htmlFile,int index,String tab,int order)
	{
		if(order < 1||htmlFile== null)
		{
			System.out.println("getContentWithTagByOrder:Error");
			return null;
		}
		String endtab = "</"+tab+">";
	//	System.out.println(endtab);
		int searchIndex=index;
		while(order >1)
		{
			searchIndex = htmlFile.indexOf(endtab, index)+tab.length();
	//		System.out.println(searchIndex);
			order = order-1;
			index = searchIndex;
		}
		return getContentWithTag(htmlFile,searchIndex,tab);
	}
	
	
	static public String removeEndTag(String s) {
		int first = s.indexOf('<');
		if(first == -1)
			return null;
		first = first +1;
		int space = s.indexOf(' ', first);
		if(space < 0)
			space = Integer.MAX_VALUE;
		int rclose = s.indexOf('>',first);
		int r = Math.min(space, rclose);
		
		String key = s.substring(first, r);
		int firstend = s.indexOf('>',r)+1;
		int lasthead = first;
		do{
			lasthead = lasthead+key.length();
			lasthead = s.indexOf(key,lasthead);
			if(lasthead == -1)
				return null;
			
		}while(s.charAt(lasthead-1)!='/');
		lasthead = s.lastIndexOf('<', lasthead);
		return s.substring(firstend,lasthead);
			
	}
	
	static public String removeAllEndTag(String s) {
		while(s!=null && s.indexOf('<') != -1) {
			s = removeEndTag(s);
//			System.out.println(s);
//			System.out.println(" ");
		}
		return s;
	}
	
	static public String removeHtmlSpace(String s) {
		s = s.trim();
		s = s.replaceAll("&nbsp;", "");
		if(s == null){
			System.out.println("Warning: removeHtmlSpace s == null");
		}
		return s;
	}
	//删除相应Tag以及其中的内容
	static public String removeCloseTag(String content,String str) {
		String begin = "<" + str;
		String end = "</" + str + ">";
		int findex = content.indexOf(begin);
		if(findex == -1)
			return content;
		int lindex = content.indexOf(end, findex+begin.length());
		String relay = content.substring(0,findex) + content.substring(lindex+end.length(), content.length());
		return relay;
		
	}
	//只删除Tag部分，不删除其中的Body
	static public String removeTagIdentity(String content,String str) {
		String begin = "<" + str;
		String end = ">";
		int findex = content.indexOf(begin);
		if(findex == -1)
			return content;
		int lindex = content.indexOf(end, findex+begin.length());
		String endTag2 = "</"+str+">";
		int f2index = content.indexOf(endTag2,lindex);
		String relay = content.substring(0, findex)+ content.substring(lindex+end.length(),f2index)+content.substring(f2index+endTag2.length(), content.length());
		return relay;
	}
	
	static public Vector parseTR(String s) {
		Vector vs = new Vector();
		s = s.trim();
//		System.out.println(s);
		int index = 0;
		while(index!= -1 && index < s.length() -1) {
			String tr = getContentWithTag(s,index,"td");
//			index = index + tr.length();
			index = s.indexOf(tr,index)+tr.length();
			index = s.indexOf("<td",index);
//			System.out.println("index:"+index+".");
			vs.add(HtmlParse.removeHtmlSpace(HtmlParse.removeAllEndTag(tr)));
		}
		return vs;
		
	}
	
	static public Vector parseTR2(String s){
		return parseTR(HtmlParse.removeCloseTag(s, "a"));
	}
	
	static public Vector parseTRSina(String s){
		return parseTR(HtmlParse.removeTagIdentity(s, "a"));
	}
	
	static public String[] parseBR(String s, String seperator) {
		s = removeHtmlSpace(s);
		if(s.indexOf("<br>") != 0 || s.length() == 4){
			return null;
		}
		s= s.substring(4);
		String[] result = s.split(seperator);
		return result;
		
	}

	static public String BetweenWithBR(String s, String firstStr, String secondStr) {
		int first = 0;
		int second = 0;
		int br = 0;
		while(true) {
			first = s.indexOf(firstStr,second);
			if(first == -1) {
				return null;
			}
			/*将 second = s.indexOf(secondStr,first);修改为 second = s.indexOf(secondStr,first+firstStr.length());*/
			second = s.indexOf(secondStr,first+firstStr.length());
			if(second == -1){
				return null;
			}
			br = s.indexOf("<br>",first);
			if(br == -1) {
				return null;
			}
			if(br > second){
				continue;
			}else {
				int start = s.indexOf("<br>",first);
				int end = s.lastIndexOf("<br>", second);
				return s.substring(start,end);
			}
		}
	}
		//将表格中的数据转化成二元数组的程序
		static public String[][] getBRTable(String s,String seperator){
//			System.out.println("seperator:"+seperator);
			String[] lines = s.split("<br>");
			if(lines == null)
				return null;
			int length = lines.length;
			int j = 0;
			String[][] table = new String[length][];
			for(int i=0; i<length; i++){
				lines[i] = HtmlParse.removeHtmlSpace(lines[i]);
				if(lines[i] == null || lines[i].equals("")||(lines[i].indexOf(horizon)!=-1)){
					continue;
				}else{
//					System.out.println(lines[i]);
					table[j] = lines[i].split(seperator);
					j++;
				}
			}
			return table;
		} 
		//第二类获取表格的方法，如果某行只有第一行有文字，则并入下一行处理
		static public String[][] getBRTable2(String s,String seperator){
//			System.out.println("seperator:"+seperator);
			String[] lines = s.split("<br>");
			if(lines == null)
				return null;
			int length = lines.length;
			int j = 0;
			String tempWord = "";//暂时存取的字符
			String[][] table = new String[length][];
			for(int i=0; i<length; i++){
				lines[i] = HtmlParse.removeHtmlSpace(lines[i]);
				if(lines[i] == null || lines[i].equals("")||(lines[i].indexOf(horizon)!=-1)){
//					System.out.println("i:"+i);
					continue;
				}else{
//					System.out.println(lines[i]);
					table[j] = lines[i].split(seperator);
					//如果只有一个元素为0,00,.00或者第二个元素开始都是0,00,.00丢弃此行，继续处理
					if(table[j].length<2||table[j][1].equals("0")||table[j][1].equals("00")||table[j][1].equals(".00")||table[j][1].equals(""))
						continue;
					//如果当前行只有两个元素，则把标题并入下一行处理
                    if (table[j].length == 2) {
//                    	System.out.println(table[j][1]);
                    	tempWord = tempWord+table[j][1];
//                    	System.out.println(j);
//                    	System.out.println(tempWord);
                    	continue;
                    }else {
                    	if(tempWord.equals("")==false){
//                    		System.out.println(tempWord);
                    		table[j][1]=tempWord+table[j][1];
//                    		System.out.println(table[j][1]);
                    		tempWord = "";
                    	}
//                    	System.out.println(table[j][1]+" "+table[j][2]);
					    j++;
                    }
                    
				}
			}
			return table;
		}
		//第三类获取表格的方法，通过横线区分每个表格内容
		static public String[][] getBRTableByline(String s,String seperator){
//			System.out.println("seperator:"+seperator);
			String[] lines = s.split("<br>");
			int col = lines[2].split(seperator).length;
			if(lines == null)
				return null;
			int length = lines.length;
			int j = 0;
			String[][] table = new String[length][col];
			for(int i=0; i<length; i++){
				if(lines[i].indexOf("─")!= -1) {
					j++;
					continue;
				}
				String[] value = lines[i].split(seperator);
				for(int k=0;k<value.length;k++) {
					if(table[j][k]==null){
						table[j][k] = HtmlParse.removeHtmlSpace(value[k]);
					}else {
				    table[j][k] = table[j][k]+HtmlParse.removeHtmlSpace(value[k]);
					}
				}
			}
			return table;
		}
		//专门提供给东方财富网第四类获取表格的方法，获取每个横线间前两项的内容，填写到数组中
		static public String[][] getBRTableEastmoneyFund(String s,String seperator){
			if(s == null){
				System.out.println("Error:getBRTableEastmoneyFund Input parameter s == null");
				return null;
			}
//			System.out.println("seperator:"+seperator);
			String[] lines = s.split("<br>");
			if(lines.length < 3) {
				return null;
			}	
			int col = lines[2].split(seperator).length;
			if(lines == null)
				return null;
			int length = lines.length;
			
			String[][] table = new String[length][col];
//			获取日期
			String[] dvalue = lines[2].split(seperator);
			for (int l=0; l<col; l++) {
				table[0][l] = HtmlParse.removeHtmlSpace(dvalue[l]);
			}
//          获取日期结束	
//          填充内部机构持股数，及百分比
			int j = 1; //实际数据的行变量
			boolean startValue = false;
			for(int i=3; i<length; i++){ //用于表示页面文本
				if(lines[i].indexOf("─")!= -1) {
					startValue = true;
					continue;
				}else if (startValue == false){
					continue;
				}
				//填充第一行数据 
				String[] value = lines[i].split(seperator);
				for(int k=0;k<value.length;k++) {
						table[j][k] = HtmlParse.removeHtmlSpace(value[k]);
				}
				i++;
				j++;
				//
				String[] value2 = lines[i].split(seperator);
				for(int k=0;k<value.length;k++) {
						table[j][k] = HtmlParse.removeHtmlSpace(value2[k]);
				}
				j++;
				startValue = false;
			}
			return table;
		}
		//专门用于东方财富网财务数据分析，获取表格数据方法
		static public String[][] getBRTableEastmoneyFinancial(String s,String seperator){
//			System.out.println("seperator:"+seperator);
			String[] lines = s.split("<br>");
			if(lines == null)
				return null;
			int length = lines.length;
			//将整行只有一个"|"分隔的行，并入上一行，并将此行设为空
			for(int i=1; i<length; i++) {
				if(lines[i] == null || lines[i].equals("")){
					continue;
				}
				if(lines[i].charAt(0)!= '│'&& lines[i].indexOf('│')!=-1) {
					lines[i-1] = lines[i-1]+lines[i];
					lines[i] = "";
				}
				
			}
			int j = 0;
			String tempWord = "";//暂时存取的字符
			String[][] table = new String[length][];
			for(int i=0; i<length; i++){
				lines[i] = HtmlParse.removeHtmlSpace(lines[i]);
				if(lines[i] == null || lines[i].equals("")||(lines[i].indexOf(horizon)!=-1)){
//					System.out.println("i:"+i);
					continue;
				}else{
//					System.out.println(lines[i]);
					table[j] = lines[i].split(seperator);
					//如果只有一个元素为0,00,.00或者第二个元素开始都是0,00,.00丢弃此行，继续处理
					if(table[j][1].equals("0")||table[j][1].equals("00")||table[j][1].equals(".00")||table[j][1].equals(""))
						continue;
					//如果当前行只有两个元素，则把标题并入下一行处理
                    if (table[j].length == 2) {
//                    	System.out.println(table[j][1]);
                    	tempWord = tempWord+table[j][1];
//                    	System.out.println(j);
//                    	System.out.println(tempWord);
                    	continue;
                    }else {
                    	if(tempWord.equals("")==false){
//                    		System.out.println(tempWord);
                    		table[j][1]=tempWord+table[j][1];
//                    		System.out.println(table[j][1]);
                    		tempWord = "";
                    	}
//                    	System.out.println(table[j][1]+" "+table[j][2]);
					    j++;
                    }
                    
				}
			}
			return table;
		}
//根据TH或者TD进行分隔，内容为空时，仍保留当前格内容		
		public static Vector getTHorTDline(String str)
		{
			String rpStr = str.replaceAll("th", "td");
//			System.out.println(rpStr);
			Vector a = HtmlParse.parseTR2(rpStr);
/*			if(a.get(0).equals(""))
			{
				System.out.println("hello");
				a.set(0, " ");
			}*/
	//		System.out.println(a);
			return a;
		}
		
		public static Vector[] getTHorTDtable(String str){
			if(str == null){
				System.out.println("getTHorTDtable:null");
				return null;
			}
			String[] lines = str.split("</tr>");
			int length = lines.length;
	//		System.out.println(length);
			Vector[] outable = new Vector[length];
			int index=0;
			String line = HtmlParse.getContent(str, index, "tr");
	        int start=0;

			while(line != null){
				outable[start] = (HtmlParse.getTHorTDline(line));
				start = start+1;
				index = index+line.length();
//				System.out.println("parsing line");
	//			System.out.println(line);
				//每行处理完毕
				line = HtmlParse.getContent(str, index, "tr");
	//			System.out.println(index);
			}
			return outable;
		}
		//判断对应查找到的对应标签是否相连,order为顺序的标签与order+1的标签
		public static boolean isTabConbine(String htmlFile,int index,String tab,int order)
		{

			if(order < 1||htmlFile== null)
			{
				System.out.println("getContentWithTagByOrder:Error");
				return false;
			}
			String endtab = "</"+tab+">";
	//		System.out.println(endtab);
			int searchIndex=index;
			while(order >=1)
			{
				searchIndex = htmlFile.indexOf(endtab, index)+endtab.length();
				System.out.println(searchIndex);
				order = order-1;
				index = searchIndex;
			}
			String starttab="<"+tab;
			int seachNextStart = htmlFile.indexOf(starttab, index);
			if(searchIndex == seachNextStart)
			{
				return true;
			}else
			{
				return false;
			}
		}
		
		public static void main(String[] args) {
			String s = new String("<tr class=\"thead bg\"><th width=\"230px\"></th><th>11-09-30</th><th>11-06-30</th><th>11-03-31</th><th>10-12-31</th><th>10-09-30</th><th>10-06-30</th><th>10-03-31</th><th>09-12-31</th></tr>");
			String s2 = new String("<td>46.90</td>");
			String tr = new String("<tr><td><a href='http://app.finance.ifeng.com/info/news_gsxw.php?code=sz002001#2010-01-29' target='_blank' style='color:#ff0000;text-decoration:underline;' title='点击查看当日公司新闻：浙江新和成股份有限公司关于非公开发行股票申请获得中国证监会核准的公告'><strong>N</strong></a>&nbsp;2010-01-29</td><td>46.90</td><td>48.97</td><td>46.90</td><td>48.00</td><td>15293</td><td>736954</td><td><span class=\"Ared\">0.81</span></td><td><span class=\"Ared\">1.72%</span></td>\n\n</tr>");
			String s3 = new String("<tr>\n\n<td>2010-02-12</td><td>16.38</td><td>17.24</td><td>16.25</td><td>17.06</td><td>188400</td><td>3171940</td><td><span class=\"Ared\">0.85</span></td><td><span class=\"Ared\">5.24%</span></td><tr>");
			String stab = new String("<br>┌───┬────┬───┬───┬───┬───┬───┬──┬───┐<br>｜股份变｜变动日期｜变动股｜成交均｜变动原｜变动后｜董监高｜职务｜变动人｜<br>｜动人姓｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜份数量｜&nbsp;&nbsp;价&nbsp;&nbsp;｜&nbsp;&nbsp;因&nbsp;&nbsp;｜持股数｜人员姓｜&nbsp;&nbsp;&nbsp;&nbsp;｜与董监｜<br>｜&nbsp;&nbsp;名&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;(股)&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;(股)&nbsp;｜&nbsp;&nbsp;名&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;｜高的关｜<br>｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;系&nbsp;&nbsp;｜<br>├───┼────┼───┼───┼───┼───┼───┼──┼───┤<br>｜丁正义｜20101216｜&nbsp;-9644｜&nbsp;15.15｜竞价交｜28932&nbsp;｜丁正义｜高管｜&nbsp;本人&nbsp;｜<br>｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;易｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜<br>├───┼────┼───┼───┼───┼───┼───┼──┼───┤<br>｜丁正义｜20090511｜-12859｜&nbsp;13.71｜竞价交｜38576&nbsp;｜丁正义｜高管｜&nbsp;本人&nbsp;｜<br>｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;易｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜<br>├───┼────┼───┼───┼───┼───┼───┼──┼───┤<br>｜董鹤年｜20070605｜&nbsp;-1035｜&nbsp;&nbsp;8.14｜二级市｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜董瑞国｜监事｜&nbsp;父母&nbsp;｜<br>｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜场买卖｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜<br>├───┼────┼───┼───┼───┼───┼───┼──┼───┤<br>｜丁正义｜20070529｜-10000｜&nbsp;13.23｜二级市｜51435&nbsp;｜丁正义｜董事｜&nbsp;本人&nbsp;｜<br>｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜场买卖｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜、高｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜<br>｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;管&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜<br>└───┴────┴───┴───┴───┴───┴───┴──┴───┘<br>");
			String sss3 = new String("<br>─────┬───────┬───────┬───────┬───────<br>&nbsp;报告日期&nbsp;｜&nbsp;&nbsp;2010-09-30&nbsp;&nbsp;｜&nbsp;&nbsp;2010-06-30&nbsp;&nbsp;｜&nbsp;&nbsp;2010-03-31&nbsp;&nbsp;｜&nbsp;2009-12-31&nbsp;&nbsp;&nbsp;<br>─────┼───────┼───────┼───────┼───────<br>&nbsp;基金持股&nbsp;｜&nbsp;&nbsp;&nbsp;34410.79&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;53692.31&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;34241.44&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;55285.51&nbsp;&nbsp;&nbsp;&nbsp;<br>占流通A比&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8.60&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;13.42&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8.56&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;13.82&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>持股家数及｜共计10&nbsp;&nbsp;增持7&nbsp;｜共计83&nbsp;&nbsp;新进72｜共计13&nbsp;&nbsp;新进3&nbsp;｜共计89&nbsp;&nbsp;新进7&nbsp;<br>&nbsp;进出情况&nbsp;｜&nbsp;&nbsp;减持3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜增持5&nbsp;&nbsp;&nbsp;减持6&nbsp;｜增持7&nbsp;&nbsp;&nbsp;减持2&nbsp;｜9&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜增持5&nbsp;&nbsp;&nbsp;减持5&nbsp;<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>─────┼───────┼───────┼───────┼───────<br>&nbsp;保险持股&nbsp;｜&nbsp;&nbsp;&nbsp;7913.65&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;7618.14&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;5618.14&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;6949.43&nbsp;&nbsp;&nbsp;&nbsp;<br>占流通A比&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.98&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.90&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.40&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;｜&nbsp;&nbsp;&nbsp;&nbsp;1.74&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>─────┴───────┴───────┴───────┴───────<br>");
			//String[][] v = HtmlParse.getBRTableEastmoneyFund(sss3, HtmlParse.sep);
			//System.out.println(v[1][0]);
			getTHorTDline(s);
//			String last = HtmlParse.removeCloseTag(s,"a");
//			Vector a = HtmlParse.parseTR2(s3);
//			String line = HtmlParse.getContent(tr, 0, "tr");
//			String line2 = HtmlParse.getContentWithTag(tr, 0, "tr");
//			System.out.println(a);
		}
	
}
