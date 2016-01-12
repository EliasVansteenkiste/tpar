package circuit;

public class Output extends Block {
	public final Pin input;

	public Output(String name) {
		super(name, BlockType.OUTPUT);
		input = new Pin(name+"_in", PinType.SINK,this);
	}
	
	public Output(Output other) {
		this(other.name);
	}
}
