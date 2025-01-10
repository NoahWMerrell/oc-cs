// @author Noah Merrell

import java.lang.*;
import java.util.*;
import java.io.*;
public class CharacterCalculator{
	public static Scanner console = new Scanner(System.in);
	private static int level = 0;
	private static int hp;
	private static int draconicHP = 0;
	private static int passivePerception;
	private static int initiative;
	private static int bonusAC;
	private static int bonusInitiative = 0;
	private static int bonusPPerception = 0;
	private static int bonusHP = 0;
	private static int multiclassNumber = 1;
	private static int[][] info = {{8, 5, 2, 3}, {12, 7, 0, 2}, {8, 5, 1, 5}, {8, 5, 4, 5}, {8, 5, 3, 4}, {10, 6, 0, 2}, {8, 5, 0, 1}, {10, 6, 4, 5}, {10, 6, 0, 1}, {8, 5, 1, 3}, {6, 4, 2, 5}, {8, 5, 4, 5}, {6, 4, 3, 4}};
	private static String[] infoName = {"artificer", "barbarian", "bard", "cleric", "druid", "fighter", "monk", "paladin", "ranger", "rogue", "sorcerer", "warlock", "wizard"};
	private static String[] abilityScores = {"strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma"};
	private static int[] abilityModifier = new int[6];
	private static int proficiencyBonus = 0;
	private static String[] skills = {"acrobatics", "animal handling", "arcana", "athletics", "deception", "history", "insight", "intimidation", "investigation", "medicine", "nature", "perception", "performance", "persuasion", "religion", "sleight of hand", "stealth", "survival", "stop"};
	private static int[] skillBonus = new int[18];
	private static int[] skillProf = new int[19];
	private static int[] savingThrowBonus = new int[6];
	private static int[][] classInfo;
	private static String[] armor = {"none", "padded", "leather", "studded leather", "hide", "chain shirt", "scale mail", "breastplate", "half plate", "ring mail", "chain mail", "splint", "plate"};
	private static int[] armorAC = {10, 11, 11, 12, 12, 13, 14, 14, 15, 14, 16, 17, 18};
	private static int armorClass;
	private static int[][] spellSlots = {{0,0,0,0,0,0,0,0,0},{2,0,0,0,0,0,0,0,0},{3,0,0,0,0,0,0,0,0},{4,2,0,0,0,0,0,0,0},{4,3,0,0,0,0,0,0,0},{4,3,2,0,0,0,0,0,0},{4,3,3,0,0,0,0,0,0},{4,3,3,1,0,0,0,0,0},{4,3,3,2,0,0,0,0,0},{4,3,3,3,1,0,0,0,0},{4,3,3,3,2,0,0,0,0},{4,3,3,3,2,1,0,0,0},{4,3,3,3,2,1,0,0,0},{4,3,3,3,2,1,1,0,0},{4,3,3,3,2,1,1,0,0},{4,3,3,3,2,1,1,1,0},{4,3,3,3,2,1,1,1,0},{4,3,3,3,2,1,1,1,1},{4,3,3,3,3,1,1,1,1},{4,3,3,3,3,2,1,1,1},{4,3,3,3,3,2,2,1,1}};
	private static int[][] pactMagicSlots = {{0,0},{1,1},{1,2},{2,2},{2,2},{3,2},{3,2},{4,2},{4,2},{5,2},{5,2},{5,3},{5,3},{5,3},{5,3},{5,3},{5,3},{5,4},{5,4},{5,4},{5,4}};
	private static int pactMagicLevel = 0;
	private static boolean isEldritchKnight;
	private static boolean isArcaneTrickster;
	private static boolean isDraconic;
	private static String[] traitsToEdit = {"ac","armor class","hit points","initiative","passive perception","saving throw","stop"};
	public static void main(String[]args) throws FileNotFoundException{		
		// Establishing variables and arrays
		boolean isMulticlass = getAnswer("Are you multiclassing? (Yes or No)");
		if(isMulticlass == true){
			multiclassNumber = askInteger("How many classes are you multiclassing into?", 1, 13);
		}
		classInfo = new int[2][multiclassNumber];
		for(int i = 0; i < multiclassNumber; i++){
			classInfo[0][i] = getCharacterClass();
			classInfo[1][i] = getLevel();
		}
		
		for(int i = 0; i < multiclassNumber; i++){
			level = level + classInfo[1][i];
		}
		// Sets isArcaneTrickster/Eldritch Knight to false if rogue/fighter level is to low to become one
		testMagicClass();
		
		// Gets pact magic level based on warlock level
		pactMagicLevel = getPactMagic();
		
		// Identifies ability scores and gets modifier
		for(int i = 0; i < 6; i ++){
			abilityModifier[i] = getAbility(abilityScores[i]);
		}
		
		double proficiencyCalc = Math.ceil(1+(level/4.0)); // Calculates proficiency bonus then turns it into an integer
		proficiencyBonus = (int)proficiencyCalc;
		
		// Gets whether character is not proficient (0), proficient (1), or has expertise (2) in the skill.
		String answer = "";
		int skillProfInd = -1;
		while(skillProfInd != 18){
			skillProfInd = askString("Enter skills you are proficient in, type \"stop\" to stop. Enter the skill twice for expertise.", skills);
			skillProf[skillProfInd] = Math.min(2, skillProf[skillProfInd] + 1);
		}
		
		// Identifies and establishes skill bonuses and saving throw bonuses
		getSkillBonus();
		getSavingThrow();
		armorClass = getArmorClass();
		
		finalizeDetails();
		
		// Calculates hit points
		hp = info[classInfo[0][0]][0] + (level * (abilityModifier[2] + bonusHP)) - info[classInfo[0][0]][1];
		for(int i = 0; i < multiclassNumber; i++){
			hp = hp + (info[classInfo[0][i]][1] * classInfo[1][i]);
			if(infoName[classInfo[0][i]] == "sorcerer"){
				hp = hp + (draconicHP * classInfo[1][i]);
			}
		}
		
		armorClass += bonusAC;
		passivePerception = 10 + skillBonus[11] + bonusPPerception;
		initiative = abilityModifier[1] + bonusInitiative;
		
		System.out.println();
		printCharacterInformation();
		
		// Sets print statements to "character.txt" file and prints within the file
		File file =new File("character.txt");
		FileOutputStream outputFile = new FileOutputStream(file);  
		System.setOut(new PrintStream(outputFile));  
		printCharacterInformation();
		
		// Sets back to console for inform user in console
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		System.out.println("\n\nYour character information has been printed to the file \"character.txt\".");
	}
	
