/**
 * @author Noah Merrell
 * The Unit object and its methods, utilized in FinalProject
 */

import java.util.*;
import java.lang.*;
public class Unit{
	String name = "Name";
	
	int hpMax = 10;
	int hp = hpMax;
	int apMax = 10;
	int ap = apMax;
	boolean active = true;
	boolean defeated = false;
	
	int experience = 0;
	int level = 1;
	
	int strength = 5;
	int magic = 5;
	int dexterity = 5;
	int cunning = 5;
	int defense = 5;
	int resistance = 5;
	
	Weapon equipped = new Weapon("None", 0, 0, 0, 0, false);
	int weaponIndex = -1;
	
	int damage = equipped.damage + strength;
	int hitRate = equipped.hit + dexterity;
	int avoid = dexterity;
	int crit = equipped.crit + cunning;
	int critAvoid = cunning;
	int protection = defense;
	int resilience = resistance;
	int aggro = equipped.aggro;
	
	int alertness = 0;
	int agility = 0;
	int brutality = 0;
	int endurance = 0;
	int faith = 0;
	int logic = 0;
	int precision = 0;
	
	int alertnessXP = 0;
	int agilityXP = 0;
	int brutalityXP = 0;
	int enduranceXP = 0;
	int faithXP = 0;
	int logicXP = 0;
	int precisionXP = 0;
	
	ArrayList<String> abilities = new ArrayList<String>();
	ArrayList<String> spells = new ArrayList<String>();
	
	public Unit(){}
	
	public Unit(String n, int str, int mag, int dex, int cun, int def, int res, Weapon wea){
		name = n;
		strength = str;
		magic = mag;
		dexterity = dex;
		cunning = cun;
		defense = def;
		resistance = res;
		equipped = wea;
	}
	
	public Unit(String n, int lvl, int xp, int hitp, int actionp, int str, int mag, int dex, int cun, int def, int res, Weapon wea, int weaIndex, int ale, int agi, int bru, int end, int fai, int log, int pre){
		name = n;
		level = lvl;
		experience = xp;
		hpMax = hitp;
		apMax = actionp;
		strength = str;
		magic = mag;
		dexterity = dex;
		cunning = cun;
		defense = def;
		resistance = res;
		equipped = wea;
		weaponIndex = weaIndex;
		alertness = ale;
		agility = agi;
		brutality = bru;
		endurance = end;
		faith = fai;
		logic = log;
		precision = pre;
	}
	
	public Unit(String n, int lvl, int xp, int hitp, int actionp, int str, int mag, int dex, int cun, int def, int res, Weapon wea, int weaIndex, int ale, int agi, int bru, int end, int fai, int log, int pre, int aleXP, int agiXP, int bruXP, int endXP, int faiXP, int logXP, int preXP){
		name = n;
		level = lvl;
		experience = xp;
		hpMax = hitp;
		apMax = actionp;
		strength = str;
		magic = mag;
		dexterity = dex;
		cunning = cun;
		defense = def;
		resistance = res;
		equipped = wea;
		weaponIndex = weaIndex;
		alertness = ale;
		agility = agi;
		brutality = bru;
		endurance = end;
		faith = fai;
		logic = log;
		precision = pre;
		alertnessXP = aleXP;
		agilityXP = agiXP;
		brutalityXP = bruXP;
		enduranceXP = endXP;
		faithXP = faiXP;
		logicXP = logXP;
		precisionXP = preXP;
	}
	
	/**
	 * Changes the units stats based on the weapon they equip, also updates weaponIndex
	 * @param index of Weapon from weapons they will be equipping
	 */
	public void equipWeapon(int index){
		Weapon w = FinalProject.weapons.get(index);
		this.equipped = w;
		if(w.isMagic == false){
			this.damage = this.equipped.damage + this.strength;
		}
		if(w.isMagic == true){
			this.damage = this.equipped.damage + this.magic;
		}
		this.hitRate = this.equipped.hit + this.dexterity;
		this.crit = this.equipped.crit + this.cunning;
		this.aggro = this.equipped.aggro;
		this.weaponIndex = index;
	}
	
