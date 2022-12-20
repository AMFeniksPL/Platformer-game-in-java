package entities;

import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;

public class Player extends Entity{
    private BufferedImage[][] animations;

    private int animTick, animIndex, animSpeed = 30;
    private int playerAction = RUNNING;
    private boolean isMoving;

    private boolean isAttacking = false;
    private boolean up, down, left, right;

    private boolean jump;
    private float playerSpeed;
    private int[][] lvlData;

    private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;

    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.25f * Game.SCALE;

    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;

    //StatusBar
    private BufferedImage statusBarImg;

    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    private int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthBarHeight = (int) (4 * Game.SCALE);
    private int healthBarXStart = (int) (34 * Game.SCALE);
    private int healthBarYStart = (int) (14 * Game.SCALE);


    private int maxHealth = 100;
    private int currentHealth = maxHealth;
    private int healthWidth = healthBarWidth;


    //AttackBox
    private Rectangle2D.Float attackBox;

    private int flipX = 0;
    private int flipW = 1;

    public Player (float x, float y, int width, int height){
        super(x, y, width, height);
        this.playerSpeed = 1 * Game.SCALE;
        this.loadAnimations();
        initHitBox(x, y, (int)(20 * Game.SCALE), (int)(27 * Game.SCALE));
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(20 * Game.SCALE), (int)(20 * Game.SCALE));
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void update(){
        updateHealthBar();
        updateAttackBox();
        updatePos();
        updateAnimationTick();
        setAnimation();
    }

    private void updateAttackBox() {
        if(right){
            attackBox.x = hitBox.x + hitBox.width + (int)(Game.SCALE * 10);
        }else if (left){
            attackBox.x = hitBox.x - hitBox.width - (int)(Game.SCALE * 10);
        }
        attackBox.y = hitBox.y + (Game.SCALE * 10);
    }

    private void updateHealthBar(){
        healthWidth = (int) ((currentHealth / (float)maxHealth) * healthBarWidth);
    }

    public void render(Graphics g, int lvlOffset){
        g.drawImage(
                animations[playerAction][animIndex],
                (int)(hitBox.x - xDrawOffset) - lvlOffset + flipX,
                (int)(hitBox.y - yDrawOffset) ,
                width * flipW,
                height,
                null);
        drawHitBox(g, lvlOffset);
        drawAttackBox(g, lvlOffset);
        drawUI(g);
    }

    private void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        g.drawRect((int)attackBox.x - lvlOffsetX, (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
    }

    private void drawUI(Graphics g) {
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.RED);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
    }

    public void setAttacking(boolean attacking) {
        this.isAttacking = attacking;
    }

    private void setAnimation(){

        int startAnim = playerAction;
        playerAction = isMoving ? RUNNING: IDLE;

        playerAction = inAir ? airSpeed < 0 ? JUMP : FALLING : playerAction;

        playerAction = isAttacking ? ATTACK : playerAction;
        
        if (startAnim != playerAction){
            resetAnimTick();
        }
    }

    private void resetAnimTick() {
        animTick = 0;
        animIndex = 0;
    }

    private void updatePos(){
        isMoving = false;

        if(jump)
            jump();

        if(!inAir)
            if((!left && !right) || (right && left))
                return;

        float xSpeed = 0;

        if (left){
            xSpeed -= playerSpeed;
            flipX = width;
            flipW = -1;
        }


        if (right){
            xSpeed += playerSpeed;
            flipX = 0;
            flipW = 1;
        }


        if(!inAir)
            if(!IsEntityOnFloor(hitBox, lvlData))
                inAir = true;

        if(inAir){
            if(CanMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height, lvlData)){
                hitBox.y += airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed);
            }
            else{
                hitBox.y = GetEntityYPosUnderRoofOrAboveFloor(hitBox, airSpeed);
                if (airSpeed > 0){
                    resetInAir();
                }
                else{
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }
        }else{
            updateXPos(xSpeed);
        }

        isMoving = true;


    }

    private void jump() {
        if(inAir){
            return;
        }
        inAir = true;
        airSpeed += jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }


    private void updateXPos(float xSpeed) {
        if(CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData)){
            hitBox.x += xSpeed;
            isMoving = true;
        }
        else{
            hitBox.x = GetEntityXPosNextToWall(hitBox, xSpeed);
        }
    }

    public void changeHealth(int value){
        currentHealth += value;
        if(currentHealth <= 0){
            currentHealth = 0;
            //GameOver();
        } else if (currentHealth >= maxHealth){
            currentHealth = maxHealth;
        }
    }


    private void updateAnimationTick(){
        animTick++;
        if(animTick >= animSpeed){
            animTick = 0;
            animIndex++;
            if (animIndex == GetSpriteAmount(playerAction)){
                isAttacking = false;
            }
            animIndex = animIndex % GetSpriteAmount(playerAction);
        }
    }

    private void loadAnimations(){
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

        animations = new BufferedImage[7][8];
        for (int j=0; j< animations.length; j++)
            for (int i=0; i <animations[j].length; i++){
                animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);
            }

        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData = lvlData;
        if(!IsEntityOnFloor(hitBox, lvlData))
        {
            inAir = true;
        }
    }

    public void resetDirBooleans(){
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

}