	public static void printCharacterInformation(){
		for(int i = 0; i < multiclassNumber; i++){
			System.out.print(infoName[classInfo[0][i]].substring(0, 1).toUpperCase() + infoName[classInfo[0][i]].substring(1) + " " + classInfo[1][i]);
			if(i < multiclassNumber-1){
				System.out.print(" / ");
			}
		}
		System.out.print("\n-----------------------------------------");
		
		//System.out.println("\nTotal Level: " + level);
		// Prints out proficiency bonus
		System.out.println("\nProfiency Bonus: " + printBonus(proficiencyBonus)+proficiencyBonus);
		
		// Prints out ability modifiers
		for(int i = 0; i < 6; i++){
			System.out.println(abilityScores[i].substring(0, 1).toUpperCase() + abilityScores[i].substring(1) + " Modifier: " + printBonus(abilityModifier[i])+abilityModifier[i]);
		}
		
		// Prints out saving throw bonuses
		System.out.println();
		for(int i = 0; i < 6; i++){
			System.out.println(abilityScores[i].substring(0, 1).toUpperCase() + abilityScores[i].substring(1) + " Saving Throw: "+ printBonus(savingThrowBonus[i])+savingThrowBonus[i]);
		}
		
		// Prints out skill bonuses
		System.out.println();
		for(int i = 0; i < 18; i++){
			System.out.println(skills[i].substring(0, 1).toUpperCase() + skills[i].substring(1) + ": "+ printBonus(skillBonus[i])+skillBonus[i]);
		}
		
		// Prints out and calculates etc. values
		System.out.println("\nPassive Perception: " + passivePerception);
		System.out.println("Initiative: " + printBonus(initiative)+initiative);
		System.out.println("AC: " + armorClass);
		System.out.println("Hit Point Maximum: " + hp);
		printHitDice();
		System.out.println();
		int spellcasterLevel = getSpellcasterLevel();
		//System.out.println("Spellcaster Level: " + spellcasterLevel);
		
		// Prints spell save DC and spell attack bonus
		if(spellcasterLevel > 0 || pactMagicLevel > 0){
			System.out.println();
			System.out.println("Spellcasting Information:\n-----------------------------------------");
			printSpellInfo();
			
			// Prints out spell slots
			for(int i = 0; i < 9; i++){
				if(spellcasterLevel > 0){
					if(i == 0){ // Seperates into multiple print statements due to the letters that come after the number (Example: 1"st", 2"nd", 3"rd", 4"th", and so on)
						System.out.println((i+1) + "st: " + spellSlots[spellcasterLevel][i]);
					} else if(i == 1){
						System.out.println((i+1) + "nd: " + spellSlots[spellcasterLevel][i]);
					} else if(i == 2){
						System.out.println((i+1) + "rd: " + spellSlots[spellcasterLevel][i]);
					} else{
						System.out.println((i+1) + "th: " + spellSlots[spellcasterLevel][i]);
					}
				}
			}
			// Prints out pact magic spell slots
			if(pactMagicLevel > 0){
				System.out.print("Pact Magic Slots = ");
				if(pactMagicLevel < 3){
					System.out.print(pactMagicSlots[pactMagicLevel][0] + "st: " + pactMagicSlots[pactMagicLevel][1]);
				} else if(pactMagicLevel >= 3 && pactMagicLevel < 5){
					System.out.print(pactMagicSlots[pactMagicLevel][0] + "nd: " + pactMagicSlots[pactMagicLevel][1]);
				} else if(pactMagicLevel >= 5 && pactMagicLevel < 7){
					System.out.print(pactMagicSlots[pactMagicLevel][0] + "rd: " + pactMagicSlots[pactMagicLevel][1]);
				} else{
					System.out.print(pactMagicSlots[pactMagicLevel][0] + "th: " + pactMagicSlots[pactMagicLevel][1]);
				}
			}
		}
	}
	
