package wjc.stock.bjtu;

import finance.stock.bjtu.Constant;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

public class StockDayListLocal extends StockDayList {
    public static String LocalDir = "D:\\StockTradeSimulator\\data\\trade\\day";
    public static String startDate = "20191120";

    public StockDayListLocal(StockInfo s) {
        super(s);
        // TODO Auto-generated constructor stub
    }

    public boolean downloadFromLocal(){
        daylist.clear();
        String code = stock.getCode();
        char first = code.charAt(0);
        String u;
        if(stock.getCode().startsWith("6")){
            u = LocalDir + '\\' + code + ".SH.xls";
        }else {
            u = LocalDir + '\\' + code + ".SZ.xls";
        }


        try {

            ExcelAccesser exc = new ExcelAccesser(u);
            boolean openState = false;
            openState = exc.openOnly(0);
            if(!openState){
                System.out.println("StockDayListLocal:readFromFile open File "+stock.getCode()+" fail");
                return false;
            }

            int row = 1;
            int readline = 0;
            String date = exc.read(1, row);
            date = date.substring(0,4) + '-' + date.substring(4,6) + '-' + date.substring(6,8);



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

    public int readFromFile(int length){
        if(length <=0){
            System.out.println("StockDayListSina:readFromFile ="+length);
            return 0;
        }
        ExcelAccesser exc = new ExcelAccesser(DayDir+ File.separator+stock.getCode()+".xls");
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

}