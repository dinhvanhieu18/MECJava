package src.object;

import java.util.ArrayList;

public class Message implements Comparable<Message>{
    public static int cnt = 0;
    public int size = 1;
    public int cpuCycle = 1;
    public int stt;
    public double startTime;
    public double currentTime;
    public boolean isDone = false;
    public boolean isDrop = false;
    public String type = "";
    public ArrayList<Integer> indexCar = new ArrayList<>();
    public ArrayList<Integer> indexRsu = new ArrayList<>();
    public ArrayList<Double> sendTime = new ArrayList<>();
    public ArrayList<Double> receiveTime = new ArrayList<>();
    public ArrayList<Integer> locations = new ArrayList<>();
    // locations: 0:car, 1:rsu, 2:gnb
    
    public Message(int indexCar, double time, int size, int cpuCycle) {
        this.indexCar.add(indexCar);
        this.locations.add(0);
        this.startTime = time;
        this.currentTime = time;
        this.stt =  Message.cnt;
        Message.cnt ++;
        this.size = size;
        this.cpuCycle = cpuCycle;
    }

    public Message(int indexCar, double time) {
        this.indexCar.add(indexCar);
        this.locations.add(0);
        this.startTime = time;
        this.currentTime = time;
        this.stt =  Message.cnt;
        Message.cnt ++;
    }

    public void setType() {
        String tmp = "";
        for (int location : locations) {
            if (location == 0) tmp += "car_";
            else if (location == 1) tmp += "rsu_";
            else tmp += "gnb_";
        }
        this.type = tmp.substring(0, tmp.length()-1);
    }

    public int compareTo(Message message) {
        if (this.currentTime < message.currentTime) return -1;
        else if (this.currentTime > message.currentTime) return 1;
        else {
            if (this.stt < message.stt) return -1;
            else return 1;
        }
    }
}
