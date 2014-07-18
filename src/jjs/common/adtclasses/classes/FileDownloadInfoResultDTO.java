package jjs.common.adtclasses.classes;

@SuppressWarnings("serial")
public class FileDownloadInfoResultDTO extends BaseResultDTO {

	private String downloadfilename = null;
	private String realfilename = null;

	public String getDownloadfilename() {
		return downloadfilename;
	}

	public String getRealfilename() {
		return realfilename;
	}

	public void setDownloadfilename(String downloadfilename) {
		this.downloadfilename = downloadfilename;
	}

	public void setRealfilename(String realfilename) {
		this.realfilename = realfilename;
	}

}