	/**
	 * Changes the units stats based on the weapon they equip, doesn't update weaponIndex
	 * @param w Weapon they will be equipping
	 */
	public void equipWeapon(Weapon w){
		this.equipped = w;
		if(w.isMagic == false){
			this.damage = this.equipped.damage + this.strength;
		}
		if(w.isMagic == true){
			this.damage = this.equipped.damage + this.magic;
		}
		this.hitRate = this.equipped.hit + this.dexterity;
		this.crit = this.equipped.crit + this.cunning;
		this.aggro = this.equipped.aggro;
	}
	
	/**
	 * Player version that performs the attack method
	 * @param defender Unit they will be attacking
	 */
	public void attack(Unit defender){
		this.attack(defender, false);
	}
	
	/**
	 * Causes unit to attack another unit and calculates results, used for both players and enemies (for players it will verify if they want to attack, while for enemies it will automatically do it)
	 * @param defender Unit they will be attacking
	 * @param isEnemy false if they are a player, true if they are an enemy
	 */
	public void attack(Unit defender, boolean isEnemy){
		int damageOutcome = Math.max(this.damage - defender.protection, 0);
		if(this.equipped.isMagic == true){
			damageOutcome = Math.max(this.damage - defender.resilience, 0);
		}
		int hitChance = Math.max(this.hitRate - defender.avoid, 0);
		int critChance = Math.max(this.crit - defender.critAvoid, 0);
		int hitRoll = FinalProject.roll(100);
		int critRoll = FinalProject.roll(100);
		boolean activated;
		if(isEnemy == false){
			System.out.println(this.name+" attacking "+defender.name+".");
			System.out.println("Damage: "+damageOutcome+", Hit: "+Math.min(hitChance,100)+", Critical: "+Math.min(critChance,100));
			activated = FinalProject.getAnswer("Would you like to perform this attack?\n1. Yes\n2. No");
		} else{
			activated = true;
		}
		if(activated == true){
			if(hitRoll <= hitChance){
				if(critRoll <= critChance){
					System.out.println(this.name+" scores a critical hit dealing "+(damageOutcome*3)+" damage to "+defender.name+"!");
					defender.hp = Math.max(defender.hp - damageOutcome*3, 0);
					defender.checkDefeated();
				} else{
					System.out.println(this.name+" hits dealing "+(damageOutcome)+" damage to "+defender.name+".");
					defender.hp = Math.max(defender.hp - damageOutcome, 0);
					defender.checkDefeated();
				}
			} else{
				System.out.println(this.name+" misses against "+defender.name+"!");
			}
			this.active = false;
			if(isEnemy == false){
				this.awardSkillXP(3);
			}
			FinalProject.sleep(FinalProject.globalSpeed);
		}
		//System.out.println("hitRoll:"+hitRoll+" critRoll:"+critRoll);
	}
	
	/**
	 * Player version that performs the attack method for an ability
	 * @param defender Unit they will be attacking
	 * @param ability name of ability they will be using
	 * @param damageBonus ability's increase to damage
	 * @param hitBonus ability's increase to hit rate
	 * @param critBonus ability's increase to crit rate
	 * @param aggroBonus ability's increase (or decrease) to aggro
	 * @param apCost ability's ap cost
	 */
	public void attack(Unit defender, String ability, int damageBonus, int hitBonus, int critBonus, int aggroBonus, int apCost, String additionalDetails){
		this.attack(defender, ability, damageBonus, hitBonus, critBonus, aggroBonus, apCost, additionalDetails, false);
	}
	
