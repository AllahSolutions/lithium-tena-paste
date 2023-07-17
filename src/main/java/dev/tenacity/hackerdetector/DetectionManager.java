package dev.tenacity.hackerdetector;

import dev.tenacity.hackerdetector.checks.flight.FlightA;
import dev.tenacity.hackerdetector.checks.flight.FlightB;
import dev.tenacity.hackerdetector.checks.combat.ReachA;
import dev.tenacity.hackerdetector.checks.phase.PhaseA;

import java.util.ArrayList;
import java.util.Arrays;

public class DetectionManager {

    private ArrayList<Detection> detections = new ArrayList<>();

    public DetectionManager() {
        addDetections(

                // Combat
                new ReachA(),

                // Movement
                new FlightA(),
                new FlightB(),
                new PhaseA()

                // Player

                // Misc

                // Exploit

        );
    }

    public void addDetections(Detection... detections) {
        this.detections.addAll(Arrays.asList(detections));
    }

    public ArrayList<Detection> getDetections() {
        return detections;
    }
}
