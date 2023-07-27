package dev.tenacity.anticheat;

import dev.tenacity.anticheat.checks.move.flight.FlightACheck;
import dev.tenacity.anticheat.checks.move.flight.FlightBCheck;
import dev.tenacity.anticheat.checks.move.sprint.SprintACheck;
import dev.tenacity.anticheat.checks.move.sprint.SprintBCheck;

import java.util.ArrayList;
import java.util.Arrays;

public class DetectionManager {

    private ArrayList<Detection> detections = new ArrayList<>();

    public DetectionManager() {
        addDetections(

                // Movement
                new FlightACheck(),
                new FlightBCheck(),
                new SprintACheck(),
                new SprintBCheck()

        );
    }

    public void addDetections(Detection... detections) {
        this.detections.addAll(Arrays.asList(detections));
    }

    public ArrayList<Detection> getDetections() {
        return detections;
    }
}
