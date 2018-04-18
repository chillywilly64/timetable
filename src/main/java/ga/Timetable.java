package ga;

import ga.entity.*;
import ga.entity.Class;

import java.time.DayOfWeek;
import java.util.*;

/**
 * Timetable is the main evaluation class for the class scheduler GA.
 * 
 * A timetable represents a potential solution in human-readable form, unlike an
 * Individual or a chromosome. This timetable class, then, can read a chromosome
 * and develop a timetable from it, and ultimately can evaluate the timetable
 * for its fitness and number of scheduling clashes.
 * 
 * The most important methods in this class are createClasses and calcClashes.
 * 
 * The createClasses method accepts an Individual (really, a chromosome),
 * unpacks its chromosome, and creates Class objects from the genetic
 * information. Class objects are lightweight; they're just containers for
 * information with getters and setters, but it's more convenient to work with
 * them than with the chromosome directly.
 * 
 * The calcClashes method is used by GeneticAlgorithm.calcFitness, and requires
 * that createClasses has been run first. calcClashes looks at the Class objects
 * created by createClasses, and figures out how many hard constraints have been
 * violated.
 * 
 */
public class Timetable {
	private final Map<Integer, Room> rooms;
	private final Map<Integer, Professor> professors;
	private final Map<Integer, Module> modules;
	private final Map<Integer, Group> groups;
	private final Map<Integer, Timeslot> timeslots;
	private Class[][][] classes;

	private int numClasses = 0;
	private List<String> dayTimeslot;

	/**
	 * Initialize new Timetable
	 */
	public Timetable() {
		this.rooms = new HashMap<>();
		this.professors = new HashMap<>();
		this.modules = new HashMap<>();
		this.groups = new HashMap<>();
		this.timeslots = new HashMap<>();
	}

	/**
	 * "Clone" a timetable. We use this before evaluating a timetable so we have
	 * a unique container for each set of classes created by "createClasses".
	 * Truthfully, that's not entirely necessary (no big deal if we wipe out and
	 * reuse the .classes property here), but Chapter 6 discusses
	 * multi-threading for fitness calculations, and in order to do that we need
	 * separate objects so that one thread doesn't step on another thread's
	 * toes. So this constructor isn't _entirely_ necessary for Chapter 5, but
	 * you'll see it in action in Chapter 6.
	 * 
	 * @param cloneable
	 */
	public Timetable(Timetable cloneable) {
		this.rooms = cloneable.getRooms();
		this.professors = cloneable.getProfessors();
		this.modules = cloneable.getModules();
		this.groups = cloneable.getGroups();
		this.timeslots = cloneable.getTimeslots();
		this.dayTimeslot = cloneable.getDayTimeslot();
	}

	public Map<Integer, Room> getRooms() {
		return this.rooms;
	}

	public Map<Integer, Group> getGroups() {
		return this.groups;
	}

	public Map<Integer, Timeslot> getTimeslots() {
		return this.timeslots;
	}

	public Map<Integer, Module> getModules() {
		return this.modules;
	}

	public Map<Integer, Professor> getProfessors() {
		return this.professors;
	}

	public List<String> getDayTimeslot() {
		return this.dayTimeslot;
	}


	/**
	 * Add new room
	 * 
	 * @param roomId
	 * @param roomName
	 * @param capacity
	 */
	public void addRoom(int roomId, String roomName, int capacity) {
		this.rooms.put(roomId, new Room(roomId, roomName, capacity));
	}

	/**
	 * Add new room
	 *
	 * @param room
	 */
	public void addRoom(Room room) {
		this.rooms.put(room.getRoomId(), room);
	}

	/**
	 * Add new professor
	 * 
	 * @param professorId
	 * @param professorName
	 */
	public void addProfessor(int professorId, String professorName) {
		this.professors.put(professorId, new Professor(professorId, professorName));
	}

	/**
	 * Add new professor
	 *
	 * @param professor
	 */
	public void addProfessor(Professor professor) {
		this.professors.put(professor.getProfessorId(), professor);
	}

	/**
	 * Add new module
	 * 
	 * @param moduleId
	 * @param moduleCode
	 * @param module
	 * @param professors
	 */
	public void addModule(int moduleId, String moduleCode, String module, int numberOfClassesPerWeek, List<Professor> professors) {
		this.modules.put(moduleId, new Module(moduleId, module, numberOfClassesPerWeek, professors));
	}

