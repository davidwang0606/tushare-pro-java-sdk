package wjc.stock.bjtu;

import java.text.DecimalFormat;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//���γ�Ȩ�ɷ�����������
public class BonusDispatch {
	String day;
	String method;
	//��Ȩ��ת�͹�
	float plusPrefix=0.0f;
	//��Ȩ�ɷ�����
	float addPrefix=0.0f;
	
	public BonusDispatch(DateInfo date,String m){
		day = date.toString();
		method = m;
	}
	
	public BonusDispatch(String date,String m){
		day = date;
		method = m;
	}
	
	public String getDay() {
		return day;
	}

	public void setDay(String d) {
		this.day = d;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public float getPlusPrefix() {
		return plusPrefix;
	}

	public void setPlusPrefix(float plusPrefix) {
		this.plusPrefix = plusPrefix;
	}

	public float getAddPrefix() {
		return addPrefix;
	}

	public void setAddPrefix(float addPrefix) {
		this.addPrefix = addPrefix;
	}
	
	public String toString(){
		return "��Ȩ���ڣ�"+day+". ��Ȩ������"+method +". �˻�����: "+plusPrefix+". �ۼӻ���: "+addPrefix;
	}
	
	public Vector toVector(){
		Vector v = new Vector();
//		DecimalFormat myformat=new java.text.DecimalFormat("0.00");
		v.add(day.toString());
		v.add(String.valueOf(method));
		return v;
	}
	
	public void parseMethod(){
		Pattern num=Pattern.compile("[0-9]+\\.*[0-9]*");
		Pattern pAdd = Pattern.compile("ת��[0-9]+");
		Pattern pGive = Pattern.compile("��[0-9]+");
		Pattern pMoney = Pattern.compile("��[0-9]+\\.*[0-9]*Ԫ");
		float add = 0.0f;
		float give = 0.0f;
		float money = 0.0f;	
		Matcher matcherAdd = pAdd.matcher(method);
		if(matcherAdd.find()){			
			String s = matcherAdd.group();
			Matcher numMatcher = num.matcher(s);
			if(numMatcher.find()){
				add = Float.parseFloat(numMatcher.group());
			}
//			System.out.println(add);
		}
		
		Matcher matcherGive = pGive.matcher(method);
		if(matcherGive.find()){			
//			System.out.println(matcherAdd.group());
			String s = matcherGive.group();
//			System.out.println(s);
			Matcher numMatcher = num.matcher(s);
			if(numMatcher.find()){
//				System.out.println(numMatcher.group());
				give = Float.parseFloat(numMatcher.group());
			}
//			System.out.println(give);
//			String s = matcher.group();
		}
		
		Matcher matcherMoney = pMoney.matcher(method);
		if(matcherMoney.find()){			
//			System.out.println(matcherAdd.group());
			String s = matcherMoney.group();
//			System.out.println(s);
			Matcher numMatcher = num.matcher(s);
			if(numMatcher.find()){
//				System.out.println(numMatcher.group());
				money = Float.parseFloat(numMatcher.group());
			}
//			System.out.println(money);
//			String s = matcher.group();
		}
		
		plusPrefix = add + give;
		addPrefix = money;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BonusDispatch b = new BonusDispatch("2011-06-30","10ת��3��");
		System.out.println(b);
		b.parseMethod();
		System.out.println(b);
//		Pattern num=Pattern.compile("[0-9]+");
//		String s = new String("ת��10");
//		Matcher numMatcher = num.matcher(s);
//		numMatcher.find();
//		System.out.println(numMatcher.group());
	}

}
