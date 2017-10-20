package kr.hs.emirim.ssm.sensorexample;

interface MotionSensor {

    void startup();

    void shutdown();

    interface Listener {
        void onMovement();
    }

}