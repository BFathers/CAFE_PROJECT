package com.mystudy.practice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mystudy.common.CommonJDBCUtil;

public class CAFE_DAO {

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

    public static void addStampAndCoupon(Connection connection, int custid) {
        try {
            String insertQuery = "INSERT INTO STAMP (STAMPID, CUSTID) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setInt(1, custid);
                preparedStatement.setInt(2, custid);
                preparedStatement.executeUpdate();
            }

            String insertCouponQuery = "INSERT INTO COUPON (COUPONID, COUPONCNT, STAMPID) VALUES (?, ?, ?)";
            try (PreparedStatement couponStatement = connection.prepareStatement(insertCouponQuery)) {
                couponStatement.setString(1, null);
                couponStatement.setInt(2, 0);
                couponStatement.setInt(3, custid);
                couponStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUserInfo(Connection connection, int custid, String newName, String newPassword, String newPhone) {
        try {
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

    public static CAFE_VO retrieveUserInfo(Connection connection, int custid) {
        CAFE_VO cafeVO = new CAFE_VO();
        try {
            String selectQuery = "SELECT CST.CUSTID, CST.CUSTNAME, CST.PASSWORD, CST.PHONE, CST.STATUS, STP.STAMPCNT, C.COUPONCNT "
                    + "FROM CUSTOMER CST JOIN STAMP STP ON CST.CUSTID = STP.CUSTID "
                    + "JOIN COUPON C ON STP.STAMPID = C.STAMPID WHERE CST.CUSTID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, custid);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    cafeVO.setCustid(resultSet.getInt("CUSTID"));
                    cafeVO.setCustname(resultSet.getString("CUSTNAME"));
                    cafeVO.setPassword(resultSet.getString("PASSWORD"));
                    cafeVO.setPhone(resultSet.getString("PHONE"));
                    cafeVO.setStatus(resultSet.getString("STATUS"));
                    cafeVO.setStampcnt(resultSet.getInt("STAMPCNT"));
                    cafeVO.setCouponcnt(resultSet.getInt("COUPONCNT"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cafeVO;
    }

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
