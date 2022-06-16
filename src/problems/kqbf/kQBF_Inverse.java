package problems.kqbf;

import solutions.Solution;

import java.io.IOException;

/**
 * Class representing the inverse of the Quadractic Binary Function
 * ({@link QBF}), which is used since the GRASP is set by
 * default as a minimization procedure.
 * 
 * @author ccavellucci, fusberti
 */
public class kQBF_Inverse extends kQBF {

	/**
	 * Constructor for the QBF_Inverse class.
	 * 
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
	 * @throws IOException
	 *             Necessary for I/O operations.
	 */
	public kQBF_Inverse(String filename) throws IOException {
		super(filename);
	}


	/* (non-Javadoc)
	 * @see problems.qbf.QBF#evaluate()
	 */
	@Override
	public Double evaluatekQBF(Solution<Integer> sol) {
		return -super.evaluatekQBF(sol);
	}
	
	/* (non-Javadoc)
	 * @see problems.qbf.QBF#evaluateInsertion(int)
	 */
	@Override
	public Double evaluateInsertionkQBF(int i) {	
		return -super.evaluateInsertionkQBF(i);
	}
	
	/* (non-Javadoc)
	 * @see problems.qbf.QBF#evaluateRemoval(int)
	 */
	@Override
	public Double evaluateRemovalkQBF(int i) {
		return -super.evaluateRemovalkQBF(i);
	}
	
	/* (non-Javadoc)
	 * @see problems.qbf.QBF#evaluateExchange(int, int)
	 */
	@Override
	public Double evaluateExchangekQBF(int in, int out) {
		return -super.evaluateExchangekQBF(in,out);
	}

}
