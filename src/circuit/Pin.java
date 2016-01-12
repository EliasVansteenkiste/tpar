package circuit;


public class Pin {
	public final String name;
	public final PinType type;
	public final Block owner;
	public Connection con;
	
	public Pin(String name, PinType type) {
		this(name, type, null);
	}
	
	public Pin(String name, PinType type, Block owner) {
		super();
		this.name = name;
		this.type = type;
		this.owner = owner;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public int compareTo(Pin otherPin){
		return this.name.compareTo(otherPin.name);
	}

//	@Override
//	public boolean equals(Object o){
//		if (o == null) return false;
//	    if (!(o instanceof Pin)) return false;
//	    Pin p = (Pin) o;
//		if(p.name.compareTo(this.name)==0){
//			return true;
//		}else{
//			return false;
//		}
//	}
	
//	@Override
//	public int hashCode(){
//		return this.name.hashCode();
//	}
}
