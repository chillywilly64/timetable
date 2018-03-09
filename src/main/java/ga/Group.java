package ga;

import java.util.List;
import java.util.Objects;

/**
 * A simple "group-of-students" abstraction. Defines the modules that the group is enrolled in.
 *
 */
public class Group {
    private int groupId;
    private final int groupSize;
    private final List<Module> modules;

    /**
     * Initialize Group
     * 
     * @param groupId
     * @param groupSize
     * @param modules
     */
    public Group(int groupId, int groupSize, List<Module> modules){
        this.groupId = groupId;
        this.groupSize = groupSize;
        this.modules = modules;
    }

    /**
     * Initialize Group
     *
     * @param groupSize
     * @param modules
     */
    public Group(int groupSize, List<Module> modules){
        this.groupSize = groupSize;
        this.modules = modules;
    }
    
    /**
     * Get groupId
     * 
     * @return groupId
     */
    public int getGroupId(){
        return this.groupId;
    }
    
    /**
     * Get groupSize
     * 
     * @return groupSize
     */
    public int getGroupSize(){
        return this.groupSize;
    }
        
    /**
     * Get array of group's moduleIds
     * 
     * @return moduleIds
     */
    public List<Module> getModules(){
        return this.modules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group group = (Group) o;
        return groupId == group.groupId &&
                groupSize == group.groupSize &&
                Objects.equals(modules, group.modules);
    }

    @Override
    public int hashCode() {

        return Objects.hash(groupId, groupSize, modules);
    }
}