	// Gets your class information
	public static int getCharacterClass(){
		int index = askString("Enter your class:", infoName);
		if (infoName[index] == "sorcerer"){ // Asks if Draconic Bloodline Sorcerer, if yes will add +1 to hp
			isDraconic = getAnswer("Are you a Draconic Bloodline Sorcerer? (Yes or No)");
			if(isDraconic == true){
				draconicHP++;
			}
		}
		if (infoName[index] == "fighter"){
			isEldritchKnight = getAnswer("Are you an Eldritch Knight? (Yes or No)");
		}
		if (infoName[index] == "rogue"){
			isArcaneTrickster = getAnswer("Are you an Arcane Trickster? (Yes or No)");
		}
		return index;
	}
	
	// Determines ability score
	public static int getAbility(String abilityName){
		int abilityScore = askInteger("Enter your " + abilityName + " score:", 1, 30);
		double abilityMod = abilityScore; // Converts ability score into ability modifier which is what is used in calculations
		abilityMod = Math.floor((abilityMod - 10)/2);
		abilityScore = (int)abilityMod;
		return abilityScore;
	}
	
	// Allows user to input level
	public static int getLevel(){
		int level = askInteger("Enter your level:", 1, 20);
		return level;
	}
	
	// Calculates appropriate skill bonuses based on proficiency and ability score needed
	public static void getSkillBonus(){
		for(int i = 0; i < 18; i++){
			if(i == 3){
				skillBonus[i] = abilityModifier[0] + (proficiencyBonus * skillProf[i]);
			} else if(i == 0 || i == 15 || i == 16){
				skillBonus[i] = abilityModifier[1] + (proficiencyBonus * skillProf[i]);
			} else if(i == 2 || i == 5 || i == 8 || i == 10 || i == 14){
				skillBonus[i] = abilityModifier[3] + (proficiencyBonus * skillProf[i]);
			} else if(i == 1 || i == 6 || i == 9 || i == 11 || i == 17){
				skillBonus[i] = abilityModifier[4] + (proficiencyBonus * skillProf[i]);
			} else{
				skillBonus[i] = abilityModifier[5] + (proficiencyBonus * skillProf[i]);
			}
		} // Applies bonuses for Jack of All Trades which adds half your proficiency bonus 
		//(rounded down) if you are not proficient in the skill. Only works for Bards level 2 and above.
		double jackTradesBonusCalc = Math.floor(proficiencyBonus/2);
		int jackTradesBonus = (int)jackTradesBonusCalc;
		for(int i = 0; i < multiclassNumber; i++){
			if(classInfo[0][i] == 2 && classInfo[1][i] >= 2){
				for(int j = 0; j < 18; j++){
					if(skillProf[j] == 0){
						skillBonus[j] += jackTradesBonus;
					}
				}
			}
		}
	}
	
