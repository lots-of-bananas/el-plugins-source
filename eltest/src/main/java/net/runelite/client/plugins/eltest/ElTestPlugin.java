package net.runelite.client.plugins.eltest;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.elutils.ElUtils;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static net.runelite.client.plugins.eltest.ElTestState.*;

@Extension
@PluginDependency(ElUtils.class)
@PluginDescriptor(
		name = "El Test",
		description = "Test"
)
@Slf4j
public class ElTestPlugin extends Plugin implements MouseListener, KeyListener {
	@Inject
	private Client client;

	@Inject
	private ElUtils utils;

	@Inject
	private ConfigManager configManager;

	@Inject
	OverlayManager overlayManager;

	@Inject
	ItemManager itemManager;

	@Inject
	private ElTestConfig config;

	@Inject
	private ElTestOverlay overlay;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SpriteManager spriteManager;

	int clientTickBreak = 0;
	int tickTimer;
	boolean startTest;
	ElTestState status;

	int timeout;

	Instant botTimer;

	MenuEntry targetMenu;
	GameObject targetObject;
	NPC targetNpc;

	int clientTickCounter;
	boolean clientClick;

	int currentWorld;

	List<WorldPoint> path = Arrays.asList(new WorldPoint(3162, 3487, 0),
			new WorldPoint(3162, 3487, 0),
			new WorldPoint(3162, 3486, 0),
			new WorldPoint(3162, 3485, 0),
			new WorldPoint(3162, 3484, 0),
			new WorldPoint(3162, 3483, 0),
			new WorldPoint(3163, 3482, 0),
			new WorldPoint(3164, 3481, 0),
			new WorldPoint(3164, 3480, 0),
			new WorldPoint(3164, 3479, 0),
			new WorldPoint(3164, 3478, 0),
			new WorldPoint(3164, 3477, 0),
			new WorldPoint(3164, 3476, 0),
			new WorldPoint(3164, 3475, 0),
			new WorldPoint(3164, 3474, 0),
			new WorldPoint(3164, 3473, 0),
			new WorldPoint(3164, 3472, 0),
			new WorldPoint(3165, 3471, 0),
			new WorldPoint(3165, 3470, 0),
			new WorldPoint(3165, 3469, 0),
			new WorldPoint(3165, 3468, 0),
			new WorldPoint(3165, 3467, 0),
			new WorldPoint(3166, 3466, 0),
			new WorldPoint(3167, 3465, 0),
			new WorldPoint(3168, 3465, 0),
			new WorldPoint(3169, 3465, 0),
			new WorldPoint(3170, 3464, 0),
			new WorldPoint(3171, 3463, 0),
			new WorldPoint(3172, 3462, 0),
			new WorldPoint(3173, 3461, 0),
			new WorldPoint(3174, 3460, 0),
			new WorldPoint(3175, 3459, 0),
			new WorldPoint(3176, 3458, 0),
			new WorldPoint(3177, 3457, 0),
			new WorldPoint(3178, 3456, 0),
			new WorldPoint(3179, 3455, 0),
			new WorldPoint(3180, 3454, 0),
			new WorldPoint(3181, 3454, 0),
			new WorldPoint(3182, 3453, 0),
			new WorldPoint(3183, 3452, 0),
			new WorldPoint(3184, 3452, 0),
			new WorldPoint(3185, 3451, 0),
			new WorldPoint(3186, 3451, 0),
			new WorldPoint(3187, 3451, 0),
			new WorldPoint(3188, 3450, 0),
			new WorldPoint(3189, 3449, 0),
			new WorldPoint(3190, 3449, 0),
			new WorldPoint(3191, 3448, 0),
			new WorldPoint(3192, 3448, 0),
			new WorldPoint(3193, 3447, 0),
			new WorldPoint(3194, 3446, 0),
			new WorldPoint(3195, 3446, 0),
			new WorldPoint(3196, 3446, 0),
			new WorldPoint(3197, 3445, 0),
			new WorldPoint(3198, 3444, 0),
			new WorldPoint(3199, 3444, 0),
			new WorldPoint(3200, 3444, 0),
			new WorldPoint(3201, 3443, 0),
			new WorldPoint(3202, 3442, 0),
			new WorldPoint(3203, 3442, 0),
			new WorldPoint(3203, 3441, 0),
			new WorldPoint(3204, 3440, 0),
			new WorldPoint(3205, 3439, 0),
			new WorldPoint(3206, 3438, 0),
			new WorldPoint(3207, 3438, 0),
			new WorldPoint(3208, 3437, 0),
			new WorldPoint(3209, 3437, 0),
			new WorldPoint(3210, 3436, 0),
			new WorldPoint(3211, 3436, 0),
			new WorldPoint(3212, 3435, 0),
			new WorldPoint(3213, 3434, 0),
			new WorldPoint(3214, 3434, 0),
			new WorldPoint(3215, 3433, 0),
			new WorldPoint(3216, 3433, 0),
			new WorldPoint(3217, 3433, 0),
			new WorldPoint(3218, 3432, 0),
			new WorldPoint(3219, 3431, 0),
			new WorldPoint(3220, 3430, 0),
			new WorldPoint(3221, 3430, 0),
			new WorldPoint(3222, 3430, 0),
			new WorldPoint(3223, 3430, 0),
			new WorldPoint(3224, 3430, 0),
			new WorldPoint(3225, 3430, 0),
			new WorldPoint(3226, 3430, 0),
			new WorldPoint(3227, 3430, 0),
			new WorldPoint(3228, 3430, 0),
			new WorldPoint(3229, 3430, 0),
			new WorldPoint(3230, 3430, 0),
			new WorldPoint(3231, 3430, 0),
			new WorldPoint(3232, 3430, 0),
			new WorldPoint(3233, 3430, 0),
			new WorldPoint(3234, 3430, 0),
			new WorldPoint(3235, 3430, 0),
			new WorldPoint(3236, 3430, 0),
			new WorldPoint(3237, 3430, 0),
			new WorldPoint(3238, 3430, 0),
			new WorldPoint(3239, 3430, 0),
			new WorldPoint(3240, 3430, 0),
			new WorldPoint(3241, 3430, 0),
			new WorldPoint(3242, 3430, 0),
			new WorldPoint(3243, 3430, 0),
			new WorldPoint(3244, 3430, 0),
			new WorldPoint(3245, 3430, 0),
			new WorldPoint(3246, 3430, 0),
			new WorldPoint(3247, 3430, 0),
			new WorldPoint(3248, 3430, 0),
			new WorldPoint(3249, 3430, 0),
			new WorldPoint(3250, 3429, 0),
			new WorldPoint(3251, 3428, 0),
			new WorldPoint(3252, 3427, 0),
			new WorldPoint(3253, 3426, 0),
			new WorldPoint(3253, 3425, 0),
			new WorldPoint(3253, 3424, 0),
			new WorldPoint(3253, 3423, 0),
			new WorldPoint(3253, 3422, 0),
			new WorldPoint(3253, 3421, 0),
			new WorldPoint(3253, 3420, 0));

