/**
 * 作者:周明航
 * 2020-08-30
 * 功能:学生事务类，负责以学生身份借出或归还充电宝
 */
package Affair;

import java.util.Scanner;
import java.sql.Date;

public class StudentAffair extends Affair{
    private String name;
    private String id;

    @Override
    public void run() {
        while ( (!login()) && run) {//登录需输入数据库中已有的学号，姓名。若不匹配且仍继续运行，则显示下方提示再重新登录
            splitLine();
            System.out.println("--Student Affair--");
            System.out.println("match failed");
            splitLine();
        }
        if(!run) return;//若选择退出则停止运行
        System.out.println("log in successfully");
        while (run) {
            splitLine();
            System.out.println("--Student Affair--");//下方选择具体事务类型：0.退出运行 1.借充电宝 2.归还充电宝
            System.out.println("please choose the affair(0 to quit):\n1. borrow\n2. return");
            splitLine();
            Scanner sc = new Scanner(System.in);
            int affair = sc.nextInt();
            switch (affair) {//根据选项进行不同事务
                case 0:
                    run = false;
                    continue;
                case 1:
                    borrowPb();
                    break;
                case 2:
                    returnPb();
                    break;
                default:
                    break;
            } 
        }
    }

    @Override
    protected boolean login() {//登录
        splitLine();//登录提示
        System.out.println("--Student Affair--");
        System.out.println("please input the id and name(0 to quit):");
        splitLine();

        Scanner sc = new Scanner(System.in);
        id = sc.next();        
        if (id.equals("0")) {//输入0以停止运行
            run = false;
            return false;
        }
        name = sc.nextLine();

        try {
            conn = connect();
            pstmt = conn.prepareStatement("select * from student where stid = ? and stname = ?;");
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            rt = pstmt.executeQuery();
            return rt.next(); //验证是否为表中的学号和姓名，以及学号姓名是否匹配
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void borrowPb() {//借充电宝
        Scanner sc = new Scanner(System.in);
        splitLine();
        System.out.println("--Student Affair:borrow powerbank--");
        System.out.println("please choose the capacity(mAh) (0 to quit)");//选择容量
        System.out.println("1. 10000\t2.20000\t3. 30000");
        splitLine();
        int capa = sc.nextInt();
        if(capa == 0) return;
        splitLine();
        System.out.println("--Student Affair:borrow powerbank--");
        System.out.println("please choose the brand(0 to quit)");//选择品牌
        System.out.println("1. apple\t2.huawei\t3. sumsung\t4. mi\t5.oppo\t6. lenovo");
        splitLine();
        int brand = sc.nextInt();
        if(brand == 0) return;
        int pbid = capa*brand;//此处利用了充电宝数据库设计时的排列特点
        try {
            conn = connect();
            pstmt = conn.prepareStatement("select available from powerbank where pbid = ?;");
            pstmt.setInt(1, pbid);
            rt = pstmt.executeQuery();
            rt.next();
            boolean avai = rt.getBoolean(1);//检查对应充电宝是否空闲可用
            if(avai) {
                try {
                    conn = connect();//下方将对应充电宝状态设为已借出，不可用
                    pstmt = conn.prepareStatement("update powerbank set available = false where pbid = ?;");
                    pstmt.setInt(1, pbid);
                    pstmt.execute();//下方在借记表中添加出借记录
                    pstmt = conn.prepareStatement("insert into debit values(?,?,?,false);");
                    pstmt.setInt(1, pbid);
                    pstmt.setString(2, id);
                    pstmt.setDate(3, new Date(new java.util.Date().getTime()));
                    pstmt.execute();
                    System.out.println("borrow successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {//若充电宝已借出，不可用，则显示下方提示
                splitLine();
                System.out.println("--Student Affair--");
                System.out.println("the powerbank is occupied");
                splitLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    private void returnPb() {//归还充电宝
        Scanner sc = new Scanner(System.in);
        splitLine();
        System.out.println("--Student Affair:return powerbank--");
        System.out.println("please input the powerbank id(0 to quit):");
        splitLine();
        int pbid = sc.nextInt();
        if(pbid == 0) return;
        try {
            conn = connect();//查询相应编号充电宝的状况
            pstmt = conn.prepareStatement("select available from powerbank where pbid = ?;");
            pstmt.setInt(1, pbid);
            rt = pstmt.executeQuery();
            rt.next();
            boolean avai = rt.getBoolean(1);
            if(avai) {//如果该充电宝空闲可用，则说明输入有误
                System.out.println("input wrong");
            } else {
                pstmt = conn.prepareStatement("update powerbank set available = true where pbid = ?;");
                pstmt.setInt(1, pbid);//将该充电宝状态恢复为空闲可用
                pstmt.execute();
                pstmt = conn.prepareStatement("update debit set returned = true where pbid =? and stid = ? and returned = false;");
                pstmt .setInt(1, pbid);//借记表记录其归还行为
                pstmt.setString(2, id);
                pstmt.execute();
                System.out.println("return successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();        
        }
    }
}