	// Calculates saving throw bonuses based on ability modifier and proficiency bonus
	public static void getSavingThrow(){
		for(int i = 0; i < 6; i++){
			savingThrowBonus[i] = abilityModifier[i];
			if(info[classInfo[0][0]][2] == i){
				savingThrowBonus[i] += proficiencyBonus;
			}
			if(info[classInfo[0][0]][3] == i){
				savingThrowBonus[i] += proficiencyBonus;
			}
		} // Acounts for level 14 Monks which gain proficiency in all saving throws
		for(int i = 0; i < multiclassNumber; i++){
			if(classInfo[0][i] == 6 && classInfo[1][i] >= 14){
				for(int j = 0; j < 6; j++){
					savingThrowBonus[j] = abilityModifier[j] + proficiencyBonus;
				}
			}
		}
	}
	
	// Calculates armor class based on armor equipped
	public static int getArmorClass(){
		int armorACIndex = askString("What armor are you wearing? Enter \"none\" if you aren't wearing any.\n(Do not input armor afterwards, for instance type \"plate\" instead of \"plate armor\".)", armor);
		int ac = 0;
		if(armorACIndex < 4){ // Determines base armor class for light armor (base + dexterity)
			ac = armorAC[armorACIndex] + abilityModifier[1];
		} else if(armorACIndex > 3 && armorACIndex < 9){ // Determines base armor class for medium armor (base + dexterity(maximum of 2 from dexterity))
			ac = armorAC[armorACIndex] + Math.min(2, abilityModifier[1]);
		} else{ // Determines base armor class for heavy armor (base)
			ac = armorAC[armorACIndex];
		}
		if(armorACIndex == 0){ // Acounts for Draconic Resilience (from Draconic Sorcerer) and Barbarian/Monk's Unarmored Defense and picks the highest among them if multiclassed
			int[] unarmoredAC = {0,0,0}; // Creates an array to store three unarmored AC
			if(isDraconic == true){ // Draconic Sorcerer's Draconic Resilience
				unarmoredAC[0] = 13 + abilityModifier[1];
			}
			for(int i = 0; i < multiclassNumber; i++){
				if(classInfo[0][i] == 1){ // Barbarian's Unarmored Defense
					unarmoredAC[1] = 10 + abilityModifier[1] + abilityModifier[2];
				}
				if(classInfo[0][i] == 6){ // Monk's Unarmored Defense
					unarmoredAC[2] = 10 + abilityModifier[1] + abilityModifier[4];
				}
			} // Compares all three AC values and picks the highest one
			for(int i = 0; i < 3; i++){
				if(unarmoredAC[i] > ac){
					ac = unarmoredAC[i];
				}
			}
		} // Determines if they have a shield equipped, if so add 2 to AC
		boolean isShield = getAnswer("Do you have a shield equipped? (Yes or No)");
		if(isShield == true){
			ac += 2;
		}
		return ac;
	}
	
