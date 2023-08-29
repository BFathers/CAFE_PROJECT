// CUSTOMER.java
package com.mystudy.practice;

// VO (Value Object) 클래스: 데이터 저장을 위한 클래스
public class CAFE_Sign_Modify_VO {
    private int custid;
    private String custname;
    private String password;
    private String phone;
    private String status;
    private int stampcnt;
    private int couponcnt;
    
    public CAFE_Sign_Modify_VO() {
		// TODO Auto-generated constructor stub
	}
    
	public int getCustid() {
		return custid;
	}
	
	public void setCustid(int custid) {
		this.custid = custid;
	}
	public String getCustname() {
		return custname;
	}
	public void setCustname(String custname) {
		this.custname = custname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getStampcnt() {
		return stampcnt;
	}
	public void setStampcnt(int stampcnt) {
		this.stampcnt = stampcnt;
	}
	public int getCouponcnt() {
		return couponcnt;
	}
	public void setCouponcnt(int couponcnt) {
		this.couponcnt = couponcnt;
	}
	
	@Override
    public String toString() {
        return "회원 정보 [고객 번호=" + custid + ", 이름=" + custname + ", 비밀번호=" + password +
               ", 핸드폰 번호=" + phone + ", 계정 상태=" + status +
               ", 스탬프 개수=" + stampcnt + ", 쿠폰 개수=" + couponcnt + "]";
    }
	
	public CAFE_Sign_Modify_VO(int custid, String custname, String password, String phone, String status, int stampcnt,
			int couponcnt) {
		super();
		this.custid = custid;
		this.custname = custname;
		this.password = password;
		this.phone = phone;
		this.status = status;
		this.stampcnt = stampcnt;
		this.couponcnt = couponcnt;
	}
}