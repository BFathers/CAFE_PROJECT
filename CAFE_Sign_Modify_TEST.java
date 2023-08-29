package com.mystudy.practice;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.mystudy.common.CommonJDBCUtil;

public class CAFE_Sign_Modify_TEST {
    public static void main(String[] args) {
    	CAFE_Sign_Modify_DAO dao = new CAFE_Sign_Modify_DAO();
    	
        try (Connection conn = CommonJDBCUtil.getConnection()) {
            Scanner scanner = new Scanner(System.in);
            
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
                        // 주문하기 기능 추가
                        break;
                    case 2:
                    	dao.performSignUp(conn, scanner); // 회원가입
                        break;
                    case 3:
                        System.out.println("1. 개별 고객 조회");
                        System.out.println("2. 전체 회원 조회");
                        System.out.print("메뉴를 선택하세요: ");
                        int subChoice = scanner.nextInt();
                        scanner.nextLine(); // 입력 버퍼 비우기

                        switch (subChoice) {
                            case 1:
                                dao.retrieveAndUpdateUserInfo(conn, scanner); // 개별 고객 조회
                                break;
                            case 2:
                                dao.retrieveAllUsers(conn); // 전체 회원 조회
                                break;
                            default:
                                System.out.println("올바른 메뉴를 선택하세요.");
                        }
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
}