	// Calculates total spellcaster level in order to calculate spell slots
	public static int getSpellcasterLevel(){
		int spellCL = 0;
		int spellCasterClasses = 0;
		for(int i = 0; i < multiclassNumber; i++){ // Updates spellCasterClasses when there is a class with spellcasting
			if(classInfo[0][i] == 2 || classInfo[0][i] == 3 || classInfo[0][i] == 4 || classInfo[0][i] == 10 || classInfo[0][i] == 12 || classInfo[0][i] == 0 || classInfo[0][i] == 7 || classInfo[0][i] == 8){
				spellCasterClasses++;
			} else if(isEldritchKnight == true && classInfo[0][i] == 5){
				spellCasterClasses++;
			} else if(isArcaneTrickster == true && classInfo[0][i] == 9){
				spellCasterClasses++;
			}
		} // half-casters and third-casters must take half or a third of their level for their spellcaster level, and
		// depending on if you multiclassed in a class having spellcasting levels it will either round up or round down
		for(int i = 0; i < multiclassNumber; i++){
			if(classInfo[0][i] == 2 || classInfo[0][i] == 3 || classInfo[0][i] == 4 || classInfo[0][i] == 10 || classInfo[0][i] == 12){ // For full casters
				spellCL += classInfo[1][i];
			} else if(classInfo[0][i] == 0 || classInfo[0][i] == 7 || classInfo[0][i] == 8){ // For half casters, uses spellCasterClasses to determine if round up or down
				if(spellCasterClasses > 1){
					double spellCLCalc = Math.floor((double)classInfo[1][i]/2);
					spellCL += (int)spellCLCalc;
				} else{
					double spellCLCalc = Math.ceil((double)classInfo[1][i]/2);
					spellCL += (int)spellCLCalc;
				}
			} else if(isEldritchKnight == true && classInfo[0][i] == 5){ // For Eldritch Knight Fighters, uses spellCasterClasses to determine if round up or down
				if(spellCasterClasses > 1){
					double spellCLCalc = Math.floor((double)classInfo[1][i]/3);
					spellCL += (int)spellCLCalc;
				} else{
					double spellCLCalc = Math.ceil((double)classInfo[1][i]/3);
					spellCL += (int)spellCLCalc;
				}
			} else if(isArcaneTrickster == true && classInfo[0][i] == 9){  // For Arcane Trickster Rogues, uses spellCasterClasses to determine if round up or down
				if(spellCasterClasses > 1){
					double spellCLCalc = Math.floor((double)classInfo[1][i]/3);
					spellCL += (int)spellCLCalc;
				} else{
					double spellCLCalc = Math.ceil((double)classInfo[1][i]/3);
					spellCL += (int)spellCLCalc;
				}
			}
		}
		return spellCL;
	}
	
	// Gets your pact magic level which is equivalent to your warlock level (if you don't have any it's 0)
	public static int getPactMagic(){
		int pactML = 0;
		for(int i = 0; i < multiclassNumber; i++){
			if(classInfo[0][i] == 11){
				pactML += classInfo[1][i];
			}
		}
		return pactML;
	}
	
