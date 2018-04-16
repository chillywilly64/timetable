package ga;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 */
public class TimetableGA {
	private static final Logger log = LoggerFactory.getLogger(TimetableGA.class);

    public static Timetable run(Timetable timetable,
                                int populationSize,
                                double mutationRate,
                                double crossoverRate,
                                int elitismCount,
                                int tournamentSize) {

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
				log.info("Late Clasees: " + timetable.calcLateClasses());

        return timetable;
    }
}
