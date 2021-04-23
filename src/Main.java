package src;

import java.io.File;
import java.util.ArrayList;

import src.object.CarSimulator;
import src.object.Network;
import src.object.RsuSimulator;

public class Main {

    public static ArrayList<RsuSimulator> getRsuList() {
        ArrayList<RsuSimulator> res = new ArrayList<>();
        String[] xList = Config.xList.split(";");
        String[] yList = Config.yList.split(";");
        String[] zList = Config.zList.split(";");
        for (int i = 0; i < Config.rsuNumbers; i++) {
            double xcord = Double.parseDouble(xList[i]);
            double ycord = Double.parseDouble(yList[i]);
            double zcord = Double.parseDouble(zList[i]);
            RsuSimulator rsu = new RsuSimulator(i, xcord, ycord, zcord);
            res.add(rsu);
        }
        return res;
    }

    public static ArrayList<CarSimulator> getCarList() {
        ArrayList<CarSimulator> res = new ArrayList<>();
        double currentTime = 0;
        int index = 0;
        while (true) {
            double timeStartCar = currentTime + Config.timeACarAppear;
            if (timeStartCar > Config.simTime) {
                return res;
            }
            CarSimulator car = new CarSimulator(index, timeStartCar);
            res.add(car);
            index ++;
            currentTime = timeStartCar;
        }
    }

    public static void run() {
        File dumpDetailFolder = new File(Config.dumpDetailFolder);
        if (!dumpDetailFolder.exists()) {
            dumpDetailFolder.mkdirs();
        }
        else {
            for (File file : dumpDetailFolder.listFiles()) {
                file.delete();
            }
        }
        ArrayList<RsuSimulator> rsuList = getRsuList();
        ArrayList<CarSimulator> carList = getCarList();
        System.out.println(carList.size());
        Network network = new Network(rsuList, carList);

        network.run();
        
    }
    public static void main(String[] args) {
        double startTime = System.currentTimeMillis();
        run();
        double stopTime = System.currentTimeMillis();
        System.out.println(stopTime - startTime);
    }
    
}

