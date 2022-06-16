package problems.kqbf.solvers;

import java.io.IOException;
import metaheuristics.ga.AbstractGA;
import problems.kqbf.kQBF;
import solutions.Solution;

/**
 * Metaheuristic GA (Genetic Algorithm) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link #QuadracticBinaryFunction}). 
 * 
 * @author ccavellucci, fusberti
 */
public class GA_kQBF extends AbstractGA<Integer, Integer> {

	// public kQBF kQBF;

	/**
	 * Constructor for the GA_QBF class. The QBF objective function is passed as
	 * argument for the superclass constructor.
	 * 
	 * @param generations
	 *            Maximum number of generations.
	 * @param popSize
	 *            Size of the population.
	 * @param mutationRate
	 *            The mutation rate.
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
	 * @throws IOException
	 *             Necessary for I/O operations.
	 */
	public GA_kQBF(Integer generations, Integer popSize, Double mutationRate, String filename) throws IOException {
		super(new kQBF(filename), generations, popSize, mutationRate);			
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
		Chromosome bestChromosome = null;
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

	/**
	 * A main method used for testing the GA metaheuristic.
	 * 
	 */
	public static void main(String[] args) throws IOException {

		Integer generations = 1000;
		Integer popSize = 100;
		Double mutationRate = 1.0 / 100.0;
		String filename = "instances/kqbf/kqbf020";

		long startTime = System.currentTimeMillis();
		GA_kQBF ga = new GA_kQBF(generations, popSize, mutationRate, filename);
		Solution<Integer> bestSol = ga.solve();
		System.out.println("maxVal = " + bestSol);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
	}
}
