/**
 * @author Noah Merrell
 * A turn based RPG.
 * Relies on character.txt, Unit.java, and Weapon.java
 */

import java.io.IOException;
import java.lang.*;
import java.util.*;
import java.io.*;
public class FinalProject{	
	public static Scanner console = new Scanner(System.in);
	public static Random rand = new Random();
	public static ArrayList<Unit> players = new ArrayList<Unit>();
	public static ArrayList<Unit> enemies = new ArrayList<Unit>();
	public static ArrayList<Unit> lvl1Enemies = new ArrayList<Unit>();
	public static ArrayList<Unit> lvl3Enemies = new ArrayList<Unit>();
	public static ArrayList<Unit> lvl5Enemies = new ArrayList<Unit>();
	public static ArrayList<Unit> lvl10Enemies = new ArrayList<Unit>();
	public static ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	public static int globalSpeed = 1;
	public static void main(String[]args)throws IOException, InterruptedException{
		buildWeapons();
		File file  = new File("characters.txt");
		Scanner scan = new Scanner(file); // Makes scanner
		players = buildUnits(scan);
		for(int i = 0; i < players.size(); i++){
			players.get(i).load();
		}
		
		buildEnemies();
		selectEnemies();
		
		//printUnitDetails(players.get(0));
		//printUnitDetails(players.get(1));
		//printUnitDetails(players.get(2));
		//printUnitDetails(players.get(3));
		
		battle();
		save();
	}
	
	/**
	 * Prints the hp, hpMax, ap, apMax, and level
	 * @param character is the Unit to print the information for
	 */
	public static void printUnitInfo(Unit character){
		if(character.defeated == true){
			System.out.print("(Defeated) ");
		}
		System.out.println(character.name + " HP:"+character.hp+"/"+character.hpMax+" AP:"+character.ap+"/"+character.apMax+" Lvl:"+character.level);
	}
	
	/**
	 * Adds onto printUnitInfo and prints the hp, hpMax, ap, apMax, level, each stat, each skill level, and the weapon equipped
	 * @param character is the Unit to print the information for
	 */
	public static void printUnitDetails(Unit character){
		printUnitInfo(character);
		System.out.println("Strength:"+character.strength+", Magic:"+character.magic+", Dexterity:"+character.dexterity+", Cunning:"+character.cunning+", Defense:"+character.defense+", Resistance:"+character.resistance);
		System.out.println("Damage:"+character.damage+", Hit Rate:"+character.hitRate+", Avoid:"+character.avoid+", Critical:"+character.crit+", Critical Avoid:"+character.critAvoid+", Protection:"+character.protection+", Resilience:"+character.resilience+", Aggro:"+character.aggro);
		System.out.println("Alertness:"+character.alertness+", Agility:"+character.agility+", Brutality:"+character.brutality+", Endurance:"+character.endurance+", Faith:"+character.faith+", Logic:"+character.logic+", Precision:"+character.precision);
		System.out.println("Weapon:"+character.equipped.name+"\n");
	}
	
	/**
	 * Uses printUnitInfo and prints for all player and enemy units
	 */
	public static void printBattleInfo(){
		System.out.println("\n");
		for(int i = 0; i < players.size(); i++){
			System.out.print((i+1)+". ");
			printUnitInfo(players.get(i));
		}
		System.out.println();
		for(int i = 0; i < enemies.size(); i++){
			System.out.print((i+1)+". ");
			printUnitInfo(enemies.get(i));
		}
	}
	
	/**
	 * Randomly selects a number between 1 and the number specified
	 * @param max the maximum roll possible
	 * @return random the randomized number
	 */
	public static int roll(int max){
		int random = rand.nextInt(max)+1;
		return random; 
	}
	
