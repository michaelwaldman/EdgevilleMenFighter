import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.MessageListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.utilities.Timer;

@ScriptManifest(author = "Xaklon", category = Category.MISC, description = "Fights men at Edgeville", name = "Xak's Men Fighter", version = 1.1)
public class Main extends AbstractScript {
	//men are 3078 3079 3080
	//large door 1521

	ArrayList<Integer> myID = new ArrayList<Integer>();
	int currId;
	GameObject bankBooth;
	NPC men;
	GameObject door;
	NPC randomMan;
	Timer t;
	int menKilled = 0;
	Area menArea = new Area(3104, 3504, 3091, 3512);
	public int chosenFood = 333;
	public int State;
	public boolean startScript = true;
	public int getState() {
		return State;
	}
	
	@Override
	public int onLoop() {
		if(getDialogues().inDialogue()){
			getDialogues().clickContinue();
		}
		
		if(startScript){
			if (getState() == 1) {
				bank();
			} else if (getState() == 2) {
				walkToMen();
			} else if (getState() == 3) {
				fightMen();
			} else if (getState() == 4) {
				//bank();
			} else if (getState() == 1) {

			}
			}
		
		return 0;
	}
	@Override
	public void onStart(){
		if(menArea.contains(getLocalPlayer())&& !getInventory().isEmpty()){
			
		}
		getSkillTracker().start(Skill.STRENGTH);
		getSkillTracker().start(Skill.ATTACK);
		getSkillTracker().start(Skill.DEFENCE);

		t = new Timer();
		myID.add(3078);
		myID.add(3079);
		myID.add(3080);

		State = 1;
	}
	@Override 
	public void onExit(){
		Integer i = new Integer(menKilled);

		String lel = new String(i.toString());
		log(lel);
	}
	public void bank(){
			if(!getInventory().isFull()){
			bankBooth = getGameObjects().closest("Bank booth");
			if(bankBooth!=null){
				bankBooth.interact("Bank");
			}
			sleepUntil(()-> getBank().isOpen(), 15000);
			if(!getInventory().isEmpty()){
				getBank().depositAllItems();
				sleepUntil(()-> getInventory().isEmpty(), 15000);

			}
			getBank().withdrawAll(chosenFood);
			sleepUntil(()-> getInventory().isFull(), 15000);
			getBank().close();
			sleepUntil(()-> !getBank().isOpen(), 15000);

		}
			if(getInventory().isFull()){
				State = 2;
			}
			else if(!getInventory().isFull()){
				State = 1;
			}
	}
	
	public void walkToMen(){
		
		Tile menTile = new Tile(3101, 3509);
		if (getWalking().getDestinationDistance() <= Calculations.random(3, 8)) {
			getWalking().walk(menTile);
		}
		if (getLocalPlayer().distance(menTile) <= 1) {
			door = getGameObjects().closest(1521);
			if(door!=null && door.hasAction("Open")){
				door.interact("Open");
				sleep(200,400);
			}
			State = 3;
		}
	}
	
	public void fightMen(){
		 currId = myID.get(Calculations.random(0,myID.size()));
		randomMan = getNpcs().closest(f -> f != null && f.getID() == currId);

	//	if(menArea.contains(getLocalPlayer())){
			if(randomMan != null && !randomMan.isInCombat()){
				getCamera().rotateToEntity(randomMan);
				randomMan.interact("Attack");
				sleepUntil(()-> !randomMan.exists(), 15000);
				if(getSkills().getBoostedLevels(Skill.HITPOINTS) < Calculations.random(3, 7)){
					getInventory().get(333).interact("Eat");
					sleepUntil(()-> getSkills().getBoostedLevels(Skill.HITPOINTS) > 5, 15000); //sleeps until hitpoints are over five and it has eaten
					menKilled++;
					
				}
			if(getInventory().isEmpty()){
				State = 4;
			}
			}
		
			
		}
		/*else if(!menArea.contains(getLocalPlayer())){
			State = 2;
		}
	}*/
	
	public void walkToBank(){
		Tile bankTile = new Tile(3096, 3495);
		if (getWalking().getDestinationDistance() <= Calculations.random(3, 8)) {
			getWalking().walk(bankTile);
		}
		if (getLocalPlayer().distance(bankTile) <= 1) {
			State = 1;

			}
		}
	

public void onPaint(Graphics g){
	//if(getStartScript()){
	Color myColor = new Color(0, 0, 0, 125);
	  Color redColor = new Color(220, 0, 80, 150);

       Font helvetica = new Font("Helvetica", Font.BOLD, 13);
		g.setColor(myColor);
		g.setFont(helvetica);
		g.fillRect(50, 45, 275, 150);
		
		g.setFont(helvetica); 
		g.setColor(Color.WHITE);
		g.drawString("Xak's Men Fighter",50, 60);
		g.setColor(redColor);

	    g.drawString("Attack XP Gained: "+ getSkillTracker().getGainedExperience(Skill.ATTACK)+ "per hour:" +getSkillTracker().getGainedExperiencePerHour(Skill.ATTACK), 50, 90);
	    g.drawString("Strength XP Gained: "+ getSkillTracker().getGainedExperience(Skill.STRENGTH)+ "per hour:" +getSkillTracker().getGainedExperiencePerHour(Skill.STRENGTH), 50, 120);
	    g.drawString("Defence XP Gained: "+ getSkillTracker().getGainedExperience(Skill.DEFENCE)+ "per hour:" +getSkillTracker().getGainedExperiencePerHour(Skill.DEFENCE), 50, 150);
		g.drawString("Current Levels | Strength:"+getSkills().getRealLevel(Skill.STRENGTH) + " Attack:"+getSkills().getRealLevel(Skill.ATTACK)+" Defence:"+getSkills().getRealLevel(Skill.DEFENCE) ,50, 180);

	}
}
	
	
