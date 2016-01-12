package circuit;

public class Input extends Block {
	public final Pin output;

	public Input(String name) {
		super(name, BlockType.INPUT);
		output = new Pin(name+"_out", PinType.SOURCE,this);
	}
	
	public Input(Input other) {
		this(other.name);
	}
}
