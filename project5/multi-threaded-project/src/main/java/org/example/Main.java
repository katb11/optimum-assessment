package org.example;

import java.util.*;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class Main {

    public static void main(String[] args) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            Subtask<ArrayList<String>> subtaskA = scope.fork(Main::taskA);
            Subtask<ArrayList<String>> subtaskB = scope.fork(Main::taskB);

            try {
                // wait
                scope.join();

                ArrayList<String> resultAfinal;
                ArrayList<String> resultBfinal = new ArrayList<>();
                try {
                    resultAfinal = subtaskA.get();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    resultAfinal = recoveryTask();
                }

                try {
                    resultBfinal = subtaskB.get();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    scope.throwIfFailed();
                }

                ArrayList<String> resultA = resultAfinal;
                ArrayList<String> resultB = resultBfinal;

                Subtask<HashMap<String, String>> subtaskC = scope.fork(() -> taskC(resultA, resultB));
                Subtask<HashMap<String, String>> subtaskD = scope.fork(() -> taskD(resultA, resultB));

                // wait
                scope.join();

                HashMap<String, String> resultC = new HashMap<>();
                HashMap<String, String> resultD = new HashMap<>();
                try {
                    resultC = subtaskC.get();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    scope.throwIfFailed();
                }

                try {
                    resultD = subtaskD.get();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    scope.throwIfFailed();
                }

                HashMap<String, String> resultF = taskF(resultC, resultD);

                for (Map.Entry<String, String> entry : resultF.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }

            } catch (InterruptedException e) {
                System.out.println("Error running task threads. Exiting...");
            } finally {
                scope.shutdown();
            }
        } catch(Exception e) {
            // thread pool couldn't be initialized - handle tasks synchronously and trigger alarm
            ArrayList<String> resultA = taskA();
            ArrayList<String> resultB = taskB();
            HashMap<String, String> resultC = taskC(resultA, resultB);
            HashMap<String, String> resultD = taskD(resultA, resultB);
            HashMap<String, String> resultF = taskF(resultC, resultD);
        }
    }

    // recoverable
    public static ArrayList<String> taskA() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Test1");
        data.add("tESt2");
        data.add("tesT3");

        return data;
    }

    public static ArrayList<String> taskB() {
        ArrayList<String> data = new ArrayList<>();
        data.add("TEst4");
        data.add("TeSt5");
        data.add("tesT6");

        return data;
    }

    public static HashMap<String, String> taskC(ArrayList<String> dataA, ArrayList<String> dataB) {
        HashMap<String, String> data = new HashMap<>();
        for (int i = 0; i < dataA.size(); i++) {
            data.put(dataA.get(i).toUpperCase(), dataB.get(i).toUpperCase());
        }
        return data;
    }

    public static HashMap<String, String> taskD(ArrayList<String> dataA, ArrayList<String> dataB)  {
        HashMap<String, String> data = new HashMap<>();
        for (int i = 0; i < dataA.size(); i++) {
            data.put(dataA.get(i).toLowerCase(), dataB.get(i).toLowerCase());
        }
        return data;
    }

    public static HashMap<String,String> taskF(HashMap<String,String> resultC, HashMap<String,String> resultD) {
        HashMap<String, String> finalData = new HashMap<>();
        finalData.putAll(resultC);
        finalData.putAll(resultD);
        return finalData;
    }

    public static ArrayList<String> recoveryTask() {
        ArrayList<String> data = new ArrayList<>();
        data.add("test7");
        data.add("test8");
        data.add("test9");

        return data;
    }

}