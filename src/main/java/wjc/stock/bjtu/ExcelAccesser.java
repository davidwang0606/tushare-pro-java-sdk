/**             
* 创建时间： 2010-02-18                                
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
* 操作Excel表格模块，包括创建，写入，读取，关闭等操作 。通过调用外部软件包jxl类实现。 

* @author 王建超
* @version 1.0
* @see jxl.read.*
* @see jxl.write.*
*/

public class ExcelAccesser {
	private String filename;
	private WritableWorkbook wbk;
	private WritableSheet st;
	/**
	* 根据文件名构造Excel输入与输出对象 
	* @param filename：文件名
	*/ 
	public ExcelAccesser(String filename) {
		this.filename = filename;
	}
	/**
     * 打开Excel文件，如果文件不存在则创建一个Excel文件
     * @param sheet Excel文件的页面位置从0开始增长 
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
     * 将字符串写入Excel的对应位置
     * @param str 写入的字符串内容
     * @param col 列数--从0开始增长 
     * @param row 行数--从0开始增长
     * @return true
     */
	public boolean write(String str,int col,int row) throws RowsExceededException, WriteException, IOException {
		Label label= new Label(col,row,str);
		st.addCell(label);
		return true;
	}
	/**
     * 在对应行插入一个空行
     * @param row 行数--从0开始增长
     * @return true
     */
	public boolean insertRow(int row){
		st.insertRow(row);
		return true;
	}
	
	public String read(int col,int row){
		jxl.write.WritableCell wc = st.getWritableCell(col, row);
//		判断单元格的类型, 做出相应的转化
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
