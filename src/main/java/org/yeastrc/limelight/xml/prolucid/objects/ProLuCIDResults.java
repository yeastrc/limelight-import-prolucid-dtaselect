package org.yeastrc.limelight.xml.prolucid.objects;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class ProLuCIDResults {

	private Map<ProLuCIDReportedPeptide, Collection<ProLuCIDPSM>> peptidePSMMap;

	private Map<String, ProLuCIDProtein> proteinIdProteinMap;

	private Map<String, BigDecimal> staticMods;
	private String prolucidVersion;
	private String dtaselectVersion;
	private String searchDatabase;

	Map<String, Integer> proteinSequenceCountIdMap;
	Map<Integer, Collection<String>> proteinSequenceCountIdUniqueProteinIdMap;
	Map<String, Integer> proteinUniqueIdSequenceCountIdMap;

	public Map<Integer, Collection<String>> getProteinSequenceCountIdUniqueProteinIdMap() {
		return proteinSequenceCountIdUniqueProteinIdMap;
	}

	public void setProteinSequenceCountIdUniqueProteinIdMap(Map<Integer, Collection<String>> proteinSequenceCountIdUniqueProteinIdMap) {
		this.proteinSequenceCountIdUniqueProteinIdMap = proteinSequenceCountIdUniqueProteinIdMap;
	}

	public Map<String, Integer> getProteinSequenceCountIdMap() {
		return proteinSequenceCountIdMap;
	}

	public void setProteinSequenceCountIdMap(Map<String, Integer> proteinSequenceCountIdMap) {
		this.proteinSequenceCountIdMap = proteinSequenceCountIdMap;
	}

	public Map<String, Integer> getProteinUniqueIdSequenceCountIdMap() {
		return proteinUniqueIdSequenceCountIdMap;
	}

	public void setProteinUniqueIdSequenceCountIdMap(Map<String, Integer> proteinUniqueIdSequenceCountIdMap) {
		this.proteinUniqueIdSequenceCountIdMap = proteinUniqueIdSequenceCountIdMap;
	}

	public Map<ProLuCIDReportedPeptide, Collection<ProLuCIDPSM>> getPeptidePSMMap() {
		return peptidePSMMap;
	}

	public void setPeptidePSMMap(Map<ProLuCIDReportedPeptide, Collection<ProLuCIDPSM>> peptidePSMMap) {
		this.peptidePSMMap = peptidePSMMap;
	}

	public Map<String, ProLuCIDProtein> getProteinIdProteinMap() {
		return proteinIdProteinMap;
	}

	public void setProteinIdProteinMap(Map<String, ProLuCIDProtein> proteinIdProteinMap) {
		this.proteinIdProteinMap = proteinIdProteinMap;
	}

	public Map<String, BigDecimal> getStaticMods() {
		return staticMods;
	}

	public void setStaticMods(Map<String, BigDecimal> staticMods) {
		this.staticMods = staticMods;
	}

	public String getProlucidVersion() {
		return prolucidVersion;
	}

	public void setProlucidVersion(String prolucidVersion) {
		this.prolucidVersion = prolucidVersion;
	}

	public String getDtaselectVersion() {
		return dtaselectVersion;
	}

	public void setDtaselectVersion(String dtaselectVersion) {
		this.dtaselectVersion = dtaselectVersion;
	}

	public String getSearchDatabase() {
		return searchDatabase;
	}

	public void setSearchDatabase(String searchDatabase) {
		this.searchDatabase = searchDatabase;
	}
}