	// Prints the hit dice based on class
	public static void printHitDice(){
		System.out.print("Hit Dice: ");
		int hd6 = 0;
		int hd8 = 0;
		int hd10 = 0;
		int hd12 = 0;
		for(int i = 0; i < multiclassNumber; i++){
			if(info[classInfo[0][i]][0] == 12){
				hd12 += classInfo[1][i];
			} else if(info[classInfo[0][i]][0] == 10){
				hd10 += classInfo[1][i];
			} else if(info[classInfo[0][i]][0] == 8){
				hd8 += classInfo[1][i];
			} else if(info[classInfo[0][i]][0] == 6){
				hd6 += classInfo[1][i];
			}
		}
		if(hd12 > 0){
			System.out.print(hd12 + "d12 ");
		} if(hd10 > 0){
			System.out.print(hd10 + "d10 ");
		} if(hd8 > 0){
			System.out.print(hd8 + "d8 ");
		} if(hd6 > 0){
			System.out.print(hd6 + "d6 ");
		}
	}
	
	// Prints the spellcasting class, spell attack bonus, spell save DC for every spellcasting class the user has levels in
	public static void printSpellInfo(){
		for(int i = 0; i < multiclassNumber; i++){
			if(classInfo[0][i] == 0 || classInfo[0][i] == 12){
				int spellAttack = abilityModifier[3] + proficiencyBonus;
				int spellDC = 8 + abilityModifier[3] + proficiencyBonus;
				System.out.println(infoName[classInfo[0][i]].substring(0, 1).toUpperCase() + infoName[classInfo[0][i]].substring(1) + " (Spell Attack Bonus " + printBonus(spellAttack)+spellAttack + ", Spell Save DC " + spellDC + ")");
			}else if(isEldritchKnight == true && classInfo[0][i] == 5){
				int spellAttack = abilityModifier[3] + proficiencyBonus;
				int spellDC = 8 + abilityModifier[3] + proficiencyBonus;
				System.out.println(infoName[classInfo[0][i]].substring(0, 1).toUpperCase() + infoName[classInfo[0][i]].substring(1) + " (Spell Attack Bonus " + printBonus(spellAttack)+spellAttack + ", Spell Save DC " + spellDC + ")");
			} else if(isArcaneTrickster == true && classInfo[0][i] == 9){
				int spellAttack = abilityModifier[3] + proficiencyBonus;
				int spellDC = 8 + abilityModifier[3] + proficiencyBonus;
				System.out.println(infoName[classInfo[0][i]].substring(0, 1).toUpperCase() + infoName[classInfo[0][i]].substring(1) + " (Spell Attack Bonus " + printBonus(spellAttack)+spellAttack + ", Spell Save DC " + spellDC + ")");
			} else if(classInfo[0][i] == 3 || classInfo[0][i] == 4 || classInfo[0][i] == 8){
				int spellAttack = abilityModifier[4] + proficiencyBonus;
				int spellDC = 8 + abilityModifier[4] + proficiencyBonus;
				System.out.println(infoName[classInfo[0][i]].substring(0, 1).toUpperCase() + infoName[classInfo[0][i]].substring(1) + " (Spell Attack Bonus " + printBonus(spellAttack)+spellAttack + ", Spell Save DC " + spellDC + ")");
			} else if(classInfo[0][i] == 2 || classInfo[0][i] == 7 || classInfo[0][i] == 10 || classInfo[0][i] == 11){
				int spellAttack = abilityModifier[5] + proficiencyBonus;
				int spellDC = 8 + abilityModifier[5] + proficiencyBonus;
				System.out.println(infoName[classInfo[0][i]].substring(0, 1).toUpperCase() + infoName[classInfo[0][i]].substring(1) + " (Spell Attack Bonus " + printBonus(spellAttack)+spellAttack + ", Spell Save DC " + spellDC + ")");
			}
		}
	}
	