	LocalPoint beforeLoc = new LocalPoint(0, 0);
	WorldPoint destination = new WorldPoint(0,0,0);


	// Provides our config
	@Provides
	ElTestConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ElTestConfig.class);
	}

	@Override
	protected void startUp()
	{
		mouseManager.registerMouseListener(this);
		keyManager.registerKeyListener(this);
		botTimer = Instant.now();
		setValues();
		startTest=false;
		log.info("Plugin started");
		currentWorld = client.getWorld();
	}

	@Override
	protected void shutDown()
	{
		mouseManager.unregisterMouseListener(this);
		keyManager.unregisterKeyListener(this);
		overlayManager.remove(overlay);
		setValues();
		startTest=false;
		log.info("Plugin stopped");
	}

	@Subscribe
	private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
	{
		if (!configButtonClicked.getGroup().equalsIgnoreCase("ElTest"))
		{
			return;
		}
		log.info("button {} pressed!", configButtonClicked.getKey());
		if (configButtonClicked.getKey().equals("startButton"))
		{
			if (!startTest)
			{
				destination = new WorldPoint(config.X(),config.Y(),0);
				startTest = true;
				botTimer = Instant.now();
				overlayManager.add(overlay);
			} else {
				shutDown();
			}
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("ElTest"))
		{
			return;
		}
		startTest = false;
	}

	private void setValues()
	{
		timeout = 0;
		beforeLoc = client.getLocalPlayer().getLocalLocation();
		clientTickCounter=-1;
		clientTickBreak=0;
		clientClick=false;
	}

	@Subscribe
	private void onGameTick(GameTick gameTick) throws IOException {

		if(!startTest){
			return;
		}
		if (timeout > 0)
		{
			timeout--;
		} else {
			testFunction();
			Player player = client.getLocalPlayer();
			beforeLoc = player.getLocalLocation();
		}
	}

	private void testFunction(){
		targetObject = utils.findNearestGameObject(5109,5108);
		if (targetObject != null)
		{
			utils.currentPath.clear();
			targetMenu = new MenuEntry("", "", targetObject.getId(), 3,
					targetObject.getSceneMinLocation().getX(), targetObject.getSceneMinLocation().getY(), false);
			if(targetObject.getConvexHull()!=null){
				utils.delayMouseClick(targetObject.getConvexHull().getBounds(), sleepDelay());
			} else {
				utils.delayMouseClick(new Point(0,0), sleepDelay());
			}

		} else {
			if(utils.pathWalk(path,0,utils.isMoving(beforeLoc),sleepDelay())){
				timeout=tickDelay();
			} else {
				log.info("Path not found");
				utils.sendGameMessage("Path not found, stopping");
				shutDown();
			}
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		if(targetMenu!=null){
			menuAction(event, targetMenu.getOption(),targetMenu.getTarget(),targetMenu.getIdentifier(),targetMenu.getMenuAction(),targetMenu.getParam0(),targetMenu.getParam1());
			targetMenu=null;
		}
	}

	public void menuAction(MenuOptionClicked menuOptionClicked, String option, String target, int identifier, MenuAction menuAction, int param0, int param1)
	{
		menuOptionClicked.setMenuOption(option);
		menuOptionClicked.setMenuTarget(target);
		menuOptionClicked.setId(identifier);
		menuOptionClicked.setMenuAction(menuAction);
		menuOptionClicked.setActionParam(param0);
		menuOptionClicked.setWidgetId(param1);
	}

	private long sleepDelay()
	{
		return utils.randomDelay(false, 60, 350, 5, 40);
	}

	private int tickDelay()
	{
		return (int) utils.randomDelay(false,1, 3, 2, 2);
	}

	private ElTestState checkPlayerStatus()
	{
		Player player = client.getLocalPlayer();
		if(player==null){
			return NULL_PLAYER;
		}
		if(player.getPoseAnimation()!=808){
			tickTimer=2;
			return MOVING;
		}

		if(player.getAnimation()!=-1){
			tickTimer=2;
			return ANIMATING;
		}
		if(tickTimer>0){
			tickTimer--;
			return TICK_TIMER;
		}
		return PUSH;
	}


	@Override
	public MouseEvent mouseClicked(MouseEvent mouseEvent) {
		log.info("click"+String.valueOf(clientTickCounter));
		return mouseEvent;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent) {
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent) {
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent mouseEvent) {
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent mouseEvent) {
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent) {
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent) {
		return mouseEvent;
	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {
		log.info("key typed + " + keyEvent.getID());
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		log.info("key pressed + " + keyEvent.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		log.info("key released + " + keyEvent.getID());
		log.info("key char + " + keyEvent.getKeyChar());
	}
}
