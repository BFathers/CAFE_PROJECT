package com.mystudy.cafetest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mystudy.cafetest.common.CommonJDBCUtil;
import com.mystudy.cafetest.vo.CAFE_Sign_Modify_VO;

// DAO (Data Access Object) 클래스: 데이터베이스 관련 작업을 수행하는 클래스
public class CAFE_Sign_Modify_DAO {
	static Connection conn = null;
	
    public static CAFE_Sign_Modify_VO getCustomerInfo(Connection connection, int custid) {
    	conn = com.mystudy.cafetest.common.CommonJDBCUtil.getConnection();
    	CAFE_Sign_Modify_VO customer = null;
        String selectQuery =  "SELECT CST.CUSTID, CST.CUSTNAME, CST.PASSWORD, CST.PHONE, CST.STATUS, " +
                             "STP.STAMPCNT " +
                             "FROM CUSTOMER CST JOIN STAMP STP ON CST.CUSTID = STP.CUSTID " +
                             //"JOIN COUPON C ON STP.STAMPID = C.STAMPID " +
                             "WHERE CST.CUSTID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, custid);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                customer = new CAFE_Sign_Modify_VO();
                customer.setCustid(resultSet.getInt("CUSTID"));
                customer.setCustname(resultSet.getString("CUSTNAME"));
                customer.setPassword(resultSet.getString("PASSWORD"));
                customer.setPhone(resultSet.getString("PHONE"));
                customer.setStatus(resultSet.getString("STATUS"));
                customer.setStampcnt(resultSet.getInt("STAMPCNT"));
                //customer.setCouponcnt(resultSet.getInt("COUPONCNT"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return customer;
    }
 