	/**
	 * Causes unit to attack another unit with a special ability and calculates results, used for both players and enemies (for players it will verify if they want to attack, while for enemies it will automatically do it)
	 * @param defender Unit they will be attacking
	 * @param ability name of ability they will be using
	 * @param damageBonus ability's increase to damage
	 * @param hitBonus ability's increase to hit rate
	 * @param critBonus ability's increase to crit rate
	 * @param aggroBonus ability's increase (or decrease) to aggro
	 * @param apCost ability's ap cost
	 * @param isEnemy false if player, true if enemy
	 */
	public void attack(Unit defender, String ability, int damageBonus, int hitBonus, int critBonus, int aggroBonus, int apCost, String additionalDetails, boolean isEnemy){
		int damageOutcome = Math.max(this.damage + damageBonus - defender.protection, 0);
		if(this.equipped.isMagic == true){
			damageOutcome = Math.max(this.damage + damageBonus - defender.resilience, 0);
		}
		int hitChance = Math.max(this.hitRate + hitBonus - defender.avoid, 0);
		int critChance = Math.max(this.crit + critBonus - defender.critAvoid, 0);
		int hitRoll = FinalProject.roll(100);
		int critRoll = FinalProject.roll(100);
		boolean activated;
		if(isEnemy == false){
			System.out.println(this.name+" attacking "+defender.name+" with "+ability+".");
			System.out.println(ability+" Bonuses = Damage:"+damageBonus+", Hit:"+hitBonus+", Crit:"+critBonus+", Aggro:"+aggroBonus+", AP:"+apCost+additionalDetails);
			System.out.println("Damage: "+damageOutcome+", Hit: "+Math.min(hitChance,100)+", Critical: "+Math.min(critChance,100));
			activated = FinalProject.getAnswer("Would you like to perform this attack?\n1. Yes\n2. No");
		} else{
			activated = true;
		}
		if(this.ap < apCost){
			System.out.println(this.name+" does not have enough AP to perform "+ability+"!");
			activated = false;
			FinalProject.sleep(FinalProject.globalSpeed);
		}
		if(activated == true){
			this.ap -= apCost;
			this.aggro += aggroBonus;
			if(hitRoll <= hitChance){
				if(critRoll <= critChance){
					System.out.println(this.name+" scores a critical hit with "+ability+" dealing "+(damageOutcome*3)+" damage to "+defender.name+"!");
					defender.hp = Math.max(defender.hp - damageOutcome*3, 0);
					defender.checkDefeated();
				} else{
					System.out.println(this.name+" hits with "+ability+" dealing "+(damageOutcome)+" damage to "+defender.name+".");
					defender.hp = Math.max(defender.hp - damageOutcome, 0);
					defender.checkDefeated();
				}
			} else{
				System.out.println(this.name+" misses with "+ability+" against "+defender.name+"!");
			}
			this.active = false;
			if(isEnemy == false){
				this.awardSkillXP(5);
			}
			FinalProject.sleep(FinalProject.globalSpeed);
		}
		//System.out.println("hitRoll:"+hitRoll+" critRoll:"+critRoll);
	}
	
	/**
	 * Player version that performs the a spell attack
	 * @param defender Unit they will be attacking
	 * @param spell name of spell they will be using
	 * @param spellDamage the amount of damage the spell will do
	 * @param spellHit the hit rate of the spell
	 * @param spellCrit the crit rate of the spell
	 * @param spellAggro the aggro of the spell
	 * @param apCost spell's ap cost
	 */
	public void spellAttack(Unit defender, String spell, int spellDamage, int spellHit, int spellCrit, int spellAggro, int apCost, String type, String additionalDetails){
		this.spellAttack(defender, spell, spellDamage, spellHit, spellCrit, spellAggro, apCost, type, additionalDetails, false);
	}
	
