package src.objectMethod;

import java.io.FileWriter;

import src.helper.Config;
import src.object.Message;
import src.object.Network;

public class NetworkMethod {
    public static void dumpOutputPerCycle(Network network, double currentTime) {
        if (network.output.size() == 0) return;
        network.totalOutsize += network.output.size();
        try {
            FileWriter fileWriterDelayDetail = new FileWriter(Config.dumpDelayDetailPath, true);
            // FileWriter fileWriterMessageDetail = new FileWriter(Config.MessageDetailPath, true);
            for (Message mes : network.output) {
                double delay = mes.currentTime - mes.startTime;
                network.maxDelay = Math.max(delay, network.maxDelay);
                if (mes.isDrop) {
                    network.countDrop ++;
                }
                else {
                    network.meanDelay += delay;
                }
                mes.setType();
                // fileWriterMessageDetail.write(mes.stt + "\t" + mes.indexCar.get(0) + "\t" + mes.startTime + 
                //                         " \t" + mes.currentTime + "\t" + delay + "\t" + mes.type + "\n");    
                if (mes.indexRsu.size() > 0) {
                    if (mes.locations.contains(2)) {
                        network.cntType3 ++;
                    }
                    else network.cntType2 ++;
                }        
                else network.cntType1 ++;
            }
            double meanDelay = (network.meanDelay + network.countDrop * network.maxDelay) / network.totalOutsize;
            fileWriterDelayDetail.write(currentTime + "\t" + meanDelay + "\t" + network.maxDelay + 
                                "\t" + network.totalOutsize + "\t" + network.countDrop + "\n");   
            network.output.clear();
            fileWriterDelayDetail.flush();
            // fileWriterMessageDetail.flush();
            fileWriterDelayDetail.close();
            // fileWriterMessageDetail.close();   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dumpOutputFinal(Network network) {
        double meanDelay = (network.meanDelay + network.countDrop * network.maxDelay) / network.totalOutsize;
        try {
            FileWriter fileWriterDelayGeneral = new FileWriter(Config.dumpDelayGeneralPath, true);
            fileWriterDelayGeneral.write(Config.carPackageStrategy + "\t" + Config.carAppearStrategy + "\t" + 
                                Config.rsuNumbers + "\t" + Config.expName + "\t" + meanDelay + "\t" + 
                                network.countDrop + "\t" + network.totalOutsize + "\t" + Config.pL + "\t" + 
                                Config.pR + "\t" + network.cntType1 + "\t" + network.cntType2 + "\t" + network.cntType3 + "\n");
            fileWriterDelayGeneral.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done dumping final output!!!");
    }

}
