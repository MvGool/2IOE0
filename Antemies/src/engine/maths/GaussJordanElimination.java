// Source: https://www.geeksforgeeks.org/program-for-gauss-jordan-elimination-method/

package engine.maths;

public class GaussJordanElimination {
	public static VectorXf solve(MatrixXf matrix, VectorXf values) {
	    int n = matrix.getSize();
	    int m = n + 1;
	    int flag = 0;
	    
	    AugmentedMatrix augmentedMatrix = new AugmentedMatrix(matrix, values);
	    flag = rowReduction(augmentedMatrix, n, m);
	    
	    if (flag == 1) {
	        flag = checkConsistency(augmentedMatrix, n, flag);
	    }
	    
	    VectorXf result = computeResult(augmentedMatrix, n, flag);
	    
	    return result;
	}
	
	public static MatrixXf solve(MatrixXf matrix, MatrixXf values) {
	    int n = matrix.getSize();
	    int m = n + values.getSize();
	    int flag = 0;
	    
	    AugmentedMatrix augmentedMatrix = new AugmentedMatrix(matrix, values);
	    flag = rowReduction(augmentedMatrix, n, m);
	    
	    if (flag == 1) {
	        flag = checkConsistency(augmentedMatrix, n, flag);
	    }
	    
	    MatrixXf result = computeResult(augmentedMatrix, n, m, flag);
	    
	    return result;
	}
	
	private static int rowReduction(AugmentedMatrix matrix, int n, int m) {
	    int i, j, k = 0, c, flag = 0;

	    for (i = 0; i < n; i++) {
	        if (matrix.get(i, i) == 0) {
	            c = 1;
	            
	            while ((i + c) < n && matrix.get(i + c, i) == 0) {
	                c++;
	            }
	            
	            if ((i + c) == n) { 
	                flag = 1;
	                break;
	            }
	            
	            float temp;
	            for (j = i, k = 0; k < m; k++) {
	            	temp = matrix.get(j, k);
	                matrix.set(j, k, matrix.get(j + c, k));
	                matrix.set(j + c, k, temp);
	            }
	        }
	        
	        float p;
	        for (j = 0; j < n; j++) {
	            if (i != j) {             
	                p = matrix.get(j, i) / matrix.get(i, i);
	                
	                for (k = 0; k < m; k++) {
	                	matrix.set(j, k, matrix.get(j, k) - (matrix.get(i, k)) * p);
	                }
	            }
	        }
	    }
	    
	    return flag;
	}
	
	private static VectorXf computeResult(AugmentedMatrix matrix, int n, int flag) {
	    VectorXf result = new VectorXf(n);
	    
	    if (flag == 2) {
	    	System.out.println("Infinite Solutions Exists");
	    } else if (flag == 3) {
	    	System.out.println("No Solution Exists");
	    } else {
	        for (int i = 0; i < n; i++) {
	            result.set(i, matrix.get(i, n) / matrix.get(i, i));
	        }
	    }
	    
	    return result;
	}
	
	private static MatrixXf computeResult(AugmentedMatrix matrix, int n, int m, int flag) {
	    MatrixXf result = new MatrixXf(m - n);
	    
	    if (flag == 2) {
	    	System.out.println("Infinite Solutions Exists");
	    } else if (flag == 3) {
	    	System.out.println("No Solution Exists");
	    } else {
	        for (int i = 0; i < m - n; i++) {
	        	VectorXf row = new VectorXf(m - n);
	        	for (int j = 0; j < m - n; j++) {
	        		row.set(j, matrix.get(i, n + j) / matrix.get(i, i));
	        	}
	        	result.setRow(i, row);
	        }
	    }
	    
	    return result;
	}
	
	private static int checkConsistency(AugmentedMatrix matrix, int n, int flag) {
	    int i, j;
	    float sum;
	    
	    // flag == 2 for infinite solution
	    // flag == 3 for no solution
	    flag = 3;
	    for (i = 0; i < n; i++) {
	        sum = 0;
	        for (j = 0; j < n; j++) {
	            sum = sum + matrix.get(i, j);
	        }
	        
	        if (sum == matrix.get(i, j)) {
	        	flag = 2;
	        }
	    }
	    
	    return flag;
	}
}
