package objects;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static utils.Constants.ANI_SPEED;
import static utils.Constants.ObjectConstants.*;



public class GameObject {
    protected int x, y, objType;
    protected Rectangle2D.Float hitBox;
    protected boolean doAnimation, active;
    protected int animTick, animIndex;
    protected int xDrawOffset, yDrawOffset;

    public GameObject(int x, int y, int objType){
        this.x = x;
        this.y = y;
        this.objType = objType;
        this.active = true;


    }

    protected void initHitBox(int width, int height){
        hitBox = new Rectangle2D.Float(x, y, (int)(width * Game.SCALE) ,(int)(height* Game.SCALE));
    }

    public void drawHitBox(Graphics g, int xLvlOffset){
        g.setColor(new Color(255, 0, 0));
        g.drawRect((int)hitBox.x - xLvlOffset, (int)hitBox.y, (int)hitBox.width, (int)hitBox.height);
    }


    protected void updateAnimationTick(){
        animTick++;
        if (animTick >= ANI_SPEED){
            animTick = 0;
            animIndex++;
            if (animIndex >= GetSpriteAmount(objType)){
                animIndex = 0;
                if(objType == BARREL || objType == BOX){
                    doAnimation = false;
                    active = false;
                }
            }
        }
    }

    public void reset(){
        animIndex = 0;
        animTick = 0;
        active = true;

        doAnimation = objType != BARREL && objType != BOX;
    }

    public int getObjType() {
        return objType;
    }

    public void setObjType(int objType) {
        this.objType = objType;
    }

    public Rectangle2D.Float getHitBox() {
        return hitBox;
    }

    public void setHitBox(Rectangle2D.Float hitBox) {
        this.hitBox = hitBox;
    }

    public boolean isActive() {
        return active;
    }

    public void setAnimation(boolean doAnimation){
        this.doAnimation = doAnimation;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getxDrawOffset() {
        return xDrawOffset;
    }


    public int getyDrawOffset() {
        return yDrawOffset;
    }

    public int getAnimIndex(){
        return animIndex;
    }

}
