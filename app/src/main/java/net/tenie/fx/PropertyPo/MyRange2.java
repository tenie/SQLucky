package net.tenie.fx.PropertyPo;

public class MyRange2 {
	private int start;
	private int end;
	
	public MyRange2(int s, int e){
		start = s;
		end   = e;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "MyRange [start=" + start + ", end=" + end + "]";
	}
	
	
}
