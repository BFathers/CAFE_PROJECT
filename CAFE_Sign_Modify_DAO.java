package com.mystudy.practice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mystudy.common.CommonJDBCUtil;

public class CAFE_Sign_Modify_DAO {
    public static void main(String[] args) {
    	try (Connection conn = CommonJDBCUtil.getConnection()) {
            Scanner scanner = new Scanner(System.in);
            
            // ------- 카페 들어와서 보는 메뉴 처음화면 --------
            
            while (true) {
                System.out.println("1. 주문하기");
                System.out.println("2. 회원가입");
                System.out.println("3. 회원조회(관리)");
                System.out.println("4. 종료");
                System.out.print("메뉴를 선택하세요: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // 입력 버퍼 비우기

                switch (choice) {
                    case 1:
                        // 메뉴 보기, 주문 하기 기능 추가
                        break;
                    case 2:
                        performSignUp(conn, scanner); // 회원가입
                        break;
                    case 3:
                        retrieveAndUpdateUserInfo(conn, scanner); //회원조회
                        break;
                    case 4:
                        System.out.println("프로그램을 종료합니다.");
                        return;
                    default:
                        System.out.println("올바른 메뉴를 선택하세요.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 회원 가입 메서드
    public static void performSignUp(Connection connection, Scanner scanner) {
    	String custname, password, phone;
        
        try {
            String getSeqQuery = "SELECT CUSTID_SEQ.NEXTVAL FROM DUAL";
            try (PreparedStatement seqStatement = connection.prepareStatement(getSeqQuery);
                 ResultSet resultSet = seqStatement.executeQuery()) {
                if (resultSet.next()) {
                    int custid = resultSet.getInt(1);

                    System.out.println("고객 번호: " + custid);
                    
                    System.out.print("이름을 입력하세요 : ");
                    custname = scanner.nextLine();

                    System.out.print("비밀번호를 입력하세요 : ");
                    password = scanner.nextLine();

                    System.out.print("핸드폰 번호를 입력하세요 : ");
                    phone = scanner.nextLine();

                    saveToDatabase(connection, custid, custname, phone, password); // 회원 정보 저장
                    addStampAndCoupon(connection, custid);
                    System.out.println("회원가입이 완료되었습니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }	

    // STAMP 및 COUPON 추가 메서드
    public static void addStampAndCoupon(Connection connection, int custid) {
    	 try {
 	        String insertQuery = "INSERT INTO STAMP (STAMPID, CUSTID) VALUES (?, ?)";
 	        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
 	            preparedStatement.setInt(1, custid); // 스탬프 테이블의 STAMPID 컬럼에 CUSTID 값을 삽입
 	            preparedStatement.setInt(2, custid); // 스탬프 테이블의 CUSTID 컬럼에 CUSTID 값을 삽입
 	            preparedStatement.executeUpdate();
 	        }
 	        
     // 동시에 COUPON 테이블에도 COUPONCNT, STAMPID 값 입력 (COUPONCNT에는 일단 0을 넣어준다)
 	        String insertCouponQuery = "INSERT INTO COUPON (COUPONID, COUPONCNT, STAMPID) VALUES (?, ?, ?)";
 	        try (PreparedStatement couponStatement = connection.prepareStatement(insertCouponQuery)) {
 	        	
     // COUPONID는 무슨 값을 넣어야 할까 PK키 지정을 해제하고 (null)값을 디폴트로 넣어주겠다.
 	            couponStatement.setString(1, null);
 	            couponStatement.setInt(2, 0);
 	            couponStatement.setInt(3, custid);
 	            couponStatement.executeUpdate();
 	        }
 	    } catch (SQLException e) {
 	        e.printStackTrace();
 	    }
 	}

    // 회원 정보 저장 메서드
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


    // 회원 정보 조회 및 수정 메서드
    public static void retrieveAndUpdateUserInfo(Connection connection, Scanner scanner) {
    	while (true) {
            System.out.print("해당 고객 번호를 입력하세요 (0을 입력하면 처음으로 돌아갑니다): ");
            
            int custid = scanner.nextInt();
            scanner.nextLine(); // 입력 버퍼 비우기

            if (custid == 0) {
                System.out.println("처음으로 돌아갑니다.");
                break;
            }

            String selectQuery =  "SELECT "
            		+ "    CST.CUSTID, "
            		+ "    CST.CUSTNAME, "
            		+ "    CST.PASSWORD, "
            		+ "    CST.PHONE, "
            		+ "    CST.STATUS, "
            		+ "    STP.STAMPCNT, "
            		+ "    C.COUPONCNT "
            		+ "FROM CUSTOMER CST JOIN STAMP STP ON CST.CUSTID = STP.CUSTID "
            		+ "JOIN COUPON C ON STP.STAMPID = C.STAMPID "
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
                    int couponcnt = resultSet.getInt("COUPONCNT");

                    System.out.println("회원 정보");
                    System.out.println("고객 번호 : " + custid);
                    System.out.println("이름 : " + custname);
                    System.out.println("비밀번호 : " + password);
                    System.out.println("핸드폰 번호 : " + phone);
                    System.out.println("계정 상태 : " + status);
                    System.out.println("스탬프 개수 : " + stampcnt + ", 쿠폰 개수 : " + couponcnt);
                    

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
                            updateUserInfo(connection, custid, scanner);
                            break;
                        case 2:
                            if (!status.equalsIgnoreCase("INACTIVE")) {
                            	System.out.println("[주의!] 스탬프 및 쿠폰이 사라집니다.");
                                System.out.print("정말 탈퇴하시겠습니까? (y/n): ");
                                String confirm = scanner.nextLine();

                                if (confirm.equalsIgnoreCase("y")) {
                                    deactivateUser(connection, custid);
                                } else if (confirm.equalsIgnoreCase("n")) {
                                    // 탈퇴하지 않고 다른 작업 선택
                                    continue;
                                } else {
                                    System.out.println("다시 입력해주세요.");
                                }
                            } 
                            break;
                        case 3:
                        	activateUser(connection, custid);
                        	break;
                        case 4:
                            System.out.println("처음으로 돌아갑니다.");
                            break;
                        default:
                            System.out.println("메뉴를 다시 선택해주세요.");
                    }
                } 
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 회원 정보 수정 메서드
    public static void updateUserInfo(Connection connection, int custid, Scanner scanner) {
    	try {
	        System.out.print("새로운 이름을 입력하세요 : ");
	        String newName = scanner.nextLine();

	        System.out.print("새로운 비밀번호를 입력하세요 : ");
	        String newPassword = scanner.nextLine();

	        System.out.print("새로운 핸드폰 번호를 입력하세요 : ");
	        String newPhone = scanner.nextLine();
	        System.out.println("변경이 완료되었습니다.");

	        String updateQuery = "UPDATE CUSTOMER SET CUSTNAME = ?, PASSWORD = ?, PHONE = ? WHERE CUSTID = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	            preparedStatement.setString(1, newName);
	            preparedStatement.setString(2, newPassword);
	            preparedStatement.setString(3, newPhone);
	            preparedStatement.setInt(4, custid);

	            preparedStatement.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

    // 회원 비활성화 메서드
    public static void deactivateUser(Connection connection, int custid) {
try {
	    	
	    	int stampcnt = getStampCount(connection, custid);
	    	int couponcnt = getCouponCount(connection, custid);
	    	
	        String updateQuery = "UPDATE CUSTOMER SET STATUS = 'INACTIVE' WHERE CUSTID = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	            preparedStatement.setInt(1, custid);

	            int rowsUpdated = preparedStatement.executeUpdate();
	            if (rowsUpdated > 0) {
	                System.out.println("회원 정보가 비활성화되었습니다.");
	                
	                if (stampcnt > 0 || couponcnt > 0) {
	                    resetStampCount(connection, custid);
	                    resetCouponCount(connection, custid);
	                }
	            } 
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

    // STAMPCNT 값 가져오기 메서드
    public static int getStampCount(Connection connection, int custid) {
    	int stampcnt = 0;
	    try {
	        String selectQuery = "SELECT STAMPCNT FROM STAMP WHERE CUSTID = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
	            preparedStatement.setInt(1, custid);

	            ResultSet resultSet = preparedStatement.executeQuery();
	            if (resultSet.next()) {
	                stampcnt = resultSet.getInt("STAMPCNT");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return stampcnt;
	}

    // COUPONCNT 값 가져오기 메서드
    public static int getCouponCount(Connection connection, int custid) {
    	int couponcnt = 0;
	    try {
	        String selectQuery = "SELECT COUPONCNT FROM COUPON WHERE STAMPID = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
	            preparedStatement.setInt(1, custid);

	            ResultSet resultSet = preparedStatement.executeQuery();
	            if (resultSet.next()) {
	                couponcnt = resultSet.getInt("COUPONCNT");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return couponcnt;
	}

    // STAMPCNT 값 초기화 메서드
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

    // COUPONCNT 값 초기화 메서드
    public static void resetCouponCount(Connection connection, int custid) {
    	try {
	        String updateQuery = "UPDATE COUPON SET COUPONCNT = 0 WHERE STAMPID = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	            preparedStatement.setInt(1, custid);
	            preparedStatement.executeUpdate();
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

	            int rowsUpdated = preparedStatement.executeUpdate();
	            if (rowsUpdated > 0) {
	                System.out.println("회원 정보가 활성화되었습니다.");
	            } 
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}