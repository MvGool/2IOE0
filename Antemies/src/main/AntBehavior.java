package main;

import engine.graphics.Mesh;
import engine.maths.Vector3f;
import engine.objects.AntObject;
import engine.objects.GameObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AntBehavior implements Runnable {

    private Thread t;

    public ArrayList<AntObject> ants;
//    public ArrayList<FoodSource> sources;
    public GameObject base;



    public enum LeaveRequestState {
        Idle {
        },
        goToSource {
        },
        goToBase {
        };
    }
    static LeaveRequestState state = LeaveRequestState.Idle;



    public void run() {
        while (true) {
            /**
            for (AntObject ant : ants){
                if (!sources.isEmpty()){
                    if (ant.state == LeaveRequestState.Idle && !ant.move ){
                        ant.moveTo(sources.get(0));
                        ant.state = LeaveRequestState.goToSource;
                    } else if (ant.state == LeaveRequestState.goToSource && !ant.move){
                        ant.moveTo(base);
                        ant.state = LeaveRequestState.goToBase;
                    } else if (ant.state == LeaveRequestState.goToBase && !ant.move){
                        ant.state = LeaveRequestState.Idle;
                    }
                }
            }
             */
        }
    }
    public void start(){
        System.out.println("starting");
        t = new Thread (this);
        if (t == null) {
            t = new Thread (this);
            t.start ();
        }
    }
}

class TestThread {

    public static void main(String args[]) throws InterruptedException {


    }
}