	/**
	 * Add new module
	 *
	 * @param module
	 */
	public void addModule(Module module) {
		this.modules.put(module.getModuleId(), module);
	}

	/**
	 * Add new group
	 * 
	 * @param groupId
	 * @param groupSize
	 * @param modules
	 */
	public void addGroup(int groupId, int groupSize, List<Module> modules) {
		this.groups.put(groupId, new Group(groupId, groupSize, modules));
		this.numClasses = 0;
	}

	/**
	 * Add new group
	 *
	 * @param group
	 */
	public void addGroup(Group group) {
		this.groups.put(group.getGroupId(), group);
		this.numClasses = 0;
	}

	/**
	 * Add new timeslot
	 * 
	 * @param timeslotId
	 * @param timeslot
	 */
	public void addTimeslot(int timeslotId, DayOfWeek dayOfWeek, String timeslot) {
		this.timeslots.put(timeslotId, new Timeslot(timeslotId, dayOfWeek, timeslot));
	}

	/**
	 * Add new timeslot
	 *
	 * @param timeslot
	 */
	public void addTimeslot(Timeslot timeslot) {
		this.timeslots.put(timeslot.getTimeslotId(), timeslot);
	}

	/**
	 * Set day's timeslot
	 *
	 * @param dayTimeslot
	 */
	public void setDaysTimeslot(List<String> dayTimeslot){
		this.dayTimeslot = dayTimeslot;
	}

	/**
	 * Create classes using individual's chromosome
	 * 
	 * One of the two important methods in this class; given a chromosome,
	 * unpack it and turn it into an array of Class (with a capital C) objects.
	 * These Class objects will later be evaluated by the calcClashes method,
	 * which will loop through the Classes and calculate the number of
	 * conflicting timeslots, rooms, professors, etc.
	 * 
	 * While this method is important, it's not really difficult or confusing.
	 * Just loop through the chromosome and create Class objects and store them.
	 * 
	 * @param individual
	 */
	public void createClasses(Individual individual) {
		// Init classes
		Class[][][] allClasses = new Class[groups.size()][DayOfWeek.values().length - 1][dayTimeslot.size()];

		// Get individual's chromosome
		int chromosome[] = individual.getChromosome();
		int chromosomePos = 0;
		int classIndex = 0;
		int groupIndex = 0;

		for (Group group : this.getGroupsAsArray()) {
			Class[][] classes = new Class[DayOfWeek.values().length - 1][dayTimeslot.size()];
			for (Module module : group.getModules()) {
				for (int number = 1; number <= module.getNumberOfClassesPerWeek(); number++) {
					Timeslot timeslot = getTimeslot(chromosome[chromosomePos]);

					int timeslotNum = dayTimeslot.indexOf(timeslot.getTimeslot());
					int dayNum = timeslot.getDayOfWeek().ordinal();

					classes[dayNum][timeslotNum] = new Class(classIndex, group, module);

					// Add timeslot
					classes[dayNum][timeslotNum].addTimeslot(getTimeslot(chromosome[chromosomePos]));
					chromosomePos++;

					// Add room
					classes[dayNum][timeslotNum].setRoom(getRoom(chromosome[chromosomePos]));
					chromosomePos++;

					// Add professor
					classes[dayNum][timeslotNum].addProfessor(getProfessor(chromosome[chromosomePos]));
					chromosomePos++;

					classIndex++;
				}
			}
			allClasses[groupIndex++] = classes;
		}

		this.classes = allClasses;
	}

	/**
	 * Get room from roomId
	 * 
	 * @param roomId
	 * @return room
	 */
	public Room getRoom(int roomId) {
		if (!this.rooms.containsKey(roomId)) {
			System.out.println("Rooms doesn't contain key " + roomId);
		}
		return this.rooms.get(roomId);
	}

	/**
	 * Get random room
	 * 
	 * @return room
	 */
	public Room getRandomRoom() {
		Object[] roomsArray = this.rooms.values().toArray();
		Room room = (Room) roomsArray[(int) (roomsArray.length * Math.random())];
		return room;
	}

