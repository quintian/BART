package ds.project4task1;

public class TrainSchedule {
    //String url;
    String trainNo;
    String date;
    String station;
    String origTime;

    public TrainSchedule( String index, String date, String station, String origTime) {
        //this.url=urlString;
        this.trainNo=index;
        this.date=date;
        this.station=station;
        this.origTime=origTime;
    }
}
