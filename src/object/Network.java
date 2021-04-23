package src.object;

import java.util.ArrayList;
import java.util.PriorityQueue;

import src.Config;
import src.objectMethod.NetworkMethod;

public class Network {
    public GnbSimulator gnb = new GnbSimulator();
    public ArrayList<RsuSimulator> rsuList;
    public ArrayList<CarSimulator> carList;
    public PriorityQueue<Message> queue = new PriorityQueue<>();
    public ArrayList<Message> output = new ArrayList<>();
    public double meanDelay = 0.0;
    public double maxDelay = 0.0;
    public int countDrop = 0;
    public int totalOutsize = 0;
    public int cntType1 = 0;
    public int cntType2 = 0;
    public int cntType3 = 0;

    public Network(ArrayList<RsuSimulator> rsuList, ArrayList<CarSimulator> carList) {
        this.rsuList = rsuList;
        this.carList = carList;
    }

    public void setNeighborCar() {
        double distanceTwoCarContinual = Config.carSpeed * Config.timeACarAppear;
        int numCarCover = (int)(Config.carCoverRadius / distanceTwoCarContinual);
        for (int i = 0; i < carList.size(); i++) {
            for (int j = Math.max(0, i-numCarCover); j < Math.min(carList.size(), i+numCarCover+1); j++) {
                if (j == i) continue;
                carList.get(i).neighborCars.add(carList.get(j));
            }
        }
    }

    public void setNeighborRsu(CarSimulator car, double currentTime) {
        if (car.neighborRsu != null) {
            if (car.distanceToRsu(car.neighborRsu, currentTime) <= Config.rsuCoverRadius) {
                return;
            }
        }
        double minDistance = Config.rsuCoverRadius;
        RsuSimulator neighborRsu = null;
        for (RsuSimulator rsu : rsuList) {
            double distance = car.distanceToRsu(rsu, currentTime);
            if (distance < minDistance) {
                minDistance = distance;
                neighborRsu = rsu;
            }
        }
        car.neighborRsu = neighborRsu;
    }

    public void working(double currentTime) {
        System.out.println(Config.expName + " " + Config.carPackageStrategy + " current time " + currentTime);
        // ArrayList<Message> messages = new ArrayList<>();
        for (CarSimulator car : carList) {
            if (car.getPosition(currentTime) > Config.roadLength || car.startTime > currentTime) {
                continue;
            }
            setNeighborRsu(car, currentTime);
            car.collectMessage(currentTime, this);
        }
        for (RsuSimulator rsu : rsuList) {
            rsu.collectMessage(currentTime, this);
        }
        gnb.collectMessage(currentTime, this);
        // for (Message mes : messages) {
        //     queue.add(mes);
        // }
        while (!queue.isEmpty()) {
            Message mes = queue.poll();
            int location = mes.locations.get(mes.locations.size()-1);
            if (location == 0) {
                int carId = mes.indexCar.get(mes.indexCar.size()-1);
                CarSimulator car = carList.get(carId);
                car.working(mes, currentTime, this);
            }
            else if (location == 1) {
                int rsuId = mes.indexRsu.get(mes.indexRsu.size()-1);
                RsuSimulator rsu = rsuList.get(rsuId);
                rsu.working(mes, currentTime, this);
            }
            else {
                gnb.working(mes, currentTime, this);
            }
        }
    }

    public void run() {
        setNeighborCar();
        double currentTime = 0;
        while (currentTime < Config.simTime) {
            working(currentTime);
            NetworkMethod.dumpOutputPerCycle(this, currentTime);
            currentTime += Config.cycleTime;
        }
        NetworkMethod.dumpOutputFinal(this);
    }


    public static void main(String[] args) {
        PriorityQueue<Message> q = new PriorityQueue<>();
        q.add(new Message(1, 0.1));
        q.add(new Message(2, 0.2));
        q.add(new Message(3, 0.1));
        while (!q.isEmpty()) {
            Message mes = q.poll();
            System.out.println(mes.startTime + " " + mes.indexCar.get(0));
        }
    }
}

