/**
 * @author Noah Merrell
 * Tests FinalProject
 * Relies on FinalProject.java, Weapon.java, and Unit.java
 */

import java.lang.*;
import java.util.*;
import java.io.*;

public class FinalProjectTest{
	private static ArrayList<Unit> testPlayers = new ArrayList<Unit>();
	private static ArrayList<Unit> testEnemies = new ArrayList<Unit>();
	private static int successCount = 0;
	private static int failCount = 0;
	public static void main(String[]args){
		System.setOut(new PrintStream(new FileOutputStream(new FileDescriptor()))); // Prevents text from being printed
		FinalProject.globalSpeed = 0;
		resetTest();
		addCount(testAttack());
		
		resetTest();
		addCount(testSkill());
		
		resetTest();
		addCount(testComputeLevel());
		
		resetTest();
		addCount(testPerformAction());
		
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out))); // Allows text to be printed again to see if the tests were successful or not
		System.out.println(successCount+" test(s) were successful.");
		System.out.println(failCount+" test(s) failed.");
	}
	
	/**
	 * Resets the ArrayLists containing test units
	 */
	public static void resetTest(){
		testPlayers.removeAll(testPlayers);
		testEnemies.removeAll(testEnemies);
		for(int i = 0; i < 4; i++){
			// Defined TestWeapon as a weapon that has 5 damage and 200 Hit, meaning that no matter what they will always hit their attacks which helps with testing
			testPlayers.add(new Unit("Player"+i,5,5,5,5,5,5,new Weapon("TestWeapon", 5, 200, 0, 7, false)));
			testEnemies.add(new Unit("Enemy"+i,5,5,5,5,5,5,new Weapon("TestWeapon", 5, 200, 0, 7, false)));
			testPlayers.get(i).load();
			testEnemies.get(i).load();
			testPlayers.get(i).active = true;
			testEnemies.get(i).active = true;
		}
	}
	
	/**
	 * Adds to the counter that determines if a test was successful or failed
	 */
	private static void addCount(boolean test){
		 if(test == true){
			 successCount++;
		 } else{
			 failCount++;
		 }
	}
	
	/**
	 * Tests the attack method and verifies that an enemy with 3 hit points
	 * will be defeated and reduced to 0 hit points (shows it won't cause negative HP).
	 * Also tests the checkDefeated method by verifying if they are defeated.
	 * (checkDefeated is in the attack method)
	 * @return true if enemy is defeated and at 0 HP
	 */
	private static boolean testAttack(){
		testEnemies.get(0).hp = 3;
		testPlayers.get(0).attack(testEnemies.get(0),true);
		if(testEnemies.get(0).hp == 0 && testEnemies.get(0).defeated == true){
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * Tests that the player has the appropriate abilities and spells for their skill level, also tests the loadSkills method
	 * @return true if Player0 has the appropriate abilities and spells
	 */
	private static boolean testSkill(){
		testPlayers.get(0).alertness = 4;
		testPlayers.get(0).logic = 4;
		FinalProject.loadSkills(testPlayers.get(0));
		if(testPlayers.get(0).abilities.contains("Lunge") && testPlayers.get(0).abilities.contains("Distant Strike") && testPlayers.get(0).spells.contains("Fire") && testPlayers.get(0).spells.contains("Blizzard") && testPlayers.get(0).spells.contains("Shock")){
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * Tests the computeLevel method
	 * @return true if given an amount of experience it correctly computes the right level
	 */
	private static boolean testComputeLevel(){
		if(FinalProject.computeLevel(0) == 0 && FinalProject.computeLevel(40) == 1 && FinalProject.computeLevel(100) == 2 && FinalProject.computeLevel(180) == 3 && FinalProject.computeLevel(280) == 4 && FinalProject.computeLevel(400) == 5){
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * Tests the performAction method by performing the Wrath and Heal action
	 * @return test which is true if Wrath reduces the enemy to the correct amount of HP and if Heal makes Player1 recover the correct amount of HP
	 */
	private static boolean testPerformAction(){
		boolean test;
		HashMap<String, Integer> empty = new HashMap<String, Integer>();
		FinalProject.performAction("Wrath", testPlayers.get(0), testEnemies, testPlayers, 0, 0, true, empty, empty);
		if(testEnemies.get(0).hp == 2){
			test = true;
		} else{
			test = false;
		}
		testPlayers.get(1).hp = 3;
		FinalProject.performAction("Heal", testPlayers.get(0), testEnemies, testPlayers, 1, 0, true, empty, empty);
		if(testPlayers.get(1).hp == 8){
			test = true;
		} else{
			test = false;
		}
		return test;
	}
}