	// Determines if fighter/rogue level is too low to use magic
	public static void testMagicClass(){
		for(int i = 0; i < multiclassNumber; i++){
			if(isEldritchKnight == true && classInfo[0][i] == 5 && classInfo[1][i] < 3){
				isEldritchKnight = false;
				System.out.println("Fighter level is below level 3, you are no longer an Eldritch Knight.");
			}
			if(isArcaneTrickster == true && classInfo[0][i] == 9 && classInfo[1][i] < 3){
				isArcaneTrickster = false;
				System.out.println("Rogue level is below level 3, you are no longer an Arcane Trickster.");
			}
		}
	}
	
	// Allows user to input any final details for bonuses they may have
	public static void finalizeDetails(){
		int traitsToEditInd = -1;
		while(traitsToEditInd != 6){
			traitsToEditInd = askString("Enter anything you wish to make changes to. Enter \"stop\" to stop.\n(AC/armor class, hit points, initiative, passive perception, saving throw)", traitsToEdit);
			if(traitsToEditInd == 0 || traitsToEditInd == 1){
				bonusAC += askInteger("Enter an additional bonus for armor class.", 0, 20);
			} else if(traitsToEditInd == 2){
				bonusHP += askInteger("Enter an additional hit point gain per level.", 0, 20);
			} else if(traitsToEditInd == 3){
				bonusInitiative += askInteger("Enter an additional bonus for initiative.", 0, 20);
			} else if(traitsToEditInd == 4){
				bonusPPerception += askInteger("Enter an additional bonus for passive perception.", 0, 20);
			} else if(traitsToEditInd == 5){
				int saveIndex = askString("Enter one saving throw proficiency you wish to add.", abilityScores);
				if(info[classInfo[0][0]][2] != saveIndex){
					savingThrowBonus[saveIndex] = abilityModifier[saveIndex] + proficiencyBonus;
				}
			}
		}
	}
	
	// Method used for yes and no questions
	public static boolean getAnswer(String question){
		boolean determine = false;
		String answer = "";
		System.out.println(question);
		while (true){ // Checks for correct input
			answer = console.nextLine();
			answer = answer.toLowerCase();
			if (!answer.equals("yes") && !answer.equals("no")){
				System.out.println("Invalid input, check spelling.");
			} else {break;}
		} // If yes, true; if no or other, false
		if (answer.equals("yes")){
			determine = true;
		} else {
			determine = false;
		}
		return determine;
	}
	
	// Asks a question and accepts specified input
	public static int askString(String question, String[] answers){
		int valueInt;
		List answers2 = Arrays.asList(answers);
		String input;
		System.out.println("\n"+question);
		while (true){ // Checks if value is valid
			input = console.nextLine().toLowerCase();
			if (!answers2.contains(input)){
				System.out.println("Invalid input. check spelling.");
			} else {break;}
		}
		return answers2.indexOf(input);
	}
	
	// Asks question for user to input an integer
	public static int askInteger(String question, int min, int max){
		String value;
		int valueInt;
		System.out.println("\n"+question);
		while (true){ // Checks if value is valid
			value = console.nextLine();
			if(!isNumeric(value)){
				System.out.println("Invalid input, must be a integer.");
				continue;
			}
			valueInt = Integer.parseInt(value);
			if (valueInt < min || valueInt > max){
				System.out.println("Invalid input, must be between " + min + "-" + max + ".");
			} else {break;}
		}
		return valueInt;
	}
	
	// Determines if value is a valid integer
	public static boolean isNumeric(String strNum){
		if (strNum == null){
			return false;
		}
		try {
			int n = Integer.parseInt(strNum);
		} catch (NumberFormatException e){
			return false;
		}
		return true;
	}
	
	// Used to add a "+" in front of the integer if it is positive so it looks like an integer
	public static String printBonus(int n){
		String str = "";
		if(n >= 0){
			str = "+";
		}
		return str;
	}
}
