package main;

import java.util.concurrent.TimeUnit;

public class AntBehavior implements Runnable {

    private Thread t;

    public static boolean foodSource = false;
    int baseX;
    int baseY;
    int playerX;
    int playerY;

    public enum LeaveRequestState {

        Idle {
            @Override

            public LeaveRequestState nextState() {

                if (foodSource ) {
                    return goToSource;
                } else {
                    return Following;
                }
            }

            @Override
            public void runTask() {
                System.out.println("IDLE STATE");
                state = this.nextState();
            }
        },
        Following {
            @Override
            public LeaveRequestState nextState() {
                if(foodSource){
                    return Idle;
                } else return this;

            }

            @Override
            public void runTask() {
                System.out.println("Following");
                //TODO Implement Following activity
                //NOTE: How will following be implemented? Go to neighbor square of player if its free?
                state = this.nextState();
            }
        },
        goToSource {
            @Override
            public LeaveRequestState nextState() {
                return goToBase;
            }

            @Override
            public void runTask() {
                System.out.println("Going to source");
                //moveTo(sourceX,sourceY);
                //if (antX == sourceX && antY == sourceY){
                state = this.nextState();
                //}
            }
        },
        goToBase {
            @Override
            public LeaveRequestState nextState() {
                return Idle;
            }

            @Override
            public void runTask() {
                System.out.println("Going to base");
                //moveto(baseX, baseY);
                //if (antX == baseX && antY == base Y) {
                state = this.nextState();
                //}
            }
        };

        public abstract LeaveRequestState nextState();
        public abstract void runTask();

    }
    static LeaveRequestState state = LeaveRequestState.Idle;

    public void run() {
        while (true) {
            state.runTask();
        }

    }

    public void start(){
        System.out.println("starting");
        if (t == null) {
            t = new Thread (this);
            t.start ();
        }
    }
}

class TestThread {

    public static void main(String args[]) throws InterruptedException {
        AntBehavior R1 = new AntBehavior();
        R1.start();
        TimeUnit.SECONDS.sleep(4);
        R1.foodSource = true;



    }
}
