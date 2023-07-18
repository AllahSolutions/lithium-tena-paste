package dev.tenacity.anticheat;

import dev.tenacity.anticheat.checks.flight.FlightA;
import dev.tenacity.anticheat.checks.flight.FlightB;
import dev.tenacity.anticheat.checks.combat.ReachA;
import dev.tenacity.anticheat.checks.phase.PhaseA;

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
