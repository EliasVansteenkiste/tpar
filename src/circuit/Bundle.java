package circuit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Bundle extends Block{
	
	public final Set<Connection> conSet;

	
	@Override
	public String toString() {
		return name;
	}
	
	public Bundle(Bundle bundle) {
		super(bundle.name, BlockType.BUNDLE);
		this.conSet = bundle.conSet;
	}
	
	public Bundle(Set<Connection> conSet){
		super("bundle_"+conSetString(conSet), BlockType.BUNDLE);
		this.conSet = conSet;
	}
	
	private static String conSetString(Set<Connection> conSet) {
		ArrayList<Connection> list = new ArrayList<Connection>(conSet);
		Collections.sort(list);
		return list.toString();
	}

	public Set<Block> blocks() {
		Set<Block> result = new HashSet<Block>();
		for(Connection con:conSet){
			result.add(con.source.owner);
			result.add(con.sink.owner);
		}
		return result;
	}
	
	public String connections(){
		String output = "";
		for(Connection con:conSet){
			output = output+"source: "+con.source+", sink: "+con.sink+"\n";
		}
		return output;
	}
	

}