	/**
	 * Causes unit to attack another unit with a spell and calculates results, used for both players and enemies (for players it will verify if they want to attack, while for enemies it will automatically do it)
	 * @param defender Unit they will be attacking
	 * @param spell name of spell they will be using
	 * @param spellDamage the amount of damage the spell will do
	 * @param spellHit the hit rate of the spell
	 * @param spellCrit the crit rate of the spell
	 * @param spellAggro the aggro of the spell
	 * @param apCost spell's ap cost
	 * @param isEnemy false if player, true if enemy
	 */
	public void spellAttack(Unit defender, String spell, int spellDamage, int spellHit, int spellCrit, int spellAggro, int apCost, String type, String additionalDetails, boolean isEnemy){
		Weapon original = this.equipped;
		this.equipWeapon(new Weapon(spell, spellDamage, spellHit, spellCrit, spellAggro, true));
		int damageOutcome = Math.max(this.damage - defender.resilience, 0);
		int hitChance = Math.max(this.hitRate - defender.avoid, 0);
		int critChance = Math.max(this.crit - defender.critAvoid, 0);
		int hitRoll = FinalProject.roll(100);
		int critRoll = FinalProject.roll(100);
		boolean activated;
		if(isEnemy == false){
			System.out.println(this.name+" attacking "+defender.name+" with "+spell+".");
			System.out.println("Damage: "+damageOutcome+", Hit: "+Math.min(hitChance,100)+", Critical: "+Math.min(critChance,100)+", Aggro:"+spellAggro+", AP:"+apCost+additionalDetails);
			activated = FinalProject.getAnswer("Would you like to perform this attack?\n1. Yes\n2. No");
		} else{
			activated = true;
		}
		if(this.ap < apCost){
			System.out.println(this.name+" does not have enough AP to perform "+spell+"!");
			activated = false;
			FinalProject.sleep(FinalProject.globalSpeed);
		}
		if(activated == true){
			this.ap -= apCost;
			if(hitRoll <= hitChance){
				if(critRoll <= critChance){
					System.out.println(this.name+" scores a critical hit with "+spell+" dealing "+(damageOutcome*3)+" damage to "+defender.name+"!");
					defender.hp = Math.max(defender.hp - damageOutcome*3, 0);
					defender.checkDefeated();
				} else{
					System.out.println(this.name+" hits with "+spell+" dealing "+(damageOutcome)+" damage to "+defender.name+".");
					defender.hp = Math.max(defender.hp - damageOutcome, 0);
					defender.checkDefeated();
				}
			} else{
				System.out.println(this.name+" misses with "+spell+" against "+defender.name+"!");
			}
			if(isEnemy == false){
				if(type == "Logic"){
					logicXP += 5;
					System.out.println(this.name+" gained 5 experience in Logic!");
				} else{
					faithXP += 5;
					System.out.println(this.name+" gained 5 experience in Faith!");
				}
			}
			this.active = false;
			FinalProject.sleep(FinalProject.globalSpeed);
		}
		this.equipWeapon(original);
		//System.out.println("hitRoll:"+hitRoll+" critRoll:"+critRoll);
	}
	
	/**
	 * Resets hp, ap, defeated, and attributes of unit (through the reset method), used for reseting after first loading into the game
	 */
	public void load(){
		this.hp = hpMax;
		this.ap = apMax;
		this.defeated = false;
		this.reset();
		FinalProject.loadSkills(this);
	}
	
	/**
	 * Resets attributes of unit, used for reseting them after each round of combat
	 */
	public void reset(){
		if(this.equipped.isMagic == false){
			this.damage = this.equipped.damage + this.strength;
		}
		if(this.equipped.isMagic == true){
			this.damage = this.equipped.damage + this.magic;
		}
		this.hitRate = this.equipped.hit + this.dexterity;
		this.avoid = this.dexterity;
		this.crit = this.equipped.crit + this.cunning;
		this.critAvoid = this.cunning;
		this.protection = this.defense;
		this.resilience = this.resistance;
		this.aggro = this.equipped.aggro;
	}
	
