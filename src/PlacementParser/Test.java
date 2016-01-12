package PlacementParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Test {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 * @throws PlacementParser.ParseException 
	 */
	public static void main(String[] args) throws FileNotFoundException, ParseException, ParseException, PlacementParser.ParseException {
		ReadPlacement plaats_parser=new ReadPlacement(new FileInputStream("e64-4lut.p"));
		Placement p=plaats_parser.read();
		System.out.println(p);
	}

}
