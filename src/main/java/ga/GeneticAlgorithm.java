package ga;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeneticAlgorithm {

	private int populationSize;
	private double mutationRate;
	private double crossoverRate;
	private int elitismCount;
	private int tournamentSize;
	private double maxFitness;
	private int maxGenerations;

	// Create fitness hashtable
	private Map<Individual, Double> fitnessHash = Collections.
			synchronizedMap(
					new LinkedHashMap<Individual, Double>() {
						@Override
						protected boolean removeEldestEntry(Map.Entry<Individual, Double> entry) {
							// Store a maximum of 1000 fitness values
							return this.size() > 1000;
						}
					});

	public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount,
			int tournamentSize, double maxFitness, int maxGenerations) {

		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elitismCount = elitismCount;
		this.tournamentSize = tournamentSize;
		this.maxFitness = maxFitness;
		this.maxGenerations = maxGenerations;
	}

	/**
	 * Initialize population
	 * 
	 * @param timetable
	 *            The length of the individuals chromosome
	 * @return population The initial population generated
	 */
	public Population initPopulation(Timetable timetable) {
		// Initialize population
		Population population = new Population(this.populationSize, timetable);
		return population;
	}

	/**
	 * Check if population has met termination condition
	 *
	 * @param population
	 * @param generationsCount Number of generations passed
	 *
	 * @return boolean true if termination condition not met, otherwise, false
	 */
	public boolean isTerminationConditionNotMet(Population population, int generationsCount) {
		return generationsCount != maxGenerations && population.getFittest(0).getFitness() < maxFitness;
	}

	/**
	 * Calculate individual's fitness value
	 * 
	 * @param individual
	 * @param timetable
	 * @return fitness
	 */
	public double calcFitness(Individual individual, Timetable timetable) {

		Double storedFitness = this.fitnessHash.get(individual);
		if (storedFitness != null) {
			return storedFitness;
		}

		// Create new timetable object to use -- cloned from an existing timetable
		Timetable threadTimetable = new Timetable(timetable);
		threadTimetable.createClasses(individual);

		// Calculate fitness
		int clashes = threadTimetable.calcClashes();
		int earlyClasses = threadTimetable.calcEarlyClasses();
		int adjacentClasses = threadTimetable.calcAdjacentClasses();
		int classesUnderLimit = threadTimetable.calcClassesUnderLimit();
		double fitness = (- clashes + adjacentClasses * 0.5 + earlyClasses * 0.3 + classesUnderLimit * 0.05);

		individual.setFitness(fitness);

		// Store fitness in hashtable
		this.fitnessHash.put(individual, fitness);

		return fitness;
	}

	/**
	 * Evaluate population
	 * 
	 * @param population
	 * @param timetable
	 */
	public void evalPopulation(Population population, Timetable timetable) {
//		IntStream.range(0, population.size()).parallel()
//				.forEach(i -> this.calcFitness(population.getIndividual(i),
//						timetable));

		double populationFitness = 0;

		// Loop over population evaluating individuals and summing population
		// fitness
		for (Individual individual : population.getIndividuals()) {
			populationFitness += this.calcFitness(individual, timetable);
		}

		population.setPopulationFitness(populationFitness);
	}

	/**
	 * Selects parent for crossover using tournament selection
	 * 
	 * Tournament selection works by choosing N random individuals, and then
	 * choosing the best of those.
	 * 
	 * @param population
	 * @return The individual selected as a parent
	 */
	public Individual selectParent(Population population) {
		// Create tournament
		Population tournament = new Population(this.tournamentSize);

		// Add random individuals to the tournament
		population.shuffle();
		for (int i = 0; i < this.tournamentSize; i++) {
			Individual tournamentIndividual = population.getIndividual(i);
			tournament.setIndividual(i, tournamentIndividual);
		}

		// Return the best
		return tournament.getFittest(0);
	}


	/**
     * Apply mutation to population
     * 
     * @param population
     * @param timetable
     * @return The mutated population
     */
	public Population mutatePopulation(Population population, Timetable timetable) {
		// Initialize new population
		Population newPopulation = new Population(this.populationSize);

		//get best fitness
		double bestFitness = population.getFittest(0).getFitness();

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual individual = population.getFittest(populationIndex);

			// Create random individual to swap genes with
			Individual randomIndividual = new Individual(timetable);

			// Calculate adaptive mutation rate
			double adaptiveMutationRate = this.mutationRate;
			if (individual.getFitness() > population.getAvgFitness()) {
				double fitnessDelta1 = bestFitness - individual.
						getFitness();
				double fitnessDelta2 = bestFitness - population.
						getAvgFitness();
				adaptiveMutationRate = (fitnessDelta1 / fitnessDelta2) *
						this.mutationRate;
			}

			// Loop over individual's genes
			for (int geneIndex = 0; geneIndex < individual.getChromosomeLength(); geneIndex++) {
				// Skip mutation if this is an elite individual
				if (populationIndex > this.elitismCount) {
					// Does this gene need mutation?
					if (adaptiveMutationRate > Math.random()) {
						// Swap for new gene
						individual.setGene(geneIndex, randomIndividual.getGene(geneIndex));
					}
				}
			}

			// Add individual to population
			newPopulation.setIndividual(populationIndex, individual);
		}

		// Return mutated population
		return newPopulation;
	}

    /**
     * Apply crossover to population
     * 
     * @param population The population to apply crossover to
     * @return The new population
     */
	public Population crossoverPopulation(Population population) {
		// Create new population
		Population newPopulation = new Population(population.size());

		//get best fitness
		double bestFitness = population.getFittest(0).getFitness();

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual parent1 = population.getFittest(populationIndex);

			// Calculate adaptive mutation rate
			double adaptiveCrossoverRate = this.crossoverRate;
			if (parent1.getFitness() > population.getAvgFitness()) {
				double fitnessDelta1 = bestFitness - parent1.
						getFitness();
				double fitnessDelta2 = bestFitness - population.
						getAvgFitness();
				adaptiveCrossoverRate = (fitnessDelta1 / fitnessDelta2) *
						this.crossoverRate;
			}

			// Apply crossover to this individual?
			if (adaptiveCrossoverRate > Math.random() && populationIndex >= this.elitismCount) {
				// Initialize offspring
				Individual offspring = new Individual(parent1.getChromosome());
				
				// Find second parent
				//TODO: make normal selection
				Individual parent2 = selectParent(population);

				// Loop over genome
				for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength(); geneIndex++) {
					// Use half of parent1's genes and half of parent2's genes
					if (0.5 > Math.random()) {
						offspring.setGene(geneIndex, parent1.getGene(geneIndex));
					} else {
						offspring.setGene(geneIndex, parent2.getGene(geneIndex));
					}
				}

				// Add offspring to new population
				newPopulation.setIndividual(populationIndex, offspring);
			} else {
				// Add individual to new population without applying crossover
				newPopulation.setIndividual(populationIndex, parent1);
			}
		}

		return newPopulation;
	}

}
