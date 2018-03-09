package ga;

import java.util.Objects;

/**
 * A simple class abstraction -- basically a container for class, group, module, professor, timeslot, and room IDs
 */
public class Class {
    private int classId;
    private final Group group;
    private final Module module;
    private Professor professor;
    private Timeslot timeslot;
    private Room room;
    
    /**
     * Initialize new Class
     * 
     * @param classId
     * @param group
     * @param module
     */
    public Class(int classId, Group group, Module module){
        this.classId = classId;
        this.module = module;
        this.group = group;
    }

    /**
     * Initialize new Class
     *
     * @param group
     * @param module
     */
    public Class(Group group, Module module){
        this.module = module;
        this.group = group;
    }
    
    /**
     * Add professor to class
     * 
     * @param professor
     */
    public void addProfessor(Professor professor){
        this.professor = professor;
    }
    
    /**
     * Add timeslot to class
     * 
     * @param timeslot
     */
    public void addTimeslot(Timeslot timeslot){
        this.timeslot = timeslot;
    }    
    
    /**
     * Add room to class
     * 
     * @param room
     */
    public void setRoom(Room room){
        this.room = room;
    }
    
    /**
     * Get classId
     * 
     * @return classId
     */
    public int getClassId(){
        return this.classId;
    }
    
    /**
     * Get groupId
     * 
     * @return group
     */
    public Group getGroup(){
        return this.group;
    }
    
    /**
     * Get module
     * 
     * @return module
     */
    public Module getModule(){
        return this.module;
    }
    
    /**
     * Get professorId
     * 
     * @return professorId
     */
    public Professor getProfessor(){
        return this.professor;
    }
    
    /**
     * Get timeslotId
     * 
     * @return timeslotId
     */
    public Timeslot getTimeslot(){
        return this.timeslot;
    }
    
    /**
     * Get roomId
     * 
     * @return roomId
     */
    public Room getRoom(){
        return this.room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Class)) return false;
        Class aClass = (Class) o;
        return classId == aClass.classId &&
                Objects.equals(group, aClass.group) &&
                Objects.equals(module, aClass.module) &&
                Objects.equals(professor, aClass.professor) &&
                Objects.equals(timeslot, aClass.timeslot) &&
                Objects.equals(room, aClass.room);
    }

    @Override
    public int hashCode() {

        return Objects.hash(classId, group, module, professor, timeslot, room);
    }
}

