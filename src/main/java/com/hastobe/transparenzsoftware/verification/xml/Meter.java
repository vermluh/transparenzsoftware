package com.hastobe.transparenzsoftware.verification.xml;


import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.ValidationException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.beans.Transient;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Meter {

    private String descriptiveMessageText;

    private double value;

    //necessary as jaxb will complain about the timestamp
    @XmlTransient
    private OffsetDateTime timestamp;

    @XmlTransient
    private Type type;

    @XmlTransient
    private TimeSyncType timeSyncType;


    public Meter() {
        value = 0;
        timestamp = null;
        type = null;
    }

    public Meter(double value, OffsetDateTime timestamp) {
        this(value, timestamp, null, TimeSyncType.INFORMATIVE);
    }

    public Meter(double value, OffsetDateTime timestamp, Type type, TimeSyncType timeSyncType) {
        this.value = value;
        this.timestamp = timestamp;
        this.type = type;
        this.timeSyncType = timeSyncType;
    }

    public void setDescriptiveMessageText(String text){
        descriptiveMessageText = text;
    }

    public String getDescriptiveMessageText(){
        return descriptiveMessageText;
    }

    public double getValue() {
        return value;
    }

    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getAdditonalText() {
        List<String> additionalData = new ArrayList<>();
        if(timeSyncType != null && !timeSyncType.equals(TimeSyncType.SYNCHRON)) {
            additionalData.add(Translator.get(timeSyncType.message));
        }
        return String.join(", ", additionalData);
    }

    /**
     * Builds the difference between the lowest and highest
     * value
     *
     * @return
     */
    public static double getDifference(List<Meter> values) {
        if (values == null || values.size() < 2) {
            return 0;
        }
        double[] minmax = getMinMax(values);
        return minmax[1] - minmax[0];
    }

    private static double[] getMinMax(List<Meter> values) {
        double minimum = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        boolean startMarkerFound = false;
        boolean stopMarkerFound = false;
        for (Meter meter : values) {
            if (meter.type != null) {
                if (meter.type.equals(Type.START)) {
                    startMarkerFound = true;
                    minimum = meter.getValue();
                }
                if (meter.type.equals(Type.STOP)) {
                    stopMarkerFound = true;
                    max = meter.getValue();
                }
            }
            if (!startMarkerFound) {
                minimum = Math.min(minimum, meter.getValue());
            }
            if (!stopMarkerFound) {
                max = Math.max(max, meter.getValue());
            }

        }
        return new double[]{minimum, max};
    }

    /**
     * Builds the time difference between the lowest and highest
     * value
     *
     * @return
     */
    public static Duration getTimeDiff(List<Meter> values) {
        if (values == null || values.size() < 2) {
            return Duration.ofMillis(0);
        }

        boolean startMarkerFound = false;
        boolean stopMarkerFound = false;
        OffsetDateTime minimumTime = null;
        OffsetDateTime maximumTime = null;
        for (Meter meter : values) {
            if (meter.getTimestamp() == null) {
                continue;
            }
            if (meter.type != null && meter.type.equals(Type.START)) {
                startMarkerFound = true;
                minimumTime = meter.getTimestamp();
            }
            if (meter.type != null && meter.type.equals(Type.STOP)) {
                stopMarkerFound = true;
                maximumTime = meter.getTimestamp();
            }
            if (!startMarkerFound && (minimumTime == null || meter.getTimestamp().isBefore(minimumTime))) {
                minimumTime = meter.getTimestamp();
            }
            if (!stopMarkerFound && (maximumTime == null || meter.getTimestamp().isAfter(maximumTime))) {
                maximumTime = meter.getTimestamp();
            }
        }
        if (minimumTime == null || maximumTime == null) {
            return Duration.ofMillis(0);
        }
        return Duration.between(minimumTime, maximumTime);
    }

    public static void validateListStartStop(List<Meter> startList, List<Meter> stopList) throws ValidationException {
        if (startList == null || startList.isEmpty()) {
            throw new ValidationException("No start values", "error.values.no.start.meter.values");
        }
        if (stopList == null || stopList.isEmpty()) {
            throw new ValidationException("No stop values", "app.view.no.stop.meter.values");
        }
        double[] minmax1 = getMinMax(startList);
        double[] minmax2 = getMinMax(stopList);
        if (minmax1[1] > minmax2[0]) {
            throw new ValidationException("Stop value is less than start value", "app.view.stop.less.than.start");
        }
    }

    @XmlTransient
    public TimeSyncType getTimeSyncType() {
        return timeSyncType;
    }

    @XmlTransient
    public Type getType(){
        return type;
    }

    public enum Type {
        START("app.verify.start"),
        STOP("app.verify.end"),
        UPDATE("app.verify.update");

        public final String message;

        Type(String message) {
            this.message = message;
        }
    }

    public enum TimeSyncType {
        INFORMATIVE("app.informative"),
        SYNCHRON("app.synchron"),
        ;

        private final String message;

        TimeSyncType(String s) {
            this.message = s;
        }


    }
}
