package net.tenie.Sqlucky.sdk.po;

public class MyRange {
	private int start;
	private int end;
	
	public MyRange(int s, int e){
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
