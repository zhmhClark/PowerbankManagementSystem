/**
 * 作者:周明航
 * 2020-08-30
 * 功能:管理者事务类，负责以管理员身份操纵充电宝数据库
 */
package Affair;

import java.sql.Date;
import java.util.Scanner;

public class ManagerAffair extends Affair{
    
    @Override
    public void run() {
        while ( (!login()) && run) {//若登录失败且仍运行，则显示下方提示再重新登录
            splitLine();
            System.out.println("--Manager Affair--");
            System.out.println("passsword is wrong");
            splitLine();
        }
        if(!run) return;//若选择退出则停止运行
        System.out.println("log in successfully");
        while (run) {
            splitLine();
            System.out.println("--Manager Affair--");//下方选择具体事务类型：0.退出运行 1.修改密码 2.查询充电宝数据库
            System.out.println("please choose the affair(0 to quit):\n1. change the password\n2. query the powerbanks");
            splitLine(); 
            Scanner sc = new Scanner(System.in);
            int affair = sc.nextInt();
            switch (affair) {//根据选项进行不同事务
                case 0:
                    run = false;
                    continue;
                case 1:
                    changePassword();
                    break;
                case 2:
                    queryPowerbank();
                    break;
                default:
                    break;
            }        
        }
    }

    @Override
    protected boolean login() {//登录
        splitLine();//登录提示
        System.out.println("--Manager Affair--");
        System.out.println("please input the manager password(0 to quit):");
        splitLine();   
        
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        
        if (input.equals("0")) {//输入0以停止运行
            run = false;
            return false;
        }
        
        try {
            conn = connect();
            pstmt = conn.prepareStatement("select pw from ManagerPW");
            rt = pstmt.executeQuery();
            rt.next();
            return input.equals(rt.getString(1));//验证输入与密码是否相同
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void changePassword() {//修改密码
        splitLine();
        System.out.println("--Manager Affair:change password--");
        System.out.println("please input the new password");
        splitLine();
        Scanner sc = new Scanner(System.in);
        String newPw = sc.nextLine();
        try {
            conn = connect();
            pstmt = conn.prepareStatement("update ManagerPW set pw = ?;");//更新数据库，改为新密码
            pstmt.setString(1, newPw);
            pstmt.execute();           
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("password changed");
    }

    private void queryPowerbank() {//查询充电宝数据库
        splitLine();
        System.out.println("--Manager Affair: query powerbank--");
        System.out.println("please input the pbid of the powerbank");
        splitLine();
        Scanner sc = new Scanner(System.in);
        int pbid = sc.nextInt();//按充电宝唯一编号查询
        try {
            conn = connect();
            pstmt = conn.prepareStatement("select * from powerbank where pbid = ?;");
            pstmt.setInt(1, pbid);
            rt = pstmt.executeQuery();
            rt.next();
            
            String pname = rt.getString(1);
            Date pdate = rt.getDate(2);
            int price = rt.getInt(3);
            boolean avai = rt.getBoolean(5);

            if(avai) {  //如果充电宝仍由管理者保存，未借出，则显示该充电宝详细信息
                System.out.println("name"+"\t"+pname+"\n"+"perchase date"+"\t"+pdate+"\n"+"price"+"\t"+price+"\n"
                    +"pbid"+"\t"+pbid+"\n"+"is available");
            } else {    //如果充电宝已借出，则显示该充电宝详细信息与持有者
                pstmt = conn.prepareStatement("select stid from debit where pbid = ? and returned = false;");
                pstmt.setInt(1, pbid);
                rt = pstmt.executeQuery();
                rt.next();
                int stid = rt.getInt(1);
                pstmt = conn.prepareStatement("select stname from student where stid = ?;");
                pstmt.setInt(1, stid);
                rt = pstmt.executeQuery();
                rt.next();
                String stname = rt.getString(1);
                System.out.println("name"+"\t"+pname+"\n"+"perchase date"+"\t"+pdate+"\n"+"price"+"\t"+price+"\n"
                    +"pbid"+"\t"+pbid+"\n"+"is hold by " + stname + " 0whose id is " + stid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}