	/**
	 * Check if the unit has the appropriate experience to level up
	 */
	public void checkLevelUp(){
		this.load();
		HashMap<String, Integer> attributes = new HashMap<String, Integer>();
		attributes.put("strength", 0);
		attributes.put("1", 0);
		attributes.put("magic", 1);
		attributes.put("2", 1);
		attributes.put("dexterity", 2);
		attributes.put("3", 2);
		attributes.put("cunning", 3);
		attributes.put("4", 3);
		attributes.put("defense", 4);
		attributes.put("5", 4);
		attributes.put("resistance", 5);
		attributes.put("6", 5);
		HashMap<String, Integer> points = new HashMap<String, Integer>();
		points.put("hp", 0);
		points.put("1", 0);
		points.put("ap", 1);
		points.put("2", 1);
		HashMap<String, Integer> skills = new HashMap<String, Integer>();
		skills.put("alertness", 0);
		skills.put("1", 0);
		skills.put("agility", 1);
		skills.put("2", 1);
		skills.put("brutality", 2);
		skills.put("3", 2);
		skills.put("endurance", 3);
		skills.put("4", 3);
		skills.put("faith", 4);
		skills.put("5", 4);
		skills.put("logic", 5);
		skills.put("6", 5);
		skills.put("precision", 6);
		skills.put("7", 6);
		
		while(this.level < FinalProject.computeLevel(experience)+1){
			this.level++;
			System.out.println(this.name+" has leveled up!");
			// First attribute increase
			int attributeChosen = FinalProject.askString("What is the first attribute would you like to increase?\n1. Strength\n2. Magic\n3. Dexterity\n4. Cunning\n5. Defense\n6. Resistance", attributes);
			if(attributeChosen == 0){
				this.strength++;
				System.out.println(this.name+"'s Strength increased from "+(this.strength-1)+" to "+this.strength+"!");
			} else if(attributeChosen == 1){
				this.magic++;
				System.out.println(this.name+"'s Magic increased from "+(this.magic-1)+" to "+this.magic+"!");
			} else if(attributeChosen == 2){
				this.dexterity++;
				System.out.println(this.name+"'s Dexterity increased from "+(this.dexterity-1)+" to "+this.dexterity+"!");
			} else if(attributeChosen == 3){
				this.cunning++;
				System.out.println(this.name+"'s Cunning increased from "+(this.cunning-1)+" to "+this.cunning+"!");
			} else if(attributeChosen == 4){
				this.defense++;
				System.out.println(this.name+"'s Defense increased from "+(this.defense-1)+" to "+this.defense+"!");
			} else{
				this.resistance++;
				System.out.println(this.name+"'s Resistance increased from "+(this.resistance-1)+" to "+this.resistance+"!");
			}
			FinalProject.sleep(FinalProject.globalSpeed);
			// Second attribute increase
			attributeChosen = FinalProject.askString("What is the second attribute would you like to increase?\n1. Strength\n2. Magic\n3. Dexterity\n4. Cunning\n5. Defense\n6. Resistance", attributes);
			if(attributeChosen == 0){
				this.strength++;
				System.out.println(this.name+"'s Strength increased from "+(this.strength-1)+" to "+this.strength+"!");
			} else if(attributeChosen == 1){
				this.magic++;
				System.out.println(this.name+"'s Magic increased from "+(this.magic-1)+" to "+this.magic+"!");
			} else if(attributeChosen == 2){
				this.dexterity++;
				System.out.println(this.name+"'s Dexterity increased from "+(this.dexterity-1)+" to "+this.dexterity+"!");
			} else if(attributeChosen == 3){
				this.cunning++;
				System.out.println(this.name+"'s Cunning increased from "+(this.cunning-1)+" to "+this.cunning+"!");
			} else if(attributeChosen == 4){
				this.defense++;
				System.out.println(this.name+"'s Defense increased from "+(this.defense-1)+" to "+this.defense+"!");
			} else{
				this.resistance++;
				System.out.println(this.name+"'s Resistance increased from "+(this.resistance-1)+" to "+this.resistance+"!");
			}
			FinalProject.sleep(FinalProject.globalSpeed);
			// HP or AP increase
			int pointsChosen = FinalProject.askString("Would you like to increase your HP or AP?\n1. HP\n2. AP", points);
			if(pointsChosen == 0){
				this.hpMax++;
				this.hp++;
				System.out.println(this.name+"'s HP increased from "+(this.hpMax-1)+" to "+this.hpMax+"!");
			} else{
				this.apMax++;
				this.ap++;
				System.out.println(this.name+"'s AP increased from "+(this.apMax-1)+" to "+this.apMax+"!");
			}
			FinalProject.sleep(FinalProject.globalSpeed);
			// Skill XP increases (Four 10 XP increases they player can select)
			for(int j = 0; j < 4; j++){
				int skillsChosen = FinalProject.askString("Which skill would you like to gain 10 xp in?\n1. Alertness ("+this.alertnessXP+")\n2. Agility ("+this.agilityXP+")\n3. Brutality ("+this.brutalityXP+")\n4. Endurance ("+this.enduranceXP+")\n5. Faith ("+this.faithXP+")\n6. Logic ("+this.logicXP+")\n7. Precision ("+this.precisionXP+")", skills);
				if(skillsChosen == 0){
					this.alertnessXP += 10;
					System.out.println(this.name+" gained 10 experience in Alertness!");
				} else if(skillsChosen == 1){
					this.agilityXP += 10;
					System.out.println(this.name+" gained 10 experience in Agility!");
				} else if(skillsChosen == 2){
					this.brutalityXP += 10;
					System.out.println(this.name+" gained 10 experience in Brutality!");
				} else if(skillsChosen == 3){
					this.enduranceXP += 10;
					System.out.println(this.name+" gained 10 experience in Endurance!");
				} else if(skillsChosen == 4){
					this.faithXP += 10;
					System.out.println(this.name+" gained 10 experience in Faith!");
				} else if(skillsChosen == 5){
					this.logicXP += 10;
					System.out.println(this.name+" gained 10 experience in Logic!");
				} else if(skillsChosen == 6){
					this.precisionXP += 10;
					System.out.println(this.name+" gained 10 experience in Precision!");
				}
				this.checkSkillIncrease();
				FinalProject.sleep(FinalProject.globalSpeed);
			}
			this.load();
		}
	}
	
