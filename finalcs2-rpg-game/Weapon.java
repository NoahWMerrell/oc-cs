/**
 * @author Noah Merrell
 * The Weapon object, utilized in FinalProject
 */

public class Weapon{
	String name = "Name";
	int damage = 0;
	int hit = 0;
	int crit = 0;
	int aggro = 0;
	boolean isMagic = false;
	
	public Weapon(){}
	
	public Weapon(String n, int d, int h, int c, int a, boolean magic){
		name = n;
		damage = d;
		hit = h;
		crit = c;
		aggro = a;
		isMagic = magic;
	}
}