	/**
	 * Get professor from professorId
	 * 
	 * @param professorId
	 * @return professor
	 */
	public Professor getProfessor(int professorId) {
		return (Professor) this.professors.get(professorId);
	}

	/**
	 * Get module from moduleId
	 * 
	 * @param moduleId
	 * @return module
	 */
	public Module getModule(int moduleId) {
		return (Module) this.modules.get(moduleId);
	}

	/**
	 * Get moduleIds of student group
	 * 
	 * @param groupId
	 * @return moduleId array
	 */
	public List<Module> getGroupModules(int groupId) {
		Group group = this.groups.get(groupId);
		return group.getModules();
	}

	/**
	 * Get group from groupId
	 * 
	 * @param groupId
	 * @return group
	 */
	public Group getGroup(int groupId) {
		return (Group) this.groups.get(groupId);
	}

	/**
	 * Get all student groups
	 * 
	 * @return array of groups
	 */
	public Group[] getGroupsAsArray() {
		return (Group[]) this.groups.values().toArray(new Group[this.groups.size()]);
	}

	/**
	 * Get timeslot by timeslotId
	 * 
	 * @param timeslotId
	 * @return timeslot
	 */
	public Timeslot getTimeslot(int timeslotId) {
		return this.timeslots.get(timeslotId);
	}

	/**
	 * Get random timeslotId
	 * 
	 * @return timeslot
	 */
	public Timeslot getRandomTimeslot() {
		Object[] timeslotArray = this.timeslots.values().toArray();
		Timeslot timeslot = (Timeslot) timeslotArray[(int) (timeslotArray.length * Math.random())];
		return timeslot;
	}

	/**
	 * Get classes
	 * 
	 * @return classes
	 */
	public Class[][][] getClasses() {
		return this.classes;
	}

	/**
	 * Get classes by group
	 *
	 * @return classes
	 */
	public NavigableMap<Integer,List<Class>> getClassesByGroups() {
		NavigableMap<Integer,List<Class>> classesByGroups = new TreeMap<>();
		for (Class[][] group: classes) {
			for (Class[] row: group) {
				for (Class clas: row) {
					if (clas != null) {
						List<Class> classesByGroup = classesByGroups.get(clas.getGroup().getGroupId());
						if (classesByGroup == null) {
							classesByGroups.put(clas.getGroup().getGroupId(), classesByGroup = new ArrayList<>());
						}
						classesByGroup.add(clas);
					}
				}
			}
		}
		return classesByGroups;
	}

	/**
	 * Get number of classes that need scheduling
	 * 
	 * @return numClasses
	 */
	public int getNumClasses() {
		if (this.numClasses > 0) {
			return this.numClasses;
		}

		int numClasses = 0;
		Group groups[] = (Group[]) this.groups.values().toArray(new Group[this.groups.size()]);
		for (Group group : groups) {
			for (Module module: group.getModules()) {
				numClasses += module.getNumberOfClassesPerWeek();
			}
		}
		this.numClasses = numClasses;

		return this.numClasses;
	}

	/**
	 * Calculate the number of clashes between Classes generated by a
	 * chromosome.
	 * 
	 * The most important method in this class; look at a candidate timetable
	 * and figure out how many constraints are violated.
	 * 
	 * Running this method requires that createClasses has been run first (in
	 * order to populate this.classes). The return value of this method is
	 * simply the number of constraint violations (conflicting professors,
	 * timeslots, or rooms), and that return value is used by the
	 * GeneticAlgorithm.calcFitness method.
	 * 
	 * There's nothing too difficult here either -- loop through this.classes,
	 * and check constraints against the rest of the this.classes.
	 * 
	 * The two inner `for` loops can be combined here as an optimization, but
	 * kept separate for clarity. For small values of this.classes.length it
	 * doesn't make a difference, but for larger values it certainly does.
	 *
	 * @return clashes
	 */
	public int calcClashes() {
		int clashes = 0;

		for (int g = 0; g < classes.length; g++) {
			for (int i = 0; i < classes[g].length; i++) {
				for (int j = 0; j < classes[g][i].length; j++) {
					Class classA = classes[g][i][j];

					if (classA != null) {

						// Check room capacity
						int roomCapacity = classA.getRoom().getRoomCapacity();
						int groupSize = classA.getGroup().getGroupSize();

						if (roomCapacity < groupSize) {
							clashes++;
						}

						// Check if room is taken
						for (int k = 0; k < classes.length; k++) {
							Class classB = classes[k][i][j];
							if (classB != null &&
								classA.getRoom().getRoomId() == classB.getRoom().getRoomId() &&
								classA.getTimeslot().getTimeslotId() == classB.getTimeslot().getTimeslotId() &&
								classA.getClassId() != classB.getClassId()) {
								clashes++;
								break;
							}
						}

						// Check if professor is available
						for (int k = 0; k < classes.length; k++) {
							Class classB = classes[k][i][j];
							if (classB != null &&
								classA.getProfessor().getProfessorId() == classB.getProfessor().getProfessorId() &&
								classA.getTimeslot().getTimeslotId() == classB.getTimeslot().getTimeslotId() &&
								classA.getClassId() != classB.getClassId()) {
								clashes++;
								break;
							}
						}
					}
				}
			}
		}

		return clashes;
	}

