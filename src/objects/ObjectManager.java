package objects;

import entities.Player;
import gamestates.Playing;
import levels.Level;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.ObjectConstants.*;

public class ObjectManager {

    private Playing playing;
    private BufferedImage[][] potionImgs, containerImgs;
    private BufferedImage spikeImg;
    private ArrayList<Potion> potions;
    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;
    public ObjectManager(Playing playing){
        this.playing = playing;
        loadImgs();

        potions = new ArrayList<>();
        containers = new ArrayList<>();
    }

    public void checkSpikesTouched(Player p){
        for (Spike s : spikes){
            if (s.getHitBox().intersects(p.getHitBox())){
                p.kill();
            }
        }
    }

    public void checkObjectTouched(Rectangle2D.Float hitBox) {
        for (Potion p : potions)
            if (p.isActive()){
                if(hitBox.intersects(p.getHitBox())){
                    p.setActive(false);
                    applyEffectToPlayer(p);

                }
            }
    }

    public void applyEffectToPlayer(Potion p){
        if(p.getObjType() == RED_POTION)
            playing.getPlayer().changeHealth(RED_POTION_VALUE);
        else
            playing.getPlayer().changePower(BLUE_POTION_VALUE);
    }

    public void checkObjectHit(Rectangle2D.Float attackBox){
        for(GameContainer gc : containers)
            if(gc.isActive() && !gc.doAnimation){
                if(gc.getHitBox().intersects(attackBox)) {
                    gc.setAnimation(true);
                    int type = 0;
                    if(gc.getObjType() == BARREL)
                        type = 1;

                    potions.add(new Potion((int)(gc.getHitBox().x + gc.getHitBox().width /2), (int) (gc.getHitBox().y), type));
                    return;

                }
            }
    }




    private void loadImgs(){
        BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
        potionImgs = new BufferedImage[2][7];

        for (int j = 0; j < potionImgs.length; j++)
            for (int i = 0; i < potionImgs[j].length; i++)
                potionImgs[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16);

        BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
        containerImgs = new BufferedImage[2][8];

        for (int j = 0; j < containerImgs.length; j++)
            for (int i = 0; i < containerImgs[j].length; i++)
                containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);

        spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);
    }

    public void update(){
        for (Potion p: potions)
            if(p.isActive())
                p.update();
        for (GameContainer gc : containers)
            if (gc.isActive())
                gc.update();
    }


    public void draw(Graphics g, int xLvlOffset){
        drawPotions(g, xLvlOffset);
        drawContainers(g, xLvlOffset);
        drawTraps(g, xLvlOffset);
    }

    private void drawTraps(Graphics g, int xLvlOffset) {
        for (Spike s: spikes){
            g.drawImage(spikeImg, (int)(s.getHitBox().x - xLvlOffset), (int)(s.getHitBox().y - s.getyDrawOffset()), SPIKE_WIDTH, SPIKE_HEIGHT, null);
        }
    }

    private void drawPotions(Graphics g, int xLvlOffset){
        for (Potion p: potions)
            if(p.isActive()){
                int type = 0;
                if (p.getObjType() == RED_POTION)
                    type = 1;
                g.drawImage(potionImgs[type][p.getAnimIndex()],
                        (int)p.getHitBox().x - p.getxDrawOffset() - xLvlOffset,
                        (int)p.getHitBox().y - p.getyDrawOffset(),
                        POTION_WIDTH,
                        POTION_HEIGHT,
                        null);
            }
    }
    private void drawContainers(Graphics g, int xLvlOffset){
        for (GameContainer gc: containers){
            if (gc.isActive()){
                int type = 0;
                if (gc.getObjType() == BARREL)
                    type = 1;
                g.drawImage(containerImgs[type][gc.getAnimIndex()],
                        (int)gc.getHitBox().x - gc.getxDrawOffset() - xLvlOffset,
                        (int)gc.getHitBox().y - gc.getyDrawOffset(),
                        CONTAINER_WIDTH,
                        CONTAINER_HEIGHT,
                        null);
            }
        }
    }

    public void loadObjects(Level newLevel){
        potions = new ArrayList<>(newLevel.getPotions());
        containers = new ArrayList<>(newLevel.getContainers());
        spikes = newLevel.getSpikes();
    }

    public void resetAllObjects(){
        loadObjects(playing.getLevelManager().getCurrentLevel());

        for (GameContainer gc: containers)
            gc.reset();
        for (Potion p : potions)
            p.reset();
    }
}
