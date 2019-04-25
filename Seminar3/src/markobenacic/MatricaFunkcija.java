package markobenacic;

import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixProcedure;

public class MatricaFunkcija implements MatrixFunction{

	@Override
	public double evaluate(int arg0, int arg1, double arg2) {
		return Math.tanh(arg2);
	}

	
}
