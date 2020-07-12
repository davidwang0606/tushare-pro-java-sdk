/**             
* ����ʱ�䣺 2010-02-18                                
*/ 
package wjc.stock.bjtu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;
/**
* ����Excel���ģ�飬����������д�룬��ȡ���رյȲ��� ��ͨ�������ⲿ�����jxl��ʵ�֡� 

* @author ������
* @version 1.0
* @see jxl.read.*
* @see jxl.write.*
*/

public class ExcelAccesser {
	private String filename;
	private WritableWorkbook wbk;
	private WritableSheet st;
	/**
	* �����ļ�������Excel������������� 
	* @param filename���ļ���
	*/ 
	public ExcelAccesser(String filename) {
		this.filename = filename;
	}
	/**
     * ��Excel�ļ�������ļ��������򴴽�һ��Excel�ļ�
     * @param sheet Excel�ļ���ҳ��λ�ô�0��ʼ���� 
     * @return true
     */
	public boolean open(int sheet){
		File file = new File(filename);
		int numsheet = 0;
		try {
		if (file.exists()) {
			Workbook wb;
			wb = Workbook.getWorkbook(file);
			wbk = Workbook.createWorkbook(file,wb); 
		}
		else {
		    wbk = Workbook.createWorkbook(new File(filename));  
		    }
		}catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			    e.printStackTrace();
		}
		numsheet = wbk.getNumberOfSheets();
		if(sheet < numsheet)
			st = wbk.getSheet(sheet);
		else
			st = wbk.createSheet("sheet"+sheet,sheet);
		return true;
	}
	
	public  boolean openOnly (int sheet) {
		File file = new File(filename);
		int numsheet = 0;
		try {
			if (file.exists()) {
		//		FileInputStream in = new FileInputStream(file); 
				Workbook wb;
				wb = Workbook.getWorkbook(file);
				wbk = Workbook.createWorkbook(file,wb); 
			}
			else {
			    return false;
			    }
			}catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				    e.printStackTrace();
			}
			numsheet = wbk.getNumberOfSheets();
			if(sheet < numsheet)
				st = wbk.getSheet(sheet);
			else {
				try {
					wbk.close();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
			return true;
	}
	/**
     * ���ַ���д��Excel�Ķ�Ӧλ��
     * @param str д����ַ�������
     * @param col ����--��0��ʼ���� 
     * @param row ����--��0��ʼ����
     * @return true
     */
	public boolean write(String str,int col,int row) throws RowsExceededException, WriteException, IOException {
		Label label= new Label(col,row,str);
		st.addCell(label);
		return true;
	}
	/**
     * �ڶ�Ӧ�в���һ������
     * @param row ����--��0��ʼ����
     * @return true
     */
	public boolean insertRow(int row){
		st.insertRow(row);
		return true;
	}
	
	public String read(int col,int row){
		jxl.write.WritableCell wc = st.getWritableCell(col, row);
//		�жϵ�Ԫ�������, ������Ӧ��ת��
		if(wc.getType() == CellType.LABEL)
		{
		Label l = (Label)wc;
		return	l.getString();
		}
		System.out.println("error reading***************"+filename+" row:"+row+" col:"+col);
		return null;
	}
	
	public boolean insertRow(int row,Vector v) throws RowsExceededException, WriteException, IOException{
		insertRow(row);
		int num = v.size();
//		System.out.println("num:"+num);
		int col = 0;
		while(col < num){
			write((String)v.get(col),col,row);
			col++;
		}
		return true;
	}
	
	public boolean deleteRow(int row)
	{
		st.removeRow(row);
		return true;
	}
	
	public boolean close(){	
		try {
			wbk.write();
			wbk.close();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	/*	String str = new String("msc msc");
		Vector v = new Vector();
		v.add("12");
		v.add("23");
		System.out.println(v);
		ExcelAccesser exc = new ExcelAccesser("abc.xls");
		exc.open(0);
		try {
//			exc.write(str, 2, 2);
		    exc.insertRow(1,v);
//			exc.write(str, 2, 2);
		    System.out.println(exc.read(0, 3));
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			exc.close();
		}*/
		ExcelAccesser exc = new ExcelAccesser("DayExcel"+File.separator+"500003"+".xls");
		boolean openState = false;
		openState = exc.open(1);
		System.out.println(openState);
		exc.close();
		
	}

}
