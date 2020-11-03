/**
 * 作者:周明航
 * 2020-08-30
 * 功能:主函数所在类，负责项目的整体运行
 */
package Main;

import java.util.Scanner;
import Affair.*;

public class MainActivity {

	public static void splitLine() {//分割线函数
		System.out.println("\n***********************************************\n");
	}

	public static void init() {//初始化 & 角色选择 界面
		splitLine();
		System.out.println("--Powerbank System--");
		System.out.println("please choose your role to log in(0 to quit):");
		System.out.println("1. manager\t2. student");
		splitLine();
	}

	public static void quit() {//结束界面
		splitLine();
		System.out.println("--Powerbank System--");
		System.out.println("quit successfully");
		splitLine();
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		boolean run = true;
		while (run) {
			init();
			int role = sc.nextInt();
			Affair aff  = null;
			switch (role) {//根据所选角色进行不同事务
				case 0:
					run = false;
					continue;
				case 1:
					aff = new ManagerAffair();
					break;
				case 2:
					aff = new StudentAffair();
					break;			
				default:
					break;
			}
			aff.run();//多态运行事务
		}

		sc.close();
		quit();
	}
}
