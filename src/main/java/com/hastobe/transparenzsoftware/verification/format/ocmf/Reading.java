package com.hastobe.transparenzsoftware.verification.format.ocmf;

import com.hastobe.transparenzsoftware.i18n.Translator;

import java.beans.Transient;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public abstract class Reading {


    /**
     * Time string with synchronicity as last character of the string
     * ISO8601 date format + 1 character
     */
    protected String TM;
    /**
     * Transaction code can be null
     */
    protected String TX;

    /**
     * Reading value (meter value)
     */
    protected Double RV;
    /**
     * Reading Identification (OBIS Code)
     */
    protected String RI;

    /**
     * Reading Unit e.g. kWh
     */
    protected String RU;

    /**
     * Shortcode of the status of the meter
     * <p>
     * Valid values are
     * - N NOT_PRESENT
     * - G OK
     * - T TIMEOUT
     * - D DISCONNECTED
     * - R NOT_FOUND
     * - M MANIPULATED
     * - X EXCHANGED
     * - I INCOMPATIBLE
     * - O OUT OF RANGE
     * - S SUBSTIUTE
     * - E OTHER_ERROR
     * - F READ_ERROR
     */
    private String ST;

    /**
     * Parses the timestamp out of the TM field
     *
     * @return OffsetDateTime object or null if it could not be parsed
     */
    public OffsetDateTime getTimestamp() {
        if (TM == null) {
            return null;
        }
        String[] splitted = TM.split(" ");
        if (splitted.length < 2) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss,SSSZ");
            return OffsetDateTime.parse(splitted[0], formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Calculates the synchronicity code out of the timestamp field
     * - U - unknown
     * - I - informative
     * - S - synchron
     *
     * @return synchronicity code
     */
    public String getTimeSynchronicity() {
        if (TM == null) {
            return null;
        }
        String[] splitted = TM.split(" ");
        if (splitted.length < 2) {
            return null;
        }
        return splitted[1];
    }

    public boolean isTimeInformativeOnly(){
        String syncr = getTimeSynchronicity();
        if(syncr == null){
            return true;
        }
        return !syncr.toUpperCase().equals("R") && !syncr.toUpperCase().equals("S");
    }

    public String getLabelForTimeFlag(){
        if(this.getTimeSynchronicity() == null){
            return null;
        }
        String labelForFlag = "app.verify.ocmf.timesynchronicity.unknown";
        switch(this.getTimeSynchronicity()) {
            case "I":
                labelForFlag = "app.verify.ocmf.timesynchronicity.informative";
                break;
            case "S":
                labelForFlag = "app.verify.ocmf.timesynchronicity.synchronised";
                break;
            case "R":
                labelForFlag = "app.verify.ocmf.timesynchronicity.relative";
                break;
            case "U":
            default:
        }
        return labelForFlag;
    }

    public String getTM() {
        return TM;
    }

    public void setTM(String TM) {
        this.TM = TM;
    }

    public String getTX() {
        return TX;
    }

    public void setTX(String TX) {
        this.TX = TX;
    }

    public Double getRV() {
        return RV;
    }

    public void setRV(Double RV) {
        this.RV = RV;
    }

    public String getRI() {
        return RI;
    }

    public void setRI(String RI) {
        this.RI = RI;
    }

    public String getRU() {
        return RU;
    }

    public void setRU(String RU) {
        this.RU = RU;
    }

    public String getST() {
        return ST;
    }

    public void setST(String ST) {
        this.ST = ST;
    }

    public boolean isStartTransaction() {
        if (getTX() != null && getTX().trim().equals("B")) {
            return true;
        }
        return false;
    }

    public boolean isStopTransaction() {
        List<String> stopCodes = Arrays.asList("E", "L", "R", "A", "P");
        if (getTX() != null && stopCodes.contains(getTX().trim())) {
            return true;
        }
        return false;
    }


    public Double getEI(){
        return null;
    }

    public String getEF(){
        return null;
    }
}