	/**
	 * Checks every skill and increases them if the player has enough experience
	 */
	public void checkSkillIncrease(){
		while(alertness < FinalProject.computeLevel(alertnessXP)){
			alertness++;
			System.out.println(this.name+"'s Alertness has increased from "+(this.alertness-1)+" to "+this.alertness+"!");
		}
		while(agility < FinalProject.computeLevel(agilityXP)){
			agility++;
			System.out.println(this.name+"'s Agility has increased from "+(this.agility-1)+" to "+this.agility+"!");
		}
		while(brutality < FinalProject.computeLevel(brutalityXP)){
			brutality++;
			System.out.println(this.name+"'s Brutality has increased from "+(this.brutality-1)+" to "+this.brutality+"!");
		}
		while(endurance < FinalProject.computeLevel(enduranceXP)){
			endurance++;
			System.out.println(this.name+"'s Endurance has increased from "+(this.endurance-1)+" to "+this.endurance+"!");
		}
		while(faith < FinalProject.computeLevel(faithXP)){
			faith++;
			System.out.println(this.name+"'s Faith has increased from "+(this.faith-1)+" to "+this.faith+"!");
		}
		while(logic < FinalProject.computeLevel(logicXP)){
			logic++;
			System.out.println(this.name+"'s Logic has increased from "+(this.logic-1)+" to "+this.logic+"!");
		}
		while(precision < FinalProject.computeLevel(precisionXP)){
			precision++;
			System.out.println(this.name+"'s Precision has increased from "+(this.precision-1)+" to "+this.precision+"!");
		}
	}
	
	/**
	 * Checks if the unit's hp is 0, then it will set it's defeated to true if it is
	 */
	public void checkDefeated(){
		if(this.hp <= 0){
			defeated = true;
			System.out.println(this.name+" has been defeated!");
		}
		if(this.hp > 0){
			defeated = false;
		}
	}
	
	public void awardSkillXP(int amount){
		if(this.weaponIndex == 0 || this.weaponIndex == 1){
			agilityXP += amount;
			System.out.println(this.name+" gained "+amount+" experience in Agility!");
		} else if(this.weaponIndex == 2 || this.weaponIndex == 3){
			alertnessXP += amount;
			System.out.println(this.name+" gained "+amount+" experience in Alertness!");
		} else if(this.weaponIndex == 4 || this.weaponIndex == 5){
			brutalityXP += amount;
			System.out.println(this.name+" gained "+amount+" experience in Brutality!");
		} else if(this.weaponIndex == 6 || this.weaponIndex == 7){
			precisionXP += amount;
			System.out.println(this.name+" gained "+amount+" experience in Precision!");
		} else if(this.weaponIndex == 8){
			logicXP += amount;
			System.out.println(this.name+" gained "+amount+" experience in Logic!");
		} else{
			faithXP += amount;
			System.out.println(this.name+" gained "+amount+" experience in Faith!");
		}
	}
}
