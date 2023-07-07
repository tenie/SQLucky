package net.tenie.Sqlucky.sdk.excel;

public class ExcelHeadCellInfo {
	private String cellAddress;
	private String cellVal;
	private int cellIdx;

	public String getCellAddress() {
		return cellAddress;
	}

	public void setCellAddress(String cellAddress) {
		this.cellAddress = cellAddress;
	}

	public String getCellVal() {
		return cellVal;
	}

	public void setCellVal(String cellVal) {
		this.cellVal = cellVal;
	}

	public int getCellIdx() {
		return cellIdx;
	}

	public void setCellIdx(int cellIdx) {
		this.cellIdx = cellIdx;
	}

	@Override
	public String toString() {
		return "ExcelHeadCellInfo [cellAddress=" + cellAddress + ", cellVal=" + cellVal + ", cellIdx=" + cellIdx + "]";
	}

}
