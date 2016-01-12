package TRouter;

public class Combination<A> {
    private A first;
    private A second;

    public Combination(A first, A second) {
        super();
        this.first = first;
        this.second = second;
    }

    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond * hashFirst;
    }

    public boolean equals(Object other) {
    	if (other == null) return false;
        if (other instanceof Combination) {
        	@SuppressWarnings("unchecked")
			Combination<A> otherCombination = (Combination<A>) other;
        	if(this.first.equals(otherCombination.first)&&this.second.equals(otherCombination.second)){
        		return true;
        	}
        	else if(this.first.equals(otherCombination.second)&&this.second.equals(otherCombination.first))return true;
        	else return false;
        }
        return false;
    }

    public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public A getSecond() {
        return second;
    }

    public void setSecond(A second) {
        this.second = second;
    }
}