package wjc.stock.bjtu;

import finance.stock.bjtu.Constant;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

public class StockDayListWangyi extends StockDayList {
    public static String WangyiBase = "http://quotes.money.163.com/service/chddata.html?";//code=0601398&start=20171120
    public static String startDate = "20191120";
    public static String DayDir = "DayExcel";
    public StockDayListWangyi(StockInfo s) {
        super(s);
        // TODO Auto-generated constructor stub
    }

    public boolean downloadFromWangyi(){
        daylist.clear();
        String code = stock.getCode();
        char first = code.charAt(0);
        String u;
        if(stock.getCode().startsWith("sh")){
            u = WangyiBase+"code=0"+stock.getCode().substring(2)+"&start="+startDate;
        }else if(stock.getCode().startsWith("sz")){
            u = WangyiBase+"code=1"+stock.getCode().substring(2)+"&start="+startDate;
        }
        else if(stock.getCode().startsWith("6")){
            u = WangyiBase+"code=0"+stock.getCode()+"&start="+startDate;
        }else {
            u = WangyiBase+"code=1"+stock.getCode()+"&start="+startDate;
        }


        try {
            URL url = new URL(u);
            Html h = new Html(url);
            //System.out.println(url);
            boolean b = h.download("gb2312");
            if(b) {
                //System.out.println(h.getContent());
                String[] daystock = h.getContent().split("\n");
                for(String s:daystock) {
                    if(!s.startsWith("20")) {
                        continue;
                    }
                    Vector<String> v = new Vector();
                    String[] dataDaytime = s.split(",");
                    if(dataDaytime.length < 10) {
                        System.out.print("Error download");
                        break;
                    }
                    if(dataDaytime[3].equals("0.0")){
                        continue;
                    }
                    v.add(dataDaytime[0]);
                    v.add(dataDaytime[6]);
                    v.add(dataDaytime[4]);
                    v.add(dataDaytime[3]);
                    v.add(dataDaytime[5]);
                    v.add(dataDaytime[11]);
                    v.add(dataDaytime[12]);
                    DaytimeStock daytimestock = new DaytimeStockSina(v);

                    daylist.addLast(daytimestock);

                }
                //System.out.println(daylist);
                return true;
            }else{
                return false;
            }
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveToExcel() throws IOException{
        ExcelAccesser exc = new ExcelAccesser(DayDir+File.separator+stock.getCode()+".xls");
        exc.open(0);
        int index = 0;
        while(index < daylist.size()){
            try {
                exc.insertRow(1, ((DaytimeStock)daylist.getLast()).toVector());
            } catch (RowsExceededException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            daylist.removeLast();
        }
        System.out.println("list size:"+daylist.size());
        exc.close();
        return true;
    }

    public boolean saveToExcel(String Dir) throws IOException{
        ExcelAccesser exc = new ExcelAccesser(Dir+ File.separator+stock.getCode()+".xls");
        exc.open(0);
        int index = 0;
        while(index < daylist.size()){
            try {
                exc.insertRow(1, ((DaytimeStock)daylist.getLast()).toVector());
            } catch (RowsExceededException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            daylist.removeLast();
        }
        //System.out.println("list size:"+daylist.size());
        exc.close();
        return true;
    }

    public int updateToExcel(){
        boolean b = downloadFromWangyi();
        int downloadsize = daylist.size()-1;
        int index = 0;
        int lines = 0;
        if(daylist.size() == 0){
            System.out.println(""+stock.getName()+"û��������");
            return 0;
        }

        ExcelAccesser exc = new ExcelAccesser(DayDir+File.separator+stock.getCode()+".xls");
        exc.open(0);

//		System.out.println("index:"+index);
        String excelDay=exc.read(0, 1);
        if(excelDay == null){
            System.out.println("û������");
            exc.close();
            return 0;
        }

        String day=((DaytimeStock)(daylist.get(index))).getDay();
        if(b){
            while(cmpDay(day,excelDay)==1){
                index++;
                if(index > downloadsize)
                    break;
                day=((DaytimeStock)(daylist.get(index))).getDay();
            }
            lines = index;
            while(index > 0){
                index--;
                try {
                    exc.insertRow(1,((DaytimeStock)(daylist.get(index))).toVector());
                } catch (RowsExceededException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exc.close();
                    return 0;
                } catch (WriteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exc.close();
                    return 0;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exc.close();
                    return 0;
                }
            }
            exc.close();
            return lines;
        }
        return 0;
    }

    public void RefreshData(){
        File file = new File(DayDir+File.separator+stock.getCode()+".xls");
        if(file.exists()){
            System.out.println(stock.getName()+"download");
            updateToExcel();
        }else{
            boolean b=downloadFromWangyi();
            if(b){
                try{
                    saveToExcel();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public int readFromFile(int length){
        if(length <=0){
            System.out.println("StockDayListSina:readFromFile ="+length);
            return 0;
        }
        ExcelAccesser exc = new ExcelAccesser(DayDir+File.separator+stock.getCode()+".xls");
        boolean openState = false;
        openState = exc.openOnly(0);
        if(!openState){
            System.out.println("StockDayListSina:readFromFile open File "+stock.getCode()+" fail");
            return 0;
        }
        int index = 0;
        while(index < length){
            int row = 1+index;
            String date = exc.read(0, row);
            String sOpenPrice = exc.read(1, row);
            String sHighestPrice = exc.read(2, row);
            String sLowestPrice = exc.read(3, row);
            String sClosePrice = exc.read(4, row);
            String sHandoverMount = exc.read(5, row);
            String sHandoverPrice = exc.read(6, row);
            if(sOpenPrice==null||sHighestPrice==null||sLowestPrice==null||sClosePrice==null||sHandoverMount==null||sHandoverPrice==null){
                exc.close();
                return index;
            }
            float openPrice = Float.parseFloat(sOpenPrice);
            float highestPrice = Float.parseFloat(sHighestPrice);
            float lowestPrice = Float.parseFloat(sLowestPrice);
            float closePrice = Float.parseFloat(sClosePrice);
            float handoverMount = Float.parseFloat(sHandoverMount);
            float handoverPrice = Float.parseFloat(sHandoverPrice);
            //	float avgHandoverPrice = Float.parseFloat(exc.read(8, row));
            if(date!=null){
                DaytimeStock daytime = new DaytimeStock(date,openPrice,highestPrice,lowestPrice,closePrice,handoverMount,handoverPrice);
                // daylist.addFirst(daytime);
                daylist.addLast(daytime);
            }else{
                exc.close();
                return index;
            }
            index++;
        }
        exc.close();
        //2013-06-08���Ӻ�Ȩȫ�ֿ��Ʋ���
        if(Constant.restoration == 1){
            backwardPrice();
        }

        return index;
    }
    //��ȡdate1��date2֮�������,δ���
    public int readFromFile(String date1,String date2){
        if(cmpDay(date1,date2)>0){
            return 0;
        }
        System.out.println(""+stock.getName());
        ExcelAccesser exc = new ExcelAccesser(DayDir+File.separator+stock.getCode()+".xls");
        boolean openState = false;
        openState = exc.openOnly(0);
        if(!openState){
            System.out.println("StockDayListSina:readFromFile open File "+stock.getCode()+" fail");
            return 0;
        }
        int row = 1;
        int readline = 0;
        String date = exc.read(0, row);
        //���������ڷǿգ����� excel���ڴ��ڽ�ֹ����date2��������¶�ȡ
        while(date!= null && cmpDay(date,date2)>0){
            row = row +1;
            date = exc.read(0, row);
        }
        if(date == null){
            exc.close();
            return 0;
        }
        //���������ڷǿգ����� excel���ڴ��ڵ��ڽ�ֹ����date1��������¶�ȡ
        while(date!= null && cmpDay(date,date1)>=0){

            String sOpenPrice = exc.read(1, row);
            String sHighestPrice = exc.read(2, row);
            String sLowestPrice = exc.read(3, row);
            String sClosePrice = exc.read(4, row);
            String sHandoverMount = exc.read(5, row);
            String sHandoverPrice = exc.read(6, row);
            if(sOpenPrice==null||sHighestPrice==null||sLowestPrice==null||sClosePrice==null||sHandoverMount==null||sHandoverPrice==null){
                exc.close();
                return readline;
            }
            float openPrice = Float.parseFloat(sOpenPrice);
            float highestPrice = Float.parseFloat(sHighestPrice);
            float lowestPrice = Float.parseFloat(sLowestPrice);
            float closePrice = Float.parseFloat(sClosePrice);
            float handoverMount = Float.parseFloat(sHandoverMount);
            float handoverPrice = Float.parseFloat(sHandoverPrice);

            if(date!=null){
                DaytimeStock daytime = new DaytimeStock(date,openPrice,highestPrice,lowestPrice,closePrice,handoverMount,handoverPrice);
                // daylist.addFirst(daytime);
                daylist.addLast(daytime);
            }else{
                exc.close();
                return readline;
            }
            row++;
            readline++;
            date = exc.read(0, row);
        }


        exc.close();
        //2013-06-08���Ӻ�Ȩȫ�ֿ��Ʋ���
        if(Constant.restoration == 1){
            backwardPrice();
        }
//		while(cmpDay(date1,date)==1){
//			line = r.readLine();
//			array = line.split(" ");
//		}

        return readline;
    }

    public void backwardPrice(){
        if(daylist == null || daylist.size()==0){
            return;
        }
        int daysize = daylist.size();
        String firstDay = ((DaytimeStock)(daylist.getLast())).getDay();
        dispatchlist = new StockBonusDispatchList(stock);
        int n = dispatchlist.readFromFile(firstDay);
        //��ǰ��ȡ�����б���û�г�Ȩ���䣬��ֱ�ӷ���
        if(n == 0){
            return;
        }
        dispatchlist.parseMethod();
        //��ǰ��ȡ�����б�����n�ηֺ�����,��ÿ�ζԳ����ֺ��������ݽ��и�Ȩ����
        for(int k=0;k<n;k++){
            BonusDispatch bonusk= (BonusDispatch)(dispatchlist.getDispatchList().get(k));
            String bonusDay = bonusk.getDay();
            float plusPrefix = (bonusk.getPlusPrefix()+10.0f)/10.0f;
            float addPrefix = bonusk.getAddPrefix()/10.0f;
            for(int i = 0; i<daysize; i++) {
                String sDay = ((DaytimeStock)(daylist.get(i))).getDay();
                if(cmpDay(sDay,bonusDay)>=0){
                    ((DaytimeStock)(daylist.get(i))).AdjustPrice(plusPrefix, addPrefix);
                }else{
                    break;
                }
            }
        }

    }

    public static void main(String[] args) {
        StockInfo s = new StockInfo("600273","��ѣ�");
        StockDayListWangyi stockList = new StockDayListWangyi(s);
        stockList.RefreshData();

    }

}
