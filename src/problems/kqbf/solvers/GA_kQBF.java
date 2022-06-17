package problems.kqbf.solvers;

import java.io.IOException;
import java.util.ArrayList;

import metaheuristics.ga.AbstractGA;
import problems.kqbf.kQBF;
import solutions.Solution;

import static java.lang.Math.max;

public class GA_kQBF extends AbstractGA<Integer, Integer> {

	// public kQBF kQBF;
	private Boolean adaptiveMutation = false;

	/**
	 * Constructor for the GA_kQBF class. The QBF objective function is passed as
	 * argument for the superclass constructor.
	 * 
	 * @param generations
	 *            Maximum number of generations.
	 * @param popSize
	 *            Size of the population.
	 * @param mutationRate
	 *            The mutation rate.
	 * @param useUniformCrossover
	 *            If it should use the uniform crossover strategy or not.
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
	 * @throws IOException
	 *             Necessary for I/O operations.
	 */
	public GA_kQBF(Integer generations, Integer popSize, Double mutationRate, String filename, Boolean useUniformCrossover, Integer maxTimeInSeconds, Boolean adaptiveMutation) throws IOException {
		super(new kQBF(filename), generations, popSize, mutationRate, maxTimeInSeconds);
		this.useUniformCrossover = useUniformCrossover;
		this.adaptiveMutation = adaptiveMutation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This createEmptySol instantiates an empty solution and it attributes a
	 * zero cost, since it is known that a QBF solution with all variables set
	 * to zero has also zero cost.
	 */
	@Override
	public Solution<Integer> createEmptySol() {
		Solution<Integer> sol = new Solution<Integer>();		
		sol.cost = 0.0;
		sol.usedCapacity = 0.0;
		return sol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see metaheuristics.ga.AbstractGA#decode(metaheuristics.ga.AbstractGA.
	 * Chromosome)
	 */
	@Override
	protected Solution<Integer> decode(Chromosome chromosome) {	
		
		Solution<Integer> solution = createEmptySol();		

		for (int locus = 0; locus < chromosome.size(); locus++) {
			if (chromosome.get(locus) == 1) {
				solution.add(Integer.valueOf(locus));
			}
		}

		ObjFunction.evaluate(solution);
		return solution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see metaheuristics.ga.AbstractGA#generateRandomChromosome()
	 */
	@Override
	protected Chromosome generateRandomChromosome() {

		Chromosome chromosome = new Chromosome();
		for (int i = 0; i < chromosomeSize; i++) {
			chromosome.add(rng.nextInt(2));
		}

		return chromosome;
	}

	@Override
	/**
	 * Given a population of chromosome, takes the best chromosome according to
	 * the fitness evaluation.
	 * 
	 * @param population
	 *            A population of chromosomes.
	 * @return The best chromosome among the population.
	 */
	protected Chromosome getBestChromosome(Population population) {	

		double bestFitness = Double.NEGATIVE_INFINITY;
		Chromosome bestChromosome = population.get(0);
		for (Chromosome c : population) {
			Solution<Integer> fitness = fitness(c);
			double usedCapacity = fitness.usedCapacity;

			if (fitness.cost > bestFitness && usedCapacity < ObjFunction.getCapacity()) {
				bestFitness = fitness.cost;
				bestChromosome = c;
			}
		}

		return bestChromosome;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see metaheuristics.ga.AbstractGA#fitness(metaheuristics.ga.AbstractGA.
	 * Chromosome)
	 */
	@Override
	protected Solution<Integer> fitness(Chromosome chromosome) {	
		return decode(chromosome);		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * metaheuristics.ga.AbstractGA#mutateGene(metaheuristics.ga.AbstractGA.
	 * Chromosome, java.lang.Integer)
	 */
	@Override
	protected void mutateGene(Chromosome chromosome, Integer locus) {
		chromosome.set(locus, 1 - chromosome.get(locus));
	}


	@Override
	protected Population mutate(Population offsprings) {
		// the default behavior
		if(!adaptiveMutation){
			return super.mutate(offsprings);
		}

		// here the magic happens -- adaptive mutation!
		Double sumCost = 0.;
		ArrayList<Double> costs = new ArrayList<>();
		for(Chromosome c : offsprings) {
			Double cost = decode(c).cost;
			sumCost += cost;
			costs.add(cost);
		}
		Double avg_cost = sumCost / (double) popSize;
		int idx = 0;
		for (Chromosome c : offsprings) {
			// start value of probability of mutation
			mutationRate = 1. / c.size();

			// initial value from current chromosome
			Double mutationRateOfThisChromosome = 0.0;

			if(avg_cost < costs.get(idx)){
				// here, the mutation probability should be kept low because the chromosome is a good one and
				// mutation could disrupt its schema.
				Double sigma = 1 / 2. * mutationRate;
				mutationRateOfThisChromosome = max(mutationRate,(avg_cost / costs.get(idx)) * sigma);
			} else {
				// there is no reason to avoid the mutation of all bits
				// Thus, the mutation probability desity function will be assumed constant for all bits,
				// as in conventional GAs.
				mutationRateOfThisChromosome = max(mutationRate,avg_cost / (4. * costs.get(idx)));
			}
			for (int locus = 0; locus < chromosomeSize; locus++) {
				if (rng.nextDouble() < mutationRateOfThisChromosome) {
					mutateGene(c, locus);
				}
			}
			idx++;
		}
		return offsprings;
	}

	/**
	 * A main method used for testing the GA metaheuristic.
	 * 
	 */
	public static void main(String[] args) throws IOException {

		verbose = true;

		Integer generations = 5000;
		Integer popSize = 100;
		Double mutationRate = 1.0 / 100.0;
		String filename = "instances/kqbf/kqbf020";
		Boolean useUniformCrossover = false;
		Integer maxTimeInSeconds = 30 * 60; // 30 minutes
		Boolean adaptiveMutation = false;

		long startTime = System.currentTimeMillis();
		GA_kQBF ga = new GA_kQBF(generations, popSize, mutationRate, filename, useUniformCrossover, maxTimeInSeconds, adaptiveMutation);
		Solution<Integer> bestSol = ga.solve();
		System.out.println("maxVal = " + bestSol);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
	}
}
