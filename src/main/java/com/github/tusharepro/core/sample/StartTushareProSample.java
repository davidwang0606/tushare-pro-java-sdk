package com.github.tusharepro.core.sample;

import com.github.tusharepro.core.TusharePro;
import com.github.tusharepro.core.TushareProService;
import com.github.tusharepro.core.bean.IndexDaily;
import com.github.tusharepro.core.bean.StockBasic;
import com.github.tusharepro.core.common.KeyValue;
import com.github.tusharepro.core.entity.*;
import com.github.tusharepro.core.http.Request;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import wjc.stock.bjtu.*;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StartTushareProSample {

    public static void main(String[] args) throws IOException {
        start();
    }

    public static void downloadStockDaily(String startdate, String enddate) {
        StockReader reader = new StockReader("Shanghai");
        reader.load();
        reader.add("Shenzheng");
        reader.add("zhongxiaoban");
        reader.add("chuangyeban");
        reader.add("Zhishu");
        Set<String> codelist = reader.getCode();
        codelist.forEach(item-> {
            try {
                System.out.println(item);
                appendStock(item,startdate,enddate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void appendStock(String code, String startdate, String enddate) throws IOException {

        StockInfo stock = new StockInfo(code,"平安银行");
        StockDayListMsn stockdaylist = new StockDayListMsn(stock);
        if (code.startsWith("s")){

            if (code.equals("sh000001")){ code = "000001.SH";}
            else if (code.equals("sh000016")){ code = "000016.SH";}
            else if (code.equals("sz399001")){ code = "399001.SZ";}
            else if (code.equals("sh000300")){ code = "399300.SZ";}

            List<IndexDailyEntity> listDaily = TushareProService.indexDaily(new Request<IndexDailyEntity>() {}
                    .allFields()
                    .param("ts_code", code)
                    .param("end_date", enddate)
                    .param("start_date", startdate));

            listDaily.forEach(item->{
                DaytimeStock ds =new DaytimeStock(item);
                stockdaylist.insertLast(ds);
            });

        }
        else {

            if (code.startsWith("6")) {
                code = code + ".SH";
            } else {
                code = code + ".SZ";
            }

            List<DailyEntity> listDaily = TushareProService.daily(new Request<DailyEntity>() {
            }
                    .allFields()
                    .param("ts_code", code)
                    .param("end_date", enddate)
                    .param("start_date", startdate));
            listDaily.forEach(item->{
                DaytimeStock ds =new DaytimeStock(item);
                stockdaylist.insertLast(ds);
            });

        }

        stockdaylist.updateToExcelWithLocal();
    }

    private static void start() throws IOException {

        final TusharePro.Builder builder = new TusharePro.Builder()
                .setToken("bda251a804fe8a7ec888f0aa179a51725b52c3bd2cf61d98dbf149e8");  // 你的token

        TusharePro.setGlobal(builder.build());  // 设置全局配置

//        TushareProService.moneyflowHsgt(new Request<MoneyflowHsgtEntity>() {}
//            .allFields()
//            .param("trade_date", "20200210"))
//            .forEach(System.out::println);

//        TushareProService.ycCb(new Request<YcCbEntity>() {}
//                .param("ts_code", "1001.CB")
//                .param("curve_type", 1)
//                .param("trade_date", "20200203")
//                .allFields())
//                .forEach(System.out::println);

//        TushareProService.fundManager(new Request<FundManagerEntity>() {}
//            .allFields()
//            .param("ts_code", "150018.SZ"))
//            .forEach(System.out::println);

          downloadStockDaily("20200622","20200712");



//        Iterator<DailyEntity> iter = entityList.iterator();
//        while (iter.hasNext()){
//            System.out.println(iter.next());
//
//        }

        final KeyValue<String, String> list_status = StockBasic.Params.list_status.value("L");
//
//        // 打印 上海交易所所有上市的沪股通股票 信息
//        TushareProService.stockBasic(new Request<StockBasicEntity>() {}  // 使用全局配置
//                .allFields()  // 所有字段
//                .param(StockBasic.Params.exchange.value("SSE"))  // 参数
//                .param(list_status)  // 参数
//                .param("is_hs", "H"))  // 参数
//                .forEach(System.out::println);
//
        final OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
                .build();

        // 一个完整的例子
//        TushareProService.stockBasic(
//                new Request<StockBasicEntity>(builder.copy()  // 将配置拷贝
//                        .setMaxRetries(5)  // 设置重试次数, 默认为3
//                        .setRetrySleepTimeUnit(TimeUnit.SECONDS)  // 设置重试sleep单位, 默认毫秒
//                        .setRetrySleepTimeOut(60L)  // 设置重试sleep时间, 默认为0
//                        .setRequestExecutor(Executors.newSingleThreadExecutor((r -> {
//                            Thread thread = new Thread(r);
//                            thread.setDaemon(true);
//                            return thread;
//                        })))  // 设置请求线程池, 默认CachedThreadPool
//                        .setHttpFunction(requestBytes -> {  // requestBytes -> function -> responseBytes
//                            try {
//                                okhttp3.Request request = new okhttp3.Request.Builder()
//                                        .url("http://api.tushare.pro")
//                                        .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBytes))
//                                        .build();
//
//                                return defaultHttpClient.newCall(request).execute().body().bytes();
//                            }
//                            catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            return null;
//                        })
//                        .build()){}
//                .allFields()
//                .param(StockBasic.Params.exchange.value("SZSE"))
//                .param(list_status))
//                .stream()
//                .filter(x -> "银行".equals(x.getIndustry()))
//                .forEach(System.out::println);
    }

}