	/**
	 * Calculate the number of windows
	 * If timetable have free slot between two classes incremate this counter
	 *
	 * @return windows
	 */
	public int calcWindows() {
		int windows = 0;
		for (Class[][] group: classes) {
			for (Class[] clas: group) {
				for (int i = 0; i < clas.length - 2; i++) {
					if (clas[i] != null && clas[i+1] == null) {
						for (int j = i+2; j < clas.length; j++) {
							if (clas[j] != null) {
								windows ++;
								break;
							}
						}
					}
				}
			}
		}
		return windows;
	}

	/**
	 * Calculate the number of adjacent classes
	 * If timetable have two classes in a row in day incremate this counter
	 *
	 * @return windows
	 */
	public int calcAdjacentClasses() {
		int adjacent = 0;
		for (Class[][] group: classes) {
			for (Class[] clas: group) {
				for (int i = 0; i < clas.length - 1; i++) {
					if (clas[i] != null && clas[i+1] != null) {
						adjacent++;
					}
				}
			}
		}
		return adjacent;
	}

	/**
	 * Calculate the number of late classes
	 * If timetable have late class, in second half of day, incremate this counter
	 *
	 * @return windows
	 */
	public int calcLateClasses() {
			int lateClasses = 0;
			int lateTimeslotBorder = dayTimeslot.size() / 2;
			for (Class[][] group: classes) {
					for (Class[] clas: group) {
							for (int i = lateTimeslotBorder; i < clas.length; i++) {
									if (clas[i] != null) {
											lateClasses++;
									}
							}
					}
			}
			return lateClasses;
	}

	/**
	 * Calculate the number of early classes
	 * If timetable have early class, in first half of day, incremate this counter
	 *
	 * @return windows
	 */
	public int calcEarlyClasses() {
		int lateClasses = 0;
		int lateTimeslotBorder = dayTimeslot.size() / 2;
		for (Class[][] group: classes) {
			for (Class[] clas: group) {
				for (int i = lateTimeslotBorder; i < clas.length; i++) {
					if (clas[i] != null) {
						lateClasses++;
					}
				}
			}
		}
		return lateClasses;
	}

	/**
	 * Calculate the number of classes excess
	 * If timetable have class per day more than need incremate this counter
	 *
	 * @return windows
	 */
	public int calcClassesOverLimit() {
		int classesOverLimit = 0;
		for (Class[][] group: classes) {
			for (Class[] day: group) {
				int classPerDay = 0;
				for (Class clas: day) {
					if (clas != null) {
						classPerDay++;
					}
				}
				if (classPerDay >= 4) {
					classesOverLimit++;
				}
			}
		}
		return classesOverLimit;
	}

	/**
	 * Calculate the number of classes excess
	 * If timetable have class per day less than need incremate this counter
	 *
	 * @return windows
	 */
	public int calcClassesUnderLimit() {
		int classesUnderLimit = 0;
		for (Class[][] group: classes) {
			for (Class[] day: group) {
				int classPerDay = 0;
				for (Class clas: day) {
					if (clas != null) {
						classPerDay++;
					}
				}
				if (classPerDay < 4) {
					classesUnderLimit++;
				}
			}
		}
		return classesUnderLimit;
	}
}