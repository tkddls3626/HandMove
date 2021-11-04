package poly.dto;

/**
 * @author 이협건
 * @version 1.1 공지사항 DTO
 */
public class OcrDTO {
	
	private String save_file_path; // 저장된 이미지 파일의 파일 저장경로
	private String save_file_name; // 저장된 파일 이미지 파일 이름
	private String org_file_name; // 원래 파일명
	private String ext; // 파일 확장자
	private String chg_id; // 최근 수정자
	private String chg_dt; // 최근 수정일
	
	
	public String getSave_file_path() {
		return save_file_path;
	}
	public void setSave_file_path(String save_file_path) {
		this.save_file_path = save_file_path;
	}
	public String getSave_file_name() {
		return save_file_name;
	}
	public void setSave_file_name(String save_file_name) {
		this.save_file_name = save_file_name;
	}
	public String getOrg_file_name() {
		return org_file_name;
	}
	public void setOrg_file_name(String org_file_name) {
		this.org_file_name = org_file_name;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getChg_id() {
		return chg_id;
	}
	public void setChg_id(String chg_id) {
		this.chg_id = chg_id;
	}
	public String getChg_dt() {
		return chg_dt;
	}
	public void setChg_dt(String chg_dt) {
		this.chg_dt = chg_dt;
	}
	
	
	
	
}