    // 회원 비활성화 메서드
    public static void deactivateUser(Connection connection, int custid) {
        try {
            String updateQuery = "UPDATE CUSTOMER SET STATUS = 'INACTIVE' WHERE CUSTID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, custid);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    resetStampCount(connection, custid);
                    //resetCouponCount(connection, custid);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 회원 활성화 메서드
    public static void activateUser(Connection connection, int custid) {
        try {
            String updateQuery = "UPDATE CUSTOMER SET STATUS = 'ACTIVE' WHERE CUSTID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, custid);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 스탬프 개수 초기화 메서드
    public static void resetStampCount(Connection connection, int custid) {
        try {
            String updateQuery = "UPDATE STAMP SET STAMPCNT = 0 WHERE CUSTID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, custid);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 쿠폰 개수 초기화 메서드
//    public static void resetCouponCount(Connection connection, int custid) {
//        try {
//            String updateQuery = "UPDATE COUPON SET COUPONCNT = 0 WHERE STAMPID = ?";
//            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
//                preparedStatement.setInt(1, custid);
//                preparedStatement.executeUpdate();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    // 회원가입 (custid, custname, password, phone) 입력 후  saveToDatabase로 저장 
    // 그리고 custid 값을 stamp, coupon custid, stampid, couponid에 같이
	public static void performSignUp(Connection connection, Scanner scanner) {
		String custname, password, phone;
    try {
        String getSeqQuery = "SELECT CUSTID_SEQ.NEXTVAL FROM DUAL";
        try (PreparedStatement seqStatement = connection.prepareStatement(getSeqQuery);
             ResultSet resultSet = seqStatement.executeQuery()) {
            if (resultSet.next()) {
                int custid = resultSet.getInt(1);

                System.out.println("고객 번호: " + custid);
             
               
                while (true) {
                    System.out.print("이름을 입력하세요 (빈 문자열 제외): ");
                    custname = scanner.nextLine();
                         //빈문자열인지아닌지
                    if (!custname.isEmpty() && !containsNumber(custname)) {
                        break;
                    } else {
                        System.out.println("▶▷▶▷ 이름을 다시 입력해주세요.");
                    }
                }
                
            
                while (true) {
                	System.out.print("비밀번호를 입력하세요 (알파벳, 숫자 포함 7자 이상): ");
                	password = scanner.nextLine();
                	if (isValidPassword(password)) {
                        break;
                    } else {
                    	System.out.println("▶▷▶▷ 7자 이상 다시 작성해주세요.");
                    }
                }
                
                while (true) {
                	System.out.print("핸드폰 번호를 입력하세요 (010-0000-0000 형식): ");
                	phone = scanner.nextLine();
                	if (isValidPhoneNumber(phone)) {
                		break;
                	} else {
                		System.out.println("010-0000-0000 형식으로 다시 입력해주세요.");
                	}
                }

                saveToDatabase(connection, custid, custname, phone, password); // 회원 정보 저장
                addStamp(connection, custid);
                System.out.println("회원가입이 완료되었습니다.");
            }
        }
    } catch (SQLException e) {
        System.err.println("▶▷▶▷ 회원 가입 중 오류가 발생했습니다.");
        //e.printStackTrace();
    }
}	
	
	// 회원 가입시 STAMP 테이블 STAMPID, CUSTID에도 1 자동 입력 (회원 가입시 1씩 증가되어 들어감)
	public static void addStamp(Connection connection, int custid) {
	    try {
	        String insertQuery = "INSERT INTO STAMP (STAMPID, STAMPCNT, CUSTID) VALUES (?, ?, ?)";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
	            preparedStatement.setInt(1, custid); // 스탬프 테이블의 STAMPID 컬럼에 CUSTID 값을 삽입
	            preparedStatement.setInt(2, 0); // 스탬프 테이블의 STAMPCNT 컬럼에 0을 삽입
	            preparedStatement.setInt(3, custid); // 스탬프 테이블의 CUSTID 컬럼에 CUSTID 값을 삽입
	            preparedStatement.executeUpdate();
	        }
	        
    // 동시에 COUPON 테이블에도 COUPONCNT, STAMPID 값 입력 (COUPONCNT에는 일단 0을 넣어준다)
//	        String insertCouponQuery = "INSERT INTO COUPON (COUPONID, COUPONCNT, STAMPID) VALUES (?, ?, ?)";
//	        try (PreparedStatement couponStatement = connection.prepareStatement(insertCouponQuery)) {
//	        	
//    // COUPONID는 무슨 값을 넣어야 할까 PK키 지정을 해제하고 (null)값을 디폴트로 넣어주겠다.
//	            couponStatement.setString(1, null);
//	            couponStatement.setInt(2, 0);
//	            couponStatement.setInt(3, custid);
//	            couponStatement.executeUpdate();
//	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// 회원가입할때 적었던 정보들을 CUSTOMER 테이블에 넣어준다.
	public static void saveToDatabase(Connection connection, int id, String name, String password, String phoneNumber) {
        try {
            String insertQuery = "INSERT INTO CUSTOMER (CUSTID, CUSTNAME, PASSWORD, PHONE) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setString(4, password);
                preparedStatement.setString(3, phoneNumber);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	// 회원 정보 업데이트 메서드 (CUSTOMER 테이블의 고객아이디를 조회해 정보들을 업데이트)
    public static void updateUserInfo(Connection connection, int custid, String newName, String newPassword, String newPhone) {
        String updateQuery = "UPDATE CUSTOMER SET CUSTNAME = ?, PASSWORD = ?, PHONE = ? WHERE CUSTID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newPassword);
            preparedStatement.setString(3, newPhone);
            preparedStatement.setInt(4, custid);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // 회원 조회에서 2. 전체 회원 조회
    public static void retrieveAllUserInfo(Connection connection) {
        String selectQuery =  "SELECT "
                + "    CST.CUSTID, "
                + "    CST.CUSTNAME, "
                + "    CST.PASSWORD, "
                + "    CST.PHONE, "
                + "    STP.STAMPCNT, "
                + "    CST.STATUS "
                //+ "    C.COUPONCNT "
                + " FROM CUSTOMER CST JOIN STAMP STP ON CST.CUSTID = STP.CUSTID "
                + " ORDER BY CUSTID ";
                //+ "JOIN COUPON C ON STP.STAMPID = C.STAMPID";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int custid = resultSet.getInt("CUSTID");
                String custname = resultSet.getString("CUSTNAME");
                String password = resultSet.getString("PASSWORD");
                String phone = resultSet.getString("PHONE");
                int stampcnt = resultSet.getInt("STAMPCNT");
                String status = resultSet.getString("STATUS");
               //int couponcnt = resultSet.getInt("COUPONCNT");
                
                System.out.println("회원ID:" + custid + ", 이름 : " + custname + ", 비밀번호 : " + password + 
                		", 핸드폰 번호 : " + phone + ", 스탬프 개수 : " + stampcnt + ", 계정 상태 : " + status);
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
    
	// 회원 조회에서 1. 개별 고객 조회
    public static void retrieveAndUpdateUserInfo(Connection connection, Scanner scanner) { 
        while (true) {
            System.out.print("해당 고객 번호를 입력하세요 (0을 입력하면 처음으로 돌아갑니다): ");
            
            int custid = scanner.nextInt();
            scanner.nextLine(); // 입력 버퍼 비우기

            if (custid == 0) {
                System.out.println("처음으로 돌아갑니다.");
                break;
            }

            
            boolean customerExists = checkIfCustomerExists(connection, custid);

            if (customerExists) {
                processCustomerInfo(connection, scanner, custid);
            } else {
                System.out.println("▶▷▶▷ 고객 번호를 다시 입력해주세요.");
            }
        }
    }

    // 개별 회원번호 조회시 (회원 조회시 custid값이 존재하는지 카운트)
    public static boolean checkIfCustomerExists(Connection connection, int custid) {
        String query = "SELECT COUNT(*) FROM CUSTOMER WHERE CUSTID = ?";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, custid);
            ResultSet resultSet = preparedStatement.executeQuery();
        
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    
    //회원 조회 (개별 조회해서 정보를 불러들어와 1. 정보수정 2. 회원탈퇴 3. 회원복구 4. 이전으로 선택)
    public static void processCustomerInfo(Connection connection, Scanner scanner, int custid) {
    	String newName, newPassword, newPhone;
    	
    	String selectQuery =  "SELECT "
        		+ "    CST.CUSTID, "
        		+ "    CST.CUSTNAME, "
        		+ "    CST.PASSWORD, "
        		+ "    CST.PHONE, "
        		+ "    CST.STATUS, "
        		+ "    STP.STAMPCNT "
        		//+ "    C.COUPONCNT "
        		+ "FROM CUSTOMER CST JOIN STAMP STP ON CST.CUSTID = STP.CUSTID "
        		//+ "JOIN COUPON C ON STP.STAMPID = C.STAMPID "
        		+ "WHERE CST.CUSTID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, custid);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String custname = resultSet.getString("CUSTNAME");
                String password = resultSet.getString("PASSWORD");
                String phone = resultSet.getString("PHONE");
                String status = resultSet.getString("STATUS");
                int stampcnt = resultSet.getInt("STAMPCNT");
                //int couponcnt = resultSet.getInt("COUPONCNT");

                System.out.println("회원 정보");
                System.out.println("고객 번호: " + custid);
                System.out.println("이름: " + custname);
                System.out.println("비밀번호: " + password);
                System.out.println("핸드폰 번호: " + phone);
                System.out.println("계정 상태: " + status);
                System.out.println("스탬프 개수: " + stampcnt);
                

                // "ACTIVE" 상태일떄 1. 정보 수정, 2. 회원 탈퇴, 4. 이전으로 만 선택가능
                if (!status.equalsIgnoreCase("INACTIVE")) {
                	System.out.println("1. 정보 수정");
                	System.out.println("2. 회원 탈퇴");
                }
                
                // "INACTIVE" 상태일떄 3. 회원 복구 -> (ACTIVE로 상태 변경)와 4. 이전으로 만 선택가능
                if (!status.equalsIgnoreCase("ACTIVE")) {

                	System.out.println("3. 회원 복구");
                }
                System.out.println("4. 이전으로");
                System.out.print("원하는 작업을 선택하세요: ");
                int userChoice = scanner.nextInt();
                scanner.nextLine(); // 입력 버퍼 비우기

                switch (userChoice) {
                    case 1:
                        //updateUserInfo(connection, custid, scanner);
                    	
                    	while (true) {
                            System.out.print("이름을 입력하세요 (빈 문자열 제외): ");
                            newName = scanner.nextLine();
                            if (!custname.isEmpty()) {
                                break;
                            } else {
                                System.out.println("이름을 다시 입력해주세요.");
                            }
                        }
                    	
                        while (true) {
	                        System.out.print("새로운 비밀번호를 입력하세요 (알파벳, 숫자 포함 7자 이상): ");
	                        newPassword = scanner.nextLine();
	                        if (isValidPassword(newPassword)) {
	                            break;
	                        } else {
	                            System.out.println("▶▷▶▷ 다시 작성해주세요.");
	                        }
                        }
                
                        while (true) {
	                        System.out.print("새로운 핸드폰 번호를 입력하세요 (010-0000-0000 형식): ");
	                        newPhone = scanner.nextLine();
	                        if (isValidPhoneNumber(newPhone)) {
	                            break;
	                        } else {
	                            System.out.println("▶▷▶▷ 010-0000-0000 형식으로 다시 입력해주세요.");
	                        }
                        }
	                    
	                        updateUserInfo(connection, custid, newName, newPassword, newPhone); // 회원 정보 업데이트
	                        System.out.println("정보 수정이 완료되었습니다.");
	                        break;
	                        
                    case 2:
                        if (!status.equalsIgnoreCase("INACTIVE")) {
                        	System.out.println("[주의!] 스탬프가 사라집니다.");
                            System.out.print("정말 탈퇴하시겠습니까? (y/n): ");
                            String confirm = scanner.nextLine();

                            if (confirm.equalsIgnoreCase("y")) {
                                deactivateUser(connection, custid);
                                System.out.println("정상적으로 탈퇴되었습니다.");
                            } else if (confirm.equalsIgnoreCase("n")) {
                                // 탈퇴하지 않고 다른 작업 선택
                                return;
                            } else {
                                System.out.println("▶▷▶▷다시 입력해주세요.");
                            }
                        } 
                        break;
                    case 3:
                    	activateUser(connection, custid);
                    	System.out.println("정상적으로 복구되었습니다.");
                    	break;
                    case 4:
                        System.out.println("이전화면으로 돌아갑니다.");
                        break;
                    default:
                        System.out.println("▶▷▶▷메뉴를 다시 선택해주세요.");
                }
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



	public void retrieveAllUsers(Connection connection) {
		retrieveAllUserInfo(connection);
	}
	
	// 회원가입시 비밀번호에 알파벳, 숫자 포함 7자 이상 들어가는 조건
	private static boolean isValidPassword(String password) {

		return password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{7,}$");
	}

	// 회원가입시 핸드폰 번호는 010-0000-0000 형식으로 13자리
	private static boolean isValidPhoneNumber(String phoneNumber) {

	    return phoneNumber.matches("^01[0-9]{1}-[0-9]{3,4}-[0-9]{4}$");
	}
	
	// 회원가입시 이름에 숫자가 포함됐는지 확인 (포함했으면 다시 입력)
	private static boolean containsNumber(String input) {
		for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
	
}  