	/**
	 * Determines whether all the units have activated or not
	 * @param units the ArrayList containing the units to check
	 * @return false if every unit's active is false
	 */
	public static boolean isActive(ArrayList<Unit> units){
		for(int i = 0; i < units.size(); i++){
			if(units.get(i).active == true){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines whether all the units are defeated or not
	 * @param units the ArrayList containing the units to check
	 * @return false of every unit's defeated is true
	 */
	public static boolean isAlive(ArrayList<Unit> units){
		for(int i = 0; i < units.size(); i++){
			if(units.get(i).defeated == false){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Runs a combat battle and will keep running into either the enemies or players are defeated
	 */
	public static void battle(){
		int earnedXP = 0;
		for(int i = 0; i < enemies.size(); i++){
			enemies.get(i).load();
			earnedXP += enemies.get(i).experience;
		}
		
		while(isAlive(players) && isAlive(enemies)){
			combat();
			combatEnemy();
		}
		sleep(globalSpeed);
		clearScreen();
		if(isAlive(players)){
			System.out.println("You were victorious!");
			sleep(globalSpeed);
			for(int i = 0; i < players.size(); i++){
				players.get(i).experience += (int)Math.floor(earnedXP/players.size());
			}
			System.out.println("Everyone gains "+(int)Math.floor(earnedXP/players.size())+" experience!");
			for(int i = 0; i < players.size(); i++){
				players.get(i).checkLevelUp();
			}
		} else{
			System.out.println("GAME OVER!");
			System.exit(0);
		}
	}
	
	/**
	 * Performs the player's turn and asks the seperate questions to simulate a menu
	 */
	public static void combat(){
		for(int i = 0; i < players.size(); i++){
			players.get(i).reset();
			if(players.get(i).defeated == false){
				players.get(i).active = true;
			}
		}
		for(int i = 0; i < enemies.size(); i++){
			enemies.get(i).reset();
		}
		while(isActive(players) && isAlive(players) && isAlive(enemies)){
			printBattleInfo();
			HashMap<String, Integer> units = new HashMap<String, Integer>();
			for(int i = 0; i < players.size(); i++){
				if(players.get(i).defeated == false){
					units.put(players.get(i).name.toLowerCase(), i);
					units.put(""+(i+1), i);
				}
			}
			
			HashMap<String, Integer> enemyUnits = new HashMap<String, Integer>();
			for(int i = 0; i < enemies.size(); i++){
				if(enemies.get(i).defeated == false){
					enemyUnits.put(enemies.get(i).name.toLowerCase(), i);
					enemyUnits.put(""+(i+1), i);
				}
			}
			
			// Determining which unit the user will activate
			String activateQ = "Which character would you like to activate?";
			for(int i = 0; i < players.size(); i++){
				if(players.get(i).defeated == false){
					activateQ += "\n" + (i+1) + ". " + players.get(i).name;
				}
				if(players.get(i).active == false && players.get(i).defeated == false){
					activateQ += " (Activated)";
				}
			}
			int activatedPlayer = askString(activateQ, units);
			if(players.get(activatedPlayer).active == false){
				System.out.println("You have already activated "+players.get(activatedPlayer).name+" this turn!");
				sleep(1);
				continue;
			}
			
			// Check which action the unit will take
			HashMap<String, Integer> actions = new HashMap<String, Integer>();
			actions.put("attack", 0);
			actions.put("1", 0);
			if(players.get(activatedPlayer).abilities.size() > 0){
				actions.put("abilities", 1);
				actions.put("2", 1);
			}
			if(players.get(activatedPlayer).spells.size() > 0){
				actions.put("spells", 2);
				actions.put("3", 2);
			}
			actions.put("defend", 3);
			actions.put("4", 3);
			
			String actionsQ = "Which action would you like to take?";
			actionsQ += "\n1. Attack";
			if(players.get(activatedPlayer).abilities.size() > 0){
				actionsQ += "\n2. Abilities";
			}
			if(players.get(activatedPlayer).spells.size() > 0){
				actionsQ += "\n3. Spells";
			}
			actionsQ += "\n4. Defend";
			
			int actionIndex = askString(actionsQ, actions);
			if(actionIndex == 0){
				int target = findTarget(enemyUnits, enemies);
				players.get(activatedPlayer).attack(enemies.get(target));
			}
			if(actionIndex == 1){
				HashMap<String, Integer> abilitiesMap = new HashMap<String, Integer>();
				for(int i = 0; i < players.get(activatedPlayer).abilities.size(); i++){
					abilitiesMap.put(players.get(activatedPlayer).abilities.get(i).toLowerCase(), i);
					abilitiesMap.put(""+(i+1), i);
				}
				String abilityQ = "Which ability do you want to use?";
				for(int i = 0; i < players.get(activatedPlayer).abilities.size(); i++){
					abilityQ += "\n" + (i+1) + ". " + players.get(activatedPlayer).abilities.get(i);
				}
				String abilityUsed = askStringForString(abilityQ, abilitiesMap, players.get(activatedPlayer).abilities);
				
				performAction(abilityUsed, players.get(activatedPlayer), enemyUnits, units);
			}
			if(actionIndex == 2){
				HashMap<String, Integer> spellsMap = new HashMap<String, Integer>();
				for(int i = 0; i < players.get(activatedPlayer).spells.size(); i++){
					spellsMap.put(players.get(activatedPlayer).spells.get(i).toLowerCase(), i);
					spellsMap.put(""+(i+1), i);
				}
				String spellQ = "Which spell do you want to use?";
				for(int i = 0; i < players.get(activatedPlayer).spells.size(); i++){
					spellQ += "\n" + (i+1) + ". " + players.get(activatedPlayer).spells.get(i);
				}
				String spellUsed = askStringForString(spellQ, spellsMap, players.get(activatedPlayer).spells);
				
				performAction(spellUsed, players.get(activatedPlayer), enemyUnits, units);
			}
			if(actionIndex == 3){
				players.get(activatedPlayer).protection += 4;
				players.get(activatedPlayer).resilience += 4;
				players.get(activatedPlayer).active = false;
				System.out.println(players.get(activatedPlayer).name+" takes a defensive stance.");
				players.get(activatedPlayer).enduranceXP += 2;
				System.out.println(players.get(activatedPlayer).name+" gained 2 experience in Endurance!");
				sleep(globalSpeed);
			}
		}
		//System.out.println();
		clearScreen();
	}
	
	/**
	 * Performs the enemies' turn
	 */
	public static void combatEnemy(){
		ArrayList<Integer> selectedEnemy = new ArrayList<Integer>();
		int totalAlive = 0;
		for(int i = 0; i < enemies.size(); i++){
			if(enemies.get(i).defeated == false){
				selectedEnemy.add(i);
				totalAlive++;
			}
		}
		Collections.shuffle(selectedEnemy);
		for(int i = 0; i < totalAlive; i++){
			if(isAlive(players) && isAlive(enemies)){
				int currentEnemy = selectedEnemy.get(i);
				ArrayList<String> actions = new ArrayList<String>();
				actions.add("Attack");
				for(int j = 0; j < enemies.get(currentEnemy).abilities.size(); j++){
					actions.add(enemies.get(currentEnemy).abilities.get(j));
				}
				for(int j = 0; j < enemies.get(currentEnemy).spells.size(); j++){
					actions.add(enemies.get(currentEnemy).spells.get(j));
				}
				String actionChoice = actions.get(roll(actions.size())-1);

				// Picks target
				ArrayList<Integer> targets = new ArrayList<>();
				targets.add(0);
				for(int j = 1; j < players.size()+1; j++){
					if(players.get(j-1).defeated == false){
						targets.add(targets.get(targets.size()-1)+players.get(j-1).aggro);
					}
				}
				int targetRoll = roll(targets.get(targets.size()-1));
				int selectedTarget = 0;
				int k = 0;
				for(int j = 0; j < players.size(); j++){
					if(players.get(j).defeated == true){
						continue;
					}
					if(targetRoll > targets.get(k)){
						selectedTarget = j;
					}
					k++;
				}
				
				// Picks an ally target
				int selectedAllyTarget = roll(enemies.size());
				
				if(actionChoice == "Attack"){
					enemies.get(currentEnemy).attack(players.get(selectedTarget), true);
				}
				performAction(actionChoice, enemies.get(currentEnemy), selectedTarget, selectedAllyTarget);
				sleep(globalSpeed);
			}
		}
	}
	
	/**
	 * Player version of performAction that calls performAction with the correct parameters
	 * @param actionUsed the String of the action
	 * @param attacker Unit that will be performing the action
	 * @param enemyOptions HashMap that is used for displaying and getting the enemies the player can attack
	 */
	public static void performAction(String actionUsed, Unit attacker, HashMap<String, Integer> enemyOptions, HashMap<String, Integer> allyOptions){
		performAction(actionUsed, attacker, enemies, players, 0, 0, false, enemyOptions, allyOptions);
	}
	
	/**
	 * Enemy version of performAction that calls performAction with the correct parameters
	 * @param actionUsed the String of the action
	 * @param attacker Unit that will be performing the action
	 * @param targetIndex is the index of the target they will be attacking
	 */
	public static void performAction(String actionUsed, Unit attacker, int targetIndex, int allyTargetIndex){
		HashMap<String, Integer> empty = new HashMap<String, Integer>();
		performAction(actionUsed, attacker, players, enemies, targetIndex, allyTargetIndex, true, empty, empty);
	}
	
	/**
	 * Runs a series of if statements and finds the action based on the String, used for both the player and enemy
	 * @param actionUsed the String of the action
	 * @param attacker Unit that will be performing the action
	 * @param opposingGroup either the players or enemies ArrayList that contains all of the units (makes it possible to work for both players and enemies)
	 * @param targetIndex is the index of the target they will be attacking (only necessary for enemies)
	 * @param isEnemy if false it performs everything as if the player, if true it performs it as the enemy
	 * @param enemyOptions HashMap that is used for displaying and getting the enemies the player can attack (only necessary for players)
	 */
	public static void performAction(String actionUsed, Unit attacker, ArrayList<Unit> opposingGroup, ArrayList<Unit> allies, int targetIndex, int allyTargetIndex, boolean isEnemy, HashMap<String, Integer> enemyOptions, HashMap<String, Integer> allyOptions){
		
		// Abilities
		// Alertness
		if(actionUsed == "Lunge"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Lunge", 2, 10, 10, 0, 2, "", isEnemy);
		}
		if(actionUsed == "Distant Strike"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Distant Strike", 0, 10, 0, -3, 1, "", isEnemy);
		}
		if(actionUsed == "Skilled Strike"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Skilled Strike", 3, 20, 20, 1, 4, "", isEnemy);
		}
		if(actionUsed == "Coup De Grace"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Coup De Grace", 3, 30, 40, 2, 5, "", isEnemy);
		}
		
		// Agility
		if(actionUsed == "Wrath"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Wrath", 3, 10, 0, 1, 2, "", isEnemy);
		}
		if(actionUsed == "Riposte"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Riposte", 2, -10, 15, 1, 3, ", Avoid: 20", isEnemy);
			attacker.avoid += 20;
			System.out.println(attacker.name+"'s Avoid temporary increases by 20!");
			sleep(globalSpeed);
		}
		if(actionUsed == "Feint"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Feint", 1, 40, 15, 1, 4, ", Avoid: 10", isEnemy);
			attacker.avoid += 10;
			System.out.println(attacker.name+"'s Avoid temporary increases by 10!");
			sleep(globalSpeed);
		}
		if(actionUsed == "Swift Strikes"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Swift Strikes", 0, 10, 0, 1, 2, "\nSpecial: Attacks twice instead of once.\n(First Attack, if you perform this attack it will activate this unit even if you only performed one of the attacks)", isEnemy);
			target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Swift Strikes", 0, 10, 0, 1, 3, "\nSpecial: Attacks twice instead of once.\n(Second Attack, if you perform this attack it will activate this unit even if you only performed one of the attacks)", isEnemy);
		}
		
		// Brutality
		if(actionUsed == "Smash"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Smash", 5, 15, 15, 1, 3, "", isEnemy);
		}
		if(actionUsed == "Goad"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Goad", 3, 20, 5, 10, 2, "", isEnemy);
		}
		if(actionUsed == "Cleave"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Cleave", 2, 5, 0, 0, 1, "\nSpecial: Performs an additional weaker attack after the first one.\n(First Attack, if you perform this attack it will activate this unit even if you only performed one of the attacks)", isEnemy);
			target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Cleave", -2, 20, 15, 0, 2, "\nSpecial: Performs an additional weaker attack after the first one.\n(Second Attack, if you perform this attack it will activate this unit even if you only performed one of the attacks)", isEnemy);
		}
		if(actionUsed == "Death Blow"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Death Blow", 8, 10, 30, 7, 8, "", isEnemy);
		}
		
		// Endurance
		if(actionUsed == "Defensive Strike"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Defensive Strike", 2, 10, 0, 8, 4, ", Protection: 3, Resilience: 3", isEnemy);
			attacker.protection += 3;
			attacker.resilience += 3;
			System.out.println(attacker.name+"'s Protection temporary increases by 3!");
			System.out.println(attacker.name+"'s Resilience temporary increases by 3!");
			sleep(globalSpeed);
		}
		if(actionUsed == "Rally"){
			int cost = 2;
			boolean activated;
			int target = findTarget(isEnemy, allyOptions, allies, targetIndex);
			if(isEnemy == false){
				System.out.println(attacker.name+" increasing "+allies.get(target).name+"'s hit rate by "+(attacker.cunning*4)+"."+"\nAP:"+cost);
				activated = FinalProject.getAnswer("Would you like to perform this action?\n1. Yes\n2. No");
			} else{
				activated = true;
			}
			if(attacker.ap < cost){
				System.out.println(attacker.name+" does not have enough AP to perform Rally!");
				activated = false;
				sleep(globalSpeed);
			}
			if(activated == true){
				attacker.ap -= cost;
				allies.get(target).hitRate += (attacker.cunning*4);
				System.out.println(allies.get(target).name+"'s hit rate temporarly increased by "+(attacker.cunning*4)+" from "+attacker.name+"!");
				attacker.active = false;
				attacker.enduranceXP += 5;
				System.out.println(attacker.name+" gained 5 experience in Endurance!");
				sleep(globalSpeed);
			}
		}
		
		// Precision
		if(actionUsed == "Curved Shot"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Curved Shot", 1, 40, 10, 0, 2, "", isEnemy);
		}
		if(actionUsed == "Snipe"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Snipe", 0, 10, 10, -2, 1, "", isEnemy);
		}
		if(actionUsed == "Point Blank Shot"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Point Blank Shot", 4, -25, 20, 5, 1, "", isEnemy);
		}
		if(actionUsed == "Bullseye"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.attack(opposingGroup.get(target), "Bullseye", 3, 50, 30, -2, 4, "", isEnemy);
		}
		
		// Spells
		// Logic
		if(actionUsed == "Fire"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.spellAttack(opposingGroup.get(target), "Fire", 8, 80, 10, 5, 3, "Logic", "", isEnemy);
		}
		if(actionUsed == "Blizzard"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.spellAttack(opposingGroup.get(target), "Blizzard", 8, 70, 30, 6, 3, "Logic", "", isEnemy);
		}
		if(actionUsed == "Shock"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.spellAttack(opposingGroup.get(target), "Shock", 9, 90, 15, 4, 4, "Logic", "", isEnemy);
		}
		if(actionUsed == "Gale"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.spellAttack(opposingGroup.get(target), "Gale", 6, 90, 5, 5, 2, "Logic", "\nSpecial: Attacks twice instead of once.\n(First Attack, if you perform this attack it will activate this unit even if you only performed one of the attacks)", isEnemy);
			target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.spellAttack(opposingGroup.get(target), "Gale", 6, 90, 5, 5, 3, "Logic", "\nSpecial: Attacks twice instead of once.\n(Second Attack, if you perform this attack it will activate this unit even if you only performed one of the attacks)", isEnemy);
		}
		if(actionUsed == "Haste"){
			int cost = 4;
			boolean activated;
			int target = findTarget(isEnemy, allyOptions, allies, targetIndex);
			if(isEnemy == false){
				System.out.println(attacker.name+" reactivating "+allies.get(target).name+"."+"\nAP:"+cost+", Special: Reactivate the specified unit.\n(Using this on an already active unit will do nothing)");
				activated = FinalProject.getAnswer("Would you like to perform this action?\n1. Yes\n2. No");
			} else{
				activated = true;
			}
			if(attacker.ap < cost){
				System.out.println(attacker.name+" does not have enough AP to perform Haste!");
				activated = false;
				sleep(globalSpeed);
			}
			if(activated == true){
				attacker.ap -= cost;
				allies.get(target).active = true;
				System.out.println(allies.get(target).name+" has been reactivated by "+attacker.name+"!");
				attacker.active = false;
				attacker.logicXP += 5;
				System.out.println(attacker.name+" gained 5 experience in Logic!");
				sleep(globalSpeed);
			}
		}
		if(actionUsed == "Void"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.spellAttack(opposingGroup.get(target), "Void", 20, 100, 40, 9, 8, "Logic", "", isEnemy);
		}
		
		// Faith
		if(actionUsed == "Heal"){
			int cost = 3;
			boolean activated;
			int target = findTarget(isEnemy, allyOptions, allies, targetIndex);
			if(isEnemy == false){
				System.out.println(attacker.name+" restoring "+attacker.magic+" HP to "+allies.get(target).name+"\nAP:"+cost);
				activated = FinalProject.getAnswer("Would you like to perform this action?\n1. Yes\n2. No");
			} else{
				activated = true;
			}
			if(attacker.ap < cost){
				System.out.println(attacker.name+" does not have enough AP to perform Heal!");
				activated = false;
				sleep(globalSpeed);
			}
			if(activated == true){
				attacker.ap -= cost;
				allies.get(target).hp = Math.min(allies.get(target).hp + attacker.magic, allies.get(target).hpMax);
				System.out.println(allies.get(target).name+" regained "+attacker.magic+" HP from "+attacker.name+"!");
				attacker.active = false;
				attacker.faithXP += 5;
				System.out.println(attacker.name+" gained 5 experience in Faith!");
				sleep(globalSpeed);
			}
		}
		if(actionUsed == "Strengthen"){
			int cost = 1;
			boolean activated;
			int target = findTarget(isEnemy, allyOptions, allies, targetIndex);
			if(isEnemy == false){
				System.out.println(attacker.name+" increasing "+allies.get(target).name+"'s damage by "+((int)Math.floor(attacker.magic/2))+"."+"\nAP:"+cost);
				activated = FinalProject.getAnswer("Would you like to perform this action?\n1. Yes\n2. No");
			} else{
				activated = true;
			}
			if(attacker.ap < cost){
				System.out.println(attacker.name+" does not have enough AP to perform Strengthen!");
				activated = false;
				sleep(globalSpeed);
			}
			if(activated == true){
				attacker.ap -= cost;
				allies.get(target).damage += (int)Math.floor(attacker.magic/2);
				System.out.println(allies.get(target).name+"'s damage temporarly increased by "+((int)Math.floor(attacker.magic/2))+" from "+attacker.name+"!");
				attacker.active = false;
				attacker.faithXP += 5;
				System.out.println(attacker.name+" gained 5 experience in Faith!");
				sleep(globalSpeed);
			}
		}
		if(actionUsed == "Shield"){
			int cost = 2;
			boolean activated;
			int target = findTarget(isEnemy, allyOptions, allies, targetIndex);
			if(isEnemy == false){
				System.out.println(attacker.name+" increasing "+allies.get(target).name+"'s protection by "+((int)Math.floor(attacker.magic/2))+"."+"\nAP:"+cost);
				activated = FinalProject.getAnswer("Would you like to perform this action?\n1. Yes\n2. No");
			} else{
				activated = true;
			}
			if(attacker.ap < cost){
				System.out.println(attacker.name+" does not have enough AP to perform Shield!");
				activated = false;
				sleep(globalSpeed);
			}
			if(activated == true){
				attacker.ap -= cost;
				allies.get(target).protection += (int)Math.floor(attacker.magic/2);
				System.out.println(allies.get(target).name+"'s protection temporarly increased by "+((int)Math.floor(attacker.magic/2))+" from "+attacker.name+"!");
				attacker.active = false;
				attacker.faithXP += 5;
				System.out.println(attacker.name+" gained 5 experience in Faith!");
				sleep(globalSpeed);
			}
		}
		if(actionUsed == "Ward"){
			int cost = 2;
			boolean activated;
			int target = findTarget(isEnemy, allyOptions, allies, targetIndex);
			if(isEnemy == false){
				System.out.println(attacker.name+" increasing "+allies.get(target).name+"'s resilience by "+((int)Math.floor(attacker.magic/2))+"."+"\nAP:"+cost);
				activated = FinalProject.getAnswer("Would you like to perform this action?\n1. Yes\n2. No");
			} else{
				activated = true;
			}
			if(attacker.ap < cost){
				System.out.println(attacker.name+" does not have enough AP to perform Ward!");
				activated = false;
				sleep(globalSpeed);
			}
			if(activated == true){
				attacker.ap -= cost;
				allies.get(target).resilience += (int)Math.floor(attacker.magic/2);
				System.out.println(allies.get(target).name+"'s resilience temporarly increased by "+((int)Math.floor(attacker.magic/2))+" from "+attacker.name+"!");
				attacker.active = false;
				attacker.faithXP += 5;
				System.out.println(attacker.name+" gained 5 experience in Faith!");
				sleep(globalSpeed);
			}
		}
		if(actionUsed == "Smite"){
			int target = findTarget(isEnemy, enemyOptions, opposingGroup, targetIndex);
			attacker.spellAttack(opposingGroup.get(target), "Smite", 15, 90, 20, 5, 6, "Faith", "", isEnemy);
		}
		if(actionUsed == "Mass Heal"){
			int cost = 5;
			boolean activated;
			if(isEnemy == false){
				System.out.println(attacker.name+" restoring all allies' HP by "+((int)Math.floor(attacker.magic/2))+"."+"\nAP:"+cost);
				activated = FinalProject.getAnswer("Would you like to perform this action?\n1. Yes\n2. No");
			} else{
				activated = true;
			}
			if(attacker.ap < cost){
				System.out.println(attacker.name+" does not have enough AP to perform Mass Heal!");
				activated = false;
				sleep(globalSpeed);
			}
			if(activated == true){
				attacker.ap -= cost;
				for(int k = 0; k < allies.size(); k++){
					allies.get(k).hp = Math.min(allies.get(k).hp + attacker.magic, allies.get(k).hpMax);
					System.out.println(allies.get(k).name+" regained "+attacker.magic+" HP from "+attacker.name+"!");
				}
				attacker.active = false;
				attacker.faithXP += 5;
				System.out.println(attacker.name+" gained 5 experience in Faith!");
				sleep(globalSpeed);
			}
		}
	}
	
	/**
	 * Player version that asks which enemy they are targeting
	 * @param enemyOptions HashMap that is used for displaying and getting the enemies the player can attack
	 * @return target index of the selected target
	 */
	public static int findTarget(HashMap<String, Integer> enemyOptions, ArrayList<Unit> opposingGroup){
		int target = findTarget(false, enemyOptions, opposingGroup, 0);
		return target;
	}
	
	/**
	 * Version that is run for both the player and enemy that will either ask the player which enemy they will target, or have a preselected target for the enemy
	 * @param isEnemy if false it performs everything as if the player, if true it performs it as the enemy
	 * @param enemyOptions HashMap that is used for displaying and getting the enemies the player can attack
	 * @param targetIndex is the index of the target they will be attacking (only necessary for enemies)
	 * @return target index of the selected target
	 */
	public static int findTarget(boolean isEnemy, HashMap<String, Integer> enemyOptions, ArrayList<Unit> opposingGroup, int targetIndex){
		int target;
		if(isEnemy == false){
			String targetQ = "Who do you want to target?";
			for(int i = 0; i < opposingGroup.size(); i++){
				if(opposingGroup.get(i).defeated == false){
					targetQ += "\n" + (i+1) + ". " + opposingGroup.get(i).name;
				}
			}
			target = askString(targetQ, enemyOptions);
		} else{
			target = targetIndex;
		}
		return target;
	}
	
	
	
	/**
	 * Asks a question and receives an answer through an integer usually used as an index
	 * @param question String of question that will be asked
	 * @param answers list of possible answers as an ArrayList
	 * @return answers index based on input
	 */
	public static int askString(String question, ArrayList<String> answers){
		int valueInt;
		String input;
		System.out.println("\n"+question);
		while (true){ // Checks if value is valid
			input = console.nextLine().toLowerCase();
			if (!answers.contains(input)){
				System.out.println("Invalid input. check spelling.");
			} else {break;}
		}
		return answers.indexOf(input);
	}
	
	/**
	 * Asks a question and receives an answer through an integer usually used as an index
	 * @param question String of question that will be asked
	 * @param answers list of possible answers as a HashMap
	 * @return answers index based on input
	 */
	public static int askString(String question, HashMap<String, Integer> answers){
		int valueInt;
		String input;
		System.out.println("\n"+question);
		while (true){ // Checks if value is valid
			input = console.nextLine().toLowerCase();
			if (!answers.containsKey(input)){
				System.out.println("Invalid input. check spelling or index.");
			} else {break;}
		}
		return answers.get(input);
	}
	
	/**
	 * Asks a question and receives an answer through an String
	 * @param question String of question that will be asked
	 * @param answers list of possible answers as a HashMap that will be used to find the index
	 * @param answer ArrayList that will be searched through to get the answer as a String
	 * @return answer in the form of a String
	 */
	public static String askStringForString(String question, HashMap<String, Integer> answers, ArrayList<String> answer){
		int valueInt;
		String input;
		System.out.println("\n"+question);
		while (true){ // Checks if value is valid
			input = console.nextLine().toLowerCase();
			if (!answers.containsKey(input)){
				System.out.println("Invalid input. check spelling or index.");
			} else {break;}
		}
		return answer.get(answers.get(input));
	}
	
	/** 
	 * Method used for yes and no questions
	 * @param question being asked as a String
	 * @return determine which is a boolean that is either true for a yes or 1 response, or false for a no or 2 response
	 */
	public static boolean getAnswer(String question){
		boolean determine = false;
		String answer = "";
		System.out.println(question);
		while (true){ // Checks for correct input
			answer = console.nextLine();
			answer = answer.toLowerCase();
			if (!answer.equals("yes") && !answer.equals("no") && !answer.equals("1") && !answer.equals("2")){
				System.out.println("Invalid input, check spelling.");
			} else {break;}
		} // If yes, true; if no or other, false
		if (answer.equals("yes") || answer.equals("1")){
			determine = true;
		} else {
			determine = false;
		}
		return determine;
	}
	
	/**
	 * Used for reading the characters.txt file and getting the seperate attributes and stats of each character
	 * @param scan Scanner that it will read from
	 * @return units as an ArrayList that will add the seperate units and their info from the file
	 */
	public static ArrayList<Unit> buildUnits(Scanner scan){
		ArrayList<Unit> units = new ArrayList<>();
		while(scan.hasNext()){
			String name = scan.next();
			scan.skip(".*Level:");
			int level = scan.nextInt();
			scan.skip(".*Experience:");
			int experience = scan.nextInt();
			scan.skip(".*HP:");
			int hp = scan.nextInt();
			scan.skip(".*AP:");
			int ap = scan.nextInt();
			scan.skip(".*Strength:");
			int strength = scan.nextInt();
			scan.skip(".*Magic:");
			int magic = scan.nextInt();
			scan.skip(".*Dexterity:");
			int dexterity = scan.nextInt();
			scan.skip(".*Cunning:");
			int cunning = scan.nextInt();
			scan.skip(".*Defense:");
			int defense = scan.nextInt();
			scan.skip(".*Resistance:");
			int resistance = scan.nextInt();
			scan.skip(".*WeaponIndex:");
			int weaponIndex = scan.nextInt();
			scan.skip(".*Alertness:");
			int alertness = scan.nextInt();
			scan.skip(".*Agility:");
			int agility = scan.nextInt();
			scan.skip(".*Brutality:");
			int brutality = scan.nextInt();
			scan.skip(".*Endurance:");
			int endurance = scan.nextInt();
			scan.skip(".*Faith:");
			int faith = scan.nextInt();
			scan.skip(".*Logic:");
			int logic = scan.nextInt();
			scan.skip(".*Precision:");
			int precision = scan.nextInt();
			scan.skip(".*AlertnessXP:");
			int alertnessXP = scan.nextInt();
			scan.skip(".*AgilityXP:");
			int agilityXP = scan.nextInt();
			scan.skip(".*BrutalityXP:");
			int brutalityXP = scan.nextInt();
			scan.skip(".*EnduranceXP:");
			int enduranceXP = scan.nextInt();
			scan.skip(".*FaithXP:");
			int faithXP = scan.nextInt();
			scan.skip(".*LogicXP:");
			int logicXP = scan.nextInt();
			scan.skip(".*PrecisionXP:");
			int precisionXP = scan.nextInt();
			units.add(new Unit(name, level, experience, hp, ap, strength, magic, dexterity, cunning, defense, resistance, weapons.get(weaponIndex), weaponIndex, alertness, agility, brutality, endurance, faith, logic, precision, alertnessXP, agilityXP, brutalityXP, enduranceXP, faithXP, logicXP, precisionXP));
		}
		return units;
	}
	
	/**
	 * Creates every new Weapon and adds it to the weapons ArrayList
	 */
	public static void buildWeapons(){
		weapons.add(new Weapon("Sword", 5, 90, 0, 7, false)); // WeaponIndex:0
		weapons.add(new Weapon("Rapier", 4, 95, 5, 7, false)); // WeaponIndex:1
		weapons.add(new Weapon("Spear", 6, 80, 0, 6, false)); // WeaponIndex:2
		weapons.add(new Weapon("Glaive", 6, 75, 5, 6, false)); // WeaponIndex:3
		weapons.add(new Weapon("Axe", 7, 70, 5, 8, false)); // WeaponIndex:4
		weapons.add(new Weapon("Warhammer", 8, 60, 10, 9, false)); // WeaponIndex:5
		weapons.add(new Weapon("Shortbow", 4, 90, 0, 3, false)); // WeaponIndex:6
		weapons.add(new Weapon("Longbow", 5, 80, 0, 3, false)); // WeaponIndex:7
		weapons.add(new Weapon("Magic Missile", 4, 100, 0, 4, true)); // WeaponIndex:8
		weapons.add(new Weapon("Sacred Flame", 5, 85, 0, 4, true)); // WeaponIndex:9
	}
	
	/**
	 * Adds enemies of a certain level to a leveled ArrayList (For example, lvl5Enemies)
	 */
	public static void buildEnemies(){
		lvl1Enemies.add(new Unit("Cloak", 1, 20, 8, 12, 6, 6, 3, 6, 4, 5, weapons.get(6), 6, 0, 0, 0, 0, 1, 0, 0));
		lvl1Enemies.add(new Unit("Warden", 1, 20, 12, 8, 3, 7, 5, 4, 8, 3, weapons.get(4), 4, 0, 0, 1, 0, 0, 0, 0));
		lvl1Enemies.add(new Unit("Shroud", 1, 20, 9, 11, 5, 5, 4, 5, 4, 7, weapons.get(3), 3, 1, 0, 0, 0, 0, 0, 0));
		lvl1Enemies.add(new Unit("Shawl", 1, 20, 10, 10, 6, 4, 6, 4, 5, 5, weapons.get(1), 1, 0, 1, 0, 0, 0, 0, 0));
		
		lvl3Enemies.add(new Unit("Cloak", 3, 60, 9, 13, 7, 6, 5, 6, 5, 5, weapons.get(6), 6, 0, 0, 0, 0, 2, 0, 0));
		lvl3Enemies.add(new Unit("Warden", 3, 60, 14, 8, 3, 8, 5, 4, 9, 5, weapons.get(4), 4, 0, 0, 2, 0, 0, 0, 0));
		lvl3Enemies.add(new Unit("Shroud", 3, 60, 10, 12, 6, 5, 5, 6, 5, 7, weapons.get(3), 3, 2, 0, 0, 0, 0, 0, 0));
		lvl3Enemies.add(new Unit("Shawl", 3, 60, 11, 11, 7, 5, 6, 4, 6, 6, weapons.get(1), 1, 0, 2, 0, 0, 0, 0, 0));
		
		lvl5Enemies.add(new Unit("Cloak", 5, 100, 10, 14, 9, 8, 5, 6, 5, 5, weapons.get(6), 6, 0, 0, 0, 0, 3, 0, 0));
		lvl5Enemies.add(new Unit("Warden", 5, 100, 15, 10, 8, 3, 6, 4, 10, 6, weapons.get(4), 4, 0, 0, 3, 0, 0, 0, 0));
		lvl5Enemies.add(new Unit("Shroud", 5, 100, 11, 13, 7, 6, 6, 6, 5, 8, weapons.get(3), 3, 3, 0, 0, 0, 0, 0, 0));
		lvl5Enemies.add(new Unit("Shawl", 5, 100, 12, 12, 8, 5, 7, 4, 7, 7, weapons.get(1), 1, 0, 3, 0, 0, 0, 0, 0));
		
		lvl10Enemies.add(new Unit("Cloak", 10, 200, 11, 18, 11, 10, 7, 8, 6, 6, weapons.get(6), 6, 0, 0, 0, 0, 4, 0, 0));
		lvl10Enemies.add(new Unit("Warden", 10, 200, 19, 11, 10, 5, 8, 6, 12, 6, weapons.get(4), 4, 0, 0, 4, 0, 0, 0, 0));
		lvl10Enemies.add(new Unit("Shroud", 10, 200, 13, 16, 9, 8, 8, 8, 5, 10, weapons.get(3), 3, 4, 0, 0, 0, 0, 0, 0));
		lvl10Enemies.add(new Unit("Shawl", 10, 200, 14, 15, 10, 7, 9, 6, 8, 8, weapons.get(1), 1, 0, 4, 0, 0, 0, 0, 0));
	}
	
	/**
	 * Adds enemies of a specific level to the enemies array
	 */
	public static void selectEnemies(){
		int averageLevel = 0;
		for(int i = 0; i < players.size(); i++){
			averageLevel += players.get(i).level;
		}
		averageLevel = (int)Math.floor(averageLevel/players.size());
		if(averageLevel <= 1){
			for(int i = 0; i < 4; i++){
				enemies.add(lvl1Enemies.get(i));
			}
		} else if(averageLevel > 1 && averageLevel <= 3){
			for(int i = 0; i < 4; i++){
				enemies.add(lvl3Enemies.get(i));
			}
		} else if(averageLevel > 3 && averageLevel <= 5){
			for(int i = 0; i < 4; i++){
				enemies.add(lvl5Enemies.get(i));
			}
		} else{
			for(int i = 0; i < 4; i++){
				enemies.add(lvl10Enemies.get(i));
			}
		}
	}
	
	/**
	 * Adds the appropriate abilities and spells based on the unit's skill levels
	 * @param u Unit that it will be adding the skills to and reading
	 */
	public static void loadSkills(Unit u){
		// Alertness
		if(u.alertness >= 2){
			u.abilities.add("Lunge");
		}
		if(u.alertness >= 4){
			u.abilities.add("Distant Strike");
		}
		if(u.alertness >= 6){
			u.abilities.add("Skilled Strike");
		}
		if(u.alertness >= 8){
			u.abilities.add("Coup De Grace");
		}
		
		// Agility
		if(u.agility >= 2){
			u.abilities.add("Wrath");
		}
		if(u.agility >= 4){
			u.abilities.add("Riposte");
		}
		if(u.agility >= 6){
			u.abilities.add("Feint");
		}
		if(u.agility >= 8){
			u.abilities.add("Swift Strikes");
		}
		
		// Brutality
		if(u.brutality >= 2){
			u.abilities.add("Smash");
		}
		if(u.brutality >= 4){
			u.abilities.add("Goad");
		}
		if(u.brutality >= 6){
			u.abilities.add("Cleave");
		}
		if(u.brutality >= 8){
			u.abilities.add("Death Blow");
		}
		
		// Endurance
		if(u.endurance >= 2){
			u.abilities.add("Defensive Strike");
		}
		if(u.endurance >= 6){
			u.abilities.add("Rally");
		}
		
		// Faith
		if(u.faith >= 2){
			u.spells.add("Heal");
		}
		if(u.faith >= 3){
			u.spells.add("Strengthen");
		}
		if(u.faith >= 4){
			u.spells.add("Shield");
		}
		if(u.faith >= 6){
			u.spells.add("Ward");
		}
		if(u.faith >= 7){
			u.spells.add("Smite");
		}
		if(u.faith >= 8){
			u.spells.add("Mass Heal");
		}
		
		// Logic
		if(u.logic >= 2){
			u.spells.add("Fire");
		}
		if(u.logic >= 3){
			u.spells.add("Blizzard");
		}
		if(u.logic >= 4){
			u.spells.add("Shock");
		}
		if(u.logic >= 6){
			u.spells.add("Gale");
		}
		if(u.logic >= 7){
			u.spells.add("Haste");
		}
		if(u.logic >= 8){
			u.spells.add("Void");
		}
		
		// Precision
		if(u.precision >= 2){
			u.abilities.add("Curved Shot");
		}
		if(u.precision >= 4){
			u.abilities.add("Snipe");
		}
		if(u.precision >= 6){
			u.abilities.add("Point Blank Shot");
		}
		if(u.precision >= 8){
			u.abilities.add("Bullseye");
		}
	}
	
	/**
	 * Saves your progress by printing the information to the characters.txt file
	 */
	public static void save()throws FileNotFoundException{
		File f =new File("characters.txt");
		FileOutputStream outputFile = new FileOutputStream(f);  
		System.setOut(new PrintStream(outputFile));  
		
		for(int i = 0; i < players.size(); i++){
			System.out.println(players.get(i).name+" Level:"+players.get(i).level+" Experience:"+players.get(i).experience+" HP:"+players.get(i).hpMax
			+" AP:"+players.get(i).apMax+" Strength:"+players.get(i).strength+" Magic:"+players.get(i).magic+" Dexterity:"+players.get(i).dexterity
			+" Cunning:"+players.get(i).cunning+" Defense:"+players.get(i).defense+" Resistance:"+players.get(i).resistance+" WeaponIndex:"+players.get(i).weaponIndex+" Alertness:"+players.get(i).alertness
			+" Agility:"+players.get(i).agility+" Brutality:"+players.get(i).brutality+" Endurance:"+players.get(i).endurance+" Faith:"+players.get(i).faith
			+" Logic:"+players.get(i).logic+" Precision:"+players.get(i).precision+" AlertnessXP:"+players.get(i).alertnessXP
			+" AgilityXP:"+players.get(i).agilityXP+" BrutalityXP:"+players.get(i).brutalityXP+" EnduranceXP:"+players.get(i).enduranceXP+" FaithXP:"+players.get(i).faithXP
			+" LogicXP:"+players.get(i).logicXP+" PrecisionXP:"+players.get(i).precisionXP);
		}
		
		// Sets back to console for inform user in console
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}
	
	/**
	 * Mathmatical formula that computes the correct level for any given amount of XP
	 * Credit to Jonah Merrell for helping me in figuring out the formula
	 * @param xp the experience to compute from
	 * @return level the appropriate level
	 */
	public static int computeLevel(int xp){
		int level = (int)Math.floor(0.1*(Math.sqrt(10*xp+225)-15));
		return level;
	}
	
	/**
	* @author Adrian Veliz
	* Example code that may be helpful in your Final Project.
	*/
	
	/**
	 * Clears the terminal by invoking the environment's clear command.
	 * Differs between Windows and Unix
	 * 
	 * @see <a href="https://stackoverflow.com/questions/2979383/java-clear-the-console">java clear the console</a>
	 */
	public static void clearScreen() {
		try{//windows
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch(Exception e){//mac and linux
			try{
				new ProcessBuilder("clear").inheritIO().start().waitFor();
			}catch(Exception e2){}
		}
	}
	
	/**
	 * Delete the characters on a line.
	 * Has no affect if called after a println.
	 * Default is 80 characters. 
	 * 
	 * @see <a href="https://stackoverflow.com/questions/7522022/how-to-delete-stuff-printed-to-console-by-system-out-println">delete stuff</a>
	 */
	public static void clearLine(){
		clearLine(80);
	}
	/**
	 * Delete the characters on a line.
	 * @param length The number of characters to delete.
	 */
	public static void clearLine(int length){
		for(int i =0; i < length;i++){
			System.out.print("\b \b"); // backspace, blank, backspace
		}
		System.out.flush();
	}
	
	/**
	 * Sleep for some number of seconds.
	 * 
	 * @param seconds The number of seconds to sleep.
	 * @see <a href="https://stackoverflow.com/questions/24104313/how-do-i-make-a-delay-in-java">make a delay in java</a>
	 */
	public static void sleep(int seconds){
		try{
			Thread.sleep(seconds * 1000);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}

/**
 * My brother Jonah Merrell assisted me by explaining concepts I needed help
 * with and by occassionally helping me debug when I struggled to find an issue.
 * No specific code was shared. I also based a good portion of my game mechanics
 * off of Fire Emblem: Three Houses.
 * Additional information was also found on the following sites:
 * @see https://docs.oracle.com/javase/8/docs/api/overview-summary.html
 * @see https://www.geeksforgeeks.org/
 * @see https://stackoverflow.com/
 */
