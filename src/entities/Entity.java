package entities;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {
    protected float x, y;
    protected int width, height;
    protected Rectangle2D.Float hitBox;
    protected int animTick, animIndex;
    protected int state;
    protected float airSpeed;
    protected boolean inAir = false;

    protected int maxHealth;
    protected int currentHealth = maxHealth;

    protected Rectangle2D.Float attackBox;
    protected float walkSpeed;

    public Entity(float x, float y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected void drawHitBox(Graphics g, int xLvlOffset){
        g.setColor(new Color(255, 0, 0));
        g.drawRect((int)hitBox.x - xLvlOffset, (int)hitBox.y, (int)hitBox.width, (int)hitBox.height);
    }

    protected void initHitBox(int width, int height){
        hitBox = new Rectangle2D.Float(x, y, (int)(width * Game.SCALE) ,(int)(height* Game.SCALE));
    }

//    public void updateHitBox() {
//        hitBox.x = (int) x;
//        hitBox.y = (int) y;
//    }

    protected void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        g.drawRect((int)attackBox.x - lvlOffsetX, (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
    }

    public Rectangle2D.Float getHitBox(){
        return hitBox;
    }
}
