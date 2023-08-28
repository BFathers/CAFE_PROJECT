package com.mystudy.practice;

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
		return "CAFE_Sign_Modify_VO [custid=" + custid + ", custname=" + custname + ", password=" + password
				+ ", phone=" + phone + ", status=" + status + ", stampcnt=" + stampcnt + ", couponcnt=" + couponcnt
				+ "]";
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
	
	
	
	

    // 생성자, getter, setter 메서드 생략 (Lombok 등을 사용하여 자동 생성 가능)
    
    
}