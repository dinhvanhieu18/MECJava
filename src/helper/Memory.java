package src.helper;

import java.util.ArrayList;
import java.util.Random;

public class Memory {
    public int capacity;
    public MemoryE[] memory;
    public ArrayList<MemoryTmpE> memoryTmp = new ArrayList<>();
    int numExp;

    public Memory(int capacity) {
        this.capacity = capacity;
        this.memory = new MemoryE[capacity];
        this.memoryTmp = new ArrayList<>(10);
        this.numExp = 0;
    }

    public void addToMemory(MemoryE experience) {
        memory[numExp % capacity] = experience;
        numExp ++;
    }

    public void addToMemoryTmp(MemoryTmpE e) {
        memoryTmp.add(e);
    }

    public int getMemorySize() {
        return numExp;
    }

    public int getMemoryTmpSize() {
        return memoryTmp.size();
    }

    public MemoryTmpE getLastMemoryTmp() {
        return memoryTmp.get(memoryTmp.size()-1);
    }

    public void removeLastMemoryTmp() {
        memoryTmp.remove(memoryTmp.size()-1);
    }

    public MemoryE[] getRandomBatch(int batchSize) {
        MemoryE[] res = new MemoryE[batchSize];
        int last = 0;
        Random random = new Random();
        for (int i = 0; i < batchSize ; i++) {
            int id = random.nextInt(numExp < capacity ? numExp : capacity);
            MemoryE exp = memory[id];
            res[last] = exp;
            last ++;
        }
        return res;
    }

    public void print() {
        for (int i = 0; i < capacity; i++) {
            System.out.println(memory[i]);
        }
    }

    public static class MemoryE {
        public double[] state;
        public int action;
        public double reward;
        public double[] nextstate;
        public MemoryE(double[] state, int action) {
            this.state = state;
            this.action = action;
        }
    }

    public static class MemoryTmpE {
        public MemoryE experience;
        public int mesId;
        public MemoryTmpE(MemoryE experience, int mesId) {
            this.experience =  experience;
            this.mesId = mesId;
        }
    }

    public static void main(String[] args) {
        Memory mem = new Memory(3);
        System.out.println(mem.getMemorySize());
        double[] state = {0.0, 0.1, 0.2};
        MemoryE exp = new MemoryE(state, 1);
        mem.addToMemory(exp);
        System.out.println(mem.getMemorySize());
        mem.print();
        double[] state2 = {0.0, 0.1, 0.3};
        MemoryE exp2 = new MemoryE(state2, 0);
        mem.addToMemory(exp2);
        System.out.println(mem.getMemorySize());
        mem.print();
        double[] state3 = {0.0, 0.1, 0.3};
        MemoryE exp3 = new MemoryE(state3, 1);
        mem.addToMemory(exp3);
        System.out.println(mem.getMemorySize());
        mem.print();
        double[] state4 = {0.0, 0.1, 0.3};
        MemoryE exp4 = new MemoryE(state4, 1);
        mem.addToMemory(exp4);
        System.out.println(mem.getMemorySize());
        mem.print();
        double[] state5 = {0.0, 0.1, 0.3};
        MemoryE exp5 = new MemoryE(state5, 1);
        mem.addToMemory(exp5);
        System.out.println(mem.getMemorySize());
        mem.print();
        // MemoryE[] batchs = mem.getRandomBatch(2);
        // for (MemoryE e: batchs) {
        //     System.out.println(e);
        // }

        MemoryTmpE tmp1 = new MemoryTmpE(exp, 1);
        MemoryTmpE tmp2 = new MemoryTmpE(exp2, 2);
        mem.addToMemoryTmp(tmp1);
        mem.addToMemoryTmp(tmp2);
        System.out.println(mem.getMemoryTmpSize());
        MemoryTmpE preState = mem.getLastMemoryTmp();
        for (MemoryTmpE tmpE : mem.memoryTmp){
            System.out.println(tmpE);
        }
        System.out.println(preState);
        System.out.println(preState.experience.nextstate);
        if (preState.experience.nextstate == null) {
            System.out.println("Hihi");
        }
        System.out.println(preState.experience.reward);
        if (preState.experience.reward != 0.0) {
            System.out.println("Khonggg");
        }
        preState.experience.nextstate = state2;
        System.out.println(preState.experience.nextstate);
        System.out.println(mem.getLastMemoryTmp().experience.nextstate);
    }
}

