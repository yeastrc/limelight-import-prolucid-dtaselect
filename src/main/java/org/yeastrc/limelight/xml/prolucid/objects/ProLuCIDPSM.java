package org.yeastrc.limelight.xml.prolucid.objects;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class ProLuCIDPSM {

	private BigDecimal xcorr;
	private BigDecimal deltaCn;

	private int scanNumber;
	private BigDecimal observedMoverZ;
	private BigDecimal massDiff;
	private int charge;

	private String peptideSequence;
	private Map<Integer,BigDecimal> modifications;

	private boolean isDecoy;

	public boolean isDecoy() {
		return isDecoy;
	}

	public void setDecoy(boolean decoy) {
		isDecoy = decoy;
	}

	@Override
	public String toString() {
		return "ProLuCIDPSM{" +
				"xcorr=" + xcorr +
				", deltaCn=" + deltaCn +
				", scanNumber=" + scanNumber +
				", observedMoverZ=" + observedMoverZ +
				", massDiff=" + massDiff +
				", charge=" + charge +
				", peptideSequence='" + peptideSequence + '\'' +
				", modifications=" + modifications +
				", isDecoy=" + isDecoy +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProLuCIDPSM that = (ProLuCIDPSM) o;
		return scanNumber == that.scanNumber &&
				charge == that.charge &&
				isDecoy == that.isDecoy &&
				xcorr.equals(that.xcorr) &&
				deltaCn.equals(that.deltaCn) &&
				observedMoverZ.equals(that.observedMoverZ) &&
				peptideSequence.equals(that.peptideSequence) &&
				Objects.equals(modifications, that.modifications);
	}

	@Override
	public int hashCode() {
		return Objects.hash(xcorr, deltaCn, scanNumber, observedMoverZ, charge, peptideSequence, modifications, isDecoy);
	}

	public BigDecimal getXcorr() {
		return xcorr;
	}

	public void setXcorr(BigDecimal xcorr) {
		this.xcorr = xcorr;
	}

	public BigDecimal getDeltaCn() {
		return deltaCn;
	}

	public void setDeltaCn(BigDecimal deltaCn) {
		this.deltaCn = deltaCn;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public BigDecimal getObservedMoverZ() {
		return observedMoverZ;
	}

	public void setObservedMoverZ(BigDecimal observedMoverZ) {
		this.observedMoverZ = observedMoverZ;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public String getPeptideSequence() {
		return peptideSequence;
	}

	public void setPeptideSequence(String peptideSequence) {
		this.peptideSequence = peptideSequence;
	}

	public Map<Integer, BigDecimal> getModifications() {
		return modifications;
	}

	public void setModifications(Map<Integer, BigDecimal> modifications) {
		this.modifications = modifications;
	}

	public BigDecimal getMassDiff() {
		return massDiff;
	}

	public void setMassDiff(BigDecimal massDiff) {
		this.massDiff = massDiff;
	}
}
