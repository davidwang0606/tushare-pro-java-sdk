package wjc.stock.bjtu;
import java.io.*;
import java.util.*;

//��ȡ��¼�Ĺ�Ʊ���뼰��Ʊ���ƶ�Ӧ�ļ�
public class StockReader {
	public String file;              //�ļ�����
	public BufferedReader reader;    //��ȡ��
	public HashMap map;              //��Ӧ�Ĺ�ϣ��
	public AnalyzeMethod method;
	
	public StockReader(String name){
		file = name;
	}
	
	public StockReader(String name,AnalyzeMethod m){
		file = name;
		method = m;
	}
	
	public int getSize(){
		return map.size();
	}
	
	public void setMethod(AnalyzeMethod m){
		method = m;
	}
	
	public boolean Refresh(String f){
		file = f;
		reader = null;
		map = null;
		return load();
	}
	
	
	public void doAnalyze(){
		if(method == null){
			System.out.println("AnalyzeMethod is null");
			return;
		}
		int size = getSize();
		System.out.println("total:"+getSize());
		int i=0;
		Iterator shcode = getCode().iterator();
		while(shcode.hasNext()){
			String code = (String)shcode.next();
			System.out.println("Start:"+code);
			if(code.equals("601328"))
				System.out.println("here");
			StockInfo sinfo= new StockInfo(code,codeToName(code),codeToPlate(code),codeToFlowCapital(code));
			method.doAnalyze(sinfo);
			i++;
			System.out.println("Finish:"+code+" "+((float)i/size));
		}
	}
	
	//װ���ļ���Ӧ�Ĺ�Ʊ�б�
	public boolean load(){
		try{
			reader = new BufferedReader(new FileReader(file));
			map = new HashMap();
			String s;
			while((s=reader.readLine())!=null){
				String[] array = s.split(" ");
				System.out.println(Arrays.asList(array));
/*				System.out.print(Integer.parseInt(array[0]));
				System.out.print(" ");
				System.out.println(array[1]);*/
				if(array.length == 2)
					map.put(array[0], array[1]);
				else if(array.length == 3)
				    map.put(array[0], array[1]+" "+array[2]);
				else if(array.length == 4)
					map.put(array[0], array[1]+" "+array[2]+" "+array[3]);
			}
//			return true;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		
		try {
			reader.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean add(String addFile){
		try{
			reader = new BufferedReader(new FileReader(addFile));
		//	map = new HashMap();
			String s;
			while((s=reader.readLine())!=null){
				String[] array = s.split(" ");
				System.out.println(Arrays.asList(array));
/*				System.out.print(Integer.parseInt(array[0]));
				System.out.print(" ");
				System.out.println(array[1]);*/
				if(array.length == 2)
					map.put(array[0], array[1]);
				else if(array.length == 3)
				    map.put(array[0], array[1]+" "+array[2]);
				else if(array.length == 4)
					map.put(array[0], array[1]+" "+array[2]+" "+array[3]);
			}
//			return true;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		
		try {
			reader.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//�ɹ���ر�
/*	public boolean close(){
		map = null;
		try {
			reader.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}*/
	//ͨ������������
	public String codeToName(String code){
		String s = (String)(map.get(code));
		if(s.contains(" ")){
			String[] array = s.split(" ");
			return array[0];
		}
		else	
			return s;
		
	}
	//ͨ�������ð����Ϣ
	public String codeToPlate(String code){
		String s = (String)(map.get(code));
		if(s.contains(" ")){
			String[] array = s.split(" ");
			return array[1];
		}
		else	
			return null;
	}
	//ͨ����������ͨ�ɱ���Ϣ
	public String codeToFlowCapital(String code){
		String s = (String)(map.get(code));
		if(s.contains(" ")){
			String[] array = s.split(" ");
			if(array.length>=3)
				return array[2];
			else
				return null;
		}
		else	
			return null;
	}
	//��ô���ļ���
	public Set getCode(){
		return map.keySet();
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
/*		StockReader r = new StockReader("Shanghai");
		r.open();
		String name = r.codeToName("600186");
		System.out.println(name);
		r.close();*/
		/*		StockInfo s = new StockInfo("600468","��������");
		StockDayList list = new StockDayList(s);
		list.downloadFromSohu();
		*/
		
	}

}
