package ga;

import java.time.DayOfWeek;
import java.util.Objects;

/**
 * Simple timeslot abstraction -- just represents a timeslot (like "Wed 9:00am-11:00am").
 *  
 * @author bkanber
 *
 */
public class Timeslot {
    private int timeslotId;
    private final DayOfWeek dayOfWeek;
    private final String timeslot;

    /**
     * Initalize new Timeslot
     * 
     * @param timeslotId The ID for this timeslot
     * @param timeslot The timeslot being initalized
     */
    public Timeslot(int timeslotId, DayOfWeek dayOfWeek, String timeslot){
        this.timeslotId = timeslotId;
        this.dayOfWeek = dayOfWeek;
        this.timeslot = timeslot;
    }

    /**
     * Initalize new Timeslot
     *
     * @param timeslot The timeslot being initalized
     */
    public Timeslot(DayOfWeek dayOfWeek, String timeslot){
        this.dayOfWeek = dayOfWeek;
        this.timeslot = timeslot;
    }
    
    /**
     * Returns the timeslotId
     * 
     * @return timeslotId
     */
    public int getTimeslotId(){
        return this.timeslotId;
    }


    /**
     * Returns the dayOfWeek
     *
     * @return dayOfWeek
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Returns the timeslot
     * 
     * @return timeslot
     */
    public String getTimeslot(){
        return this.timeslot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Timeslot)) return false;
        Timeslot timeslot1 = (Timeslot) o;
        return timeslotId == timeslot1.timeslotId &&
                Objects.equals(timeslot, timeslot1.timeslot);
    }

    @Override
    public int hashCode() {

        return Objects.hash(timeslotId, timeslot);
    }
}
