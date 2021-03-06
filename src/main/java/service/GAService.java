package service;

import ga.GeneticAlgorithm;
import ga.Population;
import ga.Timetable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
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
@Service
public class GAService {

    @Value("${ga.populationSize}")
    private int populationSize;

    @Value("${ga.mutationRate}")
    private double mutationRate;

    @Value("${ga.crossoverRate}")
    private double crossoverRate;

    @Value("${ga.elitismCount}")
    private int elitismCount;

    @Value("${ga.tournamentSize}")
    private int tournamentSize;

    @Value("${ga.maxFitness}")
    private double maxFitness;

    @Value("${ga.maxGenerations}")
    private int maxGenerations;

    @Value("${timetable.recommendedMaxClassesPerDay:4}")
    private int recommendedMaxClassesPerDay;

	  private static final Logger log = LoggerFactory.getLogger(GAService.class);

    public Timetable runGA(Timetable timetable) {

        timetable.setRecommendedMaxClassesPerDay(recommendedMaxClassesPerDay);

        // Initialize GA
        GeneticAlgorithm ga = new GeneticAlgorithm(
            populationSize,
            mutationRate,
            crossoverRate,
            elitismCount,
            tournamentSize,
            maxFitness,
            maxGenerations);

        // Initialize population
        Population population = ga.initPopulation(timetable);

        // Evaluate population
        ga.evalPopulation(population, timetable);

        // Keep track of current generation
        int generation = 1;

        // Start evolution loop
        while (ga.isTerminationConditionNotMet(population, generation)) {
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
        log.info("Late clases: " + timetable.calcLateClasses());
        log.info("Classes over limit: " + timetable.calcClassesOverLimit());

        return timetable;
    }
}
