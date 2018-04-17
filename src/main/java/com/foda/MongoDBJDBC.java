package com.foda;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class MongoDBJDBC {
    private String host;
    private int port;

    public MongoDBJDBC(String host,int port)
    {
        this.host=host;
        this.port=port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    // 获取集合
    public MongoCollection<Document> getCollections(String dbName,String colName)
    {
        MongoCollection<Document> collection=null;
        try{
            // 连接到 mongodb 服务
            MongoClient mongoClient = new MongoClient(this.host, this.port);

            // 连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
            collection = mongoDatabase.getCollection(colName);

        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return collection;
    }
}
