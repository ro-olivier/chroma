package fr.zigomar.chroma.chroma.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Trip {

    private final ArrayList<Step> steps;
    private final double cost;

    public Trip(ArrayList<Step> steps, double cost) {
        this.steps = steps;
        this.cost = cost;
    }

    Trip(String s, double cost) {
        this.steps = parseSteps(s);
        this.cost = cost;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public double getCost() {
        return cost;
    }

    public int getNumberOfSteps() { return this.steps.size(); }

    public String tripString() {
        StringBuilder result = new StringBuilder();
        for (Step s : this.steps) {
            if (!s.getEndOfTrip()) {
                result.append(s.getStop()).append(" (").append(s.getLine()).append(") # ");
            } else {
                result.append(s.getStop());
            }
        }

        return result.toString();
    }

    private ArrayList<Step> parseSteps(String s) {
        ArrayList<Step> result = new ArrayList<>();

        for (String step : s.split(" # ")) {
            if (step.contains("(")) {
                // not the last stop
                int parenthese_open = step.indexOf("(");
                int parenthese_close = step.indexOf(")");
                try {
                    result.add(new Step(step.substring(0, parenthese_open - 1),
                            step.substring(parenthese_open + 1, parenthese_close)));
                } catch (Step.EmptyStationException | Step.EmptyLineException e) {
                    e.printStackTrace();
                }
            } else {
                // this is the last stop
                try {
                    result.add(new Step(step.substring(0, step.length())));
                } catch (Step.EmptyStationException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public String toString() {
        return this.getTripAsJSON().toString();
    }

    JSONObject getTripAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("trip", this.tripString());
            json.put("cost", this.cost);
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }
}