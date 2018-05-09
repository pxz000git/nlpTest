package com.foda;

import java.io.*;
import java.util.*;
import com.mongodb.client.FindIterable;
import com.opencsv.CSVReader;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Projections.*;

public class StanfordChineseNlpExample {
    public static void main(String[] args) throws Exception {
        StanfordNlp nlp = new StanfordNlp();

/*
        //String text = "叶凌：  经查，你隐瞒了在南平市建阳区莒口镇生态保护工作站工作的事实，存在提供虚假材料以欺骗手段取得二级建造师注册证书的行为。本厅于2017年9月8日作出《福建省住房和城乡建设厅撤销行政许可告知书》（闽建许函〔2017〕92号），并于2017年9月20日送达给你，你在规定的期限内未向本厅提出书面陈述、申辩。  根据《中华人民共和国行政许可法》第六十九条、第七十九条和《注册建造师管理规定》第三十四条规定，本厅决定撤销对你作出的二级建造师注册许可，自作出撤销决定之日";
        String text = "近日，青岛7岁男孩“小长江”随叔叔送快递在网络引发热议。14日，据青岛市市北区人民政府新闻办公室通报，14日，市北区有关部门已将小长江送至青岛市儿童福利院，并安排专人陪护。15日，将先行安排小长江入学读书。通报表示，经了解，小长江自称今年约7岁，父亲去世，母亲已改嫁。其父亲生前工友颜先生带小长江一起从老家枣庄来青务工，居住生活在徐州北路6号申通快递点。此外，市北区有关部门将赴小长江母亲所在地，与当地派出所及孩子母亲取得联系，依法就孩子监护及户口问题进行协商。在此之前，市北区将妥善安排好小长江的学习生活，确保孩子身心健康。据媒体此前报道，今年7岁的男孩小长江父亲过世，母亲改嫁，此前随父亲生前工友颜世芳在青岛打工，帮其送快递，每日能送30件。";
        //String text = "斯坦福大学在加利福尼亚，这是个很棒的大学。这个学校的老师也很优秀。";
        //String text = readTxt("/Users/fodaai01/Documents/html.txt");

        // 使用simpleNlp处理
        long startTime = System.currentTimeMillis();
        System.out.println(nlp.simpleNlp(text));
        long endTime = System.currentTimeMillis();
        System.out.println("程序耗时：" + (endTime - startTime) + "ms");*/

        // 使用coreNlp处理
//        long startTime = System.currentTimeMillis();
//        System.out.println(nlp.runChineseAnnotators(text));
//        long endTime = System.currentTimeMillis();
//        System.out.println("程序耗时：" + (endTime - startTime) + "ms");

        // 读取mongo数据并解析
        int index = 1;
        MongoDBJDBC mongoDBJDBC = new MongoDBJDBC("175.102.18.112", 27018);
        MongoCollection<Document> doc = mongoDBJDBC.getCollections("tongji_zjj", "lda_sum_data");
        BufferedWriter out = null;
        try {
            FileWriter fw = new FileWriter("./phrase.csv", true);
            out = new BufferedWriter(fw);
            // 设置batchSize为30
            for (Document document : doc.find().batchSize(30).projection(
                    Projections.fields(
                            Projections.include("html"),
                            Projections.excludeId())).limit(5000)) {
                String str = document.get("html").toString();
                System.out.println(Integer.toString(index) + "\t" + str);
//            nlp.runChineseAnnotators(document.get("html").toString());
                String result = nlp.simpleNlp(str);
                out.append(str);
                out.append(",");
                out.append(result);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


// 读取一个csv文件并解析语法
    /*public void csvParse(StanfordNlp example) throws Exception{
        //File file=new File("/Users/fodaai01/Downloads/chatdata.csv");
        FileReader fileReader = new FileReader("/Users/fodaai01/Downloads/chatdata_utf_8.csv");
        CSVReader csvReader = new CSVReader(fileReader);
        csvReader.readNext();   // 跳过表头
        List<String[]> list = csvReader.readAll();
        for (String[] str : list) {
            try {
                System.out.println(str[1]);
                example.runChineseAnnotators(str[1]);
                System.out.println("--------------------------");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }*/

// 读取一个txt文件
    /*public static String readTxt(String filePath) throws Exception{
        File filename = new File(filePath);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String content = "";
        String newline = br.readLine();

        while (newline != null) {
            content += newline;
            newline = br.readLine();
        }
        return content;
    }*/
}

