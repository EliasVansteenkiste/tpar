package Tools;

import java.io.*;

public class MatrixWireWeights {
	public int xwidth;
	public int yheight;
	
	double [][] wireWeights;
	
	public MatrixWireWeights(int xwidth, int yheight){
		this.xwidth=xwidth;
		this.yheight=yheight;
		
		wireWeights = new double[yheight][xwidth];
		
		for(int y=0;y<yheight;y++){
			for(int x=0;x<xwidth;x++){
				wireWeights[y][x]=1.0;
			}
		}
	}

	public void setWeight(int x, int y, double weight){
		wireWeights[y][x]=weight;
	}
	
	public void printMatrix(String fileName, String matrixName){
		//Tries to print the matrix to a file with matlab syntax
		try{
			// Create file 
			FileWriter fstream = new FileWriter(fileName,true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(matrixName+" = [");
			for(int y=0;y<yheight;y++){
				if(y!=0)out.write("; ");
				for(int x=0;x<xwidth;x++){
					out.write(wireWeights[y][x]+" ");
				}
				
			}
			out.write("];\n");
			//Close the output stream
			out.flush();
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	

}
