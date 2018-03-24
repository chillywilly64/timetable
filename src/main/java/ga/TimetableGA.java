package ga;


import ga.entity.Module;
import ga.entity.Professor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.DayOfWeek;
import java.util.Arrays;

/**
 * Don't be daunted by the number of classes in this chapter -- most of them are
 * just simple containers for information, and only have a handful of properties
 * with setters and getters.
 * 
 * The real stuff happens in the GeneticAlgorithm class and the Timetable class.
 * 
 * The Timetable class is what the genetic algorithm is expected to create a
 * valid version of -- meaning, after all is said and done, a chromosome is read
 * into a Timetable class, and the Timetable class creates a nicer, neater
 * representation of the chromosome by turning it into a proper list of Classes
 * with rooms and professors and whatnot.
 * 
 * The Timetable class also understands the problem's Hard Constraints (ie, a
 * professor can't be in two places simultaneously, or a room can't be used by
 * two classes simultaneously), and so is used by the GeneticAlgorithm's
 * calcFitness class as well.
 * 
 * Finally, we overload the Timetable class by entrusting it with the
 * "database information" generated here in initializeTimetable. Normally, that
 * information about what professors are employed and which classrooms the
 * university has would come from a database, but this isn't a book about
 * databases so we hardcode it.
 * 
 * @author bkanber
 *
 */
public class TimetableGA {
	private static final Logger log = LoggerFactory.getLogger(TimetableGA.class);

    public static Timetable run(Timetable timetable,
                                int populationSize,
                                double mutationRate,
                                double crossoverRate,
                                int elitismCount,
                                int tournamentSize) {
    	// Get a Timetable object with all the available information.
        if (timetable == null) timetable = initializeTimetable();

        // Initialize GA
        GeneticAlgorithm ga = new GeneticAlgorithm(
        		populationSize, mutationRate, crossoverRate, elitismCount, tournamentSize);
        
        // Initialize population
        Population population = ga.initPopulation(timetable);
        
        // Evaluate population
        ga.evalPopulation(population, timetable);
        
        // Keep track of current generation
        int generation = 1;
        
        // Start evolution loop
        while (ga.isTerminationConditionMet(generation, 1000) == false
            && ga.isTerminationConditionMet(population) == false) {
            // Print fitness
            log.info("G" + generation + " Best fitness: " + population.getFittest(0).getFitness());

            // Apply crossover
            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population, timetable);

            // Evaluate population
            ga.evalPopulation(population, timetable);

            // Increment the current generation
            generation++;
        }

        // Print fitness
        timetable.createClasses(population.getFittest(0));
       	log.info("Solution found in " + generation + " generations");
        log.info("Final solution fitness: " + population.getFittest(0).getFitness());
        log.info("Clashes: " + timetable.calcClashes());
		log.info("Windows: " + timetable.calcWindows());

        return timetable;
    }

    /**
     * Creates a Timetable with all the necessary course information.
     * 
     * Normally you'd get this info from a database.
     * 
     * @return
     */
	private static Timetable initializeTimetable() {
		// Create timetable
		Timetable timetable = new Timetable();

		// Set up rooms
		timetable.addRoom(1,"A1", 15);
		timetable.addRoom(2,"B1", 30);
		timetable.addRoom(3,"D1", 20);
		timetable.addRoom(4,"F1", 25);

		// Set up timeslots
		timetable.addTimeslot(1, DayOfWeek.MONDAY,"09:00 - 11:00");
		timetable.addTimeslot(2,DayOfWeek.MONDAY,"11:00 - 13:00");
		timetable.addTimeslot(3,DayOfWeek.MONDAY,"13:00 - 15:00");
		timetable.addTimeslot(4,DayOfWeek.TUESDAY, "09:00 - 11:00");
		timetable.addTimeslot(5,DayOfWeek.TUESDAY, "11:00 - 13:00");
		timetable.addTimeslot(6,DayOfWeek.TUESDAY, "13:00 - 15:00");
		timetable.addTimeslot(7,DayOfWeek.WEDNESDAY, "09:00 - 11:00");
		timetable.addTimeslot(8,DayOfWeek.WEDNESDAY, "11:00 - 13:00");
		timetable.addTimeslot(9,DayOfWeek.WEDNESDAY, "13:00 - 15:00");
		timetable.addTimeslot(10,DayOfWeek.THURSDAY, "09:00 - 11:00");
		timetable.addTimeslot(11,DayOfWeek.THURSDAY, "11:00 - 13:00");
		timetable.addTimeslot(12,DayOfWeek.THURSDAY, "13:00 - 15:00");
		timetable.addTimeslot(13,DayOfWeek.FRIDAY, "09:00 - 11:00");
		timetable.addTimeslot(14,DayOfWeek.FRIDAY, "11:00 - 13:00");
		timetable.addTimeslot(15,DayOfWeek.FRIDAY, "13:00 - 15:00");

		// Set up professors
		Professor professor1 = new Professor(1,"Dr P Smith");
		Professor professor2 = new Professor(2,"Mrs E Mitchell");
		Professor professor3 = new Professor(3,"Dr R Williams");
		Professor professor4 = new Professor(4,"Mr A Thompson");
		timetable.addProfessor(professor1);
		timetable.addProfessor(professor2);
		timetable.addProfessor(professor3);
		timetable.addProfessor(professor4);

		// Set up modules and define the professors that teach them
		Module module1 = new Module(1,"Computer Science", 2, Arrays.asList(professor1, professor2));
		Module module2 = new Module(2,"English", 1, Arrays.asList(professor1, professor3));
		Module module3 = new Module(3,"Maths", 3, Arrays.asList(professor1, professor2));
		Module module4 = new Module(4,"Physics", 2, Arrays.asList(professor3, professor4));
		Module module5 = new Module(5,"History", 1, Arrays.asList(professor4));
		Module module6 = new Module(6,"Drama", 1, Arrays.asList(professor1, professor4));
		timetable.addModule(module1);
		timetable.addModule(module2);
		timetable.addModule(module3);
		timetable.addModule(module4);
		timetable.addModule(module5);
		timetable.addModule(module6);

		// Set up student groups and the modules they take.
		timetable.addGroup(1, 10, Arrays.asList(module1, module3, module4));
		timetable.addGroup(2, 30, Arrays.asList(module2, module3, module5, module6));
		timetable.addGroup(3, 18, Arrays.asList(module3, module4, module5));
		timetable.addGroup(4, 25, Arrays.asList(module1, module4));
		timetable.addGroup(5, 20, Arrays.asList(module2, module3, module5));
		timetable.addGroup(6, 22, Arrays.asList(module1, module4, module5));
		timetable.addGroup(7, 16, Arrays.asList(module1, module3));
		timetable.addGroup(8, 18, Arrays.asList(module2, module6));
		timetable.addGroup(9, 24, Arrays.asList(module1, module6));
		timetable.addGroup(10, 25, Arrays.asList(module3, module4));
		return timetable;
	}
}
