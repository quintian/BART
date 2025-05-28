package edu.cmu.kunt.bart2;

import org.bson.types.ObjectId;
import java.sql.Timestamp;

public class Log {

    public Timestamp timestamp;
    public String date;
    public String trainNo;
    public String station;
    public String input;
    public String from3pApi;
    public String toAndroid;

    Log(
        Timestamp timestamp,
        String date,
        String trainNo,
        String station,
        String input,
        String from3pApi,
        String toAndroid){

        this.timestamp=timestamp;
        this.date=date;
        this.trainNo=trainNo;
        this.station=station;
        this.input =input;
        this.from3pApi=from3pApi;
        this.toAndroid=toAndroid;

    }

    @Override
    public String toString() {
        return
                "Timestamp= " + timestamp +
                ", Date=" + date  +
                ", TrainNo=" + trainNo  +
                ", Station=" + station  +
                ", Input=" + input  +
                ", Message from Api=" + from3pApi  +
                ", Message to Android=" + toAndroid + '\n';
    }
}
